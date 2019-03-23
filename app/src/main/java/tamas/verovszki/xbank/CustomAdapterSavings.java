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

public class CustomAdapterSavings extends ArrayAdapter<DataModelSavings> {

    private ArrayList<DataModelSavings> dataset;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView TextViewBankAccountNumber2;
        TextView TextViewType2;
        TextView line_a;
        TextView line_b;
        TextView line_c;
        TextView line_d;
        TextView line_e;
        TextView line_f;
        TextView line_g;
        TextView line_h;

    }

    public CustomAdapterSavings(ArrayList<DataModelSavings> data, Context context) {
        super(context, R.layout.row_item_savings, data);
        this.dataset = data;
        this.mContext = context;
    }



    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        DataModelSavings DataModelSavings = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_savings, parent, false);
            viewHolder.line_a = (TextView) convertView.findViewById(R.id.line_a);
            viewHolder.line_b = (TextView) convertView.findViewById(R.id.line_b);
            viewHolder.line_c = (TextView) convertView.findViewById(R.id.line_c);
            viewHolder.line_d = (TextView) convertView.findViewById(R.id.line_d);
            viewHolder.line_e = (TextView) convertView.findViewById(R.id.line_e);
            viewHolder.line_f = (TextView) convertView.findViewById(R.id.line_f);
            viewHolder.line_g = (TextView) convertView.findViewById(R.id.line_g);
            viewHolder.line_h = (TextView) convertView.findViewById(R.id.line_h);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        viewHolder.line_a.setText(DataModelSavings.getType() + " (" + DataModelSavings.getDuration() + " " + mContext.getString(R.string.days) + ")");
        viewHolder.line_b.setText(DataModelSavings.getExpire_date());
        //viewHolder.line_c.setText(DataModelSavings.getExpire_date());
        viewHolder.line_d.setText(String.valueOf(DataModelSavings.getRate()) + "%");
        viewHolder.line_e.setText(String.valueOf(new DecimalFormat("#,##0.00").format(DataModelSavings.getAmount())));
        viewHolder.line_f.setText(DataModelSavings.getCurrency());
        //viewHolder.line_g.setText(DataModelSavings.getCurrency());
        //viewHolder.line_h.setText(DataModelSavings.getId());
        //viewHolder.line_f.setText(DataModelSavings.getExpire_date());

        // ez jól néz ki, lehet hogy maradhatna itt is + be lehetne tenni máshova is
        convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_zebra_1));

        /*if (lastPosition % 2 == 1) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_zebra_0));
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_zebra_1));
        }*/

        // Return the completed view to render on screen
        return convertView;
    }
}
