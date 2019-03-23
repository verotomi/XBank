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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SavingsNewActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate {

    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    Spinner spinnerSourceAccount;
    Spinner spinnerSavingType;
    TextView TextViewSourceAccount;
    TextView TextViewType;
    TextView TextViewCurrency;
    EditText EditTextAmount;
    Button ButtonSubmit;
    LinearLayout LinLay0;

    boolean loginState = false;
    Boolean valasz = false;
    String userFirstName = "";
    int userId;
    boolean vanAtadvaErtek = false;

    //MySQL adatbázishoz

    PerformNetworkRequest request;

    //ArrayList<String> list = new ArrayList<>();
    List<String> bankAccountNumberIdList = new ArrayList<String>();
    List<String> bankAccountCurrencyList = new ArrayList<String>();
    String selectedBankAccountNumberId;
    int amountToTransfer = 0;
    int spinnerSelectedItemNo = 0;
    boolean _ignore2 = false; // ezzel kezelem azt, hogy a ugyanaz a bankszámla legyen kiválasztva a spinnerben az átutalás után is
    boolean currencyAddedToAmount = false; // ez lehet hogy nem is kell


    boolean _ignore = false; // indicates if the change was made by the TextWatcher itself.
    private String _before; // Unchanged sequence which is placed before the updated sequence.
    private String _old; /// updated sequence before the update.
    private String _new; // Updated sequence after the update.
    private String _after; // Unchanged sequence which is placed after the updated sequence.

    boolean ignore3 = false; // ezzel küszöbölöm ki, hogy ha programból módosítom a spinner kiválasztást, akkor a listener ne induljon el

    List<String> savingTypesIdList = new ArrayList<String>();
    List<Integer> durationList = new ArrayList<Integer>();
    String selectedSavingTypesId;
    int selectedDuration;

    DrawerLayout drawerLayout;
    String userLastLoginTime;
    String userLastName;
    boolean ignore = false;

    boolean notEnoughmoney = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleSavingsNew)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_savings_new);

        getBundle(); // átadott változók beolvasása
        init();

        if(Utility.IsNetworkAvailable(this)){
            LinLay0.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(this, "There is no internet connection!", Toast.LENGTH_SHORT).show();
        }

        navDrawer();

        //ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<String>(SavingsNewActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.recurring_transfers_frequency));
        //spinnerSavingType.setAdapter(spinnerAdapter1);

        setSupportActionBar(toolbar); // Toolbar megjelenítése
        getAccountBalances(userId);

        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean iserror = false;
                if (!EditTextAmount.getText().toString().equals("")) {
                    amountToTransfer = Integer.parseInt(EditTextAmount.getText().toString().replace(" ", "").replace(",", "")); // itt a space az nem space, hanem Alt+255 !!!
                }
                else{
                    amountToTransfer = 0;
                }
                if (EditTextAmount.getText().toString().equals("") || amountToTransfer < 1 || amountToTransfer > 999999999) {
                    if (EditTextAmount.getText().toString().equals("")) {
                        Toast.makeText(SavingsNewActivity.this, getString(R.string.amount_needed), Toast.LENGTH_SHORT).show();
                        iserror = true;
                    }
                    else if (amountToTransfer > 999999999) {
                        Toast.makeText(SavingsNewActivity.this, getString(R.string.rule_max_amount), Toast.LENGTH_SHORT).show();
                        iserror = true;
                    }
                    else if (amountToTransfer < 1) {
                        Toast.makeText(SavingsNewActivity.this, getString(R.string.rule_min_amount), Toast.LENGTH_SHORT).show();
                        iserror = true;
                    }
                }
                /*else if (spinnerSavingType.getSelectedItemId() == 0) {
                    Toast.makeText(SavingsNewActivity.this, "Kérem válasszon megtakarítási típust!", Toast.LENGTH_SHORT).show();
                    iserror = true;
                }*/
                if (iserror) {
                } else {
                    sendNewSaving(userId);
                    //finish();
                }
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

        spinnerSourceAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelectedItemNo = i;
                if (!_ignore2) {
                    selectedBankAccountNumberId = bankAccountNumberIdList.get(i);
                    TextViewCurrency.setText(bankAccountCurrencyList.get(i));
                    getSavingTypes(TextViewCurrency.getText().toString());
                }
                _ignore2 = false;
                //Toast.makeText(TransferMoneyOneTimeActivity.this, selectedBankAccountNumber, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedBankAccountNumberId = bankAccountNumberIdList.get(0);
                getSavingTypes(TextViewCurrency.getText().toString());
                //Toast.makeText(TransferMoneyOneTimeActivity.this, "Default:" + selectedBankAccountNumber, Toast.LENGTH_SHORT).show();
            }
        });

        spinnerSavingType.setOnTouchListener(new View.OnTouchListener() {
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

        spinnerSavingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedSavingTypesId = savingTypesIdList.get(i);
                selectedDuration = durationList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedSavingTypesId = savingTypesIdList.get(0);
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
                    Utility.saveLoginState(SavingsNewActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(SavingsNewActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            SavingsNewActivity.this.finish();
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
                //{"error":false,"message":"Saving types list query was succesfull!","savingtypes":[{"id":2,"type":"Monthly saving","rate":"2.11","duration":"30","currency":"Forint"},{"id":4,"type":"Quarterly saving","rate":"1.82","duration":"91","currency":"Forint"},{"id":1,"type":"Simple saving","rate":"1.35","duration":"365","currency":"Forint"}]}
            case Constants.RESPONSE_MESSAGE_SAVING_TYPES_SUCCESSFUL:
                HashMap<String, String> item2;    //Used to link data to lines
                JSONArray savingTypesList;
                try {
                    JSONObject object = new JSONObject(result);
                    savingTypesList = object.getJSONArray(Constants.RESPONSE_SAVING_TYPES);
                    List<String> list = new ArrayList<String>();
                    if (!object.getBoolean(Constants.RESPONSE_ERROR)) {
                        for (int i = 0; i < savingTypesList.length(); i++) {
                            JSONObject obj = savingTypesList.getJSONObject(i);
                            savingTypesIdList.add(obj.getString(Constants.COL_SAVING_TYPES_ID));
                            durationList.add(obj.getInt(Constants.COL_SAVING_TYPES_DURATION));
                            String savingType = "";
                            switch (obj.getString(Constants.COL_SAVINGS_TYPE)){
                                case Constants.YEARLY_SAVING:
                                    savingType = getString(R.string.yearly_saving);
                                    break;
                                case Constants.QUARTERLY_SAVING:
                                    savingType = getString(R.string.quarterly_saving);
                                    break;
                                case Constants.MONTHLY_SAVING:
                                    savingType = getString(R.string.monthly_saving);
                                    break;
                                case Constants.WEEKLY_SAVING:
                                    savingType = getString(R.string.weekly_saving);
                                    break;
                                default:
                                    savingType = "";
                                    break;
                            }

                            list.add(
                                    savingType + getString(R.string.tab) +
                                            obj.getInt(Constants.COL_SAVING_TYPES_DURATION) + " " + getString(R.string.days) + getString(R.string.tab) +
                                            new DecimalFormat("#,##0.00").format(obj.getDouble(Constants.COL_SAVING_TYPES_RATE)) + "%"
                            );
                        }
                        addItemsOnSpinnerSavingTypes(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.RESPONSE_MESSAGE_ACCOUNTLIST_UNSUCCESSFUL: {
                Toast.makeText(this, getString(R.string.no_active_account), Toast.LENGTH_SHORT).show();
                ButtonSubmit.setEnabled(false);
                break;
            }
            case Constants.RESPONSE_MESSAGE_NEW_SAVING_SUCCESSFUL: {
                Toast.makeText(this, R.string.new_saving_successful, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SavingsNewActivity.this, DoneAnimation.class);
                startActivity(intent);
                getAccountBalances(userId);
                //resetFields();
                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
                finish();
                break;
            }
            case Constants.RESPONSE_MESSAGE_NEW_SAVING_UNSUCCESSFUL: {
                Toast.makeText(this, getString(R.string.recurring_transfer_not_ok), Toast.LENGTH_SHORT).show();
                getAccountBalances(userId);
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
                break;}
        }
    }

    // add items into spinner dynamically
    public void addItemsOnSpinnerSourceAccount(List<String> listOfBankAccounts) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_transfer_money, listOfBankAccounts);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item_xbank);
        spinnerSourceAccount.setAdapter(dataAdapter);
    }

    // add items into spinner dynamically
    public void addItemsOnSpinnerSavingTypes(List<String> listOfSavingTypes) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOfSavingTypes);
        //dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_bank_accounts);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item_xbank);
        spinnerSavingType.setAdapter(dataAdapter);
    }

    private void sendNewSaving(int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_SAVINGS_ID_USER, String.valueOf(user_id));
            params.put(Constants.COL_SAVINGS_ID_BANK_ACCOUNT, selectedBankAccountNumberId);
            params.put(Constants.COL_SAVINGS_ID_TYPE, selectedSavingTypesId);
            params.put(Constants.COL_SAVINGS_AMOUNT, String.valueOf(amountToTransfer));

            /**
             * Aktuális dátum beállítása a 'To'-hoz
             *
             Date d = Calendar.getInstance().getTime();
             //SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
             SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
             SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
             to = df2.format(d);
             textViewTo.setText(df.format(d));
             Calendar c = Calendar.getInstance();
             c.add(Calendar.MONTH,-1);
             from = df2.format(c.getTime());
             textViewFrom.setText(df.format(c.getTime()));*/

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR,selectedDuration);
            String expireDate = df.format(c.getTime());


            params.put(Constants.COL_SAVINGS_EXPIRE_DATE, expireDate);
            request = new PerformNetworkRequest(Constants.URL_INSERT_SAVING, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            //SwipeRefreshLayout.setRefreshing(false);
        }


    }

    /**
     *
     * Aszinkron művelet!
     * @param currency
     */
    private void getSavingTypes(String currency) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_SAVING_TYPES_CURRENCY, String.valueOf(currency));
            request = new PerformNetworkRequest(Constants.URL_READ_SAVING_TYPES, params, Constants.CODE_POST_REQUEST, this);
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
        spinnerSavingType = findViewById(R.id.SpinnerSavingType);
        TextViewSourceAccount = findViewById(R.id.TextViewSourceAccount);
        TextViewCurrency = findViewById(R.id.TextViewCurrency);
        EditTextAmount = findViewById(R.id.EditTextAmount);
        EditTextAmount.addTextChangedListener(new NumberTextWatcher(EditTextAmount)); // külső osztálbyan lévő listener, az ezres tagolást csinálja
        ButtonSubmit = findViewById(R.id.ButtonSubmit);
        drawerLayout = findViewById(R.id.drawer_layout);
        LinLay0= findViewById(R.id.LinLay0);
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
            spinnerSelectedItemNo = Integer.parseInt(b.getString(Constants.SHAREDPREFERENCES_ACCOUNT_NUMBER_POSITION));
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
        MenuItem navMenuAccountHistory = navMenu.findItem(R.id.Savings);
        navMenuAccountHistory.setEnabled(false);

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
                                intent = new Intent(SavingsNewActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(SavingsNewActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(SavingsNewActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(SavingsNewActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(SavingsNewActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(SavingsNewActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(SavingsNewActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(SavingsNewActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(SavingsNewActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(SavingsNewActivity.this, PinCodeChangeActivity.class);
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

                                intent = new Intent(SavingsNewActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(SavingsNewActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(SavingsNewActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(SavingsNewActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                SavingsNewActivity.this.finish();
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
                                intent = new Intent(SavingsNewActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
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
        EditTextAmount.setText("");
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