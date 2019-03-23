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

//public class CustomAdapterCreditCards extends ArrayAdapter<DataModelCreditCards> implements View.OnClickListener { // kiszedtem a settings ikon klikkelését, de itt hagytam a kódot, hátha fog kelleni. Még 2 másik helyen is van kapcsolódó módosítás
public class CustomAdapterCreditCards2 extends ArrayAdapter<DataModelCreditCards>{

    private ArrayList<DataModelCreditCards> dataset;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView line_a;
        TextView line_b;
        TextView line_c;
        TextView line_d;
    }

    public CustomAdapterCreditCards2(ArrayList<DataModelCreditCards> data, Context context) {
        super(context, R.layout.row_item_credit_cards_2, data);
        this.dataset = data;
        this.mContext = context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModelCreditCards dataModelCreditCards = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_credit_cards_2, parent, false);
            viewHolder.line_a = (TextView) convertView.findViewById(R.id.line_a);
            viewHolder.line_b = (TextView) convertView.findViewById(R.id.line_b);
            viewHolder.line_c = (TextView) convertView.findViewById(R.id.line_c);
            viewHolder.line_d = (TextView) convertView.findViewById(R.id.line_d);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        //if (dataModelCreditCards.getStatus().equals(mContext.getResources().getString(R.string.active))){
        if (dataModelCreditCards.getStatus().equals(Constants.ACTIVE)){
            viewHolder.line_d.setTextColor(mContext.getResources().getColor(R.color.holo_red_dark));
            //viewHolder.line_d.setText(mContext.getResources().getString(R.string.open));
        }
        else {
            viewHolder.line_d.setTextColor(mContext.getResources().getColor(R.color.midgrey2));
            //viewHolder.line_d.setText(mContext.getResources().getString(R.string.closed));
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        lastPosition = position;
        //viewHolder.line_a.setText(dataModelCreditCards.getCardNumber());
        viewHolder.line_a.setText(
                dataModelCreditCards.getCardNumber().toString().substring(0,4) + "-" +
                dataModelCreditCards.getCardNumber().toString().substring(4,8) + "-" +
                dataModelCreditCards.getCardNumber().toString().substring(8,12) + "-" +
                dataModelCreditCards.getCardNumber().toString().substring(12,16)
        );
        viewHolder.line_b.setText(dataModelCreditCards.getCardType());
        viewHolder.line_c.setText(dataModelCreditCards.getValidTo());
        viewHolder.line_d.setText(dataModelCreditCards.getStatus().equals(Constants.ACTIVE) ? mContext.getString(R.string.active) : mContext.getString(R.string.inactive));
        // Return the completed view to render on screen
        return convertView;
    }
}
