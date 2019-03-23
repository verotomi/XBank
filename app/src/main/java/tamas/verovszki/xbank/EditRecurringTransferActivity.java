package tamas.verovszki.xbank;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditRecurringTransferActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate{

    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    Spinner spinnerSourceAccount;
    Spinner SpinnerFrequency;
    Spinner SpinnerFrequencyDays;
    TextView TextViewSourceAccount;
    TextView TextViewBeneficiaryName;
    TextView TargetAccount;
    TextView TextViewName;
    TextView TextViewAmount;
    TextView TextViewComment;
    TextView TextViewCurrency;
    EditText EditTextBeneficiaryName;
    EditText EditTextTargetAccount;
    EditText EditTextName;
    EditText EditTextAmount;
    EditText EditTextComment;
    EditText EditTextFrequency;
    EditText EditTextDays;
    Button ButtonSubmit;
    Switch SwitchStatus;
    TextView TextViewStatus;
    RelativeLayout RelLay_SpinerFrequencyDays;


    boolean loginState = false;
    Boolean valasz = false;
    String userFirstName = "";
    int userId;

    //MySQL adatbázishoz

    PerformNetworkRequest request;

    //ArrayList<String> list = new ArrayList<>();
    List<String> bankAccountNumberIdList = new ArrayList<String>();
    List<String> bankAccountCurrencyList = new ArrayList<String>();
    String selectedBankAccountNumberId;
    double amountToTransfer = 0;
    int spinnerSelectedItemNo = 0;
    boolean _ignore2 = false; // ezzel kezelem azt, hogy a ugyanaz a bankszámla legyen kiválasztva a spinnerben az átutalás után is
    boolean currencyAddedToAmount = false; // ez lehet hogy nem is kell

    boolean _ignore = false; // indicates if the change was made by the TextWatcher itself.
    private String _before; // Unchanged sequence which is placed before the updated sequence.
    private String _old; /// updated sequence before the update.
    private String _new; // Updated sequence after the update.
    private String _after; // Unchanged sequence which is placed after the updated sequence.

    DataModelRecurringTransfers recurringTransfer;
    //boolean spinnerSourceAccountSelectedByUser = false;
    boolean ignore3 = false; // ezzel küszöbölöm ki, hogy ha programból módosítom a spinner kiválasztást, akkor a listener ne induljon el

    DrawerLayout drawerLayout;
    String userLastLoginTime;
    String userLastName;
    boolean ignore = false;

    DecimalFormat df = new DecimalFormat("#,###.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleEditRecurringTransfers)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_edit_recurring_transfer);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        recurringTransfer = (DataModelRecurringTransfers) bundle.getSerializable(Constants.SHAREDPREFERENCES_RECURRING_TRANSFER); // cast-olni kellett!! (Alt + Enter)

        userFirstName = bundle.getString((Constants.SHAREDPREFERENCES_USER_FIRSTNAME));
        userId = recurringTransfer.getId_user();

        getBundle(); // átadott változók beolvasása
        init();
        navDrawer();

        ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<String>(EditRecurringTransferActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.recurring_transfers_frequency));
        spinnerAdapter1.setDropDownViewResource(R.layout.spinner_item_xbank_small);
        SpinnerFrequency.setAdapter(spinnerAdapter1);

        EditTextName.setText(recurringTransfer.getName());
        EditTextBeneficiaryName.setText(recurringTransfer.getPartner_name());
        EditTextTargetAccount.setText(recurringTransfer.getPartner_account_number());
        EditTextAmount.setText(recurringTransfer.getCurrency().equals("Forint") ? new DecimalFormat("#,##0").format(recurringTransfer.getAmount()) : new DecimalFormat("#,##0.00").format(recurringTransfer.getAmount()));
        EditTextAmount.addTextChangedListener(new NumberTextWatcher(EditTextAmount)); // külső osztálbyan lévő listener, az ezres tagolást csinálja
        //EditTextAmount.setText(new DecimalFormat("#,##0.00").format(recurringTransfer.getAmount()));
        EditTextComment.setText(recurringTransfer.getComment());
        TextViewCurrency.setText(recurringTransfer.getCurrency());
        //String s1 = recurringTransfer.getStatus();
        //boolean s2 = recurringTransfer.getStatus().equals("Aktiv");
        SwitchStatus.setChecked(recurringTransfer.getStatus().equals(Constants.ACTIVE) ? true : false);
        SwitchStatus.setText(recurringTransfer.getStatus().equals(Constants.ACTIVE) ? getString(R.string.active) : getString(R.string.inactive));
        SwitchStatus.setTextColor(recurringTransfer.getStatus().equals(Constants.ACTIVE) ? getResources().getColor(R.color.holo_red_dark) : getResources().getColor(R.color.black));

        /*int k1 = 0;
        String[] sa = getResources().getStringArray(R.array.recurring_transfers_frequency);
        for (k1 = 0; k1 < sa.length; k1++) {
            if (sa[k1].equals(recurringTransfer.getFrequency())){
                SpinnerFrequency.setSelection(k1);
                Toast.makeText(this, "" + recurringTransfer.getFrequency(), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "" + k1, Toast.LENGTH_SHORT).show();
            }
        }

        int k2 = 0;
        String[] sa2 = getResources().getStringArray(R.array.recurring_transfers_days_for_weeks);
        for (k2 = 0; k2 < sa2.length; k2++) {
            if (sa2[k2].equals(recurringTransfer.getDays())){
                SpinnerFrequencyDays.setSelection(k2);
                Toast.makeText(this, "" + k2, Toast.LENGTH_SHORT).show();
            }
        }*/

        //int k1 = 0;
        //int set1=0;
        //String[] sa = getResources().getStringArray(R.array.recurring_transfers_frequency);
        //String  sb = recurringTransfer.getFrequency().toString();
        String[] workingArray = null;

        workingArray = new String[] {" ", Constants.EVERY_DAY, Constants.EVERY_WEEK, Constants.EVERY_MONTH};

        for (int i = 0; i < getResources().getStringArray(R.array.recurring_transfers_frequency).length; i++) {
            if (workingArray[i].equals(recurringTransfer.getFrequency().toString())) {
                SpinnerFrequency.setSelection(i);
                //boolean igaz = sb.equals(sa[i]);
                //Toast.makeText(this, "" + recurringTransfer.getFrequency(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "" + k1, Toast.LENGTH_SHORT).show();
            }
        }

        //SpinnerFrequency.setSelection(set1);

        //int k2 = 0;
        //int set2 = 0;
        //String[] sc = getResources().getStringArray(R.array.recurring_transfers_days_for_weeks);
        //String  sd = recurringTransfer.getDays().toString();

        switch (SpinnerFrequency.getSelectedItemPosition()){
            case 0:
            case 1: // ha "napok" van kiválasztva,
                ArrayAdapter<String> spinnerAdapter5= new ArrayAdapter<String>(EditRecurringTransferActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.recurring_transfers_days_empty));
                spinnerAdapter5.setDropDownViewResource(R.layout.spinner_item_xbank_small);
                SpinnerFrequencyDays.setAdapter(spinnerAdapter5);
                //SpinnerFrequencyDays.setVisibility(View.INVISIBLE);
                RelLay_SpinerFrequencyDays.setVisibility(View.INVISIBLE);
                workingArray = getResources().getStringArray(R.array.recurring_transfers_days_for_weeks);
                break;
            case 2: // ha "hetek" van kiválasztva
                ArrayAdapter<String> spinnerAdapter2= new ArrayAdapter<String>(EditRecurringTransferActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.recurring_transfers_days_for_weeks));
                spinnerAdapter2.setDropDownViewResource(R.layout.spinner_item_xbank_small);
                SpinnerFrequencyDays.setAdapter(spinnerAdapter2);
                //workingArray = getResources().getStringArray(R.array.recurring_transfers_days_for_weeks);
                workingArray = new String[] {" ", Constants.MONDAY, Constants.TUESDAY, Constants.WEDNESDAY, Constants.THURSDAY, Constants.FRIDAY, Constants.SATURDAY, Constants.SUNDAY};
                break;
            case 3: // ha "hónapok" van kiválasztva
                ArrayAdapter<String> spinnerAdapter3= new ArrayAdapter<String>(EditRecurringTransferActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.recurring_transfers_days_for_months));
                spinnerAdapter3.setDropDownViewResource(R.layout.spinner_item_xbank_small);
                SpinnerFrequencyDays.setAdapter(spinnerAdapter3);
                //workingArray = getResources().getStringArray(R.array.recurring_transfers_days_for_months);
                workingArray = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
                break;
        }

        //String s1;
        //String s2;
        SpinnerFrequencyDays.setSelection(0); // ez kell ide azért, hogy az üres  (" " van benne) spinnernek is legyen magassága
        for (int i = 0; i < workingArray.length; i++) {
            //s1 = workingArray[i];
            //s2 = recurringTransfer.getDays();

            if (workingArray[i].equals(recurringTransfer.getDays().toString())) {
                //int set2 = i;
                SpinnerFrequencyDays.setSelection(i);
                ignore3 = true; // ezzel küszöbölöm ki, hogy ha programból módosítom a spinner kiválasztást, akkor a listener ne induljon el
                //Toast.makeText(this, "" + set2, Toast.LENGTH_SHORT).show();
                //boolean igaz = recurringTransfer.getDays().toString().equals(getResources().getStringArray(R.array.recurring_transfers_days_for_weeks)[i]);
                //Toast.makeText(this, "" + recurringTransfer.getFrequency(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "" + k2, Toast.LENGTH_SHORT).show();
            }
        }
        //SpinnerFrequencyDays.setSelection(set2);

        setSupportActionBar(toolbar); // Toolbar megjelenítése
        getAccountBalances(userId);

        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean iserror = false;
                if (!EditTextAmount.getText().toString().equals("")) {
                    //amountToTransfer = Integer.parseInt(EditTextAmount.getText().toString().replace(" ", "").replace(",", "")); // itt a space az nem space, hanem Alt+255 !!!
                    amountToTransfer = Double.parseDouble(EditTextAmount.getText().toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "").replace(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()), ".")); // itt a space az nem space, hanem Alt+255 !!!
                }
                else{
                    amountToTransfer = 0;
                }

                if (EditTextName.getText().toString().equals("")) {
                    Toast.makeText(EditRecurringTransferActivity.this, getString(R.string.recurring_transfer_name_needed), Toast.LENGTH_SHORT).show();
                    iserror = true;
                }
                else if (EditTextBeneficiaryName.getText().toString().equals("")) {
                    Toast.makeText(EditRecurringTransferActivity.this, getString(R.string.beneficiary_name_needed), Toast.LENGTH_SHORT).show();
                    iserror = true;
                }
                else if (!((EditTextTargetAccount.getText().toString().length() == 17) || (EditTextTargetAccount.getText().length() == 26))){
                    Toast.makeText(EditRecurringTransferActivity.this, getString(R.string.rule_account_number), Toast.LENGTH_SHORT).show();
                    iserror = true;
                }
                else if (EditTextAmount.getText().toString().equals("") || amountToTransfer < 1 || amountToTransfer > 999999999) {
                    if (EditTextAmount.getText().toString().equals("")) {
                        Toast.makeText(EditRecurringTransferActivity.this, R.string.amount_needed, Toast.LENGTH_SHORT).show();
                        iserror = true;
                    }
                    else if (amountToTransfer > 999999999) {
                        Toast.makeText(EditRecurringTransferActivity.this, R.string.rule_max_amount, Toast.LENGTH_SHORT).show();
                        iserror = true;
                    }
                    else if (amountToTransfer < 1) {
                        Toast.makeText(EditRecurringTransferActivity.this, R.string.rule_min_amount, Toast.LENGTH_SHORT).show();
                        iserror = true;
                    }
                }
                else if (SpinnerFrequency.getSelectedItemId() == 0) {
                    Toast.makeText(EditRecurringTransferActivity.this, R.string.choose_period, Toast.LENGTH_SHORT).show();
                    iserror = true;
                }
                else if (SpinnerFrequencyDays.getSelectedItemId() == 0 && SpinnerFrequency.getSelectedItemPosition() > 1) {
                    Toast.makeText(EditRecurringTransferActivity.this, R.string.choose_day, Toast.LENGTH_SHORT).show();
                    iserror = true;
                }
                if (!(amountToTransfer == Math.round(amountToTransfer)) && TextViewCurrency.getText().equals(getString(R.string.forint))){
                    Toast.makeText(EditRecurringTransferActivity.this, getString(R.string.no_specie), Toast.LENGTH_SHORT).show();
                    iserror = true;
                }


                if (iserror) {
                } else {
                    updateTransferRecurring(userId);

                    Intent intent = new Intent(EditRecurringTransferActivity.this, DoneAnimation.class);
                    startActivity(intent);

                    finish();
                }
            }
        });

        SwitchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    SwitchStatus.setTextColor(getResources().getColor(R.color.holo_red_dark));
                    SwitchStatus.setText(getString(R.string.active));
                    //recurringTransfer.setStatus("Aktiv");
                }
                else{
                    SwitchStatus.setTextColor(getResources().getColor(R.color.black));
                    SwitchStatus.setText(getString(R.string.inactive));
                    //recurringTransfer.setStatus("Inaktiv");
                }
            }
        });

        SpinnerFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (ignore3) {
                }
                else{
                    switch (i) {
                        case 0:
                            SpinnerFrequencyDays.setSelection(0);
                            SpinnerFrequencyDays.setEnabled(false);
                            RelLay_SpinerFrequencyDays.setVisibility(View.INVISIBLE);
                            break;
                        case 1:
                            SpinnerFrequencyDays.setSelection(0);
                            SpinnerFrequencyDays.setEnabled(false);
                            RelLay_SpinerFrequencyDays.setVisibility(View.INVISIBLE);
                            break;
                        case 2:
                            RelLay_SpinerFrequencyDays.setVisibility(View.VISIBLE);
                            SpinnerFrequencyDays.setEnabled(true);
                            ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<String>(EditRecurringTransferActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.recurring_transfers_days_for_weeks));
                            spinnerAdapter1.setDropDownViewResource(R.layout.spinner_item_xbank_small);
                            SpinnerFrequencyDays.setAdapter(spinnerAdapter1);
                            break;
                        case 3:
                            RelLay_SpinerFrequencyDays.setVisibility(View.VISIBLE);
                            SpinnerFrequencyDays.setEnabled(true);
                            ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<String>(EditRecurringTransferActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.recurring_transfers_days_for_months));
                            spinnerAdapter2.setDropDownViewResource(R.layout.spinner_item_xbank_small);
                            SpinnerFrequencyDays.setAdapter(spinnerAdapter2);
                            break;
                    }
                }
                ignore3 = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        EditTextBeneficiaryName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                /*if(!hasfocus){
                    //if (EditTextBeneficiaryName.getText().toString().trim().equalsIgnoreCase("")){
                    if (EditTextBeneficiaryName.getText().toString().equals("")){
                        Toast.makeText(TransferMoneyOneTimeActivity.this, "Ezt ki kell tölteni!", Toast.LENGTH_SHORT).show();
                        EditTextBeneficiaryName.requestFocus();
                        getCurrentFocus().clearFocus();
                    };
                }
                Toast.makeText(TransferMoneyOneTimeActivity.this, "View" + view.getTag() + " Focus: " + hasfocus, Toast.LENGTH_SHORT).show();*/
            }
        });

        EditTextTargetAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if (!EditTextTargetAccount.getText().toString().equals("")) {
                    if (EditTextTargetAccount.getText().toString().substring(EditTextTargetAccount.length() - 1, EditTextTargetAccount.length()).equals("-")) {
                        EditTextTargetAccount.setText(EditTextTargetAccount.getText().toString().substring(0, EditTextTargetAccount.length() - 1));
                    }
                }


                //Toast.makeText(TransferMoneyOneTimeActivity.this, "View" + view.getTag() + " Focus: " + hasfocus, Toast.LENGTH_SHORT).show();

                    /*Toast.makeText(TransferMoneyOneTimeActivity.this, "Ezt ki kell tölteni!", Toast.LENGTH_SHORT).show();
                    EditTextBeneficiaryName.clearFocus();
                    EditTextAmount.clearFocus();
                    EditTextComment.clearFocus();
                    EditTextTargetAccount.setText("N: " + hasfocus + "T: " + EditTextTargetAccount.hasFocus() + "A: " + EditTextAmount.hasFocus() + "C: " + EditTextComment.hasFocus());
                    EditTextTargetAccount.requestFocus();*/

            }
        });

        EditTextAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if (hasfocus && !EditTextAmount.getText().toString().equals("")) {
                    EditTextAmount.setSelection(EditTextAmount.getText().length());  // kurzor pozicíonálása a legvégére
                }
                if (hasfocus) {
                    TextViewCurrency.setText(bankAccountCurrencyList.get(spinnerSelectedItemNo));
                    TextViewCurrency.setTextColor(getResources().getColor(R.color.black));
                }
                //if (!hasfocus && !EditTextAmount.getText().toString().equals("") && Integer.parseInt(EditTextAmount.getText().toString()) >=1) {

                // float
                // összevontam ezt a 3 sort
                //float f1 = Float.parseFloat(EditTextAmount.getText().toString());
                //String s1 = String.format("%.2f", f1);
                //int i1 = Integer.parseInt(s1);
                //amountToTransfer = Integer.parseInt(String.format("%.2f", Float.parseFloat(EditTextAmount.getText().toString())));

                // int

                // ide kellett a try-catch blokk, csak ezzel tudtam megoldani azt, hogy hol ott van a pénznem az érték mögött, hol nincs. MÉg egy ilyen try-catch blokk van lejjebb is
                //Lényegében megpróbálja kinyerni a mezőből úgy is, hogy ha hozzá van adva a pénznem és ugy is, ha nincs. és vagy az egyik, vagy a másik sikerül.

                  /*
                    try {
                        amountToTransfer = Integer.parseInt(EditTextAmount.getText().toString());
                        EditTextAmount.setText(new DecimalFormat("#,##0.00").format(amountToTransfer) + " " + bankAccountCurrencyList.get(spinnerSourceAccount.getSelectedItemPosition()));
                        currencyAddedToAmount = true;
                    }catch(Exception e){
                        //Toast.makeText(TransferMoneyOneTimeActivity.this, "1. " + e, Toast.LENGTH_SHORT).show();
                    }*/

                /*Toast.makeText(TransferMoneyOneTimeActivity.this, "View" + view.getTag() + " Focus: " + hasfocus, Toast.LENGTH_SHORT).show();
                /*if (!hasfocus){
                    Toast.makeText(TransferMoneyOneTimeActivity.this, "Ezt ki kell tölteni!", Toast.LENGTH_SHORT).show();
                    EditTextTargetAccount.clearFocus();
                    EditTextBeneficiaryName.clearFocus();
                    EditTextComment.clearFocus();
                    EditTextAmount.requestFocus();
                }*/
                /*}
                else if(hasfocus && amountToTransfer > 0) {
                    // EditTextAmount.setText(amountToTransfer + "0"); // ez a nulla azért kell ide, mert a parseint során csak egy darab 0 kerül be az integerbe.
                    EditTextAmount.setText(String.valueOf(amountToTransfer));
                    currencyAddedToAmount = false;
                }*/
            }
        });

        /*EditTextTargetAccount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                _before = charSequence.subSequence(0, start).toString();
                _old = charSequence.subSequence(start, start + count).toString();
                _after = charSequence.subSequence(start + count, charSequence.length()).toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                _new = charSequence.subSequence(start, start + count).toString();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (_ignore)
                    return;
                _ignore = true; // prevent infinite loop
                // Change your text here.
                // myTextView.setText(myNewText);

                //Ezt beállítottam xml-ben
                /*if (editable.toString().length() >26){
                    //EditTextTargetAccount.setText(_before);
                    EditTextTargetAccount.setText(editable.toString().substring(0,26));
                }*/

                //Ez előre hozzáadja a kötőjelet Egyszerre csak az egyik lehet aktív!
                /*if (editable.toString().length() == 8) {
                    if (_before.length() == 7) {
                        EditTextTargetAccount.setText(editable.toString() + "-");
                    } else if (_before.length() > editable.length()) {
                        EditTextTargetAccount.setText(editable.toString().substring(0, 8));
                    }
                }

                // ez utólag adja hozzá. Egyszerre csak az egyik lehet aktív!
                /*if (editable.toString().length() == 9) {
                    if (_before.length() == 8) {
                        //Toast.makeText(TransferMoneyOneTimeActivity.this, "1: " + EditTextTargetAccount.getText().toString().substring(0, 8) + "2:" + EditTextTargetAccount.getText().toString().substring(8, 9), Toast.LENGTH_SHORT).show();
                        EditTextTargetAccount.setText(editable.toString().substring(0, 8) + "-" + editable.toString().substring(8, 9));
                    } else if (_before.length() > editable.length()) {
                        EditTextTargetAccount.setText(editable.toString().substring(0, 8));
                    }
                }*/

                // ez utólag adja hozzá. VEgyszerre csak az egyik lehet aktív!
                /*if (editable.toString().length() == 18) {
                    if (_before.length() == 17) {
                        EditTextTargetAccount.setText(editable.toString() + "-");
                        EditTextTargetAccount.setText(editable.toString().substring(0, 17) + "-" + editable.toString().substring(17, 18));
                    } else if (_before.length() > editable.length()) {
                        EditTextTargetAccount.setText(editable.toString().substring(0, 17));
                    }
                }*/

                //Ez előre hozzáadja a kötőjelet Egyszerre csak az egyik lehet aktív!
                /*if (editable.toString().length() == 17){
                    if (_before.length() == 16){
                        EditTextTargetAccount.setText(editable.toString() + "-");
                    }
                    else if  (_before.length() > editable.length()) {
                        EditTextTargetAccount.setText(editable.toString().substring(0,17));
                    }
                }*/

                /*_ignore = false; // release, so the TextWatcher start to listen again.
                EditTextTargetAccount.setSelection(EditTextTargetAccount.getText().length());  // kurzor pozicíonálása a legvégére
            }
        });
        */

        spinnerSourceAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelectedItemNo = i;
                //spinnerSourceAccountSelectedByUser = true;
                if (!_ignore2) {
                    selectedBankAccountNumberId = bankAccountNumberIdList.get(i);
                    TextViewCurrency.setText(bankAccountCurrencyList.get(i));
                }
                _ignore2 = false;
                //Toast.makeText(TransferMoneyOneTimeActivity.this, selectedBankAccountNumber, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedBankAccountNumberId = bankAccountNumberIdList.get(0);
                //Toast.makeText(TransferMoneyOneTimeActivity.this, "Default:" + selectedBankAccountNumber, Toast.LENGTH_SHORT).show();
            }
        });

        SpinnerFrequency.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });
        SpinnerFrequencyDays.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });
        spinnerSourceAccount.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mobilbankmainactivity_without_name, menu); // elemek hozzáadása a toolbarhoz
        menuitem_logout_3 = menu.findItem(R.id.logout_toolbar_icon_3);
        //menuitem_toolbar_name = menu.findItem(R.id.logout_toolbar_name);
        loginState = Utility.getLoginState(this);

        if (loginState) {
            menuitem_logout_3.setVisible(true);
            menuitem_logout_3.setEnabled(true);
            //menuitem_toolbar_name.setTitle(userFirstName);
            //menuitem_toolbar_name.setVisible(true);
        } else {
            menuitem_logout_3.setEnabled(false);
            menuitem_logout_3.setVisible(false);
            //menuitem_toolbar_name.setTitle("");
            //menuitem_toolbar_name.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!
        int id = item.getItemId();
        if (id == R.id.logout_toolbar_icon_3) {
            CustomDialogClass cdd = new CustomDialogClass(this);
            cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                @Override
                public void onYes(String title) {
                    menuitem_logout_3.setEnabled(false);
                    menuitem_logout_3.setVisible(false);
                    //menuitem_toolbar_name.setTitle("");
                    //menuitem_toolbar_name.setVisible(false);
                    loginState = false;
                    Utility.saveLoginState(EditRecurringTransferActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(EditRecurringTransferActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            EditRecurringTransferActivity.this.finish();
                            //finish();
                        }
                    }, 3000);
                }

                @Override
                public void onNo(String title) {

                }
            });
            cdd.show();
            return true;
        }
        if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * itt be lehetne iktatni, hogy a bankszámla-lekérdezés csak 1x fusson le, az activity elején. Később már nincs rá szükség!
     *
     * @param result
     */
    @Override
    public void taskCompletionResult(String result) {
        String returnedMessage = "";
        try {
            JSONObject object2 = new JSONObject(result);
            returnedMessage = object2.getString(Constants.RESPONSE_MESSAGE);
        } catch (JSONException e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
        switch (returnedMessage) {
            case Constants.RESPONSE_MESSAGE_ACCOUNTLIST_SUCCESSFUL:
                HashMap<String, String> item;    //Used to link data to lines
                JSONArray accountList;
                try {
                    JSONObject object = new JSONObject(result);
                    accountList = object.getJSONArray(Constants.RESPONSE_ACCOUNT_BALANCES);
                    List<String> list = new ArrayList<String>();
                    if (!object.getBoolean(Constants.RESPONSE_ERROR)) {
                        for (int i = 0; i < accountList.length(); i++) {
                            JSONObject obj = accountList.getJSONObject(i);
                            bankAccountNumberIdList.add(obj.getString(Constants.COL_BANK_ACCOUNTS_ID));
                            bankAccountCurrencyList.add(obj.getString(Constants.COL_BANK_ACCOUNTS_CURRENCY));
                            String bankAccountType = "";
                            switch (obj.getString(Constants.COL_BANK_ACCOUNTS_TYPE)){
                                case Constants.RETAIL_BANK_ACCOUNT:
                                    bankAccountType = getString(R.string.retail_bank_account);
                                    break;
                                case Constants.SAVING_ACCOUNT:
                                    bankAccountType = getString(R.string.saving_account);
                                    break;
                                case Constants.FOREIGN_CURRENCY_ACCOUNT:
                                    bankAccountType = getString(R.string.foreign_currency_account);
                                    break;
                                default:
                                    bankAccountType = "";
                                    break;
                            }
                            list.add(
                                    bankAccountType + getString(R.string.tab) +
                                            new DecimalFormat("#,##0.00").format(obj.getInt(Constants.COL_BANK_ACCOUNTS_BALANCE)) + " " +
                                            obj.getString(Constants.COL_BANK_ACCOUNTS_CURRENCY) + "\n" +
                                            obj.getString(Constants.COL_BANK_ACCOUNTS_NUMBER)
                            );
                        }
                        addItemsOnSpinnerSourceAccount(list);
                        //int k3 = 0;
                        //if (!spinnerSourceAccountSelectedByUser) {
                            for (int i = 0; i < bankAccountNumberIdList.size(); i++) {
                                if (bankAccountNumberIdList.get(i).equals(String.valueOf(recurringTransfer.getId_bank_account_number()))){
                                    //Toast.makeText(this, "" + k3, Toast.LENGTH_SHORT).show();
                                    spinnerSelectedItemNo = i;
                                }
                            }
                        //}
                        spinnerSourceAccount.setSelection(spinnerSelectedItemNo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.RESPONSE_MESSAGE_ACCOUNTLIST_UNSUCCESSFUL: {
                Toast.makeText(this, R.string.no_bank_account, Toast.LENGTH_SHORT).show();
                ButtonSubmit.setEnabled(false);
                break;
            }

            case Constants.RESPONSE_MESSAGE_RECURRING_TRANSFER_UPDATE_SUCCESSFUL: {
                Toast.makeText(this, getString(R.string.modification_ok), Toast.LENGTH_SHORT).show();
                //getAccountBalances(userId);
                resetFields();
                break;
            }
            case Constants.RESPONSE_MESSAGE_RECURRING_TRANSFER_UPDATE_UNSUCCESSFUL: {
                Toast.makeText(this, getString(R.string.modification_not_ok), Toast.LENGTH_SHORT).show();
                //getAccountBalances(userId);
                resetFields();
                break;
            }
        }
    }

    // add items into spinner dynamically
    public void addItemsOnSpinnerSourceAccount(List<String> listOfBankAccounts) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_transfer_money, listOfBankAccounts);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item_xbank);
        spinnerSourceAccount.setAdapter(dataAdapter);
    }

    private void updateTransferRecurring(int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_RECURRING_TRANSFERS_ID_BANK_ACCOUNT_NUMBER, selectedBankAccountNumberId);
            params.put(Constants.COL_RECURRING_TRANSFERS_NAME, EditTextName.getText().toString());
            params.put(Constants.COL_RECURRING_TRANSFERS_CURRENCY, TextViewCurrency.getText().toString());
            params.put(Constants.COL_RECURRING_TRANSFERS_AMOUNT, String.valueOf(amountToTransfer));
            params.put(Constants.COL_RECURRING_TRANSFERS_PARTNER_NAME, EditTextBeneficiaryName.getText().toString());
            params.put(Constants.COL_RECURRING_TRANSFERS_PARTNER_ACCOUNT_NUMBER, EditTextTargetAccount.getText().toString());
            params.put(Constants.COL_RECURRING_TRANSFERS_COMMENT, EditTextComment.getText().toString());
            params.put(Constants.COL_RECURRING_TRANSFERS_STATUS, SwitchStatus.getText().toString().equals(getString(R.string.active)) ? Constants.ACTIVE : Constants.INACTIVE);
            params.put(Constants.COL_RECURRING_TRANSFERS_ID, String.valueOf(recurringTransfer.getId()));
            //params.put(Constants.COL_RECURRING_TRANSFERS_DAYS, SpinnerFrequencyDays.getSelectedItem().toString());
            switch (SpinnerFrequencyDays.getSelectedItemPosition()){
                case 1:
                    params.put(Constants.COL_RECURRING_TRANSFERS_DAYS, SpinnerFrequency.getSelectedItemPosition() == 2 ? Constants.MONDAY : String.valueOf(SpinnerFrequencyDays.getSelectedItemPosition()));
                    break;
                case 2:
                    params.put(Constants.COL_RECURRING_TRANSFERS_DAYS, SpinnerFrequency.getSelectedItemPosition() == 2 ? Constants.TUESDAY : String.valueOf(SpinnerFrequencyDays.getSelectedItemPosition()));
                    break;
                case 3:
                    params.put(Constants.COL_RECURRING_TRANSFERS_DAYS, SpinnerFrequency.getSelectedItemPosition() == 2 ? Constants.WEDNESDAY : String.valueOf(SpinnerFrequencyDays.getSelectedItemPosition()));
                    break;
                case 4:
                    params.put(Constants.COL_RECURRING_TRANSFERS_DAYS, SpinnerFrequency.getSelectedItemPosition() == 2 ? Constants.THURSDAY : String.valueOf(SpinnerFrequencyDays.getSelectedItemPosition()));
                    break;
                case 5:
                    params.put(Constants.COL_RECURRING_TRANSFERS_DAYS, SpinnerFrequency.getSelectedItemPosition() == 2 ? Constants.FRIDAY : String.valueOf(SpinnerFrequencyDays.getSelectedItemPosition()));
                    break;
                case 6:
                    params.put(Constants.COL_RECURRING_TRANSFERS_DAYS, SpinnerFrequency.getSelectedItemPosition() == 2 ? Constants.SATURDAY : String.valueOf(SpinnerFrequencyDays.getSelectedItemPosition()));
                    break;
                case 7:
                    params.put(Constants.COL_RECURRING_TRANSFERS_DAYS, SpinnerFrequency.getSelectedItemPosition() == 2 ? Constants.SUNDAY : String.valueOf(SpinnerFrequencyDays.getSelectedItemPosition()));
                    break;
                default:
                    params.put(Constants.COL_RECURRING_TRANSFERS_DAYS, String.valueOf(SpinnerFrequencyDays.getSelectedItemPosition()));
                    break;
            }
            //params.put(Constants.COL_RECURRING_TRANSFERS_FREQUENCY, SpinnerFrequency.getSelectedItem().toString());
            switch (SpinnerFrequency.getSelectedItemPosition()){
                case 1:
                    params.put(Constants.COL_RECURRING_TRANSFERS_FREQUENCY, Constants.EVERY_DAY);
                    params.put(Constants.COL_RECURRING_TRANSFERS_DAYS, "");
                    break;
                case 2:
                    params.put(Constants.COL_RECURRING_TRANSFERS_FREQUENCY, Constants.EVERY_WEEK);
                    break;
                case 3:
                    params.put(Constants.COL_RECURRING_TRANSFERS_FREQUENCY, Constants.EVERY_MONTH);
                    break;
            }



            recurringTransfer.setId_bank_account_number(Integer.parseInt(selectedBankAccountNumberId));
            recurringTransfer.setName(EditTextName.getText().toString());
            recurringTransfer.setAmount(amountToTransfer);
            recurringTransfer.setPartner_name(EditTextBeneficiaryName.getText().toString());
            recurringTransfer.setPartner_account_number(EditTextTargetAccount.getText().toString());
            recurringTransfer.setComment(EditTextComment.getText().toString());
            recurringTransfer.setStatus(SwitchStatus.getText().toString().equals(getString(R.string.active)) ? Constants.ACTIVE : Constants.INACTIVE);
            recurringTransfer.setFrequency(SpinnerFrequency.getSelectedItem().toString());
            recurringTransfer.setDays(SpinnerFrequencyDays.getSelectedItem().toString());



            request = new PerformNetworkRequest(Constants.URL_UPDATE_RECURRING_TRANSFER, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            //SwipeRefreshLayout.setRefreshing(false);
        }



    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        spinnerSourceAccount = findViewById(R.id.SpinnerSourceAccount);
        SpinnerFrequency = findViewById(R.id.SpinnerFrequency);
        SpinnerFrequencyDays = findViewById(R.id.SpinnerFrequencyDays);

        TextViewSourceAccount = findViewById(R.id.TextViewSourceAccount);
        TextViewBeneficiaryName = findViewById(R.id.TextViewBeneficiaryName);
        TargetAccount = findViewById(R.id.TargetAccount);
        TextViewName = findViewById(R.id.TextViewName);
        TextViewAmount = findViewById(R.id.TextViewAmount);
        TextViewComment = findViewById(R.id.TextViewComment);
        TextViewCurrency = findViewById(R.id.TextViewCurrency);
        drawerLayout = findViewById(R.id.drawer_layout);


        EditTextBeneficiaryName = findViewById(R.id.EditTextBeneficiaryName);
        EditTextTargetAccount = findViewById(R.id.EditTextTargetAccount);
        EditTextName = findViewById(R.id.EditTextName);
        EditTextAmount = findViewById(R.id.EditTextAmount);
        // EditTextAmount.addTextChangedListener(new NumberTextWatcher(EditTextAmount)); // külső osztálbyan lévő listener, az ezres tagolást csinálja // át kellett raknom az összeg beolvasása utáni helyre
        EditTextComment = findViewById(R.id.EditTextComment);
        ButtonSubmit = findViewById(R.id.ButtonSubmit);
        SwitchStatus = findViewById(R.id.SwitchStatus);
        RelLay_SpinerFrequencyDays = findViewById(R.id.RelLay_SpinerFrequencyDays);


        EditTextBeneficiaryName.setTag(1);
        EditTextTargetAccount.setTag(2);
        EditTextAmount.setTag(3);
        EditTextComment.setTag(4);

    }

    /**
     * Aszinkron művelet!
     *
     * @param user_id
     */
    private void getAccountBalances(int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_BANK_ACCOUNTS_ID_USER, String.valueOf(user_id));
            request = new PerformNetworkRequest(Constants.URL_READ_ACCOUNT_BALANCES, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            //SwipeRefreshLayout.setRefreshing(false);
        }


    }

    public void getBundle(){
        try{
        /*Intent i = getIntent();
        userFirstName = i.getStringExtra("user_firstname");
        userId = Integer.parseInt(i.getStringExtra("user_id"));*/

            Bundle b = getIntent().getExtras();
            userId = Integer.parseInt(b.getString(Constants.SHAREDPREFERENCES_USER_ID));
            userFirstName = b.getString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME);
            userLastName = b.getString(Constants.SHAREDPREFERENCES_USER_LASTNAME);
            userLastLoginTime = b.getString(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME);
        }
        catch (Exception e){ // ez a blokk azért került ide, mert nem akart működni a bundle-s paraméter átadás. Már működik, így ez a blokk csak biztonsági szempontból maradt itt.
            SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
            userId = Integer.parseInt(sp.getString(Constants.SHAREDPREFERENCES_USER_ID, ""));
            userFirstName = sp.getString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, "");
        }
    }

    public void navDrawer(){
        final NavigationView navigationView = findViewById(R.id.nav_view);
        //View hView =  navigationView.getHeaderView(0);
        View hView =  findViewById(R.id.Navigation_Header_Layout);
        TextView nav_name = (TextView)hView.findViewById(R.id.TextViewName);
        TextView nav_lastlogin = (TextView)hView.findViewById(R.id.TextViewLastLogin);
        nav_name.setText(userLastName + " " + userFirstName);
        nav_lastlogin.setText(userLastLoginTime);

        Menu navMenu = navigationView.getMenu();
        MenuItem navMenuAccountHistory = navMenu.findItem(R.id.Recurring_transfers);
        navMenuAccountHistory.setEnabled(false);

        ColorStateList cs1 = Utility.navigationDrawerColors();
        navigationView.setItemTextColor(cs1);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        //menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        Intent intent;
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.Overviewe:
                                intent = new Intent(EditRecurringTransferActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(EditRecurringTransferActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(EditRecurringTransferActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(EditRecurringTransferActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(EditRecurringTransferActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(EditRecurringTransferActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(EditRecurringTransferActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(EditRecurringTransferActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(EditRecurringTransferActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(EditRecurringTransferActivity.this, PinCodeChangeActivity.class);
                                break;
                            case R.id.Logout:
                                //intent=null;

                                // az előzőleg kiválasztott elem úgy maradt, ezért törölni kell a kijelölést
                                // a -2 oka: navmenu utolsó eleme egy ures sor, az azelőtti meg a kilépés. Ezeknek nem kell törölni a kijelölését.
                                int size = navigationView.getMenu().size();
                                //for (int i = 0; i < size-2; i++) {
                                for (int i = 0; i < size; i++) {
                                    navigationView.getMenu().getItem(i).setChecked(false);
                                }

                                intent = new Intent(EditRecurringTransferActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(EditRecurringTransferActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(EditRecurringTransferActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(EditRecurringTransferActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                EditRecurringTransferActivity.this.finish();
                                                //finish();
                                            }
                                        }, 3000);
                                    }

                                    @Override
                                    public void onNo(String title) {

                                    }
                                });
                                cdd.show();
                                break;

                            default: // van egy üres sor a navigation drawer legalján, amiatt, hogy oda is kerüljön elválasztóvonal. Ennek az üres sornak a lekezelése miatt kell ide az ignore, hogy ha arra kattintunk, ne történjen semmi
                                ignore = true;
                                intent = new Intent(EditRecurringTransferActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                break;
                        }
                        if (!ignore){
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
                            finish();
                        }
                        ignore = false;
                        return true;
                    }
                });
        setSupportActionBar(toolbar); // Toolbar megjelenítése
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    public void resetFields() {
        currencyAddedToAmount = false; // ez fontos, hogy az edittext-törlések előtt legyen!
        EditTextTargetAccount.setText("");
        EditTextComment.setText("");
        EditTextAmount.setText("");
        EditTextBeneficiaryName.setText("");
        amountToTransfer = 0;
        _ignore2 = true;
        spinnerSourceAccount.setSelection(spinnerSelectedItemNo);
        TextViewCurrency.setText("");

    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

}


