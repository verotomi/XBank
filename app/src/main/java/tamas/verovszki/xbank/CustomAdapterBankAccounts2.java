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

//public class CustomAdapterCreditCards extends ArrayAdapter<DataModelBankAccounts> implements View.OnClickListener { // kiszedtem a settings ikon klikkelését, de itt hagytam a kódot, hátha fog kelleni. Még 2 másik helyen is van kapcsolódó módosítás
public class CustomAdapterBankAccounts2 extends ArrayAdapter<DataModelBankAccounts>{





    private ArrayList<DataModelBankAccounts> dataset;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView line_a;
        TextView line_b;
        TextView line_c;
        TextView line_d;
        TextView line_e;
    }

    public CustomAdapterBankAccounts2(ArrayList<DataModelBankAccounts> data, Context context) {
        super(context, R.layout.row_item_bank_account_balance_2, data);
        this.dataset = data;
        this.mContext = context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModelBankAccounts DataModelBankAccounts = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_bank_account_balance_2, parent, false);
            viewHolder.line_a = (TextView) convertView.findViewById(R.id.line_a); // account number
            viewHolder.line_b = (TextView) convertView.findViewById(R.id.line_b); // type
            viewHolder.line_c = (TextView) convertView.findViewById(R.id.line_c); // "balance" text
            viewHolder.line_d = (TextView) convertView.findViewById(R.id.line_d); // balance
            viewHolder.line_e = (TextView) convertView.findViewById(R.id.line_e); // currency
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        /*if (DataModelBankAccounts.getBalance() < 0){
            viewHolder.line_d.setTextColor(mContext.getResources().getColor(R.color.holo_red_dark));
            //viewHolder.line_d.setText(mContext.getResources().getString(R.string.open));
        }
        else {
            viewHolder.line_d.setTextColor(mContext.getResources().getColor(R.color.black));
            //viewHolder.line_d.setText(mContext.getResources().getString(R.string.closed));
        }*/

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        lastPosition = position;
        //viewHolder.line_a.setText(DataModelBankAccounts.getCardNumber());
        viewHolder.line_a.setText(DataModelBankAccounts.getNumber());
        viewHolder.line_b.setText(DataModelBankAccounts.getType());
        viewHolder.line_d.setText(new DecimalFormat("#,##0.00").format(DataModelBankAccounts.getBalance()));
        viewHolder.line_e.setText(DataModelBankAccounts.getCurrency());
        // Return the completed view to render on screen
        return convertView;
    }
}
