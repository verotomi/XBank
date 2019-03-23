package tamas.verovszki.xbank;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DataModelBranches {
    String address1;
    String address2;
    Double latitude;
    Double longitude;
    String openingTimeSunday;
    String openingTimeMonday;
    String openingTimeTuesday;
    String openingTimeWednesday;
    String openingTimeThursday;
    String openingTimeFriday;
    String openingTimeSaturday;

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

    public DataModelBranches(String address1,
                             String address2,
                             Double latitude,
                             Double longitude,
                             String openingTimeSunday,
                             String openingTimeMonday,
                             String openingTimeTuesday,
                             String openingTimeWednesday,
                             String openingTimeThursday,
                             String openingTimeFriday,
                             String openingTimeSaturday) {

        this.address1 = address1;
        this.address2 = address2;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openingTimeSunday = openingTimeSunday;
        this.openingTimeMonday = openingTimeMonday;
        this.openingTimeTuesday = openingTimeTuesday;
        this.openingTimeWednesday = openingTimeWednesday;
        this.openingTimeThursday = openingTimeThursday;
        this.openingTimeFriday = openingTimeFriday;
        this.openingTimeSaturday = openingTimeSaturday;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getOpeningTimeSunday() {
        return openingTimeSunday;
    }

    public String getOpeningTimeMonday() {
        return openingTimeMonday;
    }

    public String getOpeningTimeTuesday() {
        return openingTimeTuesday;
    }

    public String getOpeningTimeWednesday() {
        return openingTimeWednesday;
    }

    public String getOpeningTimeThursday() {
        return openingTimeThursday;
    }

    public String getOpeningTimeFriday() {
        return openingTimeFriday;
    }

    public String getOpeningTimeSaturday() {
        return openingTimeSaturday;
    }

    /**
     * megnézi, hogy jelen pillanatban az adott bankfiók nyitva-van-e?
     * @return
     */
    public boolean getOpen() {
        //Date currentTime = Calendar.getInstance().getTime();
        //int dayOfWeek = currentTime.get()

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE"); // EEE -> Mon; EEEE -> Monday
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        boolean isOpen = false;

        switch (dayOfTheWeek) { // Ezt jó lenne átalakítani úgy, hogy string-erőforrásból vegye a napokat!
            case Constants.SUNDAY:
            case Constants.SONNTAG:
            case Constants.VASARNAP:
                isOpen = checkOpenState(openingTimeSunday);
                break;
            case Constants.MONDAY:
            case Constants.MONTAG:
            case Constants.HETFO:
                isOpen = checkOpenState(openingTimeMonday);
                break;
            case Constants.TUESDAY:
            case Constants.DIENSTAG:
            case Constants.KEDD:
                isOpen = checkOpenState(openingTimeWednesday);
                break;
            case Constants.WEDNESDAY:
            case Constants.MITTWOCH:
            case Constants.SZERDA:
                isOpen = checkOpenState(openingTimeThursday);
                break;
            case Constants.THURSDAY:
            case Constants.DONNERSTAG:
            case Constants.CSUTORTOK:
                isOpen = checkOpenState(openingTimeFriday);
                break;
            case Constants.FRIDAY:
            case Constants.FREITAG:
            case Constants.PENTEK:
                isOpen = checkOpenState(openingTimeSaturday);
                break;
            case Constants.SATURDAY:
            case Constants.SAMSTAG:
            case Constants.SZOMBAT:
                isOpen = checkOpenState(openingTimeSaturday);
                break;
        }
        return isOpen;
    }

    /**
     *  a banki nyitvatartást kiszámoló metódus segédmetódusa. Ez végzi az időpontok összehasonlítását
     * @param time
     * @return
     */
    public boolean checkOpenState(String time) {
        try {
            String[] times = time.split("-");
            String[] from = times[0].split(":");
            String[] until = times[1].split(":");

            fromTime = Calendar.getInstance();
            fromTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(from[0]));
            fromTime.set(Calendar.MINUTE, Integer.valueOf(from[1]));

            toTime = Calendar.getInstance();
            toTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(until[0]));
            toTime.set(Calendar.MINUTE, Integer.valueOf(until[1]));

            currentTime = Calendar.getInstance();
            //currentTime.set(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY); // ez nem kell ide, de a stackoverflow példában benne volt
            //currentTime.set(Calendar.MINUTE, Calendar.MINUTE); // ez nem kell ide, de a stackoverflow példában benne volt
            if (currentTime.after(fromTime) && currentTime.before(toTime)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public Integer getDistance(Context context) {
        Location branchLocation = new Location("Branch location");
        branchLocation.setLatitude(this.latitude);
        branchLocation.setLongitude(this.longitude);
        Location currentLocation = new Location("vero");
        SharedPreferences sp = context.getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        currentLocation.setLatitude(Double.parseDouble(sp.getString(Constants.SHAREDPREFERENCES_LATITUDE_CURRENT, ("" + 0))));
        currentLocation.setLongitude(Double.parseDouble(sp.getString(Constants.SHAREDPREFERENCES_LONGITUDE_CURRENT, ("" + 0))));
        return Math.round(currentLocation.distanceTo(branchLocation)) / 1000;
    }


}