package tamas.verovszki.xbank;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
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

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.content.ContextCompat.startActivity;


public class CustomAdapterBranches extends ArrayAdapter<DataModelBranches> implements View.OnClickListener{

    private ArrayList<DataModelBranches> dataSet;
    Context mContext;

    Location location;
    LocationManager locationManager;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtAddress;
        TextView txtDistance;
        TextView txtOpen;
        ImageView locationIcon;
    }



    public CustomAdapterBranches(ArrayList<DataModelBranches> data, Context context) {
        super(context, R.layout.row_item_branches, data);
        this.dataSet = data;
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {


        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModelBranches dataModelBranches =(DataModelBranches) object;




        switch (v.getId())
        {

            case R.id.item_infob:

                /*Snackbar.make(v,  "hello", Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();*/

                // IDE FOG JÖNNI A TÉRKÉPEN VALÓ MEGJELENÍTÉS + ÚTVONALTERVEZÉS

                // a Tab-oknál működik a getString, itt nem, ezért kihagytam.  :( Nem tudom, miért?
                // Snackbar.make(v,  getString(R.string.text_date) + " " +dataModelCurrencies.getValidfrom(), Snackbar.LENGTH_LONG)
                /*Snackbar.make(v,  dataModelBranches.getOpen(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();*/
                //Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/?q=" + dataModelBranches.getLatitude() + "," + dataModelBranches.getLongitude()));
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/?q=" + dataModelBranches.getAddress1() + " " + dataModelBranches.getAddress2()));
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
        DataModelBranches dataModelBranches = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {


            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_branches, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.address_1);
            viewHolder.txtAddress = (TextView) convertView.findViewById(R.id.address_2);
            viewHolder.txtDistance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.txtOpen = (TextView) convertView.findViewById(R.id.open);
            viewHolder.locationIcon = (ImageView) convertView.findViewById(R.id.item_infob);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.txtName.setText(dataModelBranches.getAddress1());
        viewHolder.txtAddress.setText(dataModelBranches.getAddress2());

        // Ha engedélyezve van a helymeghatározás, megjelenítjük a távolság adatokat, ellenkező esetben
        if (checkGrantedPermissonForGetLocation()){
            viewHolder.txtDistance.setText(dataModelBranches.getDistance(getContext()).toString() + "km"); // korábban a getDistance()-nek nem volt paramétere, de ki kellett egészítsem a sharedPreferences-beolvasás miatt
        }
        else{ // ha nincs engedélyezve a helymeghatározás, nem írunk ki távolság-adatokat, sem térkép-ikont nem rajzolunk ki.
            viewHolder.txtDistance.setText("");
        }


        if (dataModelBranches.getOpen()){
            viewHolder.txtOpen.setTextColor(mContext.getResources().getColor(R.color.holo_red_dark));
            viewHolder.txtOpen.setText(mContext.getString(R.string.open));
        }
        else {
            viewHolder.txtOpen.setTextColor(mContext.getResources().getColor(R.color.darkgrey));
            viewHolder.txtOpen.setText(mContext.getString(R.string.closed));
        }

        /*
        if (dataModelBranches.getOpen().equals(mContext.getResources().getString(R.string.open))){
            viewHolder.txtOpen.setTextColor(mContext.getResources().getColor(R.color.holo_red_dark));
        }
        else if (dataModelBranches.getOpen().equals(mContext.getResources().getString(R.string.closed))) {
            viewHolder.txtOpen.setTextColor(mContext.getResources().getColor(R.color.darkgrey));
        }*/

        /* ki kellett cserélnem a switch-case-t if-else-re, mert a switch-case csak konstans értékeket fogad el
        // meg lehetett volna oldani ugy is hogy a case-hez beírom az "open" és a "nyitva"-t is de az nem lett volna elegáns megoldás.
        switch (dataModelBranches.getOpen()){
            case "open":
                viewHolder.txtOpen.setTextColor(mContext.getResources().getColor(R.color.holo_red_dark));
                break;
            case "closed":
                viewHolder.txtOpen.setTextColor(mContext.getResources().getColor(R.color.darkgrey));
                break;
        }*/

        viewHolder.locationIcon.setOnClickListener(this);
        viewHolder.locationIcon.setTag(position);
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
