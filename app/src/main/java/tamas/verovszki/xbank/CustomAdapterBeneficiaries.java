package tamas.verovszki.xbank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

//public class CustomAdapterCreditCards extends ArrayAdapter<DataModelCreditCards> implements View.OnClickListener { // kiszedtem a settings ikon klikkelését, de itt hagytam a kódot, hátha fog kelleni. Még 2 másik helyen is van kapcsolódó módosítás
public class CustomAdapterBeneficiaries extends ArrayAdapter<DataModelBeneficiaries>{

    LinearLayout controls = (LinearLayout) ((BeneficiariesActivity) getContext()).findViewById(R.id.LinLayControls);
    SeekBar seekBarAtm = (SeekBar)((BeneficiariesActivity) getContext()).findViewById(R.id.SeekBarAtm);
    SeekBar seekBarPos = (SeekBar)((BeneficiariesActivity) getContext()).findViewById(R.id.SeekBarPos);
    SeekBar seekBarOnline = (SeekBar)((BeneficiariesActivity) getContext()).findViewById(R.id.SeekBarOnline);
    Switch switchStatus = ((BeneficiariesActivity) getContext()).findViewById(R.id.SwitchStatus);
    TextView textViewAtmLimit = ((BeneficiariesActivity) getContext()).findViewById(R.id.TextViewAtmLimit);
    TextView textViewPosLimit = ((BeneficiariesActivity) getContext()).findViewById(R.id.TextViewPosLimit);
    TextView textViewOnlineLimit = ((BeneficiariesActivity) getContext()).findViewById(R.id.TextViewOnlineLimit);
    TextView textViewStatus1 = ((BeneficiariesActivity) getContext()).findViewById(R.id.TextViewStatus1);
    TextView textViewStatus2 = ((BeneficiariesActivity) getContext()).findViewById(R.id.TextViewStatus2);
    TextView textViewCardNumber = ((BeneficiariesActivity) getContext()).findViewById(R.id.TextViewCardNumber);



    private ArrayList<DataModelBeneficiaries> dataset;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView line_a;
        TextView line_b;
        TextView line_c;
        TextView line_d;
        ImageView ImageViewSettings;
    }

    public CustomAdapterBeneficiaries(ArrayList<DataModelBeneficiaries> data, Context context) {
        super(context, R.layout.row_item_beneficiaries, data);
        this.dataset = data;
        this.mContext = context;
    }

    //
    // kiszedtem a settings ikon klikkelését, de itt hagytam a kódot, hátha fog kelleni. Még 2 másik helyen is van kapcsolódó módosítás
    /*
    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        DataModelBeneficiaries dataModelBeneficiaries =(DataModelBeneficiaries) object;

        switch (v.getId())
        {
            case R.id.ImageViewSettings:

                //Toast.makeText(mContext, "" + dataModelBeneficiaries.getStatus(), Toast.LENGTH_SHORT).show();
                controls.setVisibility(controls.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                seekBarAtm.setProgress((dataModelBeneficiaries.getLimitAtm() - 10000 )/ 10000);
                seekBarPos.setProgress(dataModelBeneficiaries.getLimitPos() - 10000 / 10000);
                seekBarOnline.setProgress(dataModelBeneficiaries.getLimitOnline() - 10000 / 10000);

                switchStatus.setChecked(dataModelBeneficiaries.getStatus().equals("aktív") ? true : false);
                //textViewAtmLimit.setText(dataModelBeneficiaries.getLimitAtm());
                //textViewPosLimit.setText(dataModelBeneficiaries.getLimitPos());
                //textViewOnlineLimit.setText(dataModelBeneficiaries.getLimitOnline());
                textViewStatus1.setText("A kártya állapota:" + " ");
                textViewStatus2.setText(dataModelBeneficiaries.getStatus());
                textViewStatus2.setTextColor(dataModelBeneficiaries.getStatus().equals("aktív") ? mContext.getResources().getColor(R.color.holo_red_dark) : mContext.getResources().getColor(R.color.midgrey2));
                textViewCardNumber.setText(
                        dataModelBeneficiaries.getCardNumber().substring(0,4) + "-" +
                                dataModelBeneficiaries.getCardNumber().toString().substring(4,8) + "-" +
                                dataModelBeneficiaries.getCardNumber().toString().substring(8,12) + "-" +
                                dataModelBeneficiaries.getCardNumber().toString().substring(12,16) + " " +
                                dataModelBeneficiaries.getCardType() + " " + "kártya");

                break;
        }
    }*/

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModelBeneficiaries dataModelBeneficiaries = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item_beneficiaries, parent, false);
            viewHolder.line_a = (TextView) convertView.findViewById(R.id.line_a);
            viewHolder.line_b = (TextView) convertView.findViewById(R.id.line_b);
            viewHolder.line_c = (TextView) convertView.findViewById(R.id.line_c);
            //viewHolder.line_d = (TextView) convertView.findViewById(R.id.line_d);
            //viewHolder.ImageViewSettings = (ImageView) convertView.findViewById(R.id.ImageViewSettings);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        //if (dataModelBeneficiaries.getStatus().equals(mContext.getResources().getString(R.string.active))){
        /*if (dataModelBeneficiaries.getStatus().equals("Active")){
            viewHolder.line_d.setTextColor(mContext.getResources().getColor(R.color.holo_red_dark));
            //viewHolder.line_d.setText(mContext.getResources().getString(R.string.open));
        }
        else {
            viewHolder.line_d.setTextColor(mContext.getResources().getColor(R.color.midgrey2));
            //viewHolder.line_d.setText(mContext.getResources().getString(R.string.closed));
        }*/


        convertView.setBackgroundColor(mContext.getResources().getColor(R.color.grey_zebra_1));


        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        //viewHolder.line_a.setText(dataModelBeneficiaries.getCardNumber());
        viewHolder.line_a.setText(dataModelBeneficiaries.getName());
        viewHolder.line_b.setText(dataModelBeneficiaries.getPartner_name());
        viewHolder.line_c.setText(dataModelBeneficiaries.getPartner_account_number());
        //viewHolder.line_d.setText(dataModelBeneficiaries.getStatus().equals("Active") ? mContext.getString(R.string.active) : mContext.getString(R.string.inactive));
        //viewHolder.ImageViewSettings.setOnClickListener(this); // kiszedtem a settings ikon klikkelését, de itt hagytam a kódot, hátha fog kelleni. Még 2 másik helyen is van kapcsolódó módosítás
        //viewHolder.ImageViewSettings.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
