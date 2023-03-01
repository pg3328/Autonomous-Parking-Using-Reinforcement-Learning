import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

public class Search {

    public LinkedList<City> graph = new LinkedList<>();

    public void createGraph(){


    }

    public void readCities(){
        try {
            FileReader fr = new FileReader("city.dat");
            BufferedReader bufferedReader = new BufferedReader(fr);
            String line = bufferedReader.readLine();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: city.dat");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    public static void main(String[] args) {
        if(args.length!=2){
            String a = "1 2 3 4";
            String[] b = a.split(" ");
            for(int i=0;i<b.length;i++){
                System.out.println(b[i]);
            }
            System.out.println("Usage: java Search inputFile outputFile");
        }
        else{
            String input = args[0];
            String output = args[1];
            Search search = new Search();
            search.readCities();
        }

    }
}
