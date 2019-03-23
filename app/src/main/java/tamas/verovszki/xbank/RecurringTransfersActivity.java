package tamas.verovszki.xbank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class RecurringTransfersActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate {

    private SwipeRefreshLayout SwipeRefreshLayout;
    ListView ListViewRecurringTransfers;
    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    FloatingActionButton floatingActionButton;

    boolean loginState = false;
    boolean answer;
    Boolean valasz = false;
    String userFirstName="";
    int userId;

    //MySQL adatbázishoz

    PerformNetworkRequest request;

    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    private SimpleAdapter sa;

    List<DataModelRecurringTransfers> recurringTransfersList = new ArrayList<DataModelRecurringTransfers>();

    DrawerLayout drawerLayout;
    String userLastLoginTime;
    String userLastName;
    boolean ignore = false;
    boolean isBankAccount = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleRecurringTransfers)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_recurring_transfers);

        getBundle(); // átadott változók beolvasása
        init();
        navDrawer();

        setSupportActionBar(toolbar); // Toolbar megjelenítése

        getRecurringTransfers(userId);
        getAccountBalances(userId);


        /**
         * Képernyő lehúzás hatására frissítés
         */
        SwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.darkgreen3, R.color.darkgreen);
        SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SwipeRefreshLayout.setRefreshing(true);
                getRecurringTransfers(userId);
                // a SwipeRefreshLayout.setRefreshing(false)akkor kerül végrehajtásra, ha a mySQL kommunikáció lezajlott
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBankAccount){
                    Utility.saveLoginState(RecurringTransfersActivity.this, true); // bejelentkezve változó beállítása, mentése sharedpreferences-be
                    Intent intent = new Intent(RecurringTransfersActivity.this, TransferMoneyRecurringActivity.class);
                    intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                    intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                    intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                    intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);

                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
                    //finish();
                }
                else{
                    Toast.makeText(RecurringTransfersActivity.this, getString(R.string.no_active_account), Toast.LENGTH_SHORT).show();
                }


            }
        });

    }

    public void init(){
        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        ListViewRecurringTransfers = findViewById(R.id.ListViewRecurringTransfers);
        floatingActionButton = findViewById(R.id.FloatingActionButton);
        drawerLayout = findViewById(R.id.drawer_layout);
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
                    Utility.saveLoginState(RecurringTransfersActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(RecurringTransfersActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            RecurringTransfersActivity.this.finish();
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
    private void getRecurringTransfers(int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_RECURRING_TRANSFERS_ID_USER, String.valueOf(user_id));
            request = new PerformNetworkRequest(Constants.URL_READ_RECURRING_TRANSFERS, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            SwipeRefreshLayout.setRefreshing(false);
        }


    }

    private void deleteRecurringTransfer(String id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_RECURRING_TRANSFERS_ID, String.valueOf(id));
            request = new PerformNetworkRequest(Constants.URL_DELETE_RECURRING_TRANSFER, params, Constants.CODE_POST_REQUEST, this);
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
            case Constants.RESPONSE_MESSAGE_ACCOUNTLIST_UNSUCCESSFUL:
                //Toast.makeText(this, "Önnek nincs aktív bankszámlája!", Toast.LENGTH_SHORT).show();
                isBankAccount = false;
                break;
            case Constants.RESPONSE_MESSAGE_RECURRRING_TRANSFER_LIST_SUCCESSFUL:

                //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                HashMap<String, String> item;    //Used to link data to lines
                list.clear();
                recurringTransfersList.clear();

                JSONArray recurringTransfersListJSON;
                try {
                    //JSONObject object = new JSONObject(request.getResult());
                    JSONObject object = new JSONObject(result);
                    recurringTransfersListJSON = object.getJSONArray(Constants.RESPONSE_RECURRING_TRANSFERS);
                    if (!object.getBoolean(Constants.RESPONSE_ERROR)) {
                        for (int i = 0; i < recurringTransfersListJSON.length(); i++) {
                            JSONObject obj = recurringTransfersListJSON.getJSONObject(i);
                            String recurringTransferFrequency = "";
                            String recurringTransferFrequencyDays = "";
                            switch (obj.getString(Constants.COL_RECURRING_TRANSFERS_FREQUENCY)){
                                case Constants.EVERY_DAY:
                                    recurringTransferFrequency = getString(R.string.every_day);
                                    break;
                                case Constants.EVERY_WEEK:
                                    recurringTransferFrequency = getString(R.string.every_week);
                                    break;
                                case Constants.EVERY_MONTH:
                                    recurringTransferFrequency = getString(R.string.every_month);
                                    break;
                                default:
                                    recurringTransferFrequency = "";
                                    break;
                            }
                            switch (obj.getString(Constants.COL_RECURRING_TRANSFERS_DAYS)){
                                case Constants.MONDAY:
                                    recurringTransferFrequencyDays = getString(R.string.on_mondays);
                                    break;
                                case Constants.TUESDAY:
                                    recurringTransferFrequencyDays = getString(R.string.on_tuesdays);
                                    break;
                                case Constants.WEDNESDAY:
                                    recurringTransferFrequencyDays = getString(R.string.on_wednesdays);
                                    break;
                                case Constants.THURSDAY:
                                    recurringTransferFrequencyDays = getString(R.string.on_thursdays);
                                    break;
                                case Constants.FRIDAY:
                                    recurringTransferFrequencyDays = getString(R.string.on_fridays);
                                    break;
                                case Constants.SATURDAY:
                                    recurringTransferFrequencyDays = getString(R.string.on_saturdays);
                                    break;
                                case Constants.SUNDAY:
                                    recurringTransferFrequencyDays = getString(R.string.on_sundays);
                                    break;
                                case "1":
                                    recurringTransferFrequencyDays = getString(R.string.on_first);
                                    break;
                                case "2":
                                    recurringTransferFrequencyDays = getString(R.string.on_second);
                                    break;
                                case "3":
                                    recurringTransferFrequencyDays = getString(R.string.on_third);
                                    break;
                                case "4":
                                    recurringTransferFrequencyDays = getString(R.string.on_fourth);
                                    break;
                                case "5":
                                    recurringTransferFrequencyDays = getString(R.string.on_fifth);
                                    break;
                                case "6":
                                    recurringTransferFrequencyDays = getString(R.string.on_sixth);
                                    break;
                                case "7":
                                    recurringTransferFrequencyDays = getString(R.string.on_seventh);
                                    break;
                                case "8":
                                    recurringTransferFrequencyDays = getString(R.string.on_eighth);
                                    break;
                                case "9":
                                    recurringTransferFrequencyDays = getString(R.string.on_nineth);
                                    break;
                                case "10":
                                    recurringTransferFrequencyDays = getString(R.string.on_tenth);
                                    break;
                                case "11":
                                    recurringTransferFrequencyDays = getString(R.string.on_eleventh);
                                    break;
                                case "12":
                                    recurringTransferFrequencyDays = getString(R.string.on_twelveth);
                                    break;
                                case "13":
                                    recurringTransferFrequencyDays = getString(R.string.on_thirteenth);
                                    break;
                                case "14":
                                    recurringTransferFrequencyDays = getString(R.string.on_fourteenth);
                                    break;
                                case "15":
                                    recurringTransferFrequencyDays = getString(R.string.on_fifteenth);
                                    break;
                                case "16":
                                    recurringTransferFrequencyDays = getString(R.string.on_sixteenth);
                                    break;
                                case "17":
                                    recurringTransferFrequencyDays = getString(R.string.on_seventeenth);
                                    break;
                                case "18":
                                    recurringTransferFrequencyDays = getString(R.string.on_eighteenth);
                                    break;
                                case "19":
                                    recurringTransferFrequencyDays = getString(R.string.on_nineteenth);
                                    break;
                                case "20":
                                    recurringTransferFrequencyDays = getString(R.string.on_twentieth);
                                    break;
                                case "21":
                                    recurringTransferFrequencyDays = getString(R.string.on_twenty_first);
                                    break;
                                case "22":
                                    recurringTransferFrequencyDays = getString(R.string.on_twenty_second);
                                    break;
                                case "23":
                                    recurringTransferFrequencyDays = getString(R.string.on_twenty_third);
                                    break;
                                case "24":
                                    recurringTransferFrequencyDays = getString(R.string.on_twenty_fourth);
                                    break;
                                case "25":
                                    recurringTransferFrequencyDays = getString(R.string.on_twenty_fifth);
                                    break;
                                case "26":
                                    recurringTransferFrequencyDays = getString(R.string.on_twenty_sixth);
                                    break;
                                case "27":
                                    recurringTransferFrequencyDays = getString(R.string.on_twenty_seventh);
                                    break;
                                case "28":
                                    recurringTransferFrequencyDays = getString(R.string.on_twenty_eighth);
                                    break;
                                case "29":
                                    recurringTransferFrequencyDays = getString(R.string.on_twenty_nineth);
                                    break;
                                case "30":
                                    recurringTransferFrequencyDays = getString(R.string.on_thirtieth);
                                    break;
                                case "31":
                                    recurringTransferFrequencyDays = getString(R.string.on_thirty_first);
                                    break;
                                default:
                                    recurringTransferFrequencyDays = "";
                                    break;
                            }
                            DataModelRecurringTransfers recurringTransfers = new DataModelRecurringTransfers(
                                    obj.getInt(Constants.COL_RECURRING_TRANSFERS_ID),
                                    obj.getInt(Constants.COL_RECURRING_TRANSFERS_ID_USER),
                                    obj.getInt(Constants.COL_RECURRING_TRANSFERS_ID_BANK_ACCOUNT_NUMBER),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_NAME),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_TYPE),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_DIRECTION),
                                    obj.getInt(Constants.COL_RECURRING_TRANSFERS_REFERENCE_NUMBER),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_CURRENCY),
                                    obj.getDouble(Constants.COL_RECURRING_TRANSFERS_AMOUNT),
                                    //Double.parseDouble(obj.getString(Constants.COL_RECURRING_TRANSFERS_AMOUNT)),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_PARTNER_NAME),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_PARTNER_ACCOUNT_NUMBER),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_COMMENT),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_ARRIVED_ON),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_VALUE_DATE),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_STATUS),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_LAST_FULFILLED),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_FREQUENCY),
                                    obj.getString(Constants.COL_RECURRING_TRANSFERS_DAYS)
                            );
                            recurringTransfersList.add(recurringTransfers);

                            item = new HashMap<String, String>();
                            /*
                            item.put( "line2", obj.getString(Constants.COL_RECURRING_TRANSFERS_PARTNER_NAME));
                            item.put( "line3", obj.getString(Constants.COL_RECURRING_TRANSFERS_FREQUENCY) + " ");
                            item.put( "line4", obj.getString(Constants.COL_RECURRING_TRANSFERS_DAYS));
                            item.put( "line5", new DecimalFormat("#,##0.00").format(obj.getInt(Constants.COL_RECURRING_TRANSFERS_AMOUNT)));
                            item.put( "line6", obj.getString(Constants.COL_RECURRING_TRANSFERS_CURRENCY));
                            item.put( "line7", obj.getString(Constants.COL_RECURRING_TRANSFERS_STATUS));
                            item.put( "line8", obj.getString(Constants.COL_RECURRING_TRANSFERS_ID)); //  rejtett, nem látható. azért kell, hogy ez alapján tudjam majd törölni, ha kell!*/

                            item.put("line1", recurringTransfers.getName());
                            item.put("line2", recurringTransfers.getPartner_name());
                            item.put("line3", recurringTransferFrequency + " ");
                            item.put("line4", recurringTransferFrequencyDays);
                            item.put("line5", new DecimalFormat("#,##0.00").format(recurringTransfers.getAmount()));
                            item.put("line6", recurringTransfers.getCurrency());
                            item.put("line7", recurringTransfers.getStatus().equals(Constants.ACTIVE) ? getString(R.string.active) : getString(R.string.inactive));
                            item.put("line8", String.valueOf(recurringTransfers.getId())); // rejtett, nem látható. azért kell, hogy ez alapján tudjam majd törölni, ha kell!
                            list.add(item);

                            //Use an Adapter to link data to Views
                            sa = new SimpleAdapter(this, list,
                                    R.layout.row_item_recurring_transfers,
                                    new String[]{"line1", "line2", "line3", "line4", "line5", "line6", "line7", "line8"},
                                    new int[]{R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e, R.id.line_f, R.id.line_g, R.id.line_h});
                            //Link the Adapter to the list
                            ((ListView) findViewById(R.id.ListViewRecurringTransfers)).setAdapter(sa);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        ListViewRecurringTransfers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(RecurringTransfersActivity.this, " " + recurringTransfersList.get(i).getPartner_name(), Toast.LENGTH_SHORT).show();
                // Így tudom kinyerni az id-
                //
                //
                //
                //
                // t!!!
                /*TextView textView = (TextView) view.findViewById(R.id.line_h);
                String id = textView.getText().toString();*/

                Utility.saveLoginState(RecurringTransfersActivity.this, true); // bejelentkezve változó beállítása, mentése sharedpreferences-be
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.SHAREDPREFERENCES_RECURRING_TRANSFER, recurringTransfersList.get(i)); // ehhez az osztálynak implementálni kell a Serializable interfészt!
                Intent intent = new Intent(RecurringTransfersActivity.this, EditRecurringTransferActivity.class);

                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                intent.putExtras(bundle);
                startActivity(intent);
                //finish();*/

            }
        });

        /**
         * Korábban összeakadt a click és a longclick (mindkettő elindult), ezért:
         * - elmentem az m1 változóba a listenert
         * - amig tart a longclick, letiltom a clicket.
         * - a longclick végén visszaállítom m1-ből a listenert.
         */
        final ListView.OnItemClickListener m1 = ListViewRecurringTransfers.getOnItemClickListener();
        ListViewRecurringTransfers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, int i, long l) {
                ListViewRecurringTransfers.setOnItemClickListener(null);
                CustomDialogClass cdd = new CustomDialogClass(RecurringTransfersActivity.this, getString(R.string.delete_order));
                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                    @Override
                    public void onYes(String title) {
                        TextView textView = (TextView) view.findViewById(R.id.line_h);
                        String id = textView.getText().toString();
                        deleteRecurringTransfer(id);
                        getRecurringTransfers(userId);
                    }

                    @Override
                    public void onNo(String title) {

                    }
                });
                cdd.show();
                ListViewRecurringTransfers.setOnItemClickListener(m1);
                return true;
            }
        });

        SwipeRefreshLayout.setRefreshing(false);
    }

    public void getBundle(){
        try{
        /*Intent i = getIntent();
        userFirstName = i.getStringExtra("user_firstname");
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
                        // menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        Intent intent;
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.Overviewe:
                                intent = new Intent(RecurringTransfersActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(RecurringTransfersActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(RecurringTransfersActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(RecurringTransfersActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(RecurringTransfersActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(RecurringTransfersActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(RecurringTransfersActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(RecurringTransfersActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(RecurringTransfersActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(RecurringTransfersActivity.this, PinCodeChangeActivity.class);
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

                                intent = new Intent(RecurringTransfersActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(RecurringTransfersActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(RecurringTransfersActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(RecurringTransfersActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                RecurringTransfersActivity.this.finish();
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
                                intent = new Intent(RecurringTransfersActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
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
        getRecurringTransfers(userId);
        super.onRestart();
    }
}

