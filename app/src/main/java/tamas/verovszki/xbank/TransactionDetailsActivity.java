package tamas.verovszki.xbank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

/**
 * Ez az Activity AlertBox-ként jelenik meg
 */
public class TransactionDetailsActivity extends Activity { // át kellett írni AppCompatActivity-ról Activityre

    TextView TextViewBankAccountNumber2;
    TextView TextViewType2;
    TextView TextViewDirection2;
    TextView TextViewAmount2;
    TextView TextViewCurrency2;
    TextView TextViewReferenceNumber2;
    TextView TextViewPartnerName2;
    TextView TextViewArrivedOn2;
    TextView TextViewPartnerAccountNumber2;
    TextView TextViewComment2;

    DataModelTransactions transaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // fejléc kiiktatása
        setContentView(R.layout.activity_transaction_details);

        getBundle();
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        init();

        TextViewBankAccountNumber2.setText(String.valueOf(transaction.getBank_account_number()));
        TextViewType2.setText(transaction.getType());
        TextViewDirection2.setText(transaction.getDirection().equals(Constants.IN) ? getString(R.string.deposit) : getString(R.string.withdrawal));
        TextViewReferenceNumber2.setText(String.valueOf(transaction.getReference_number()));
        TextViewAmount2.setText(new DecimalFormat("#,##0.00").format(transaction.getAmount()));
        TextViewPartnerName2.setText(transaction.getPartner_name().equals("") ? "-" : transaction.getPartner_name());
        TextViewPartnerAccountNumber2.setText(transaction.getPartner_account_number().equals("") ? "-" : transaction.getPartner_account_number());
        TextViewCurrency2.setText(transaction.getCurrency());
        TextViewComment2.setText(transaction.getComment());
        TextViewArrivedOn2.setText(transaction.getArrived_on());

    }

    public void init(){
        TextViewBankAccountNumber2 = findViewById(R.id.TextViewBankAccountNumber2);
        TextViewType2 = findViewById(R.id.TextViewType2);
        TextViewDirection2 = findViewById(R.id.TextViewDirection2);
        TextViewReferenceNumber2 = findViewById(R.id.TextViewReferenceNumber2);
        TextViewAmount2 = findViewById(R.id.TextViewAmount2);
        TextViewPartnerName2 = findViewById(R.id.TextViewPartnerName2);
        TextViewPartnerAccountNumber2 = findViewById(R.id.TextViewPartnerAccountNumber2);
        TextViewCurrency2 = findViewById(R.id.TextViewCurrency2);
        TextViewComment2 = findViewById(R.id.TextViewComment2);
        TextViewArrivedOn2 = findViewById(R.id.TextViewArrivedOn2);
    }

    public void getBundle(){
        try{
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            transaction = (DataModelTransactions) bundle.getSerializable(Constants.SHAREDPREFERENCES_TRANSACTION); // cast-olni kellett!! (Alt + Enter)
        }
        catch (Exception e){ // ez a blokk azért került ide, mert nem akart működni a bundle-s paraméter átadás. Már működik, így ez a blokk csak biztonsági szempontból maradt itt.
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }
}