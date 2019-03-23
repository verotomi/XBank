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

import java.io.File;
import java.util.ArrayList;


public class CustomAdapterStatements extends ArrayAdapter<DataModelStatements>{

    private ArrayList<DataModelStatements> dataset;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView line_a;
        TextView line_b;
        TextView line_c;
        TextView line_d;
        TextView line_e;
        ImageView imageView;
    }

    public CustomAdapterStatements(ArrayList<DataModelStatements> data, Context context) {
        super(context, R.layout.row_item_bank_account_statements, data);
        this.dataset = data;
        this.mContext = context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModelStatements dataModelStatements = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        CustomAdapterStatements.ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_bank_account_statements, parent, false);
            viewHolder.line_a = (TextView) convertView.findViewById(R.id.line_a);
            viewHolder.line_b = (TextView) convertView.findViewById(R.id.line_b);
            viewHolder.line_c = (TextView) convertView.findViewById(R.id.line_c);
            viewHolder.line_d = (TextView) convertView.findViewById(R.id.line_d);
            viewHolder.line_e = (TextView) convertView.findViewById(R.id.line_e);
            viewHolder.imageView = convertView.findViewById(R.id.ImageView1);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_zebra_1));

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        //viewHolder.line_a.setText(dataModelCreditCards.getCardNumber());
        //viewHolder.line_a.setText(String.valueOf(dataModelStatements.getId()));
        //viewHolder.line_b.setText(mContext.getString(R.string.tab) + String.valueOf(dataModelStatements.getNumber()));
        viewHolder.line_b.setText(String.valueOf(dataModelStatements.getNumber()));
        viewHolder.line_e.setText(String.valueOf(dataModelStatements.getFilename())); // nem látható
        //viewHolder.line_c.setText(String.valueOf(dataModelStatements.getId_bank_account()));

        //viewHolder.line_e.setText(dataModelStatements.getFilename()); // nem látható!
        // Return the completed view to render on screen
        viewHolder.line_c.setTag(position);

            File file=new File(mContext.getExternalFilesDir(null),dataModelStatements.getFilename());
            if(file.exists()){
                viewHolder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.pdf128));
                viewHolder.line_d.setTextColor(mContext.getResources().getColor(R.color.darkgreen));
                viewHolder.line_d.setText(mContext.getString(R.string.open_statement));
                //viewHolder.line_c.setText(mContext.getString(R.string.tab) + mContext.getString(R.string.downloaded));
                viewHolder.line_c.setText(mContext.getString(R.string.downloaded));
            }
            else {
                viewHolder.imageView.setBackground(mContext.getResources().getDrawable(R.drawable.download_2));
                viewHolder.line_d.setTextColor(mContext.getResources().getColor(R.color.holo_red_dark));
                viewHolder.line_d.setText(mContext.getString(R.string.download_statement));
                //viewHolder.line_c.setText(mContext.getString(R.string.tab) + mContext.getString(R.string.not_downloaded));
                viewHolder.line_c.setText(mContext.getString(R.string.not_downloaded));
            }
        return convertView;
    }
}