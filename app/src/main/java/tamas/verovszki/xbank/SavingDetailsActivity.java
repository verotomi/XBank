package tamas.verovszki.xbank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Ez az Activity DialogBox-ként jelenik meg
 */
public class SavingDetailsActivity extends Activity { // át kellett írni AppCompatActivity-ról Activityre

    TextView TextViewBankAccountNumber2;
    TextView TextViewType2;
    TextView TextViewAmount2;
    TextView TextViewExpireDate2;
    TextView TextViewStatus2;
    TextView TextViewCurrency2;
    TextView TextViewReferenceNumber2;
    TextView TextViewExpectedAmount2;
    TextView TextViewArrivedOn2;

    DataModelSavings saving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // fejléc kiiktatása
        setContentView(R.layout.activity_saving_details);

        getWindow().setBackgroundDrawableResource(R.color.transparent);
        init();
        getBundle();

        TextViewBankAccountNumber2.setText(String.valueOf(saving.getBank_account_number()));
        TextViewType2.setText(saving.getType());
        TextViewAmount2.setText(new DecimalFormat("#,##0.00").format(saving.getAmount()));
        TextViewExpireDate2.setText(saving.getExpire_date());
        TextViewStatus2.setText(saving.getStatus());
        TextViewCurrency2.setText(saving.getCurrency());
        TextViewReferenceNumber2.setText(String.valueOf(saving.getReference_number()));
        TextViewExpectedAmount2.setText(saving.getExpectedAmount());
        TextViewArrivedOn2.setText(saving.getArrived_on().substring(0,11));

    }

    public void init(){
        TextViewBankAccountNumber2 = findViewById(R.id.TextViewBankAccountNumber2);
        TextViewType2 = findViewById(R.id.TextViewType2);
        TextViewAmount2 = findViewById(R.id.TextViewAmount2);
        TextViewExpireDate2 = findViewById(R.id.TextViewExpireDate2);
        TextViewStatus2 = findViewById(R.id.TextViewStatus2);
        TextViewCurrency2 = findViewById(R.id.TextViewCurrency2);
        TextViewReferenceNumber2 = findViewById(R.id.TextViewReferenceNumber2);
        TextViewExpectedAmount2 = findViewById(R.id.TextViewExpectedAmount2);
        TextViewArrivedOn2 = findViewById(R.id.TextViewArrivedOn2);

    }

    public void getBundle(){
        try{
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            saving = (DataModelSavings) bundle.getSerializable(Constants.SHAREDPREFERENCES_SAVING); // cast-olni kellett!! (Alt + Enter)
        }
        catch (Exception e){ // ez a blokk azért került ide, mert nem akart működni a bundle-s paraméter átadás. Már működik, így ez a blokk csak biztonsági szempontból maradt itt.
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }
}
