import java.io.*;
import java.util.*;

import static java.lang.Math.round;


public class Search {

    public LinkedList<LinkedList<City>> adjacencyList = new LinkedList<>();
    public HashMap<String,Double> distanceMap = new HashMap<>();



    public void addNodes(City city){
        LinkedList<City> b = new LinkedList<>();
        b.add(city);
        adjacencyList.add(b);
    }

    public double calculateDistance(City parent, City child){
        float lat1 = parent.latitude;
        float lat2 = child.latitude;
        float lon1 = parent.longitude;
        float lon2 = child.longitude;
        return Math.sqrt( Math.pow((lat1-lat2),2) + Math.pow((lon1-lon2),2))* 100;
    }
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
            System.out.println("File not found: edge.dat");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

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
            System.out.println("File not found: city.dat");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public int indexFromLine(String start){
        for(int i=0;i<adjacencyList.size();i++){
            if(adjacencyList.get(i).get(0).name.equals(start)) return i;
        }
        return -1;
    }

    public int findIndex(City city){
        for(int i=0;i<adjacencyList.size();i++){
            if(adjacencyList.get(i).get(0)==city) return i;
        }
        return -1;
    }
    public double findTheDistance(ArrayList<City> pathList) {
        double distance =0.0;
        for(int i=0;i<pathList.size()-1;i++){
            distance+=calculateDistance(pathList.get(i),pathList.get(i+1));
        }
        return distance;

    }

    public void dfs(String start, String end, FileWriter writer) throws IOException {
        int startIndex = indexFromLine(start);
        if(startIndex!=-1){
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
            int endIndex = indexFromLine(end);
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
            System.out.println("Start city is not in the list");
        }
    }
    public void bfs(String start, String end, FileWriter writer) throws IOException {
        int startIndex = indexFromLine(start);
        if(startIndex!=-1){
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
            int endIndex = indexFromLine(end);
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
            System.out.println("Start city is not in the list");
        }
    }

    public void performBreadthFirstSearch(String input_filename , FileWriter writer){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input_filename));
            String start = reader.readLine();
            String end = reader.readLine();
            bfs(start,end,writer);

        } catch (FileNotFoundException e) {
            System.out.println("File not found: "+input_filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void performDepthFirstSearch(String inputFileName , FileWriter writer)  {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
            String start = reader.readLine();
            String end = reader.readLine();
            dfs(start,end,writer);
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: output file ");
            System.exit(-1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public float euclideanDistance(float currentLatitude, float currentLongitude, float endLatitude, float endLongitude)
    {
        return (float) Math.sqrt(Math.pow((endLatitude-currentLatitude),2)+Math.pow((endLongitude-currentLongitude),2));

    }

    public HashMap<City, Float> createHeuristics(float latitude , float longitude){
        HashMap<City,Float> heuristic = new HashMap<>();
        for (LinkedList<City> cities : adjacencyList) {
            City current = cities.get(0);
            heuristic.put(current, euclideanDistance(current.getLatitude(), current.getLongitude(), latitude, longitude));
        }
        return heuristic;
    }

    public void aStar(HashMap<City,Float> heuristic , FileWriter writer, int startIndex,City destination) throws IOException {
        int pathcost;
        Comparator<City> nameComparator = Comparator.comparing(City::getPriority);
        PriorityQueue<City> pq = new PriorityQueue<>(heuristic.size(),nameComparator);
        City start = adjacencyList.get(startIndex).get(0);
        HashSet<City> set = new HashSet<>();
        HashMap<City,Integer> pathCostMap = new HashMap<>();
        HashMap<City, City> pathMap = new HashMap<>();
        pathMap.put(start,null);
        ArrayList<City> pathList = new ArrayList<>();
        pathCostMap.put(start,0);
        pq.add(start);

        while(!(pq.isEmpty())){
            City current = pq.poll();
            pathcost = pathCostMap.get(current);
            if(current == destination ){
                break;
            }
            int currentIndex = findIndex(current);
            if(!(adjacencyList.get(currentIndex).iterator().hasNext())) continue;
            for(City city: adjacencyList.get(currentIndex)){
                if(city!=current && city!=start && set.add(city))
                {
                    int cost = (int) (pathcost+calculateDistance(city,current));
                    city.priority = (int) (cost+heuristic.get(city));
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
        writer.write("Total Distance = "+ pathCostMap.get(destination) +" miles." );
        writer.flush();


    }

    public void performAStar(BufferedReader reader, FileWriter writer) throws IOException {
        try {

            String start = reader.readLine();
            int startIndex = indexFromLine(start);
            String end = reader.readLine();
            int endIndex = indexFromLine(end);
            HashMap<City,Float> heuristic =createHeuristics(adjacencyList.get(endIndex).get(0).getLatitude(),adjacencyList.get(endIndex).get(0).getLongitude());
            aStar(heuristic,writer,startIndex,adjacencyList.get(endIndex).get(0));

            }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws IOException {
        if(args.length!=2){

            System.out.println("Usage: java Search inputFile outputFile");
        }
        else{
            System.out.println(args[0]);
            System.out.println(args[1]);
            String input = args[0];
            String output = args[1];
            Search search = new Search();
            search.readCities();
            search.readEdges();
            BufferedReader reader = new BufferedReader(new FileReader(input));
            FileWriter writer = new FileWriter(output);
            search.performBreadthFirstSearch(input,writer);
            search.performDepthFirstSearch(input,writer);
            search.performAStar(reader,writer);
        }
    }
}

