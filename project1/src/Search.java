import java.io.*;
import java.util.*;
import static java.lang.Math.round;

/**
 * Search class to search between two cities from a given input list.
 * @author Pradeep Kumar Gontla.
 */
public class Search {

    public LinkedList<LinkedList<City>> adjacencyList = new LinkedList<>();
    public HashMap<String,Double> distanceMap = new HashMap<>();


    /**
     * adds the nodes to the adjacency list.
     * @param city city object to be added.
     */
    public void addNodes(City city){
        LinkedList<City> b = new LinkedList<>();
        b.add(city);
        adjacencyList.add(b);
    }

    /**
     * calculates the distance between parent and child objects
     * @param parent parent object
     * @param child child object
     * @return the distance between two cities.
     */
    public double calculateDistance(City parent, City child){
        float lat1 = parent.latitude;
        float lat2 = child.latitude;
        float lon1 = parent.longitude;
        float lon2 = child.longitude;
        return Math.sqrt( Math.pow((lat1-lat2),2) + Math.pow((lon1-lon2),2))* 100;
    }

    /**
     * Adds edges to the nodes created.
     * @param parent parent city name
     * @param child child city name.
     */
    public void addEdge(String parent, String child){
        int parIndex =-1;
        int childIndex=-1;
        for(int i = 0; i< adjacencyList.size(); i++){
            if(adjacencyList.get(i).get(0).name.equals(parent)) parIndex = i;

            else if (adjacencyList.get(i).get(0).name.equals(child))  childIndex = i;

        }
        double distance = calculateDistance(adjacencyList.get(parIndex).get(0), adjacencyList.get(childIndex).get(0));
        adjacencyList.get(parIndex).add(adjacencyList.get(childIndex).get(0));
        adjacencyList.get(childIndex).add(adjacencyList.get(parIndex).get(0));
        distanceMap.put(parent+child,distance);
        distanceMap.put(child+parent,distance);

    }

    /**
     * adds edges to the graph created.
     */
    public void readEdges(){
        try {
            FileReader fr = new FileReader("C:\\Users\\Pradeep Gontla\\OneDrive - rit.edu\\Documents\\GitHub\\ArtificialIntelligence\\project1\\src\\edge.dat");
            BufferedReader bufferedReader = new BufferedReader(fr);
            String line = bufferedReader.readLine();
            while (line!=null){
                String[] cities = line.split("\\s+");
                addEdge(cities[0] , cities[1]);
                line = bufferedReader.readLine();
            }

        } catch (FileNotFoundException e) {
            System.err.println("File not found: edge.dat");
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * reads the cities form the given filename and adds the nodes to adjacency list.
     */
    public void readCities(){
        try {
            FileReader fr = new FileReader("C:\\Users\\Pradeep Gontla\\OneDrive - rit.edu\\Documents\\GitHub\\ArtificialIntelligence\\project1\\src\\city.dat");
            BufferedReader bufferedReader = new BufferedReader(fr);
            String line = bufferedReader.readLine();
            while (line != null) {
                addNodes(new City(line));
                line = bufferedReader.readLine();
            }


        } catch (FileNotFoundException e) {
            System.err.println("File not found: city.dat");
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * finds the index based on city name provided.
     * @param start String format of city for which the index to be found.
     * @return index of the city in adjacency list / -1 if the city not found.
     */
    public int indexFromLine(String start){
        for(int i=0;i<adjacencyList.size();i++){
            if(adjacencyList.get(i).get(0).name.equals(start)) return i;
        }
        return -1;
    }

    /**
     * finds the index given a city object.
     * @param city city object for which the index to be found.
     * @return index of the city in graph / -1 if not found.
     */

    public int findIndex(City city){
        for(int i=0;i<adjacencyList.size();i++){
            if(adjacencyList.get(i).get(0)==city) return i;
        }
        return -1;
    }

    /**
     * calculates distance for a given path.
     * @param pathList path of dfs/bfs.
     * @return solution path cost
     */
    public double findTheDistance(ArrayList<City> pathList) {
        double distance =0.0;
        for(int i=0;i<pathList.size()-1;i++){
            distance+=calculateDistance(pathList.get(i),pathList.get(i+1));
        }
        return distance;

    }

    /**
     * dfs for the given search inputs.
     * @param start start city for the search.
     * @param end destination city for the search.
     * @param writer writes the output to the given output file.
     * @throws IOException exception thrown if the writer object input output is not found.
     */

    public void dfs(String start, String end, FileWriter writer) throws IOException {
        int startIndex = indexFromLine(start);
        int endIndex = indexFromLine(end);
        if(startIndex!=-1 && endIndex!=-1){
            Stack<City> stack = new Stack<>();
            HashMap<City,City> parentMap = new HashMap<>();
            ArrayList<City> pathList = new ArrayList<>();
            City source = adjacencyList.get(startIndex).get(0);
            stack.push(source);
            parentMap.put(source,null);
            while(!(stack.isEmpty())){
                City current = stack.pop();
                if(current.name.equals(end)) break;
                int index = findIndex(current);
                Comparator<City> nameComparator = Comparator.comparing(City::getName);
                ArrayList<City> cities = new ArrayList<>(adjacencyList.get(index));
                cities.sort(Collections.reverseOrder(nameComparator));
                for(City city :cities){
                    if(!parentMap.containsKey(city)){
                        stack.push(city);
                        parentMap.put(city,current);
                }
            }}
            City star = adjacencyList.get(endIndex).get(0);
            while(star!=null){
                pathList.add(0,star);
                star = parentMap.get(star);
            }
            String distance = String.valueOf((int)round(findTheDistance(pathList)));
            writer.write("\nDepth-First Search Results:\n");
            for (City city : pathList) {
                writer.write(city.name + "\n");
            }
            String hops = String.valueOf(pathList.size()-1);
            writer.write("That took "+hops+ " hops to find"+"\n" );
            writer.write("Total distance =  " + distance + " miles."+"\n");
            writer.flush();

        }
        else{
            if(startIndex==-1) System.err.println("No such city "+start);
            else System.err.println("No such city "+end);
            System.exit(0);
        }
    }

    /**
     * bfs for the given search inputs.
     * @param start start city for the search.
     * @param end destination city for the search.
     * @param writer writes the output to the given output file.
     * @throws IOException exception thrown if the writer object input output is not found.
     */
    public void bfs(String start, String end, FileWriter writer) throws IOException {
        int startIndex = indexFromLine(start);
        int endIndex = indexFromLine(end);
        if(startIndex!=-1 && endIndex!=-1){
            LinkedList<City> queue = new LinkedList<>();
            HashMap<City,City> parentMap = new HashMap<>();
            queue.add(adjacencyList.get(startIndex).get(0));
            parentMap.put(adjacencyList.get(startIndex).get(0),null);
            ArrayList<City> pathList = new ArrayList<>();
            while(queue.size()!=0){
                City star =queue.poll();
                if(star.name.equals(end)) {
                    break;
                }
                int index = findIndex(star);
                Comparator<City> nameComparator = Comparator.comparing(City::getName);
                ArrayList<City> cities = new ArrayList<>(adjacencyList.get(index));
                cities.sort(nameComparator);
                for(City city :cities){
                    if(!parentMap.containsKey(city)){
                        queue.add(city);
                        parentMap.put(city,star);
                    }
                }}
            City star = adjacencyList.get(endIndex).get(0);
            while(star!=null){
                pathList.add(0,star);
                star = parentMap.get(star);
            }
            String distance = String.valueOf((int)round(findTheDistance(pathList)));
            writer.write("\nBreadth-First Search Results:\n");
            for (City city : pathList) {
                writer.write(city.name + "\n");
            }
            String hops = String.valueOf(pathList.size()-1);
            writer.write("That took "+hops+ " hops to find"+"\n" );
            writer.write("Total distance =  " + distance + " miles."+"\n");
            writer.flush();
        }
        else{
            if(startIndex==-1) System.err.println("No such city "+start);
            else System.err.println("No such city "+end);
            System.exit(0);
        }
    }

    /**
     * driver for the breadth first search.
     * @param input input file name
     * @param writer writes the output to the provided output file.
     */
    public void performBreadthFirstSearch(String input , FileWriter writer){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String start = reader.readLine();
            String end = reader.readLine();
            bfs(start,end,writer);
        }
        catch (FileNotFoundException ignored){
            System.err.println(" File not found: "+input);
            System.exit(0);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * driver for the depth first search.
     * @param input input file name
     * @param writer writes the output to the provided output file.
     */
    public void performDepthFirstSearch(String input, FileWriter writer)  {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String start = reader.readLine();
            String end = reader.readLine();
            dfs(start,end,writer);
        }
        catch (FileNotFoundException ignored){
            System.err.println(" File not found: "+input);
            System.exit(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * euclidean distance between two cities
     * @param currentLatitude latitude of a current city
     * @param currentLongitude longitude of a current city
     * @param endLatitude destination latitude
     * @param endLongitude destination longitude
     * @return euclidean distance calculated with the formula.
     */
    public double euclideanDistance(float currentLatitude, float currentLongitude, float endLatitude, float endLongitude)
    {
        return (double) Math.sqrt(Math.pow((endLatitude-currentLatitude),2)+Math.pow((endLongitude-currentLongitude),2));

    }

    /**
     * create heuristic dictionary with the required distance.
     * @param latitude latitude of the destination city.
     * @param longitude longitude of the start city.
     * @return dictionary with the distance between city and destination.
     */
    public HashMap<City, Double> createHeuristics(float latitude , float longitude){
        HashMap<City,Double> heuristic = new HashMap<>();
        for (LinkedList<City> cities : adjacencyList) {
            City current = cities.get(0);
            heuristic.put(current, euclideanDistance(current.getLatitude(), current.getLongitude(), latitude, longitude));
        }
        return heuristic;
    }

    /**
     * Performs a star search on the given input .
     * @param heuristic heuristic used for a star search.
     * @param writer used to write the output to the given file.
     * @param start start country for the search.
     * @param end end country for the search.
     * @throws IOException if the writer object is not found.
     */
    public void aStar(HashMap<City,Double> heuristic , FileWriter writer, String start, String end) throws IOException {

        int endIndex = indexFromLine(end);
        int startIndex = indexFromLine(start);
        if(startIndex!=-1 && endIndex !=-1){
        Double pathCost;
        Comparator<City> nameComparator = Comparator.comparing(City::getPriority);
        PriorityQueue<City> pq = new PriorityQueue<>(heuristic.size(),nameComparator);
        City startCity = adjacencyList.get(startIndex).get(0);
        City destination = adjacencyList.get(endIndex).get(0);
        HashSet<City> set = new HashSet<>();
        HashMap<City,Double> pathCostMap = new HashMap<>();
        HashMap<City, City> pathMap = new HashMap<>();
        pathMap.put(startCity,null);
        ArrayList<City> pathList = new ArrayList<>();
        pathCostMap.put(startCity, 0.0);
        pq.add(startCity);

        while(!(pq.isEmpty())){
            City current = pq.poll();
            pathCost = pathCostMap.get(current);
            if(current == destination ){
                break;
            }
            int currentIndex = findIndex(current);
            if(!(adjacencyList.get(currentIndex).iterator().hasNext())) continue;
            for(City city: adjacencyList.get(currentIndex)){
                if(city!=current && city!=startCity && set.add(city))
                {
                    double cost =  (pathCost+calculateDistance(city,current));
                    city.priority =  (cost+heuristic.get(city));
                    pathMap.put(city,current);
                    pathCostMap.put(city,cost);
                    pq.add(city);
                }
            }
        }
        City star = destination;
        while(star!=null){
            pathList.add(0,star);
            star = pathMap.get(star);
        }
        writer.write("\nA* Search Results:\n");
        for(City city:pathList){
            writer.write(city.getName() +"\n");
        }
        writer.write("That took"+(pathList.size()-1)+"hops to find.\n");

        writer.write("Total Distance = "+ Math.round(pathCostMap.get(destination)) +" miles." );
        writer.flush();}
        else{
            if(startIndex==-1) System.err.println("No such city "+start);
            else System.err.println("No such city "+end);
            System.exit(0);
        }


    }

    /**
     * performs A star search and writes the results to output file provided.
     * @param input input file name.
     * @param writer writes the output to the output file.
     * @throws IOException  exception thrown if the writer object input output is not found.
     */
    public void performAStar(String input, FileWriter writer) throws IOException {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            String start = reader.readLine();
            String end = reader.readLine();
            int endIndex = indexFromLine(end);
            HashMap<City,Double> heuristic =createHeuristics(adjacencyList.get(endIndex).get(0).getLatitude(),adjacencyList.get(endIndex).get(0).getLongitude());
            aStar(heuristic,writer,start,end);

            }
            catch (FileNotFoundException ignored){
                System.err.println(" File not found: "+input);
                System.exit(0);
        }
    }

    /**
     * The driver code of the code calls in all the required methods in required chronology.
     * @param args commandline arguments.
     */
    public static void main(String[] args) {

        if(args.length!=2){

            System.err.println("Usage: java Search inputFile outputFile");
            System.exit(0);
        }
        else{
            try{
            String input = args[0];
            String output = args[1];
            Search search = new Search();
            search.readCities();
            search.readEdges();
            FileWriter writer = new FileWriter(output);
            search.performBreadthFirstSearch(input,writer);
            search.performDepthFirstSearch(input,writer);
            search.performAStar(input,writer);
            }
            catch (FileNotFoundException ignored){
                System.err.println(" File not found: "+args[1]);
                System.exit(0);
            }
             catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

