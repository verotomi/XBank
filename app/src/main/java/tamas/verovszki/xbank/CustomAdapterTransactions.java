package tamas.verovszki.xbank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomAdapterTransactions extends ArrayAdapter<DataModelTransactions> {

    private ArrayList<DataModelTransactions> dataset;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView line_a1;
        TextView line_a2;
        TextView line_b1;
        TextView line_b2;
        TextView line_c1;
        TextView line_c2;
        TextView line_c3;
    }

    public CustomAdapterTransactions(ArrayList<DataModelTransactions> data, Context context) {
        super(context, R.layout.row_item_bank_account_history, data);
        this.dataset = data;
        this.mContext = context;
    }



    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        DataModelTransactions dataModelTransactions = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_bank_account_history, parent, false);
            viewHolder.line_a1 = (TextView) convertView.findViewById(R.id.line_a1);
            viewHolder.line_a2 = (TextView) convertView.findViewById(R.id.line_a2);
            viewHolder.line_b1 = (TextView) convertView.findViewById(R.id.line_b1);
            viewHolder.line_b2 = (TextView) convertView.findViewById(R.id.line_b2);
            viewHolder.line_c1 = (TextView) convertView.findViewById(R.id.line_c1);
            viewHolder.line_c2 = (TextView) convertView.findViewById(R.id.line_c2);
            viewHolder.line_c3 = (TextView) convertView.findViewById(R.id.line_c3);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.line_a1.setText(dataModelTransactions.getType());
        viewHolder.line_a2.setText(dataModelTransactions.getArrived_on());

        if (dataModelTransactions.getPartner_name().equals("-")){
            viewHolder.line_b1.setText(dataModelTransactions.getComment());
        }
        else{
            viewHolder.line_b1.setText(dataModelTransactions.getComment());
            viewHolder.line_c1.setText(dataModelTransactions.getPartner_account_number());
        }


        if (dataModelTransactions.getDirection().equals("in")){
            viewHolder.line_c2.setText(new DecimalFormat("#,##0.00").format(dataModelTransactions.getAmount()));
        }
        else{
            viewHolder.line_c2.setText(String.valueOf(-dataModelTransactions.getAmount()));
        }
        viewHolder.line_c3.setText(dataModelTransactions.getCurrency());

        convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_zebra_1));

        /*
        if (lastPosition % 2 == 1) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_zebra_0));
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_zebra_1));
        }*/

        // Return the completed view to render on screen
        return convertView;
    }
}
