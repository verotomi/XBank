package tamas.verovszki.xbank;


public class DataModelBranches {



    String address1;
    String address2;
    String distance;
    String open;



    public DataModelBranches(String address1, String address2, String distance, String open ) {
        this.address1 = address1;
        this.address2 = address2;
        this.distance = distance;
        this.open = open;

    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getDistance() {
        return distance;
    }

    public String getOpen() {
        return open;
    }
}