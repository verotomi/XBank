package tamas.verovszki.xbank;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.content.ContextCompat.startActivity;


public class CustomAdapterAtms extends ArrayAdapter<DataModelAtms> implements View.OnClickListener{

    private ArrayList<DataModelAtms> dataSet;
    Context mContext;

    Location location;
    LocationManager locationManager;

    // View lookup cache
    private static class ViewHolder {
        TextView txtAddress1;
        TextView txtAddress2;
        TextView txtDistance;
        TextView txtType;
        ImageView info;
    }

    public CustomAdapterAtms(ArrayList<DataModelAtms> data, Context context) {
        super(context, R.layout.row_item_atms, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModelAtms dataModelAtms =(DataModelAtms)object;
        //Toast.makeText(mContext, "" + v.getId(), Toast.LENGTH_SHORT).show();
        switch (v.getId())
        {
            case R.id.item_infob:
                // IDE FOG JÖNNI A TÉRKÉPEN VALÓ MEGJELENÍTÉS + ÚTVONALTERVEZÉS

                // a Tab-oknál működik a getString, itt nem, ezért kihagytam.  :( Nem tudom, miért?
                //Snackbar.make(v,  getString(R.string.text_date) + " " +dataModelCurrencies.getValidfrom(), Snackbar.LENGTH_LONG)
                /*Snackbar.make(v,  "hello", Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();*/

                // WOW működik!!!
                // a startactivity-be be kellett rakni egsy bundle-t
                // ehhez kellett csinálni egy bundle-t, kellett a setFlag is
                // a startactivity-hez kellett egy static import (Alt+enter): android.support.v4.content.ContextCompat.startActivity;
                // Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/?q=" + dataModelAtms.getLatitude() + "," + dataModelAtms.getLongitude()));
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/?q=" + dataModelAtms.getAddress1() + " " + dataModelAtms.getAddress2()));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);
                startActivity(getContext(), intent, bundle);
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModelAtms dataModelAtms = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_atms, parent, false);
            viewHolder.txtAddress1 = (TextView) convertView.findViewById(R.id.address_1);
            viewHolder.txtAddress2 = (TextView) convertView.findViewById(R.id.address_2);
            viewHolder.txtDistance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_infob);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtAddress1.setText(dataModelAtms.getAddress1());
        viewHolder.txtAddress2.setText(dataModelAtms.getAddress2());

        // Ha engedélyezve van a helymeghatározás, megjelenítjük a távolság adatokat, ellenkező esetben
        if (checkGrantedPermissonForGetLocation()){
            viewHolder.txtDistance.setText(dataModelAtms.getDistance(getContext()).toString() + "km"); // korábban a getDistance()-nek nem volt paramétere, de ki kellett egészítsem a sharedPreferences-beolvasás miatt
        }
        else{ // ha nincs engedélyezve a helymeghatározás, nem írunk ki távolság-adatokat, sem térkép-ikont nem rajzolunk ki.
            viewHolder.txtDistance.setText("");
        }

        viewHolder.txtType.setText(dataModelAtms.getType());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * megnézi, hogy engedélyezve van -e a helymeghatározás
     * @return
     */
    public boolean checkGrantedPermissonForGetLocation(){
        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE); // ide kellett a getContext() !
        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return true;
        } catch (SecurityException e) {
            //dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
            return false;
        }
    }

}
