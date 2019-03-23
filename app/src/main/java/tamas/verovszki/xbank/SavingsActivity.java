package tamas.verovszki.xbank;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SavingsActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate {

    private android.support.v4.widget.SwipeRefreshLayout SwipeRefreshLayout;
    ListView ListViewSavings;
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

    //List<DataModelSavings> savingsList = new ArrayList<DataModelSavings>();
    ArrayList<DataModelSavings> dataModels;
    private static CustomAdapterSavings adapter;

    int position = 0;

    DrawerLayout drawerLayout;
    String userLastLoginTime;
    String userLastName;
    boolean ignore = false;

    boolean isBankAccount = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleSavings)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_savings);

        getBundle(); // átadott változók beolvasása
        init();
        navDrawer();

        dataModels = new ArrayList<>();

        getSavings(userId);
        getAccountBalances(userId);

        setSupportActionBar(toolbar); // Toolbar megjelenítése


        /**
         * Képernyő lehúzás hatására frissítés
         */
        SwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.darkgreen3, R.color.darkgreen);
        SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SwipeRefreshLayout.setRefreshing(true);
                getSavings(userId);
                // a SwipeRefreshLayout.setRefreshing(false)akkor kerül végrehajtásra, ha a mySQL kommunikáció lezajlott
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBankAccount) {
                    //Toast.makeText(SavingsActivity.this, "You clicked me", Toast.LENGTH_SHORT).show();
                    Utility.saveLoginState(SavingsActivity.this, true); // bejelentkezve változó beállítása, mentése sharedpreferences-be
                    Intent intent = new Intent(SavingsActivity.this, SavingsNewActivity.class);
                    intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                    intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                    intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                    intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);

                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
                    //finish();
                }
                else{
                    Toast.makeText(SavingsActivity.this, getString(R.string.no_active_account), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void init(){
        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        ListViewSavings = findViewById(R.id.ListViewSavings);
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
                    Utility.saveLoginState(SavingsActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(SavingsActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            SavingsActivity.this.finish();
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

    private void breakSaving(String id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_SAVINGS_ID, String.valueOf(id));
            request = new PerformNetworkRequest(Constants.URL_UPDATE_SAVING, params, Constants.CODE_POST_REQUEST, this);
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

            case Constants.RESPONSE_MESSAGE_SAVINGS_LIST_SUCCESSFUL:
                //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                HashMap<String, String> item;    //Used to link data to lines
                list.clear();
                dataModels.clear();
                JSONArray savingsListJSON;
                try {
                    //JSONObject object = new JSONObject(request.getResult());
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
                            dataModels.add(new DataModelSavings(
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
                            ));

                            /*item = new HashMap<String,String>();

                            item.put( "line1", dataModels.get(i).getType() + " " + dataModels.get(i).getDuration());
                            item.put( "line2", "lejárat: " + dataModels.get(i).getExpire_date());
                            item.put( "line3", "kamat: ");
                            item.put( "line4", String.valueOf(dataModels.get(i).getRate()) + "%");
                            item.put( "line5", new DecimalFormat("#,##0.00").format(dataModels.get(i).getAmount()));
                            item.put( "line6", String.valueOf(dataModels.get(i).getCurrency()));
                            item.put( "line7", "Lekötött összeg");
                            item.put( "line8", String.valueOf(dataModels.get(i).getId())); // rejtett, nem látható. azért kell, hogy ez alapján tudjam majd törölni, ha kell!
                            list.add( item );

                            //Use an Adapter to link data to Views
                            sa = new SimpleAdapter(this, list,
                                    R.layout.row_item_savings,
                                    new String[] { "line1","line2", "line3", "line4", "line5", "line6", "line7", "line8"},
                                    new int[] {R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_d, R.id.line_e, R.id.line_f, R.id.line_g, R.id.line_h});
                            //Link the Adapter to the list
                            ((ListView)findViewById(R.id.ListViewSavings)).setAdapter(sa);*/


                            adapter = new CustomAdapterSavings(dataModels, this); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben
                            ListViewSavings.setAdapter(adapter);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Constants.RESPONSE_MESSAGE_SAVINGS_LIST_UNSUCCESSFUL:
                Toast.makeText(this, R.string.no_active_savings, Toast.LENGTH_SHORT).show();
                break;
            case Constants.RESPONSE_MESSAGE_EARLY_WITHDRAWAL_SUCCESSFUL:
                Toast.makeText(this, getString(R.string.deposit_breaked), Toast.LENGTH_SHORT).show();
                break;
            case Constants.RESPONSE_MESSAGE_EARLY_WITHDRAWAL_UNSUCCESFUL:
                Toast.makeText(this, R.string.deposit_break_unsuccessful, Toast.LENGTH_SHORT).show();
                break;
        }

        SwipeRefreshLayout.setRefreshing(false);

        ListViewSavings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { // final-é kellett tenni, mert belső osztályból is el kellett érnem
                //Toast.makeText(SavingsActivity.this, "Részletes adatok: " + i, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SavingsActivity.this, CreditCardsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.SHAREDPREFERENCES_SAVING, dataModels.get(i)); // ehhez az osztálynak implementálni kell a Serializable interfészt!
                intent = new Intent(SavingsActivity.this, SavingDetailsActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);


            }
        });

        /**
         * Korábban összeakadt a click és a longclick (mindkettő elindult), ezért:
         * - elmentem az m1 változóba a listenert
         * - amig tart a longclick, letiltom a clicket.
         * - a longclick végén visszaállítom m1-ből a listenert.
         */
        final ListView.OnItemClickListener m1 = ListViewSavings.getOnItemClickListener();
        ListViewSavings.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View view, final int i, long l) {
                ListViewSavings.setOnItemClickListener(null);
                CustomDialogClass cdd = new CustomDialogClass(SavingsActivity.this, getString(R.string.break_deposit));
                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                    @Override
                    public void onYes(String title) {
                        // Így tudom kinyerni az id-t!!!
                        //TextView textView = (TextView) view.findViewById(R.id.line_h);
                        //String id = textView.getText().toString();

                        breakSaving(String.valueOf(dataModels.get(i).getId()));
                        dataModels.clear(); //  ez kell ide, különben ott marad a listában a törölt megtakarítás, ha már csak egy megtakarítás van összesen
                        ListViewSavings.setAdapter(null);

                        Intent intent = new Intent(SavingsActivity.this, DoneAnimation.class);
                        startActivity(intent);

                        getSavings(userId);
                        //Toast.makeText(savingsActivity.this, "MYSQL-ből törlöni itt, majd listát frissíteni", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNo(String title) {

                    }
                });
                cdd.show();

                /*if (Utility.askYesOrNoWithQuestion(SavingsActivity.this, "Feltöri ezt a megtakarítást?")){

                    // Így tudom kinyerni az id-t!!!
                    TextView textView = (TextView) view.findViewById(R.id.line_h);
                    String id = textView.getText().toString();

                    deleteSaving(id);
                    getSavings(userId);
                    //Toast.makeText(savingsActivity.this, "MYSQL-ből törlöni itt, majd listát frissíteni", Toast.LENGTH_SHORT).show();
                };*/
                ListViewSavings.setOnItemClickListener(m1);
                return true;
            }
        });


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
        MenuItem navMenuSavings = navMenu.findItem(R.id.Savings);
        navMenuSavings.setEnabled(false);

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
                                intent = new Intent(SavingsActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(SavingsActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(SavingsActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(SavingsActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(SavingsActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(SavingsActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(SavingsActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(SavingsActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(SavingsActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(SavingsActivity.this, PinCodeChangeActivity.class);
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

                                intent = new Intent(SavingsActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(SavingsActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(SavingsActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(SavingsActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                SavingsActivity.this.finish();
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
                                intent = new Intent(SavingsActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
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
        getSavings(userId);
        super.onRestart();
    }
}
