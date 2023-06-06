<h1>Autonomous Parking Using Reinforcement Learning</h1>
<p>
    This project is an implementation of an autonomous parking system using reinforcement learning (RL). The agent program learns to parallel park a Dubins' car while avoiding obstacles such as sidewalks and other cars.  
</p>
 <h2> Project Objective </h2>
    <p>
      The objective of this project is to design an agent program that can autonomously park a Dubins' car by learning from reinforcement learning. The agent should be able to find the optimal state and control histories for the car to successfully parallel park without colliding with any obstacles.
    </p>
<h2>Problem Definition</h3>
    <p>
      The environment model used is a dynamical system for a Dubins' car with acceleration. The state variables of the system include the position on the x-axis, position on the y-axis, heading angle with respect to the local horizontal, and velocity. The control variables are the heading angle rate and acceleration. The car's motion is described by a set of ordinary differential equations.
      The parking task has a time duration of 10 seconds, and the initial and boundary conditions for the car are given. The feasible region is defined by geometric constraints on the x and y coordinates.
    </p>
<h2>Usage</h3>
    <p>To run the project, execute the Parking.py Python script. The code will generate a series of generations, with each generation improving the fitness of the individuals. The program will output the generation number and the Jaccard similarity coefficient (J) for the fittest individual. Once the termination conditions are met, the final state values will be printed.
        Additionally, the program provides a function to plot the solution trajectory of the car. This can be called after the termination of the algorithm to visualize the car's path during the parking process. 
    </p>
  <h2>Requirements</h2>
    <p>
      The project requires the following dependencies:
      <ul>
        <li>Python (version >=3.8)</li>
        <li>NumPy (version >=1.20.3)</li>
        <li>SciPy (version >=1.4.1)</li>
        <li>Matplotlib (version >=3.6.0)</li>
      </ul>
      Make sure to install these dependencies before running the code.
    </p>
 <h2> Results </h2>
    <p>
      The project aims to find an optimal solution for the autonomous parking task. The fitness of the fittest individual is continuously improved through the generations. The final state values of the fittest individual provide the successful parking configuration. 
      The project also provides the plot of the solution trajectory, which shows the path taken by the car during the parking process.
    </p>
 
   
