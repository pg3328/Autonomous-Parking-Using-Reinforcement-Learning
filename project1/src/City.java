public class City  {
    public String getName() {
        return name;
    }

    String name;
    String state;
    float latitude;
    float longitude;
    double priority;

    public double getPriority() {
        return priority;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public City(String line){
        String[] arr =parseline(line);
        this.name =arr[0];
        this.state = arr[1];
        this.latitude = Float.parseFloat(arr[2]);
        this.longitude = Float.parseFloat(arr[3]);
        this.priority = -1;
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
