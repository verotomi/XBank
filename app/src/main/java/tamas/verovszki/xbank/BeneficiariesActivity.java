package tamas.verovszki.xbank;

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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.design.widget.FloatingActionButton;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;


import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BeneficiariesActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate{

    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    FloatingActionButton floatingActionButton;

    boolean loginState = false;
    boolean answer;
    Boolean valasz = false;
    ListView listViewBeneficiaries;
    String userFirstName="";
    int userId;
    ArrayList<DataModelBeneficiaries> beneficiariesList;
    private SwipeRefreshLayout SwipeRefreshLayout;

    PerformNetworkRequest request;

    // listview-hez kellő változók
    ArrayList<DataModelBeneficiaries> dataModels;
    private static CustomAdapterBeneficiaries adapter;

    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    private SimpleAdapter sa;

    DrawerLayout drawerLayout;
    String userLastLoginTime;
    String userLastName;
    boolean ignore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleBeneficiaries)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_beneficiaries);

        getBundle(); // átadott változók beolvasása
        init();
        navDrawer();

        setSupportActionBar(toolbar); // Toolbar megjelenítése



        /**
         * Képernyő lehúzás hatására frissítés
         */
        SwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.darkgreen3, R.color.darkgreen);
        SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SwipeRefreshLayout.setRefreshing(true);
                //recreate();
                getBeneficiaries(userId); // elinditom a kártyaadatok bekérését, és amikor megérkezett, akkor építem fel a listview-et
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(SavingsActivity.this, "You clicked me", Toast.LENGTH_SHORT).show();
                Utility.saveLoginState(BeneficiariesActivity.this, true); // bejelentkezve változó beállítása, mentése sharedpreferences-be
                Intent intent = new Intent(BeneficiariesActivity.this, BeneficiariesNewActivity.class);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                startActivity(intent);
                //finish();
            }
        });

        getBeneficiaries(userId); // elinditom a kártyaadatok bekérését, és amikor megérkezett, akkor építem fel a listview-et

        dataModels = new ArrayList<>();
        listViewBeneficiaries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                Utility.saveLoginState(BeneficiariesActivity.this, true); // bejelentkezve változó beállítása, mentése sharedpreferences-be
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.SHAREDPREFERENCES_BENEFICIARY, dataModels.get(position)); // ehhez az osztálynak implementálni kell a Serializable interfészt!

                Intent intent = new Intent(BeneficiariesActivity.this, BeneficiaryEditActivity.class);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
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
        final ListView.OnItemClickListener m1 = listViewBeneficiaries.getOnItemClickListener();
        listViewBeneficiaries.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                listViewBeneficiaries.setOnItemClickListener(null);
                CustomDialogClass cdd = new CustomDialogClass(BeneficiariesActivity.this, getString(R.string.delete_beneficiary));
                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                    @Override
                    public void onYes(String title) {
                        deleteBeneficiaries(dataModels.get(i).getId());
                        getBeneficiaries(userId);
                    }

                    @Override
                    public void onNo(String title) {

                    }
                });
                cdd.show();


                /*if (Utility.askYesOrNoWithQuestion(BeneficiariesActivity.this, "Tényleg törölni akarja ezt a kedvezményezette?")){
                    deleteBeneficiaries(dataModels.get(i).getId());
                    getBeneficiaries(userId);

                }*/
                listViewBeneficiaries.setOnItemClickListener(m1);
                return true;
            }
        });

    }

    /**
     * Aszinkron művelet!
     *
     */
    private void deleteBeneficiaries(int id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_BENEFICIARIES_ID, String.valueOf(id));
            request = new PerformNetworkRequest(Constants.URL_DELETE_BENEFICIARY, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            SwipeRefreshLayout.setRefreshing(false);
        }



    }



    public void init() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        listViewBeneficiaries = findViewById(R.id.ListViewBeneficiaries);
        floatingActionButton = findViewById(R.id.FloatingActionButton);
        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        drawerLayout = findViewById(R.id.drawer_layout);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mobilbankmainactivity_without_name, menu); // elemek hozzáadása a toolbarhoz
        menuitem_logout_3 = menu.findItem(R.id.logout_toolbar_icon_3);
        //menuitem_toolbar_name = menu.findItem(R.id.logout_toolbar_name);
        loginState = Utility.getLoginState(BeneficiariesActivity.this);

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
                    Utility.saveLoginState(BeneficiariesActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(BeneficiariesActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            BeneficiariesActivity.this.finish();
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
    private void getBeneficiaries(int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_BENEFICIARIES_ID_USER, String.valueOf(user_id));
            request = new PerformNetworkRequest(Constants.URL_READ_BENEFICIARIES, params, Constants.CODE_POST_REQUEST, this);
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
            JSONObject object2 = new JSONObject(result);
            returnedMessage = object2.getString(Constants.RESPONSE_MESSAGE);
        } catch (JSONException e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
        switch (returnedMessage) {
            case Constants.RESPONSE_MESSAGE_BENECICIARIES_SUCCESSFUL:
                HashMap<String, String> item;    //Used to link data to lines
                dataModels.clear();
                final JSONArray beneficiariesListJSON;
                try {
                    //JSONObject object = new JSONObject(request.getResult());
                    JSONObject object = new JSONObject(result);
                    beneficiariesListJSON = object.getJSONArray(Constants.RESPONSE_BENEFICIARIES);
                    if (!object.getBoolean(Constants.RESPONSE_ERROR)) {
                        dataModels.clear();
                        for (int i = 0; i < beneficiariesListJSON.length(); i++) {
                            JSONObject obj = beneficiariesListJSON.getJSONObject(i);
                            dataModels.add(new DataModelBeneficiaries(
                                    obj.getInt(Constants.COL_BENEFICIARIES_ID),
                                    obj.getInt(Constants.COL_BENEFICIARIES_ID_USER),
                                    obj.getString(Constants.COL_BENEFICIARIES_NAME),
                                    obj.getString(Constants.COL_BENEFICIARIES_PARTNER_NAME),
                                    obj.getString(Constants.COL_BENEFICIARIES_PARTNER_ACCOUNT_NUMBER),
                                    obj.getString(Constants.COL_BENEFICIARIES_STATUS),
                                    obj.getString(Constants.COL_BENEFICIARIES_CREATED_ON)
                            ));
                        }
                        adapter = new CustomAdapterBeneficiaries(dataModels, this); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben
                        listViewBeneficiaries.setAdapter(adapter);
                        //SwipeRefreshLayout.setRefreshing(false);
                        //recreate();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); // itt értékes infót kapok arról, hogy mi nem jó!!!
                }
        }
        SwipeRefreshLayout.setRefreshing(false);
        //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
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
    protected void onRestart() {
        getBeneficiaries(userId);
        super.onRestart();
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
        MenuItem navMenuBeneficiaries = navMenu.findItem(R.id.Beneficiaries);
        navMenuBeneficiaries.setEnabled(false);

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
                                intent = new Intent(BeneficiariesActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(BeneficiariesActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(BeneficiariesActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(BeneficiariesActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(BeneficiariesActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(BeneficiariesActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(BeneficiariesActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(BeneficiariesActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(BeneficiariesActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(BeneficiariesActivity.this, PinCodeChangeActivity.class);
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

                                intent = new Intent(BeneficiariesActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(BeneficiariesActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(BeneficiariesActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(BeneficiariesActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                BeneficiariesActivity.this.finish();
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
                                intent = new Intent(BeneficiariesActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
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
}
