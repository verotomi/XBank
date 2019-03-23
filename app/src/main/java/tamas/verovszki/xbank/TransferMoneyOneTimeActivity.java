package tamas.verovszki.xbank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.DrawerLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransferMoneyOneTimeActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate {

    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    Spinner spinnerSourceAccount;
    Spinner spinnerBeneficiaries;
    TextView TextViewSourceAccount;
    TextView TextViewBeneficiaryName;
    TextView TargetAccount;
    TextView TextViewAmount;
    TextView TextViewComment;
    TextView TextViewCurrency;
    EditText EditTextBeneficiaryName;
    EditText EditTextTargetAccount;
    EditText EditTextAmount;
    EditText EditTextComment;
    Button ButtonSubmit;
    ImageView imageViewDownArrow;
    TextView TextViewSablon;
    CheckBox CheckBox1;
    LinearLayout LinLay0;


    boolean loginState = false;
    Boolean valasz = false;
    String userFirstName="";
    int userId;
    boolean vanAtadvaErtek = false;
    private SwipeRefreshLayout SwipeRefreshLayout;

    //MySQL adatbázishoz
    PerformNetworkRequest request;

    //ArrayList<String> list = new ArrayList<>();
    List<String> bankAccountNumberIdList = new ArrayList<String>();
    List<String> bankAccountCurrencyList = new ArrayList<String>();
    String selectedBankAccountNumberId;
    double amountToTransfer=0;
    int spinnerSelectedItemNo = 0;
    boolean _ignore2 = false; // ezzel kezelem azt, hogy a ugyanaz a bankszámla legyen kiválasztva a spinnerben az átutalás után is
    boolean currencyAddedToAmount = false; // ez lehet hogy nem is kell

    DrawerLayout drawerLayout;
    String userLastLoginTime;
    String userLastName;
    boolean ignore = false;

    boolean _ignore = false; // indicates if the change was made by the TextWatcher itself.
    private String _before; // Unchanged sequence which is placed before the updated sequence.
    private String _old; /// updated sequence before the update.
    private String _new; // Updated sequence after the update.
    private String _after; // Unchanged sequence which is placed after the updated sequence.

    ArrayList<DataModelBeneficiaries> dataModels;

    boolean notEnoughmoney = false;
    DecimalFormat df = new DecimalFormat("#,###.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleTransferMoneyOneTime)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_transfer_money_one_time);

        //Utility.hideStatusBar(this); // Statusbar elrejtése

        getBundle();

        init();

        if(Utility.IsNetworkAvailable(this)){
            LinLay0.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(this, "There is no internet connection!", Toast.LENGTH_SHORT).show();
        }

        navDrawer();
        dataModels = new ArrayList<>();

        getAccountBalances(userId);
        getBeneficiaries(userId);


        setSupportActionBar(toolbar); // Toolbar megjelenítése

        /**
         * Képernyő lehúzás hatására frissítés
         */
        SwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.darkgreen3, R.color.darkgreen);
        SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SwipeRefreshLayout.setRefreshing(true);
                Utility.hideKeyboard(TransferMoneyOneTimeActivity.this);
                Utility.hideKeyboard(TransferMoneyOneTimeActivity.this);
                resetFields();
                //recreate();
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);
                //getCreditCards(userId); // elinditom a kártyaadatok bekérését, és amikor megérkezett, akkor építem fel a listview-et
            }
        });


        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean iserror= false;

                if (EditTextBeneficiaryName.getText().toString().equals("")){
                    Toast.makeText(TransferMoneyOneTimeActivity.this, getString(R.string.beneficiary_name_needed), Toast.LENGTH_SHORT).show();
                    iserror = true;
                }
                else if (!((EditTextTargetAccount.getText().toString().length() == 17) || (EditTextTargetAccount.getText().length() == 26))){
                //if (true){
                    Toast.makeText(TransferMoneyOneTimeActivity.this, getString(R.string.rule_account_number), Toast.LENGTH_SHORT).show();
                    iserror = true;
                }
                else if (!EditTextAmount.getText().toString().equals("")){
                    //if (Integer.parseInt(EditTextAmount.getText().toString()) < 1){

                    //char c1 = df.getDecimalFormatSymbols().getGroupingSeparator();
                    //String s1 = String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator());
                    //char c2 = df.getDecimalFormatSymbols().getDecimalSeparator();
                    //String s2 = String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator());
                    //String d1 = EditTextAmount.getText().toString();
                    //String d2 = d1;
                    //String s3 = EditTextAmount.getText().toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "").replace(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()), ".");

                    amountToTransfer = Double.parseDouble(EditTextAmount.getText().toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "").replace(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()), ".")); // itt a space az nem space, hanem Alt+255 !!!
                    //Toast.makeText(TransferMoneyOneTimeActivity.this, "" + amountToTransfer, Toast.LENGTH_SHORT).show();
                    if (amountToTransfer < 1){
                        Toast.makeText(TransferMoneyOneTimeActivity.this, getString(R.string.rule_min_amount), Toast.LENGTH_SHORT).show();
                        iserror = true;
                    }
                    if (amountToTransfer > 999999999){
                        Toast.makeText(TransferMoneyOneTimeActivity.this, getString(R.string.rule_max_amount), Toast.LENGTH_SHORT).show();
                        iserror = true;
                    }
                    if (!(amountToTransfer == Math.round(amountToTransfer)) && TextViewCurrency.getText().equals("Forint")){
                        Toast.makeText(TransferMoneyOneTimeActivity.this, getString(R.string.no_specie), Toast.LENGTH_SHORT).show();
                        iserror = true;
                    }
                }else{
                    Toast.makeText(TransferMoneyOneTimeActivity.this, getString(R.string.amount_needed), Toast.LENGTH_SHORT).show();
                    iserror = true;
                }

                if (iserror){
                }
                else {
                    sendTransferOneTime(userId);

                    //Intent intent = new Intent(TransferMoneyOneTimeActivity.this, DoneAnimation.class);
                    //startActivity(intent);

                    if (CheckBox1.isChecked()){
                        insertNewBeneficiary(userId);
                    }
                    //finish();
                }
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
                if (!EditTextTargetAccount.getText().toString().equals("")){
                    if (EditTextTargetAccount.getText().toString().substring(EditTextTargetAccount.length()-1, EditTextTargetAccount.length()).equals("-")){
                        EditTextTargetAccount.setText(EditTextTargetAccount.getText().toString().substring(0, EditTextTargetAccount.length()-1 ));
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


                if (hasfocus && !EditTextAmount.getText().toString().equals("")){
                    EditTextAmount.setSelection(EditTextAmount.getText().length());  // kurzor pozicíonálása a legvégére
                }
                if (hasfocus){
                    try { // itt leállt a program, ha volt kiválasztott partner és refresh-eltem. ezzel a try-al tudtam megoldani. Valsezg még üres a lista és az a baj.
                        TextViewCurrency.setText(bankAccountCurrencyList.get(spinnerSelectedItemNo));
                        TextViewCurrency.setTextColor(getResources().getColor(R.color.black));
                    }
                    catch (Exception e){
                        //Toast.makeText(TransferMoneyOneTimeActivity.this, "" + e, Toast.LENGTH_SHORT).show();
                    }
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



        EditTextTargetAccount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                _before = charSequence.subSequence(0,start).toString();
                _old = charSequence.subSequence(start, start+count).toString();
                _after = charSequence.subSequence(start+count, charSequence.length()).toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                _new = charSequence.subSequence(start, start+count).toString();

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
                if (editable.toString().length() == 8){
                    if (_before.length() == 7){
                        EditTextTargetAccount.setText(editable.toString()+ "-");
                    }
                    else if  (_before.length() > editable.length()) {
                        EditTextTargetAccount.setText(editable.toString().substring(0,8));
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
                if (editable.toString().length() == 18){
                    if (_before.length() == 17){
                        EditTextTargetAccount.setText(editable.toString() + "-");
                        EditTextTargetAccount.setText(editable.toString().substring(0, 17) + "-" + editable.toString().substring(17, 18));
                    }
                    else if  (_before.length() > editable.length()) {
                        EditTextTargetAccount.setText(editable.toString().substring(0,17));
                    }
                }

                //Ez előre hozzáadja a kötőjelet Egyszerre csak az egyik lehet aktív!
                /*if (editable.toString().length() == 17){
                    if (_before.length() == 16){
                        EditTextTargetAccount.setText(editable.toString() + "-");
                    }
                    else if  (_before.length() > editable.length()) {
                        EditTextTargetAccount.setText(editable.toString().substring(0,17));
                    }
                }*/

                _ignore = false; // release, so the TextWatcher start to listen again.
                EditTextTargetAccount.setSelection(EditTextTargetAccount.getText().length());  // kurzor pozicíonálása a legvégére
            }
        });

        EditTextAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*CharSequence c = charSequence;
                int s = i;
                int s1 = i1;
                int s2 = i2;
                int x = 0;*/
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /*String c = charSequence.toString();
                int s = i;
                int s1 = i1;
                int s2 = i2;
                int x = 0;*/
                //String ss = charSequence.toString().substring(charSequence.length()-1, charSequence.length());
                //String ss2 = String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator());
                //int x = 0;
                /*if (charSequence.toString().substring(charSequence.length()-1, charSequence.length()).equals(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()))){
                    EditTextAmount.setFilters(new InputFilter[] {new InputFilter.LengthFilter(14)});
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {
                /*String e = editable.toString();
                int x = 0;
                if (EditTextAmount.getText().toString().substring(EditTextAmount.length()-1, EditTextAmount.length()).equals(df.getDecimalFormatSymbols().getDecimalSeparator())){
                    EditTextAmount.setFilters(new InputFilter[] {new InputFilter.LengthFilter(14)});
                }*/

                /*double tempAmountToTransfer = Double.parseDouble(EditTextAmount.getText().toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "").replace(String.valueOf(
                .getDecimalFormatSymbols().getDecimalSeparator()), ".")); // itt a space az nem space, hanem Alt+255 !!!
                if (tempAmountToTransfer == Math.round(tempAmountToTransfer)){
                    EditTextAmount.setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)});
                }
                else{
                    EditTextAmount.setFilters(new InputFilter[] {new InputFilter.LengthFilter(14)});
                }*/

            }
        });

        spinnerSourceAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelectedItemNo = i;
                if (!_ignore2){
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

        spinnerBeneficiaries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        break;
                    case 1:
                        changeSpinnerToEditText();
                        break;
                    default:
                        imageViewDownArrow.setVisibility(View.INVISIBLE);
                        EditTextBeneficiaryName.setVisibility(View.VISIBLE);
                        EditTextBeneficiaryName.setText(dataModels.get(i-2).getPartner_name()); // a -2 azért van, mert a spinner 0-dik eleme üres, az első meg az "új kedvezményezett"
                        EditTextTargetAccount.setText(dataModels.get(i-2).getPartner_account_number());
                        EditTextBeneficiaryName.setVisibility(View.VISIBLE);
                        spinnerBeneficiaries.setVisibility(View.INVISIBLE);
                        EditTextAmount.requestFocus();
                        //Utility.showKeyboard(TransferMoneyOneTimeActivity.this);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

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
        MenuItem navMenuOneTimeTransfer = navMenu.findItem(R.id.One_time_transfer);
        navMenuOneTimeTransfer.setEnabled(false);

        ColorStateList cs1 = Utility.navigationDrawerColors();
        navigationView.setItemTextColor(cs1);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        // menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        Intent intent;
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.Overviewe:
                                intent = new Intent(TransferMoneyOneTimeActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(TransferMoneyOneTimeActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(TransferMoneyOneTimeActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(TransferMoneyOneTimeActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(TransferMoneyOneTimeActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(TransferMoneyOneTimeActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(TransferMoneyOneTimeActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(TransferMoneyOneTimeActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(TransferMoneyOneTimeActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(TransferMoneyOneTimeActivity.this, PinCodeChangeActivity.class);
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

                                intent = new Intent(TransferMoneyOneTimeActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(TransferMoneyOneTimeActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(TransferMoneyOneTimeActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(TransferMoneyOneTimeActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                TransferMoneyOneTimeActivity.this.finish();
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
                                intent = new Intent(TransferMoneyOneTimeActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mobilbankmainactivity_without_name, menu); // elemek hozzáadása a toolbarhoz
        menuitem_logout_3 = menu.findItem(R.id.logout_toolbar_icon_3);
        //menuitem_toolbar_name = menu.findItem(R.id.logout_toolbar_name);
        loginState = Utility.getLoginState(this);

        if (loginState){
            menuitem_logout_3.setVisible(true);
            menuitem_logout_3.setEnabled(true);
            //menuitem_toolbar_name.setTitle(userFirstName);
            //menuitem_toolbar_name.setVisible(true);
        }
        else{
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
        if (id == R.id.logout_toolbar_icon_3){
            CustomDialogClass cdd = new CustomDialogClass(this);
            cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                @Override
                public void onYes(String title) {
                    menuitem_logout_3.setEnabled(false);
                    menuitem_logout_3.setVisible(false);
                    //menuitem_toolbar_name.setTitle("");
                    //menuitem_toolbar_name.setVisible(false);
                    loginState = false;
                    Utility.saveLoginState(TransferMoneyOneTimeActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(TransferMoneyOneTimeActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            TransferMoneyOneTimeActivity.this.finish();
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

    @Override
    public void taskCompletionResult(String result) {
        String returnedMessage ="";
        try {
            JSONObject object2 = new JSONObject(result);
            returnedMessage = object2.getString(Constants.RESPONSE_MESSAGE);
        }
        catch(JSONException e){
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
                        if (vanAtadvaErtek) {
                            spinnerSourceAccount.setSelection(Integer.valueOf(selectedBankAccountNumberId));
                            vanAtadvaErtek = false;
                        } else {
                            spinnerSourceAccount.setSelection(spinnerSelectedItemNo);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.RESPONSE_MESSAGE_BENECICIARIES_SUCCESSFUL:
                HashMap<String, String> item2;    //Used to link data to lines
                dataModels.clear();
                JSONArray beneficiariesList;
                try {
                    JSONObject object = new JSONObject(result);
                    beneficiariesList = object.getJSONArray(Constants.RESPONSE_BENEFICIARIES);
                    List<String> list = new ArrayList<String>();
                    list.add(" "); // ez azért kell, mert új kedvezményezettnél átváltunk spinnerről edittextre.Ha az új kedvezményezett lenne a default, akkor nem lehetne váltani
                    list.add(getString(R.string.new_beneficiary));
                    if (!object.getBoolean("error")) {
                        for (int i = 0; i < beneficiariesList.length(); i++) {
                            JSONObject obj = beneficiariesList.getJSONObject(i);
                            dataModels.add(new DataModelBeneficiaries(
                                    obj.getInt(Constants.COL_BENEFICIARIES_ID),
                                    obj.getInt(Constants.COL_BENEFICIARIES_ID_USER),
                                    obj.getString(Constants.COL_BENEFICIARIES_NAME),
                                    obj.getString(Constants.COL_BENEFICIARIES_PARTNER_NAME),
                                    obj.getString(Constants.COL_BENEFICIARIES_PARTNER_ACCOUNT_NUMBER),
                                    obj.getString(Constants.COL_BENEFICIARIES_STATUS),
                                    obj.getString(Constants.COL_BENEFICIARIES_CREATED_ON)
                            ));
                            list.add(
                                    /*getString(R.string.template_name) + " " + obj.getString(Constants.COL_BENEFICIARIES_NAME) + "\n" +
                                    getString(R.string.partner_name) + " " + obj.getString(Constants.COL_BENEFICIARIES_PARTNER_NAME) + "\n" +
                                    getString(R.string.account_number) + " " + obj.getString(Constants.COL_BENEFICIARIES_PARTNER_ACCOUNT_NUMBER)*/
                                    obj.getString(Constants.COL_BENEFICIARIES_NAME) + "\n" +
                                    obj.getString(Constants.COL_BENEFICIARIES_PARTNER_NAME) + "\n" +
                                    obj.getString(Constants.COL_BENEFICIARIES_PARTNER_ACCOUNT_NUMBER)
                            );
                        }
                        addItemsOnSpinnerBeneficiaries(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LinLay0.setVisibility(View.VISIBLE);
                break;
            case Constants.RESPONSE_MESSAGE_BENECICIARIES_UNSUCCESSFUL:
                if (bankAccountCurrencyList.size() != 0)
                    changeSpinnerToEditText();
                break;

            case Constants.RESPONSE_MESSAGE_ACCOUNTLIST_UNSUCCESSFUL:{
                Toast.makeText(this, getString(R.string.no_active_account), Toast.LENGTH_SHORT).show();
                ButtonSubmit.setEnabled(false);
                //finishWithDelay(2);
                //clearScereen();
                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
                finish();
                break;
            }

            case Constants.RESPONSE_MESSAGE_TRANSFER_SUCCESSFUL:{
                Toast.makeText(this, getString(R.string.transfer_ok), Toast.LENGTH_SHORT).show();
                //getAccountBalances(userId);
                //resetFields();
                Intent intent = new Intent(TransferMoneyOneTimeActivity.this, DoneAnimation.class);
                startActivity(intent);
                finish();
                break;
            }
            case Constants.RESPONSE_MESSAGE_TRANSFER_UNSUCCESSFUL: {
                Toast.makeText(this, getString(R.string.transfer_not_ok), Toast.LENGTH_SHORT).show();
                //getAccountBalances(userId);
                //resetFields();
                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
                finish();
                break;
            }
            case Constants.RESPONSE_MESSAGE_NOT_ENOUGH_MONEY:{ // ez még kidolgozásra vár PHP oldalon
                Toast.makeText(this, getString(R.string.notenoughmoney), Toast.LENGTH_SHORT).show();
                //getAccountBalances(userId);
                //resetFields();
                notEnoughmoney = true;
                break;
            }
        }
        SwipeRefreshLayout.setRefreshing(false);
    }

    /**
     *
     * Aszinkron művelet!
     * @param user_id
     */
    private void getBeneficiaries(int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_BENEFICIARIES_ID_USER, String.valueOf(user_id));
            request = new PerformNetworkRequest(Constants.URL_READ_BENEFICIARIES, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "There is no internet connection!", Toast.LENGTH_SHORT).show();
            SwipeRefreshLayout.setRefreshing(false);
        }


    }

    // add items into spinner dynamically
    public void addItemsOnSpinnerSourceAccount(List<String> listofBeneficiaries) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_transfer_money, listofBeneficiaries);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item_xbank);
        spinnerSourceAccount.setAdapter(dataAdapter);
    }

    // add items into spinner dynamically
    public void addItemsOnSpinnerBeneficiaries(List<String> listOfBeneficiaries) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOfBeneficiaries);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item_xbank);
        spinnerBeneficiaries.setAdapter(dataAdapter);
    }

    private void sendTransferOneTime(int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_TRANSACTIONS_ID_USER, String.valueOf(user_id));
            //params.put(Constants.COL_TRANSACTIONS_ID_BANK_ACCOUNT_NUMBER, String.valueOf(EditTextSourceAccount.getText().toString()));
            params.put(Constants.COL_TRANSACTIONS_ID_BANK_ACCOUNT_NUMBER, selectedBankAccountNumberId);
            params.put(Constants.COL_TRANSACTIONS_CURRENCY, String.valueOf(TextViewCurrency.getText().toString()));
            params.put(Constants.COL_TRANSACTIONS_AMOUNT, String.valueOf(amountToTransfer));
            params.put(Constants.COL_TRANSACTIONS_PARTNER_NAME, EditTextBeneficiaryName.getText().toString());
            params.put(Constants.COL_TRANSACTIONS_PARTNER_ACCOUNT_NUMBER, EditTextTargetAccount.getText().toString());
            params.put(Constants.COL_TRANSACTIONS_COMMENT, EditTextComment.getText().toString());
            request = new PerformNetworkRequest(Constants.URL_INSERT_TRANSFER_ONE_TIME, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            SwipeRefreshLayout.setRefreshing(false);
        }


    }

    public void init(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        spinnerSourceAccount = findViewById(R.id.SpinnerSourceAccount);
        spinnerBeneficiaries = findViewById(R.id.SpinnerBeneficiary);
        TextViewSourceAccount = findViewById(R.id.TextViewSourceAccount);
        TextViewBeneficiaryName = findViewById(R.id.TextViewBeneficiaryName);
        TargetAccount = findViewById(R.id.TargetAccount);
        TextViewAmount = findViewById(R.id.TextViewAmount);
        TextViewComment = findViewById(R.id.TextViewComment);
        TextViewCurrency = findViewById(R.id.TextViewCurrency);
        imageViewDownArrow = findViewById(R.id.ImageViewDownArrow);
        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);

        EditTextBeneficiaryName = findViewById(R.id.EditTextBeneficiaryName);
        EditTextTargetAccount = findViewById(R.id.EditTextTargetAccount);
        EditTextAmount = findViewById(R.id.EditTextAmount);
        EditTextAmount.addTextChangedListener(new NumberTextWatcher(EditTextAmount)); // külső osztálbyan lévő listener, az ezres tagolást csinálja
        EditTextComment = findViewById(R.id.EditTextComment);
        ButtonSubmit = findViewById(R.id.ButtonSubmit);

        TextViewSablon = findViewById(R.id.TextViewSablon);
        CheckBox1 = findViewById(R.id.CheckBox1);
        drawerLayout = findViewById(R.id.drawer_layout);

        EditTextBeneficiaryName.setTag(1);
        EditTextTargetAccount.setTag(2);
        EditTextAmount.setTag(3);
        EditTextComment.setTag(4);

        LinLay0 = findViewById(R.id.LinLay0);
    }

    /**
     *
     * Aszinkron művelet!
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
            SwipeRefreshLayout.setRefreshing(false);
        }


    }

    private void insertNewBeneficiary(int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_BENEFICIARIES_ID_USER, String.valueOf(user_id));
            params.put(Constants.COL_BENEFICIARIES_NAME, EditTextBeneficiaryName.getText().toString());
            params.put(Constants.COL_BENEFICIARIES_PARTNER_NAME, EditTextBeneficiaryName.getText().toString());
            params.put(Constants.COL_BENEFICIARIES_PARTNER_ACCOUNT_NUMBER, EditTextTargetAccount.getText().toString());
            request = new PerformNetworkRequest(Constants.URL_INSERT_BENEFICIARY, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            SwipeRefreshLayout.setRefreshing(false);
        }


    }


    public void resetFields(){
        currencyAddedToAmount = false; // ez fontos, hogy az edittext-törlések előtt legyen!
        EditTextTargetAccount.setText("");
        EditTextComment.setText("");
        EditTextAmount.setText("");
        EditTextBeneficiaryName.setText("");
        amountToTransfer=0;
        _ignore2 = true;
        spinnerSourceAccount.setSelection(spinnerSelectedItemNo);
        TextViewCurrency.setText("");
    }

    public void changeSpinnerToEditText(){
        imageViewDownArrow.setVisibility(View.INVISIBLE);
        EditTextBeneficiaryName.setVisibility(View.VISIBLE);
        spinnerBeneficiaries.setVisibility(View.INVISIBLE);
        EditTextBeneficiaryName.requestFocus();
        Utility.showKeyboard(TransferMoneyOneTimeActivity.this);
        CheckBox1.setVisibility(View.VISIBLE);
        TextViewSablon.setVisibility(View.VISIBLE);
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
            try {
                selectedBankAccountNumberId = (b.getString(Constants.SHAREDPREFERENCES_ACCOUNT_NUMBER_POSITION));
                if (selectedBankAccountNumberId.equals(null)){
                    vanAtadvaErtek = false;
                }
                else{
                    vanAtadvaErtek = true;
                }
            }catch(Exception e){
                vanAtadvaErtek = false;
            }
        }
        catch (Exception e){ // ez a blokk azért került ide, mert nem akart működni a bundle-s paraméter átadás. Már működik, így ez a blokk csak biztonsági szempontból maradt itt.
            SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
            userId = Integer.parseInt(sp.getString(Constants.SHAREDPREFERENCES_USER_ID, ""));
            userFirstName = sp.getString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, "");
        }

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

    /**
     * Ez a metódus tartalmaz egy visszaszámláló időzítőt, az időzítő letelte után pedig elindítja a következő activity-t. ????
     * Ennél a visszaszámlálónál lehetőség lenne a visszaszámlálás alatt bizonyos időközönként elindítani valamilyen utasítást is a jelzett helyen.
     */
    public void finishWithDelay(int delayInSec) {
        new CountDownTimer(delayInSec*1000, 1000) { // Visszaszámlálás (absztrakt osztály) ??
            public void onTick(long millisUntilFinished) { // meghatározott időközönként végrehajtódik ez a metódus
            }
            public void onFinish() { // számláló lejáratakor hajtódik végre
                finish();
            }
        }.start();
    }

    /**
     * végül itt nem használom. HElyette alapból láthatatlan a LInLay0 és akkor lesz látható, ha van bvankszámlaszám
     */
    public void clearScereen(){
        Utility.hideKeyboard(TransferMoneyOneTimeActivity.this);
        LinLay0.setVisibility(View.INVISIBLE);
    }

}


