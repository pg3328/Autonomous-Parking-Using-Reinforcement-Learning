import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

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
        return Math.sqrt( (lat1-lat2)*(lat1-lat2) + (lon1-lon2)*(lon1-lon2) ) * 100;
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

    public Search(){}

    public void printStuff(){

        for(int i = 0; i< adjacencyList.size(); i++){
            System.out.println(adjacencyList.get(i).get(0).name);
        }
    }
    public void readEdges(){
        try {
            FileReader fr = new FileReader("edge.dat");
            BufferedReader bufferedReader = new BufferedReader(fr);
            String line = bufferedReader.readLine();
            while (line!=null){
                String[] cities = line.split("//s+");
//                addEdges(cities[0] , cities[1]);
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
            while (line!=null){
                addNodes(new City(line));
                line = bufferedReader.readLine();
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found: city.dat");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public static void main(String[] args) {
//        if(args.length!=2){
//
//            System.out.println("Usage: java Search inputFile outputFile");
//        }
//        else{
//            String input = args[0];
//            String output = args[1];
            Search search = new Search();
            search.readCities();
            search.printStuff();
//        }

    }
}
