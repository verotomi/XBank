package tamas.verovszki.xbank;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BankAccountHistoryActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate{

    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    LinearLayout linearLayoutSeparator;
    Spinner spinnerSourceAccount;
    TextView textViewFrom;
    TextView textViewTo;
    ImageView calendarIconFrom;
    ImageView calendarIconTo;
    LinearLayout LinLay0;
    TextView TextViewEmptyList;

    boolean loginState = false;
    boolean answer;
    Boolean valasz = false;
    ListView ListViewBankAccountsHistory;
    String userFirstName="";
    int userId;
    int bankAccountNumberId;
    private SwipeRefreshLayout SwipeRefreshLayout;
    CheckBox checkBoxOut;
    CheckBox checkBoxIn;

    //MySQL adatbázishoz

    PerformNetworkRequest request;

    // listview-hez kellő változók
    ArrayList<DataModelTransactions> dataModels;
    private static CustomAdapterTransactions adapter;


    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    private SimpleAdapter sa;

    List<Integer> bankAccountNumberIdList = new ArrayList<Integer>();
    int selectedBankAccountNumberId = 0;
    int spinnerSelectedItemNo = 0;
    boolean _ignore2 = false; // ezzel kezelem azt, hogy a ugyanaz a bankszámla legyen kiválasztva a spinnerben az átutalás után is
    List<String> bankAccountCurrencyList = new ArrayList<String>();

    boolean vanAtadvaErtek = false;

    String filter="inout";
    String filterIn = "in";
    String filterOut = "out";
    String from;
    String to;

    DrawerLayout drawerLayout;
    String userLastLoginTime;
    String userLastName;
    boolean ignore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleBankAccountHistory)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_bank_account_history);

        getBundle(); // átadott változók beolvasása
        init();
        navDrawer();

        setSupportActionBar(toolbar); // Toolbar megjelenítése

        /**
         * Aktuális dátum beállítása a 'To'-hoz
         */
        Date d = Calendar.getInstance().getTime();
        //SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMdd");
        to = df2.format(d);
        textViewTo.setText(df.format(d));
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH,-1);
        from = df2.format(c.getTime());
        textViewFrom.setText(df.format(c.getTime()));


        getAccountBalances(userId);

        checkBoxIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b && !checkBoxOut.isChecked()){
                    checkBoxOut.setChecked(true);
                }
                filterIn = b? "in" : "";
                filter = filterIn + filterOut;
                getBankAccountHistory(userId, selectedBankAccountNumberId, from, to, filter);
                _ignore2 =  false; // ez lehet hogy nem is kell ide
            }
        });

        checkBoxOut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b && !checkBoxIn.isChecked()){
                    checkBoxIn.setChecked(true);
                }
                filterOut = b? "out" : "";
                filter = filterIn + filterOut;
                getBankAccountHistory(userId, selectedBankAccountNumberId, from, to, filter);
                _ignore2 =  false; // ez lehet hogy nem is kell ide
            }
        });

        /**
         * Képernyő lehúzás hatására frissítés
         */
        SwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.darkgreen3, R.color.darkgreen);
        SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SwipeRefreshLayout.setRefreshing(true);
                //recreate();
                getBankAccountHistory(userId, selectedBankAccountNumberId, from, to, filter);
                _ignore2 = true;
                //SwipeRefreshLayout.setRefreshing(false);
            }
        });

        //getBankAccountHistory(userId, selectedBankAccountNumberId, "2010-01-01", "2019-12-31", "filter"); // elinditom a kártyaadatok bekérését, és amikor megérkezett, akkor építem fel a listview-et

        dataModels = new ArrayList<>();
        ListViewBankAccountsHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                Intent intent = new Intent(BankAccountHistoryActivity.this, CreditCardsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.SHAREDPREFERENCES_TRANSACTION, dataModels.get(position)); // ehhez az osztálynak implementálni kell a Serializable interfészt!
                intent = new Intent(BankAccountHistoryActivity.this, TransactionDetailsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        spinnerSourceAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelectedItemNo = i;
                if (!_ignore2){
                    selectedBankAccountNumberId = bankAccountNumberIdList.get(i);
                    getBankAccountHistory(userId, selectedBankAccountNumberId, from, to, filter);
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

        textViewFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(BankAccountHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(year) + "-" + ((monthOfYear + 1) < 10  ? "0" : "") + String.valueOf(monthOfYear + 1) // a +1 oka: Java: január: 0 , február: 1, ...
                                + "-" + (dayOfMonth < 10  ? "0" : "") + String.valueOf(dayOfMonth);
                        textViewFrom.setText(date);
                        from = String.valueOf(year) + ((monthOfYear + 1) < 10  ? "0" : "") + String.valueOf(monthOfYear + 1) // a +1 oka: Java: január: 0 , február: 1, ...
                                + (dayOfMonth < 10  ? "0" : "") + String.valueOf(dayOfMonth);
                        getBankAccountHistory(userId, selectedBankAccountNumberId, from, to, filter);
                        _ignore2 =  false; // ez lehet hogy nem is kell ide

                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });

        calendarIconFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(BankAccountHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(year) + "-" + ((monthOfYear + 1) < 10  ? "0" : "") + String.valueOf(monthOfYear + 1) // a +1 oka: Java: január: 0 , február: 1, ...
                                + "-" + (dayOfMonth < 10  ? "0" : "") + String.valueOf(dayOfMonth);
                        textViewFrom.setText(date);
                        from = String.valueOf(year) + ((monthOfYear + 1) < 10  ? "0" : "") + String.valueOf(monthOfYear + 1) // a +1 oka: Java: január: 0 , február: 1, ...
                                + (dayOfMonth < 10  ? "0" : "") + String.valueOf(dayOfMonth);
                        getBankAccountHistory(userId, selectedBankAccountNumberId, from, to, filter);
                        _ignore2 =  false; // ez lehet hogy nem is kell ide

                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });

        textViewTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int yy = calendar.get(Calendar.YEAR);
                int mm = calendar.get(Calendar.MONTH);
                int dd = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePicker = new DatePickerDialog(BankAccountHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = String.valueOf(year) + "-" + ((monthOfYear + 1) < 10  ? "0" : "") + String.valueOf(monthOfYear + 1) // a +1 oka: Java: január: 0 , február: 1, ...
                                + "-" + (dayOfMonth < 10  ? "0" : "") + String.valueOf(dayOfMonth);
                        textViewTo.setText(date);
                        to = String.valueOf(year) + ((monthOfYear + 1) < 10  ? "0" : "") + String.valueOf(monthOfYear + 1) // a +1 oka: Java: január: 0 , február: 1, ...
                                + (dayOfMonth < 10  ? "0" : "") + String.valueOf(dayOfMonth);
                        getBankAccountHistory(userId, selectedBankAccountNumberId, from, to, filter);
                        _ignore2 =  false; // ez lehet hogy nem is kell ide

                    }
                }, yy, mm, dd);
                datePicker.show();
            }
        });

        calendarIconTo.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePicker = new DatePickerDialog(BankAccountHistoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String date = String.valueOf(year) + "-" + ((monthOfYear + 1) < 10  ? "0" : "") + String.valueOf(monthOfYear + 1) // a +1 oka: Java: január: 0 , február: 1, ...
                            + "-" + (dayOfMonth < 10  ? "0" : "") + String.valueOf(dayOfMonth);
                    textViewTo.setText(date);
                    to = String.valueOf(year) + ((monthOfYear + 1) < 10  ? "0" : "") + String.valueOf(monthOfYear + 1) // a +1 oka: Java: január: 0 , február: 1, ...
                            + (dayOfMonth < 10  ? "0" : "") + String.valueOf(dayOfMonth);
                    getBankAccountHistory(userId, selectedBankAccountNumberId, from, to, filter);
                    _ignore2 =  false; // ez lehet hogy nem is kell ide

                }
            }, yy, mm, dd);
            datePicker.show();
            }
        });

        /**
         * (Többek között) ez kell ahhoz, hogy a listview-ekben működjön együtt a felfele görgetés és a lehúzásos frissítés
         *  Lényegében akkor teszi aktivvá a lehúzható frissítést, ha a felfele görgetés során elértük a lista legtetejét
         */
        ListViewBankAccountsHistory.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (ListViewBankAccountsHistory == null || ListViewBankAccountsHistory.getChildCount() == 0) ? 0 : ListViewBankAccountsHistory.getChildAt(0).getTop();
                SwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });
    }


    public void init() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        spinnerSourceAccount = findViewById(R.id.SpinnerSourceAccount);
        ListViewBankAccountsHistory = findViewById(R.id.ListViewBankAccountHistory);
        linearLayoutSeparator = findViewById(R.id.LinLaySeparator);
        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        calendarIconFrom = findViewById(R.id.CalendarIconFrom);
        calendarIconTo = findViewById(R.id.CalendarIconTo);
        textViewFrom = findViewById(R.id.TextViewFrom2);
        textViewTo = findViewById(R.id.TextViewTo2);
        checkBoxIn = findViewById(R.id.CheckBoxIn);
        checkBoxOut = findViewById(R.id.CheckBoxOut);
        drawerLayout = findViewById(R.id.drawer_layout);
        LinLay0 = findViewById(R.id.LinLay0);
        TextViewEmptyList = findViewById(R.id.TextViewEmptyList);


    }


    // add items into spinner dynamically
    public void addItemsOnSpinnerSourceAccount(List<String> listOfBankAccounts) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_transfer_money, listOfBankAccounts);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item_xbank);
        spinnerSourceAccount.setAdapter(dataAdapter);
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
            CustomDialogClass cdd = new CustomDialogClass(BankAccountHistoryActivity.this);
            cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                @Override
                public void onYes(String title) {
                    menuitem_logout_3.setEnabled(false);
                    menuitem_logout_3.setVisible(false);
                    //menuitem_toolbar_name.setTitle("");
                    //menuitem_toolbar_name.setVisible(false);
                    loginState = false;
                    Utility.saveLoginState(BankAccountHistoryActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(BankAccountHistoryActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            BankAccountHistoryActivity.this.finish();
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
     *
     * Aszinkron művelet!
     * @param user_id
     */
    private void getBankAccountHistory(int user_id, int bankAccountNumberId, String from, String to, String filter) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_TRANSACTIONS_ID_USER, String.valueOf(user_id));
            params.put(Constants.COL_TRANSACTIONS_ID_BANK_ACCOUNT_NUMBER, String.valueOf(bankAccountNumberId));
            params.put(Constants.FROM, from);
            params.put(Constants.TO, to);
            params.put(Constants.FILTER, filter);
            request = new PerformNetworkRequest(Constants.URL_READ_BANK_ACCOUNT_HISTORY, params, Constants.CODE_POST_REQUEST, BankAccountHistoryActivity.this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            SwipeRefreshLayout.setRefreshing(false);
        }

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
                            bankAccountNumberIdList.add(obj.getInt(Constants.COL_BANK_ACCOUNTS_ID));
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
                            spinnerSourceAccount.setSelection(Integer.valueOf(spinnerSelectedItemNo));
                            selectedBankAccountNumberId = bankAccountNumberIdList.get(spinnerSelectedItemNo);
                            vanAtadvaErtek = false;
                        } else {
                            selectedBankAccountNumberId = bankAccountNumberIdList.get(0);
                            spinnerSourceAccount.setSelection(spinnerSelectedItemNo);
                        }
                        getBankAccountHistory(userId, selectedBankAccountNumberId, from, to, filter);
                        _ignore2 =  true;

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LinLay0.setVisibility(View.VISIBLE);

                break;
            case Constants.RESPONSE_MESSAGE_ACCOUNTLIST_UNSUCCESSFUL:{
                //Toast.makeText(this, "Nem található a feltételeknek megfelelő tranzakció!", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, getString(R.string.no_active_account), Toast.LENGTH_SHORT).show();

                break;
            }

            case Constants.RESPONSE_MESSAGE_BANK_ACCOUNT_HISTORY_SUCCESSFUL:{
                dataModels.clear();
                final JSONArray transactionListJSON;
                try {
                    //JSONObject object = new JSONObject(request.getResult());
                    JSONObject object = new JSONObject(result);
                    transactionListJSON = object.getJSONArray("bankaccounthistory");
                    if (!object.getBoolean("error")) {
                        dataModels.clear();
                        for (int i = 0; i < transactionListJSON.length(); i++) {
                            JSONObject obj = transactionListJSON.getJSONObject(i);
                            String transactionType = "";
                            String comment = "";

                            if (obj.getString(Constants.COL_TRANSACTIONS_COMMENT).contains(Constants.COMMENT_ACCOUNT_OPENING)) {
                                    comment = obj.getString(Constants.COL_TRANSACTIONS_COMMENT).replace(Constants.COMMENT_ACCOUNT_OPENING,getString(R.string.comment_account_opening));
                            }
                            if (obj.getString(Constants.COL_TRANSACTIONS_COMMENT).contains(Constants.COMMENT_FUND_WITHDRAWAL)) {
                                comment = obj.getString(Constants.COL_TRANSACTIONS_COMMENT).replace(Constants.COMMENT_FUND_WITHDRAWAL,getString(R.string.comment_fund_withdrawal));
                            }
                            if (obj.getString(Constants.COL_TRANSACTIONS_COMMENT).contains(Constants.COMMENT_INTEREST_WITHDRAWAL)) {
                                comment = obj.getString(Constants.COL_TRANSACTIONS_COMMENT).replace(Constants.COMMENT_INTEREST_WITHDRAWAL,getString(R.string.comment_interest_withdrawal));
                            }
                            if (obj.getString(Constants.COL_TRANSACTIONS_COMMENT).contains(Constants.COMMENT_MONTHLY_FEE)) {
                                comment = obj.getString(Constants.COL_TRANSACTIONS_COMMENT).replace(Constants.COMMENT_MONTHLY_FEE,getString(R.string.comment_monthly_fee));
                            }
                            if (obj.getString(Constants.COL_TRANSACTIONS_COMMENT).contains(Constants.COMMENT_RANDOM_COMMENT)) {
                                comment = obj.getString(Constants.COL_TRANSACTIONS_COMMENT).replace(Constants.COMMENT_RANDOM_COMMENT,getString(R.string.comment_random_comment));
                            }
                            if (obj.getString(Constants.COL_TRANSACTIONS_COMMENT).contains(Constants.COMMENT_SAVING)) {
                                comment = obj.getString(Constants.COL_TRANSACTIONS_COMMENT).replace(Constants.COMMENT_SAVING,getString(R.string.comment_saving));
                            }
                            if (obj.getString(Constants.COL_TRANSACTIONS_COMMENT).contains(Constants.COMMENT_EARLY_WITHDRAWAL)) {
                                comment = obj.getString(Constants.COL_TRANSACTIONS_COMMENT).replace(Constants.COMMENT_EARLY_WITHDRAWAL,getString(R.string.comment_early_withdrawal));
                            }

                            switch (obj.getString(Constants.COL_TRANSACTIONS_TYPE)){
                                case Constants.TYPE_ACCOUNT_OPEN:
                                    transactionType = getString(R.string.account_open);
                                    break;
                                case Constants.TYPE_DEPOSIT:
                                    transactionType = getString(R.string.deposit);
                                    break;
                                case Constants.TYPE_WITHDRAWAL:
                                    transactionType = getString(R.string.withdrawal);
                                    break;
                                case Constants.TYPE_TRANSFER_FEE:
                                    transactionType = getString(R.string.transfer_fee);
                                    break;
                                case Constants.TYPE_SAVING:
                                    transactionType = getString(R.string.saving);
                                    break;
                                case Constants.TYPE_EARLY_WITHDRAWAL:
                                    transactionType = getString(R.string.early_withdrawal);
                                    break;
                                case Constants.TYPE_FUND_WITHDRAWAL:
                                    transactionType = getString(R.string.fund_withdrawal);
                                    break;
                                case Constants.TYPE_INTEREST_WITHDRAWAL:
                                    transactionType = getString(R.string.interest_withdrawal);
                                    break;
                                case Constants.TYPE_INCOMING_TRANSFER:
                                    transactionType = getString(R.string.incoming_transfer);
                                    break;
                                case Constants.TYPE_OUTGOING_TRANSFER:
                                    transactionType = getString(R.string.outgoing_transfer);
                                    break;
                                case Constants.TYPE_RECURRING_TRANSFER:
                                    transactionType = getString(R.string.recurring_transfer);
                                    break;
                                case Constants.TYPE_RECURRING_TRANSFER_FEE:
                                    transactionType = getString(R.string.recurring_transfer_fee);
                                    break;
                                case Constants.TYPE_MONTHLY_FEE:
                                    transactionType = getString(R.string.monthly_fee);
                                    break;
                                case Constants.TYPE_PURCHASE_WITH_CREDIT_CARD:
                                    transactionType = getString(R.string.purchase_with_credit_card);
                                    break;
                                default:
                                    transactionType = getString(R.string.not_initialized);
                                    break;
                            }

                            dataModels.add(new DataModelTransactions(
                                    obj.getInt(Constants.COL_TRANSACTIONS_ID),
                                    obj.getInt(Constants.COL_TRANSACTIONS_ID_USER),
                                    obj.getInt(Constants.COL_TRANSACTIONS_ID_BANK_ACCOUNT_NUMBER),
                                    //getString(R.string.validto) + ": " + obj.getString(Constants.COL_CREDIT_CARDS_EXPIRE_DATE),
                                    transactionType,
                                    obj.getString(Constants.COL_TRANSACTIONS_DIRECTION),
                                    obj.getInt(Constants.COL_TRANSACTIONS_REFERENCE_NUMBER),
                                    obj.getString(Constants.COL_TRANSACTIONS_CURRENCY),
                                    obj.getInt(Constants.COL_TRANSACTIONS_AMOUNT),
                                    (obj.getString(Constants.COL_TRANSACTIONS_PARTNER_NAME).equals(Constants.NULL) ? "" : obj.getString(Constants.COL_TRANSACTIONS_PARTNER_NAME)),
                                    (obj.getString(Constants.COL_TRANSACTIONS_PARTNER_ACCOUNT_NUMBER).equals(Constants.NULL) ? "" : obj.getString(Constants.COL_TRANSACTIONS_PARTNER_ACCOUNT_NUMBER)),
                                    comment,
                                    obj.getString(Constants.COL_TRANSACTIONS_ARRIVED_ON),
                                    obj.getString(Constants.COL_TRANSACTIONS_BANK_ACCOUNT_NUMBER)
                            ));
                        }
                        adapter = new CustomAdapterTransactions(dataModels, this); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben

                        try{ // try nélkül leállt a program
                            TextViewEmptyList.setVisibility(View.INVISIBLE);
                            ListViewBankAccountsHistory.setVisibility(View.VISIBLE);
                        }
                        catch(Exception e){
                        }

                        ListViewBankAccountsHistory.setAdapter(adapter);
                        //recreate();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); // itt értékes infót kapok arról, hogy mi nem jó!!!
                }
                break;
            }
            case Constants.RESPONSE_MESSAGE_BANK_ACCOUNT_HISTORY_UNSUCCESSFUL:{
                //Toast.makeText(this, "Valami hiba történt, vagy a felhasználónak nincs bankszámlája", Toast.LENGTH_SHORT).show();
                ListViewBankAccountsHistory.setAdapter(null);
                try{
                    TextViewEmptyList.setVisibility(View.VISIBLE);
                    ListViewBankAccountsHistory.setVisibility(View.INVISIBLE);
                }
                catch(Exception e){
                }
                break;
            }
        }
        SwipeRefreshLayout.setRefreshing(false);
    }
    /*@Override
    public void taskCompletionResult(String result) {
        //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        HashMap<String,String> item;    //Used to link data to lines
        dataModels.clear();
        final JSONArray transactionListJSON;
        try {
            //JSONObject object = new JSONObject(request.getResult());
            JSONObject object = new JSONObject(result);
            transactionListJSON = object.getJSONArray("bankaccounthistory");
            if (!object.getBoolean("error")) {
                dataModels.clear();
                for (int i = 0; i < transactionListJSON.length(); i++) {
                    JSONObject obj = transactionListJSON.getJSONObject(i);
                    dataModels.add(new DataModelTransactions(
                            obj.getInt(Constants.COL_TRANSACTIONS_ID),
                            obj.getInt(Constants.COL_TRANSACTIONS_ID_USER),
                            obj.getInt(Constants.COL_TRANSACTIONS_ID_BANK_ACCOUNT_NUMBER),
                            //getString(R.string.validto) + ": " + obj.getString(Constants.COL_CREDIT_CARDS_EXPIRE_DATE),
                            obj.getString(Constants.COL_TRANSACTIONS_TYPE),
                            obj.getString(Constants.COL_TRANSACTIONS_DIRECTION),
                            obj.getInt(Constants.COL_TRANSACTIONS_REFERENCE_NUMBER),
                            obj.getString(Constants.COL_TRANSACTIONS_CURRENCY),
                            obj.getInt(Constants.COL_TRANSACTIONS_AMOUNT),
                            obj.getString(Constants.COL_TRANSACTIONS_PARTNER_NAME),
                            obj.getString(Constants.COL_TRANSACTIONS_PARTNER_ACCOUNT_NUMBER),
                            obj.getString(Constants.COL_TRANSACTIONS_COMMENT),
                            obj.getString(Constants.COL_TRANSACTIONS_ARRIVED_ON)
                    ));
                }
                adapter = new CustomAdapterTransactions(dataModels, this); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben
                listView.setAdapter(adapter);
                SwipeRefreshLayout.setRefreshing(false);
                //recreate();
            }
        } catch (JSONException e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
            e.printStackTrace(); // itt értékes infót kapok arról, hogy mi nem jó!!!
        }
    }*/

    public void getBundle(){
        try{
        /*Intent i = getIntent();
        userFirstName = i.getStringExtra("user_firstname");
        userId = Integer.parseInt(i.getStringExtra("user_id"));*/

            Bundle b = getIntent().getExtras();
            userFirstName = b.getString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME);
            userId = Integer.parseInt(b.getString(Constants.SHAREDPREFERENCES_USER_ID));
            userLastName = b.getString(Constants.SHAREDPREFERENCES_USER_LASTNAME);
            userLastLoginTime = b.getString(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME);
            spinnerSelectedItemNo = Integer.parseInt(b.getString(Constants.SHAREDPREFERENCES_ACCOUNT_NUMBER_POSITION));
            //Toast.makeText(this, "" + spinnerSourceAccount, Toast.LENGTH_SHORT).show();
            vanAtadvaErtek = true;
        }
        catch (Exception e){ // ez a blokk azért került ide, mert nem akart működni a bundle-s paraméter átadás. Már működik, így ez a blokk csak biztonsági szempontból maradt itt.
            SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
            userFirstName = sp.getString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, "");
            userId = Integer.parseInt(sp.getString(Constants.SHAREDPREFERENCES_USER_ID, ""));
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
        MenuItem navMenuAccountHistory = navMenu.findItem(R.id.Account_history);
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
                                intent = new Intent(BankAccountHistoryActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(BankAccountHistoryActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(BankAccountHistoryActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(BankAccountHistoryActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(BankAccountHistoryActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(BankAccountHistoryActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(BankAccountHistoryActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(BankAccountHistoryActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(BankAccountHistoryActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(BankAccountHistoryActivity.this, PinCodeChangeActivity.class);
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

                                intent = new Intent(BankAccountHistoryActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(BankAccountHistoryActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(BankAccountHistoryActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(BankAccountHistoryActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                BankAccountHistoryActivity.this.finish();
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
                                intent = new Intent(BankAccountHistoryActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                break;
                        }
                        if (!ignore){
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
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



    /**
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
