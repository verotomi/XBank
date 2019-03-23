package tamas.verovszki.xbank;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MobilBankMainActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate {

    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    private android.support.v4.widget.SwipeRefreshLayout SwipeRefreshLayout;

    boolean loginState = false;
    boolean answer;
    Boolean valasz = false;
    String userFirstName="";
    int userId;
    DrawerLayout drawerLayout;
    boolean ignore = false;
    TextView TextViewLastLogin;
    TextView TextViewName;
    String userLastLoginTime;
    String userLastName;
    ListView ListViewBankAccounts;
    ListView ListViewSavings;
    ListView ListViewCreditCards;
    LinearLayout LinearLayout1;
    ConstraintLayout Constlay1;
    ConstraintLayout Constlay2;
    ConstraintLayout Constlay3;
    TextView TextViewEmptyList1;
    TextView TextViewEmptyList2;
    TextView TextViewEmptyList3;


    //MySQL adatbázishoz

    PerformNetworkRequest request;

    ArrayList<HashMap<String,String>> listBankAccounts = new ArrayList<HashMap<String,String>>();
    List<DataModelSavings> savingsList = new ArrayList<DataModelSavings>();
    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    private SimpleAdapter sa;

    ArrayList<DataModelSavings> dataModelsSavings;
    ArrayList<DataModelCreditCards> dataModelsCreditCards;
    private static CustomAdapterCreditCards2 adapterCreditCards;

    ArrayList<DataModelBankAccounts> dataModels;
    private static CustomAdapterBankAccounts2 adapter;

    public static Activity mobilBankMainActivity; // ahhoz kell, hogy máshonnan be tudjam zárni ezt az activityt


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleNetBankMain)); // Fejléc átállítása a kiválasztott nyelvre. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_mobil_bank_main);
        getBundle(); // átadott változók beolvasása
        Toast.makeText(this, getString(R.string.welcome) + " "  + userFirstName + "!", Toast.LENGTH_SHORT).show();
        init();

        mobilBankMainActivity = this; // ahhoz kell, hogy máshonnan be tudjam zárni ezt az activityt


        getAccountBalances(userId);
        getSavings(userId);
        getCreditCards(userId);

        dataModels = new ArrayList<>();

        SwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.darkgreen3, R.color.darkgreen);
        SwipeRefreshLayout.setOnRefreshListener(new android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SwipeRefreshLayout.setRefreshing(true);
                getAccountBalances(userId);
                getSavings(userId);
                getCreditCards(userId);
                // a SwipeRefreshLayout.setRefreshing(false)akkor kerül végrehajtásra, ha a mySQL kommunikáció lezajlott
            }
        });

        dataModelsSavings = new ArrayList<>();
        dataModelsCreditCards = new ArrayList<>();
        navDrawer();
        loginState = Utility.getLoginState(MobilBankMainActivity.this); // "belépve" állapot beolvasása SharedPreferencesből
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.mobilebank_mainmenu_list, android.R.layout.simple_list_item_1);

        Constlay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MobilBankMainActivity.this, BankAccountBalancesActivity.class);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
            }
        });

        Constlay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MobilBankMainActivity.this, SavingsActivity.class);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
            }
        });

        Constlay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MobilBankMainActivity.this, CreditCardsActivity.class);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
            }
        });

        ListViewBankAccounts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MobilBankMainActivity.this, BankAccountBalancesActivity.class);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
            }
        });

        ListViewSavings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MobilBankMainActivity.this, SavingsActivity.class);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
            }
        });

        ListViewCreditCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MobilBankMainActivity.this, CreditCardsActivity.class);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
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

        // aktuális menüpont inaktívvá tétele
        Menu navMenu = navigationView.getMenu();
        MenuItem navMenuOverview = navMenu.findItem(R.id.Overviewe);
        navMenuOverview.setEnabled(false);
        // inaktívvá tett menüpont átszinezése
        ColorStateList cs1 = Utility.navigationDrawerColors();
        navigationView.setItemTextColor(cs1);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        // menuItem.setChecked(true);
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        Intent intent;
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.Overviewe:
                                intent = new Intent(MobilBankMainActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(MobilBankMainActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(MobilBankMainActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(MobilBankMainActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(MobilBankMainActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(MobilBankMainActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(MobilBankMainActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(MobilBankMainActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(MobilBankMainActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(MobilBankMainActivity.this, PinCodeChangeActivity.class);
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

                                intent = new Intent(MobilBankMainActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(MobilBankMainActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        Utility.saveLoginState(MobilBankMainActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        loginState = false;
                                        Intent intent = new Intent(MobilBankMainActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        //finish();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                finish();
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
                                intent = new Intent(MobilBankMainActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                break;
                        }
                        if (!ignore){
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
                        }
                        ignore = false;

                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();

                        return true;
                    }
                });

        setSupportActionBar(toolbar); // Toolbar megjelenítése
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mobilbankmainactivity_without_name, menu); // elemek hozzáadása a toolbarhoz
        menuitem_logout_3 = menu.findItem(R.id.logout_toolbar_icon_3);
        //menuitem_toolbar_name = menu.findItem(R.id.logout_toolbar_name);
        loginState = Utility.getLoginState(MobilBankMainActivity.this);

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
        // Toast.makeText(this, "loginstate: " + loginState, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!

        int id = item.getItemId();
        if (id == R.id.logout_toolbar_icon_3){
            //valasz = Utility.askForConfirmExit(MobilBankMainActivity.this);

            CustomDialogClass cdd = new CustomDialogClass(MobilBankMainActivity.this);
            cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                @Override
                public void onYes(String title) {
                    menuitem_logout_3.setEnabled(false);
                    menuitem_logout_3.setVisible(false);
                    //menuitem_toolbar_name.setTitle("");
                    //menuitem_toolbar_name.setVisible(false);
                    Utility.saveLoginState(MobilBankMainActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    loginState = false;
                    Intent intent = new Intent(MobilBankMainActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    //finish();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            finish();
                        }
                    }, 3000);
                }

                @Override
                public void onNo(String title) {

                }
            });
            cdd.show();

            //valasz = Utility.askForConfirmExit2(MobilBankMainActivity.this);
            //Toast.makeText(this, "Válasz " + valasz.toString(), Toast.LENGTH_SHORT).show();
            /*if (valasz){
                menuitem_logout_3.setEnabled(false);
                menuitem_logout_3.setVisible(false);
                menuitem_toolbar_name.setTitle("");
                menuitem_toolbar_name.setVisible(false);

                Utility.saveLoginState(MobilBankMainActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                loginState = false;

                // Toast.makeText(this, "Vissza a főmenübe!", Toast.LENGTH_SHORT).show();
                // Új Activity
                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class); emiatt többször elindult a mainactivity!

                /* átraktam külső metódusba :)
                Intent intent = new Intent(MobilBankMainActivity.this, MainActivity.class); // új Activity példányosítása
                startActivity(intent); // Új Activity elindítása
                 *
                finish();
            }*/
            return true;
        }
        if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        ListViewBankAccounts = findViewById(R.id.ListViewBankAccounts);
        ListViewSavings = findViewById(R.id.ListViewSavings);
        ListViewCreditCards = findViewById(R.id.ListViewCreditCards);

        drawerLayout = findViewById(R.id.drawer_layout);
        TextViewLastLogin = findViewById(R.id.TextViewLastLogin);
        TextViewName = findViewById(R.id.TextViewName);

        Constlay1 = findViewById(R.id.ConstLay1);
        Constlay2 = findViewById(R.id.ConstLay2);
        Constlay3 = findViewById(R.id.ConstLay3);

        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);

        TextViewEmptyList1 = findViewById(R.id.TextViewEmptyList1);
        TextViewEmptyList2 = findViewById(R.id.TextViewEmptyList2);
        TextViewEmptyList3 = findViewById(R.id.TextViewEmptyList3);


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

    /**
     *
     * Aszinkron művelet!
     * @param user_id
     */
    private void getSavings(int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_SAVINGS_ID_USER, String.valueOf(user_id));
            request = new PerformNetworkRequest(Constants.URL_READ_SAVINGS, params, Constants.CODE_POST_REQUEST, this);
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
    private void getCreditCards(int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_CREDIT_CARDS_ID_USER, String.valueOf(user_id));
            request = new PerformNetworkRequest(Constants.URL_READ_CREDIT_CARDS, params, Constants.CODE_POST_REQUEST, this);
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
        String returnedMessage = "";
        try {
            JSONObject object = new JSONObject(result);
            returnedMessage = object.getString(Constants.RESPONSE_MESSAGE);
        } catch (JSONException e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
        switch (returnedMessage) {
            case Constants.RESPONSE_MESSAGE_ACCOUNTLIST_SUCCESSFUL:
                HashMap<String,String> item;    //Used to link data to lines
                listBankAccounts.clear();
                dataModels.clear();
                JSONArray accountList;
                try{
                    JSONObject object = new JSONObject(result);
                    accountList = object.getJSONArray(Constants.RESPONSE_ACCOUNT_BALANCES);
                    if (!object.getBoolean(Constants.RESPONSE_ERROR)) {
                        for (int i = 0; i < accountList.length(); i++) {
                            JSONObject obj = accountList.getJSONObject(i);
                            String bankAccountType = "";
                            String bankAccountStatus = "";
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
                            switch (obj.getString(Constants.COL_BANK_ACCOUNTS_STATUS)){
                                case Constants.ACTIVE:
                                    bankAccountStatus = getString(R.string.active);
                                    break;
                                case Constants.INACTIVE:
                                    bankAccountStatus = getString(R.string.inactive);
                                    break;
                                default:
                                    bankAccountStatus = "";
                                    break;
                            }


                            dataModels.add(new DataModelBankAccounts(
                                    obj.getInt(Constants.COL_BANK_ACCOUNTS_ID),
                                    obj.getInt(Constants.COL_BANK_ACCOUNTS_ID_USER),
                                    obj.getString(Constants.COL_BANK_ACCOUNTS_NUMBER),
                                    bankAccountType,
                                    obj.getString(Constants.COL_BANK_ACCOUNTS_CURRENCY),
                                    obj.getString(Constants.COL_BANK_ACCOUNTS_CURRENCY).equals(getString(R.string.forint)) ? obj.getInt(Constants.COL_BANK_ACCOUNTS_BALANCE) : obj.getDouble(Constants.COL_BANK_ACCOUNTS_BALANCE),
                                    bankAccountStatus,
                                    obj.getString(Constants.COL_BANK_ACCOUNTS_CREATED_ON),
                                    obj.getString(Constants.COL_BANK_ACCOUNTS_UPDATED_ON),
                                    obj.getString(Constants.COL_BANK_ACCOUNTS_USER_FIRSTNAME),
                                    obj.getString(Constants.COL_BANK_ACCOUNTS_USER_LASTNAME)
                            ));

                            /*
                            item = new HashMap<String, String>();
                            item.put("line1", obj.getString(Constants.COL_BANK_ACCOUNTS_NUMBER));
                            item.put("line2", obj.getString(Constants.COL_BANK_ACCOUNTS_TYPE));
                            item.put("line3", getString(R.string.balance));
                            item.put("line4", new DecimalFormat("#,##0.00").format(obj.getInt(Constants.COL_BANK_ACCOUNTS_BALANCE)));
                            item.put("line5", obj.getString(Constants.COL_BANK_ACCOUNTS_CURRENCY));
                            listBankAccounts.add(item);*/
                        }
                            //Use an Adapter to link data to Views
                        /*
                        sa = new SimpleAdapter(this, listBankAccounts,
                                    R.layout.row_item_bank_account_balance_2,
                                    new String[] { "line1","line2", "line3", "line4", "line5" },
                                    new int[] {R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e});
                            //Link the Adapter to the list
                            ListViewBankAccounts.setAdapter(sa);*/
                        try{
                            TextViewEmptyList1.setVisibility(View.INVISIBLE);
                            //ListViewBankAccounts.setVisibility(View.INVISIBLE);
                        }
                        catch(Exception e){
                        }

                        adapter = new CustomAdapterBankAccounts2(dataModels, this);
                        ListViewBankAccounts.setAdapter(adapter);
                            //SwipeRefreshLayout.setRefreshing(false);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();

                }
                break;
            case Constants.RESPONSE_MESSAGE_SAVINGS_LIST_SUCCESSFUL:
                HashMap<String,String> item2;    //Used to link data to lines
                savingsList.clear();
                list.clear();
                JSONArray savingsListJSON;
                try {
                    JSONObject object = new JSONObject(result);
                    savingsListJSON = object.getJSONArray(Constants.RESPONSE_SAVINGS);
                    if (!object.getBoolean(Constants.RESPONSE_ERROR)) {
                        for (int i = 0; i < savingsListJSON.length(); i++) {
                            JSONObject obj = savingsListJSON.getJSONObject(i);
                            String savingStatus = "";
                            String savingType = "";
                            switch (obj.getString(Constants.COL_SAVINGS_STATUS)){
                                case Constants.ACTIVE:
                                    savingStatus = getString(R.string.active);
                                    break;
                                case Constants.BREAKED:
                                    savingStatus = getString(R.string.breaked);
                                    break;
                                case Constants.EXPIRED:
                                    savingStatus = getString(R.string.expired);
                                    break;
                                default:
                                    savingStatus = "";
                                    break;
                            }
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
                            DataModelSavings savings = new DataModelSavings(
                                    obj.getInt(Constants.COL_SAVINGS_ID),
                                    obj.getInt(Constants.COL_SAVINGS_ID_USER),
                                    obj.getInt(Constants.COL_SAVINGS_ID_BANK_ACCOUNT),
                                    obj.getInt(Constants.COL_SAVINGS_ID_TYPE),
                                    obj.getInt(Constants.COL_SAVINGS_AMOUNT),
                                    obj.getString(Constants.COL_SAVINGS_EXPIRE_DATE),
                                    savingStatus,
                                    obj.getInt(Constants.COL_SAVINGS_REFERENCE_NUMBER),
                                    obj.getString(Constants.COL_SAVINGS_ARRIVED_ON),
                                    savingType,
                                    obj.getDouble(Constants.COL_SAVINGS_RATE),
                                    obj.getInt(Constants.COL_SAVINGS_DURATION),
                                    obj.getString(Constants.COL_SAVINGS_CURRENCY),
                                    obj.getString(Constants.COL_SAVINGS_BANK_ACCOUNT_NUMBER)
                            );
                            savingsList.add(savings);
                            item = new HashMap<String,String>();
                            //item.put( "line1", savings.getType() + " (" + savings.getDuration() + " " + getString(R.string.days) + ")");
                            item.put( "line1", savings.getType());
                            item.put( "line2", getString(R.string.expire) + " " + savings.getExpire_date());
                            item.put( "line3", getString(R.string.rate) + " ");
                            item.put( "line4", String.valueOf(savings.getRate()) + "%");
                            item.put( "line5", new DecimalFormat("#,##0.00").format(savings.getAmount()));
                            item.put( "line6", String.valueOf(savings.getCurrency()));
                            item.put( "line7", getString(R.string.lockup_amount));
                            item.put( "line8", String.valueOf(savings.getId())); // rejtett, nem látható. azért kell, hogy ez alapján tudjam majd törölni, ha kell!
                            list.add( item );

                            try{
                                TextViewEmptyList2.setVisibility(View.INVISIBLE);
                                //ListViewBankAccounts.setVisibility(View.INVISIBLE);
                            }
                            catch(Exception e){
                            }

                            //Use an Adapter to link data to Views
                            sa = new SimpleAdapter(this, list,
                                    R.layout.row_item_savings_2,
                                    new String[] { "line1", "line2", "line3", "line4", "line5", "line6", "line7", "line8"},
                                    new int[] {R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e, R.id.line_f, R.id.line_g, R.id.line_h});
                            //Link the Adapter to the list
                            ((ListView)findViewById(R.id.ListViewSavings)).setAdapter(sa);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.RESPONSE_MESSAGE_CREDITCARDS_SUCCESSFUL:
                HashMap<String,String> item3;    //Used to link data to lines
                dataModelsCreditCards.clear();
                final JSONArray creditcardListJSON;
                try {
                    //JSONObject object = new JSONObject(request.getResult());
                    JSONObject object = new JSONObject(result);
                    creditcardListJSON = object.getJSONArray(Constants.RESPONSE_CREDIT_CARDS);
                    if (!object.getBoolean(Constants.RESPONSE_ERROR)) {
                        dataModelsCreditCards.clear();
                        for (int i = 0; i < creditcardListJSON.length(); i++) {
                            JSONObject obj = creditcardListJSON.getJSONObject(i);
                            dataModelsCreditCards.add(new DataModelCreditCards(
                                    obj.getInt(Constants.COL_CREDIT_CARDS_ID),
                                    obj.getString(Constants.COL_CREDIT_CARDS_NUMBER),
                                    obj.getString(Constants.COL_CREDIT_CARDS_TYPE),
                                    getString(R.string.validto) + ": " + obj.getString(Constants.COL_CREDIT_CARDS_EXPIRE_DATE),
                                    obj.getString(Constants.COL_CREDIT_CARDS_STATUS),
                                    obj.getInt(Constants.COL_CREDIT_CARDS_LIMIT_ATM),
                                    obj.getInt(Constants.COL_CREDIT_CARDS_LIMIT_POS),
                                    obj.getInt(Constants.COL_CREDIT_CARDS_LIMIT_ONLINE),
                                    obj.getInt(Constants.COL_CREDIT_CARDS_ID_USER)
                            ));

                        }

                        try{
                            TextViewEmptyList3.setVisibility(View.INVISIBLE);
                            //ListViewBankAccounts.setVisibility(View.INVISIBLE);
                        }
                        catch(Exception e){
                        }

                        adapterCreditCards = new CustomAdapterCreditCards2(dataModelsCreditCards, this); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben
                        ListViewCreditCards.setAdapter(adapterCreditCards);
                        //SwipeRefreshLayout.setRefreshing(false);
                        //recreate();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); // itt értékes infót kapok arról, hogy mi nem jó!!!
                }
                break;
            case Constants.RESPONSE_MESSAGE_ACCOUNTLIST_UNSUCCESSFUL:
                try{
                    TextViewEmptyList1.setVisibility(View.VISIBLE);
                    //ListViewBankAccounts.setVisibility(View.INVISIBLE);
                }
                catch(Exception e){
                }
                break;
            case Constants.RESPONSE_MESSAGE_SAVINGS_LIST_UNSUCCESSFUL:
                try{
                    TextViewEmptyList2.setVisibility(View.VISIBLE);
                    //ListViewBankAccounts.setVisibility(View.INVISIBLE);
                }
                catch(Exception e){
                }
                break;
            case Constants.RESPONSE_MESSAGE_CREDITCARDS_UNSUCCESFUL:
                try{
                    TextViewEmptyList3.setVisibility(View.VISIBLE);
                    //ListViewBankAccounts.setVisibility(View.INVISIBLE);
                }
                catch(Exception e){
                }
                break;
        }
        SwipeRefreshLayout.setRefreshing(false);

    }

    /*
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.really_exit)
                    //.setMessage("Are you sure you want to exit?")
                    .setNegativeButton(android.R.string.no, null) // érdekesség: előre definiált szöveg. Nem r.string.xxx, hanem android.R.String.xxx
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            Utility.saveLoginState(MobilBankMainActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                            // MobilBankMainActivity.super.onBackPressed(); // ha ez nincs itt, akkor nem menti el a "kilépve" állapotot az emulátoron! A telefonon meg egyáltalán nem menti el :( Megoldás: onDestroy()-ba raktam ki a "kilépve" változó mentését.
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class); emiatt többször elindult a mainactivity!
                            finish();
                        }
                    }).create().show();
        }
    }*/

    /*@Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom, viewGroup, false);
            new AlertDialog.Builder(this)
            .setView(dialogView)
            .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    Utility.saveLoginState(MobilBankMainActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    finish();
                }
            }).create().show();
        }
    }*/

    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            CustomDialogClass cdd = new CustomDialogClass(MobilBankMainActivity.this);
            cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                @Override
                public void onYes(String title) {
                    Utility.saveLoginState(MobilBankMainActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be

                    Intent intent = new Intent(MobilBankMainActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    //finish();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            finish();
                        }
                    }, 3000);
                }

                @Override
                public void onNo(String title) {

                }
            });
            cdd.show();
        }
    }



    @Override
    protected void onRestart() {
        getAccountBalances(userId);
        getSavings(userId);
        getCreditCards(userId);
        super.onRestart();
    }

}
