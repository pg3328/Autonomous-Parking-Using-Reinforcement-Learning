import random
import graycode
import numpy as np
from scipy.interpolate import CubicSpline
import math
import matplotlib.pyplot as plt

"""
    Program that is used to park the dubins car
    @author : pradeepkumargontla
"""


class Individual:
    """
        Class that holds the individual control variables
    """

    def __init__(self, individual, state=None, gamma=None, beta=None, j=None, fitnesser=None, set_of_states=None,
                 intrapolated_gamma=None, intrapolated_beta=None):
        self.individual_control_variables = individual
        self.state = state
        self.gamma_intrapolated = intrapolated_gamma
        self.beta_intrapolated = intrapolated_beta
        self.gamma = gamma
        self.beta = beta
        self.j = j
        self.fitness = fitnesser
        self.set_of_states = set_of_states


FILE_NAME = "controls.dat"
GAMMA_LIST = [-0.524, 0.524]
BETA_LIST = [-5, 5]
BIT_SIZE = 7
GAMMA_RANGE = 1.048
BETA_RANGE = 10
optimizer = 127
K = 200
# TIME_LIMITS = [i for i in range(10)]
TIME_LIMITS = np.linspace(0, 10, 10)
INITIAL_STATE = [0, 8, 0, 0]
FINAL_STATE = [0, 0, 0, 0]
MUTATION_RATE = 0.005


def gray_code(n):
    individual = []
    for i in range(n):
        x = random.randrange(0, 128)
        individual.append('{:07b}'.format(graycode.tc_to_gray_code(x)))
    return individual


def get_first_generation_list(number_of_individuals):
    generation = []
    for i in range(number_of_individuals):
        individual = Individual(gray_code(20))
        generation.append(individual)
    return generation


def compute_real_decimal_helper(decimal_value, type_of_values):
    if type_of_values == 'gamma':
        lower_bound = GAMMA_LIST[0]
        range_of_values = GAMMA_RANGE
    else:
        lower_bound = BETA_LIST[0]
        range_of_values = BETA_RANGE

    return (decimal_value / optimizer) * range_of_values + lower_bound


def check_feasibility(value, type_of_values):
    if type_of_values == 'gamma':
        lower_bound = GAMMA_LIST[0]
        upper_bound = GAMMA_LIST[1]
    else:
        lower_bound = BETA_LIST[0]
        upper_bound = BETA_LIST[1]
    if lower_bound <= value <= upper_bound:
        return True
    else:
        return False


def compute_real_decimal(generation):
    for individual in generation:
        gamma_values = []
        beta_values = []
        for index in range(len(individual.individual_control_variables)):
            bits = graycode.gray_code_to_tc(int("0b" + individual.individual_control_variables[index],2))
            if index % 2 == 0:
                gamma = compute_real_decimal_helper(bits, 'gamma')
                if check_feasibility(gamma, 'gamma'):
                    gamma_values.append(gamma)
                else:
                    individual.j = K
                    break
            else:
                beta = compute_real_decimal_helper(bits,'beta')
                if check_feasibility(beta, 'beta'):
                    beta_values.append(beta)
                else:
                    individual.j = K
                    break
        else:
            individual.gamma = gamma_values
            individual.beta = beta_values


def compute_states_and_cost(generation):
    for index in range(len(generation)):
        compute_states_individual(generation[index])
        jaccard_similarity(generation[index])
        fitness(generation[index])


def crossover(individual1, individual2):
    random_number = random.randrange(1, 140)
    string_for_individual1 = ''.join(individual1.individual_control_variables)
    string_for_individual2 = ''.join(individual2.individual_control_variables)
    child1 = string_for_individual1[:random_number] + string_for_individual2[random_number:]
    child2 = string_for_individual2[:random_number] + string_for_individual1[random_number:]
    return child1, child2


def fitness(individual):
    individual.fitness = 1 / (individual.j + 1)


def check_state_feasibility(state):
    x = state[0]
    y = state[1]
    if x <= -4 and y > 3:
        return True
    elif -4 < x < 4 and y > -1:
        return True
    elif x >= 4 and y > 3:
        return True
    else:
        return False


def compute_states_individual(individual):
    if individual.j is None:
        gamma_spline = generate_cubic_spline(individual.gamma)
        beta_spline = generate_cubic_spline(individual.beta)
        count = 10
        step = 0.1
        step_counter = 0.1
        previous_state = INITIAL_STATE
        set_of_states = []
        gamma_values = []
        beta_values = []
        while step_counter <= count:
            v = previous_state[3]
            alpha = previous_state[2]
            cosine = math.cos(alpha)
            sine = math.sin(alpha)
            gamma = cubic_spline_value(gamma_spline, step_counter)
            beta = cubic_spline_value(beta_spline, step_counter)
            update_state = [(v * cosine), (v * sine), float(gamma), float(beta)]
            resultant_state = [step * element for element in update_state]
            previous_state = [previous_state[i] + resultant_state[i] for i in range(len(previous_state))]
            if check_state_feasibility(previous_state):
                individual.state = previous_state
                set_of_states.append(previous_state)
                gamma_values.append(gamma)
                beta_values.append(beta)
                step_counter += step
            else:
                individual.j = K
                break

        individual.set_of_states = set_of_states
        individual.gamma_intrapolated = gamma_values
        individual.beta_intrapolated = beta_values


def jaccard_similarity(individual):
    if individual.j is None:
        d = [(individual.state[i] - FINAL_STATE[i]) ** 2 for i in range(len(individual.state))]
        sum_of_elements = 0
        for element in d:
            sum_of_elements += element
        individual.j = math.sqrt(sum_of_elements)


def generate_cubic_spline(values):
    """
        cubic spline for the required values
    :param values:
    :return:
    """
    cs = CubicSpline(TIME_LIMITS, np.array(values))
    return cs


def cubic_spline_value(cubic_spline, time):
    """
        returns the time gamma, beta value at a given time
    :param cubic_spline: cubic spline object
    :param time: time of the error
    :return: gamma,beta values
    """
    return cubic_spline(time)


def check_the_conditions(generation, generation_number):
    """
        check the final parameters
    :param generation: for the generation
    :param generation_number:Number of the generation
    :return: boolean based on the input
    """
    if generation[0].j > 0.1:
        return True
    elif generation_number < 1200:
        return True
    else:
        return False


def mutation(child1, child2):
    """
        Mutation of the child based on the given mutation rate
    :param child1: child of the previous generation
    :param child2: child of the previous generation
    :return: mutated children
    """
    for i in range(len(child1)):
        if random.random() < MUTATION_RATE:
            if child1[i] == '0':
                value = '1'
            else:
                value = '0'
            child1 = child1[:i] + value + child1[i + 1:]
        if random.random() < MUTATION_RATE:
            if child2[i] == '0':
                value = '1'
            else:
                value = '0'
            child2 = child2[:i] + str(value) + child2[i + 1:]
    child1_individual = [child1[i:i + 7] for i in range(0, len(child1), 7)]
    child2_individual = [child2[i:i + 7] for i in range(0, len(child2), 7)]
    return child1_individual, child2_individual


def add_to_generation(child1, child2, new_generation):
    """
        adds the children to generation
    :param child1: individual of the new generation
    :param child2: individual of the new generation
    :param new_generation: new generation
    :return: None
    """
    new_generation.append(child1)
    new_generation.append(child2)


def go_with_the_generations(generation):
    """
        creates the generations of the individuals upto the constraints are met
    :param generation: first generation
    :return: fittest generation
    """
    generation_number = 1
    current_generation = generation
    while check_the_conditions(current_generation, generation_number):
        new_generation = []
        add_to_generation(current_generation[0], current_generation[1], new_generation)
        fitness_values = [ind.fitness for ind in current_generation]
        for i in range(99):
            parent1, parent2 = random.choices(current_generation, weights=fitness_values, k=2)
            child1, child2 = crossover(parent1, parent2)
            child1, child2 = mutation(child1, child2)
            add_to_generation(Individual(child1), Individual(child2), new_generation)
        compute_real_decimal(new_generation)
        compute_states_and_cost(new_generation)
        new_generation.sort(key=lambda x: x.fitness, reverse=True)
        # print(f"Generation {generation_number} : J = " + str(new_generation[0].j))
        print("Generation %d : J = %s" % (generation_number, str(new_generation[0].j)))
        current_generation = new_generation
        generation_number += 1
    return current_generation


def get_the_result(generation):
    """
        Prints the result of the fittest individual.
    :param generation: fittest generation
    :return: None
    """
    print("Final State Values : ")
    print("x_f = " + str(generation[0].state[0]))
    print("y_f = " + str(generation[0].state[1]))
    print("alpha_f = " + str(generation[0].state[2]))
    print("v_f = " + str(generation[0].state[3]))


def get_the_metrics(individual):
    """
        Based on individual returns the state variables
    :param individual: Fittest individual
    :return: state variables
    """
    x = []
    y = []
    alpha = []
    v = []

    for state in individual.set_of_states:
        x.append(state[0])
        y.append(state[1])
        alpha.append(state[2])
        v.append(state[3])
    return x, y, alpha, v


def plot_solution_trajectory(x_region, y_region):
    """
        Plots the solution trajectory
    :param x_region: x of the plot
    :param y_region: y of the plot
    :return: None
    """
    fig, ax = plt.subplots(figsize=(8, 8))
    ax.set_xlim(-15, 15)
    ax.set_ylim(-15, 15)
    ax.set_xlabel('X')
    ax.set_ylabel('Y')
    x1, y1 = -15, 3
    x2, y2 = -4, 3
    x3, y3 = -4, -1
    x4, y4 = 4, -1
    x5, y5 = 4, 3
    x6, y6 = 15, 3
    plt.plot([x1, x2], [y1, y2])
    plt.plot([x2, x3], [y2, y3])
    plt.plot([x3, x4], [y3, y4])
    plt.plot([x4, x5], [y4, y5])
    plt.plot([x5, x6], [y5, y6])
    plt.plot(x_region, y_region)
    plt.grid(True)
    plt.show()


def plot_the_horizontal(x, t):
    plt.plot(t, x)
    plt.grid(True)
    plt.xlabel("time(s)")
    plt.ylabel("x(ft)")
    plt.title("X VS TIME")
    plt.show()


def plot_the_vertical(y, t):
    plt.plot(t, y)
    plt.grid(True)
    plt.xlabel("time(s)")
    plt.ylabel("y(ft)")
    plt.title("Y VS TIME")
    plt.show()


def plot_alpha(alpha, t):
    plt.plot(t, alpha)
    plt.grid(True)
    plt.xlabel("time(s)")
    plt.ylabel("alpha(rad)")
    plt.title("ALPHA VS TIME")
    plt.show()


def plot_v(v, t):
    plt.plot(t, v)
    plt.grid(True)
    plt.xlabel("time(s)")
    plt.ylabel("v(ft/s)")
    plt.title("V VS TIME")
    plt.show()


def plot_gamma(gamma, t):
    plt.plot(t, gamma)
    plt.grid(True)
    plt.xlabel("time(s)")
    plt.ylabel("gamma(rad/s)")
    plt.title("GAMMA VS TIME")
    plt.show()


def plot_beta(beta, t):
    """
        Plots the beta graph
    :param beta: beta variables
    :param t: time for x axis
    :return: None
    """
    plt.plot(t, beta)
    plt.grid(True)
    plt.xlabel("time(s)")
    plt.ylabel("beta(ft/s2)")
    plt.title("BETA VS TIME")
    plt.show()


def get_the_graph(generation):
    """
        Plots the graphs as per requirement
    :param generation: final generation which has fittest individual
    :return: None
    """
    x, y, alpha, v = get_the_metrics(generation[0])
    t = np.arange(0, 10, 0.1).tolist()
    plot_solution_trajectory(x, y)
    plot_the_horizontal(x, t)
    plot_the_vertical(y, t)
    plot_alpha(alpha, t)
    plot_v(v, t)
    plot_gamma(generation[0].gamma_intrapolated, t)
    plot_beta(generation[0].beta_intrapolated, t)
    plt.show()


def write_to_file(file_name, individual):
    """
        writes controls.dat file with the control variables
    :param file_name: name of the file to be written
    :param individual: individual of the generation for which is solution
    :return: None
    """
    with open(file_name, 'w') as file:
        for i in range(len(individual.gamma)):
            file.write(str(individual.gamma[i]) + '\n')
            file.write(str(individual.beta[i]) + '\n')


def main():
    """
        Driver code of the program
    :return: None
    """
    generation = get_first_generation_list(200)
    compute_real_decimal(generation)
    compute_states_and_cost(generation)
    generation.sort(key=lambda x: x.fitness, reverse=True)
    print("Generation %d : J = %s" % (0, str(generation[0].j)))
    final_generation = go_with_the_generations(generation)
    get_the_result(final_generation)
    get_the_graph(final_generation)
    write_to_file(FILE_NAME, final_generation[0])


if __name__ == '__main__':
    """
        Starter code of the program
    """
    main()
