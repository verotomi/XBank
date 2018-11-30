package tamas.verovszki.xbank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class CustomAdapterBranches extends ArrayAdapter<DataModelBranches> implements View.OnClickListener{

    private ArrayList<DataModelBranches> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtAddress;
        TextView txtDistance;
        TextView txtOpen;
        //ImageView info;
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

                // IDE FOG JÖNNI A TÉRKÉPEN VALÓ MEGJELENÍTÉS + ÚTVONALTERVEZÉS

                // a Tab-oknál működik a getString, itt nem, ezért kihagytam.  :( Nem tudom, miért?
                // Snackbar.make(v,  getString(R.string.text_date) + " " +dataModelCurrencies.getDate(), Snackbar.LENGTH_LONG)
                /*Snackbar.make(v,  dataModelBranches.getOpen(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();

                break;*/


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
            //viewHolder.info = (ImageView) convertView.findViewById(R.id.item_infob);

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
        viewHolder.txtDistance.setText(dataModelBranches.getDistance());
        viewHolder.txtOpen.setText(dataModelBranches.getOpen());
        //viewHolder.info.setOnClickListener(this);
        //viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }


}
