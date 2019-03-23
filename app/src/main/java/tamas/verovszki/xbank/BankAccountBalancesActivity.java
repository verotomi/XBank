package tamas.verovszki.xbank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.design.widget.NavigationView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BankAccountBalancesActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate {

    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    boolean loginState = false;
    boolean answer;
    Boolean valasz = false;
    ListView ListViewBankAccount;
    String userFirstName="";
    int userId;
    private SwipeRefreshLayout SwipeRefreshLayout;
    List<Integer> bankAccountNumberIdList = new ArrayList<Integer>();
    int selectedBankAccountNumberId;
    int position = 0;



    //MySQL adatbázishoz
    PerformNetworkRequest request;

    DrawerLayout drawerLayout;
    boolean ignore = false;

    String userLastLoginTime;
    String userLastName;

    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    private SimpleAdapter sa;

    ArrayList<DataModelBankAccounts> dataModels;
    private static CustomAdapterBankAccounts adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleBankAccountBalance)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_bank_account_balances);

        getBundle();
        init();

        getAccountBalances(userId);
        navDrawer();

        dataModels = new ArrayList<>();

        /**
         * Képernyő lehúzás hatására frissítés
         */
        SwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.darkgreen3, R.color.darkgreen);
        SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SwipeRefreshLayout.setRefreshing(true);

                //recreate();
                getAccountBalances(userId);

                /*(new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getRecurringTransfers(userId);

                        SwipeRefreshLayout.setRefreshing(false);
                    }
                },3000);*/
            }
        });

        ListViewBankAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
                //Creating the instance of PopupMenu
                ContextThemeWrapper ctw = new ContextThemeWrapper(BankAccountBalancesActivity.this, R.style.CustomPopupTheme); // saját style használata a popup menühöz. A többi a style-ban!
                //PopupMenu popup = new PopupMenu(BankAccountBalancesActivity.this, view);
                PopupMenu popup = new PopupMenu(ctw, view); // saját style használata a popup menühöz
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu_bankaccountbalances, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent;
                        selectedBankAccountNumberId = dataModels.get(position).getId();
                        switch (item.getItemId()){
                            case R.id.one:
                                intent = new Intent(BankAccountBalancesActivity.this, TransferMoneyOneTimeActivity.class);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                                intent.putExtra(Constants.SHAREDPREFERENCES_ACCOUNT_NUMBER_POSITION, String.valueOf(position));
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között

                                //finish();
                                break;
                            case R.id.two:
                                intent = new Intent(BankAccountBalancesActivity.this, TransferMoneyRecurringActivity.class);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                                intent.putExtra(Constants.SHAREDPREFERENCES_ACCOUNT_NUMBER_POSITION, String.valueOf(position));
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között

                                //finish();
                                break;
                            case R.id.three:
                                intent = new Intent(BankAccountBalancesActivity.this, SavingsNewActivity.class);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                                intent.putExtra(Constants.SHAREDPREFERENCES_ACCOUNT_NUMBER_POSITION, String.valueOf(position));
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között

                                //finish();
                                break;
                            case R.id.four:
                                intent = new Intent(BankAccountBalancesActivity.this, BankAccountHistoryActivity.class);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                                intent.putExtra(Constants.SHAREDPREFERENCES_ACCOUNT_NUMBER_POSITION, String.valueOf(position));
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között

                                //finish();
                                break;
                            case R.id.five:
                                intent = new Intent(BankAccountBalancesActivity.this, BankAccountStatementsActivity.class);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                                intent.putExtra(Constants.SHAREDPREFERENCES_ACCOUNT_NUMBER_POSITION, String.valueOf(position));
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között

                                //finish();
                                break;
                            case R.id.six:
                                //Toast.makeText(BankAccountBalancesActivity.this, "Számla részletes adatai " + dataModels.get(position).getId(), Toast.LENGTH_SHORT).show();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constants.SHAREDPREFERENCES_BANK_ACCOUNT, dataModels.get(position)); // ehhez az osztálynak implementálni kell a Serializable interfészt!
                                intent = new Intent(BankAccountBalancesActivity.this, BankAccountDetailsActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között

                                //finish();
                                break;
                        }

                        //Toast.makeText(BankAccountBalancesActivity.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        ListViewBankAccount = findViewById(R.id.ListViewBankAccountBalance);
        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        drawerLayout = findViewById(R.id.drawer_layout);

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
        MenuItem navMenuBalanceQuery = navMenu.findItem(R.id.Balance_query);
        navMenuBalanceQuery.setEnabled(false);

        ColorStateList cs1 = Utility.navigationDrawerColors();
        navigationView.setItemTextColor(cs1);

        // Utility.setMenuTextColor(BankAccountBalancesActivity.this, navigationView, R.id.One_time_transfer, getResources().getColor(R.color.red)); // nem működik
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
                                intent = new Intent(BankAccountBalancesActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                            case R.id.Balance_query:
                                intent = new Intent(BankAccountBalancesActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(BankAccountBalancesActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(BankAccountBalancesActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(BankAccountBalancesActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(BankAccountBalancesActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(BankAccountBalancesActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(BankAccountBalancesActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(BankAccountBalancesActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(BankAccountBalancesActivity.this, PinCodeChangeActivity.class);
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

                                intent = new Intent(BankAccountBalancesActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(BankAccountBalancesActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(BankAccountBalancesActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be

                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(BankAccountBalancesActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                BankAccountBalancesActivity.this.finish();
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
                                intent = new Intent(BankAccountBalancesActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mobilbankmainactivity_without_name, menu); // elemek hozzáadása a toolbarhoz
        menuitem_logout_3 = menu.findItem(R.id.logout_toolbar_icon_3);
        //menuitem_toolbar_name = menu.findItem(R.id.logout_toolbar_name);
        loginState = Utility.getLoginState(BankAccountBalancesActivity.this);

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
            CustomDialogClass cdd = new CustomDialogClass(BankAccountBalancesActivity.this);
            cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                @Override
                public void onYes(String title) {
                    menuitem_logout_3.setEnabled(false);
                    menuitem_logout_3.setVisible(false);
                    //menuitem_toolbar_name.setTitle("");
                    //menuitem_toolbar_name.setVisible(false);
                    loginState = false;
                    Utility.saveLoginState(BankAccountBalancesActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be

                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(BankAccountBalancesActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            BankAccountBalancesActivity.this.finish();
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
        String returnedMessage = "";
        try {
            JSONObject object = new JSONObject(result);
            returnedMessage = object.getString(Constants.RESPONSE_MESSAGE);
        } catch (JSONException e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
        switch (returnedMessage) {
            case Constants.RESPONSE_MESSAGE_ACCOUNTLIST_SUCCESSFUL:


                //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                HashMap<String, String> item;    //Used to link data to lines
                list.clear();
                dataModels.clear();
                JSONArray accountList;
                try {
                    //JSONObject object = new JSONObject(request.getResult());
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
                    //item.put( "line2", obj.getString(Constants.COL_BANK_ACCOUNTS_TYPE) + " " + getString(R.string.account));
                    item.put("line2", obj.getString(Constants.COL_BANK_ACCOUNTS_TYPE));
                    item.put("line3", getString(R.string.balance));
                    item.put("line4", new DecimalFormat("#,##0.00").format(obj.getInt(Constants.COL_BANK_ACCOUNTS_BALANCE)));
                    item.put("line5", obj.getString(Constants.COL_BANK_ACCOUNTS_CURRENCY));
                    list.add(item);*/
                        }
                        adapter = new CustomAdapterBankAccounts(dataModels, this);
                        ListViewBankAccount.setAdapter(adapter);

                        //Use an Adapter to link data to Views
                    /*sa = new SimpleAdapter(this, list,
                            R.layout.row_item_bank_account_balance,
                            new String[] { "line1","line2", "line3", "line4", "line5" },
                            new int[] {R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e});
                    //Link the Adapter to the list
                    ListViewBankAccount.setAdapter(sa);*/

                    /*ListViewBankAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            //ide majd egy popup/context menüt tervezek: új átutalás; számlatörténet, számlakivonat(ok)
                        }
                    });
                    ListViewBankAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(BankAccountBalancesActivity.this, " " + i, Toast.LENGTH_SHORT).show();
                        }
                    });*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.RESPONSE_MESSAGE_ACCOUNTLIST_UNSUCCESSFUL:
                Toast.makeText(this, getString(R.string.no_active_account), Toast.LENGTH_SHORT).show();
                break;
        }
        SwipeRefreshLayout.setRefreshing(false);
    }

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
        }
        catch (Exception e){ // ez a blokk azért került ide, mert nem akart működni a bundle-s paraméter átadás. Már működik, így ez a blokk csak biztonsági szempontból maradt itt.
            SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
            userFirstName = sp.getString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, "");
            userId = Integer.parseInt(sp.getString(Constants.SHAREDPREFERENCES_USER_ID, ""));
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

    @Override
    protected void onRestart() {
        getAccountBalances(userId);
        super.onRestart();
    }
}
