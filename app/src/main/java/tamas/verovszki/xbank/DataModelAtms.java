package tamas.verovszki.xbank;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import java.util.Calendar;

public class DataModelAtms {

    String address1;
    String address2;
    Double latitude;
    Double longitude;
    String type;

    Calendar fromTime;
    Calendar toTime;
    Calendar currentTime;
    Context mContext;

    LocationManager locationManager;

    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    /*public String getOpen() {
        return "hello";
    }*/

    public DataModelAtms(String address1,
                         String address2,
                         Double latitude,
                         Double longitude,
                         String type ) {
        this.address1 = address1;
        this.address2 = address2;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;

    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getType() {
        return type;
    }

    public Integer getDistance(Context context) {
        Location branchLocation = new Location("ATM location");
        branchLocation.setLatitude(this.latitude);
        branchLocation.setLongitude(this.longitude);
        Location currentLocation = new Location("vero");
        SharedPreferences sp = context.getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        currentLocation.setLatitude(Double.parseDouble(sp.getString(Constants.SHAREDPREFERENCES_LATITUDE_CURRENT, ("" + 0))));
        currentLocation.setLongitude(Double.parseDouble(sp.getString(Constants.SHAREDPREFERENCES_LONGITUDE_CURRENT, ("" + 0))));
        return Math.round(currentLocation.distanceTo(branchLocation)) / 1000;
    }

}