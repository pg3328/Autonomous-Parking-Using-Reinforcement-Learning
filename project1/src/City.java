public class City {
    String name;
    String state;
    float latitude;
    float longitude;
    public City(String line){
        String[] arr =parseline(line);
        this.name =arr[0];
        this.state = arr[1];
        this.latitude = Float.parseFloat(arr[2]);
        this.longitude = Float.parseFloat(arr[3]);
    }
    public String[] parseline(String line){
        String[] splitter = line.split("\\s+");
        return splitter;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                '}';
    }
}
