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
public class BankAccountDetailsActivity extends Activity { // át kellett írni AppCompatActivity-ról Activityre

    TextView TextViewOwner2;
    TextView TextViewNumber2;
    TextView TextViewType2;
    TextView TextViewBalance2;
    TextView TextViewStatus2;
    TextView TextViewOpened2;
    TextView TextViewCurrency2;


    DataModelBankAccounts bankAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // fejléc kiiktatása
        setContentView(R.layout.activity_bank_account_details);

        getWindow().setBackgroundDrawableResource(R.color.transparent);
        init();
        getBundle();
        TextViewOwner2.setText(bankAccount.getUserFirstname() + " " + bankAccount.getUserLastname());
        TextViewNumber2.setText(bankAccount.getNumber());
        TextViewType2.setText(bankAccount.getType());
        TextViewBalance2.setText(new DecimalFormat("#,##0.00").format(bankAccount.getBalance()));
        TextViewStatus2.setText(bankAccount.getStatus());
        TextViewOpened2.setText(bankAccount.getCreated_on().substring(0,10));
        TextViewCurrency2.setText(bankAccount.getCurrency());


    }

    public void init(){
        TextViewOwner2 = findViewById(R.id.TextViewOwner2);
        TextViewNumber2 = findViewById(R.id.TextViewNumber2);
        TextViewType2 = findViewById(R.id.TextViewType2);
        TextViewBalance2 = findViewById(R.id.TextViewBalance2);
        TextViewStatus2 = findViewById(R.id.TextViewStatus2);
        TextViewOpened2 = findViewById(R.id.TextViewOpened2);
        TextViewCurrency2 = findViewById(R.id.TextViewCurrency2);
    }

    public void getBundle(){
        try{
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            bankAccount = (DataModelBankAccounts) bundle.getSerializable(Constants.SHAREDPREFERENCES_BANK_ACCOUNT); // cast-olni kellett!! (Alt + Enter)
        }
        catch (Exception e){ // ez a blokk azért került ide, mert nem akart működni a bundle-s paraméter átadás. Már működik, így ez a blokk csak biztonsági szempontból maradt itt.
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }
}
