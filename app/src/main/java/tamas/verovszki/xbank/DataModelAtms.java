package tamas.verovszki.xbank;


public class DataModelAtms {



    String address1;
    String address2;
    String distance;
    String type;



    public DataModelAtms(String address1, String address2, String distance, String type ) {
        this.address1 = address1;
        this.address2 = address2;
        this.distance = distance;
        this.type = type;

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

    public String getType() {
        return type;
    }

}