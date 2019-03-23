package tamas.verovszki.xbank;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;



public class CustomAdapterCurrencies extends ArrayAdapter<DataModelCurrencies> implements View.OnClickListener{

    private ArrayList<DataModelCurrencies> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView txtCurrency;
        TextView txtBuy;
        TextView txtSell;
        ImageView info;
    }

    public CustomAdapterCurrencies(ArrayList<DataModelCurrencies> data, Context context) {
        super(context, R.layout.row_item_currencies, data);
        this.dataSet = data;
        this.mContext=context;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModelCurrencies dataModelCurrencies =(DataModelCurrencies)object;

        switch (v.getId())
        {
            case R.id.item_infoc:
                // a Tab-oknál működik a getString, itt nem, ezért kihagytam.  :( Nem tudom, miért?
                // Snackbar.make(v,  getString(R.string.text_date) + " " +dataModelCurrencies.getValidfrom(), Snackbar.LENGTH_LONG)
                Snackbar.make(v,  mContext.getString(R.string.updated_on) + " " + dataModelCurrencies.getValidfrom(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        DataModelCurrencies dataModelCurrencies = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_currencies, parent, false);
            viewHolder.txtBuy = (TextView) convertView.findViewById(R.id.buy_valuec);
            viewHolder.txtSell = (TextView) convertView.findViewById(R.id.sell_valuec);
            viewHolder.txtCurrency = (TextView) convertView.findViewById(R.id.currencyc);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.item_infoc);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.txtCurrency.setText(dataModelCurrencies.getCurrency());
        DecimalFormat percentageFormat = new DecimalFormat("#0.00");
        viewHolder.txtBuy.setText(percentageFormat.format(dataModelCurrencies.getBuy()));
        viewHolder.txtSell.setText(percentageFormat.format(dataModelCurrencies.getSell()));
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);

        /*if (lastPosition % 2 == 1) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_zebra_0));
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_zebra_2));
        }*/

        // Return the completed view to render on screen
        return convertView;
    }


}
