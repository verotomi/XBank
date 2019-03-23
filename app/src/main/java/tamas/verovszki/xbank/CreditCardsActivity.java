package tamas.verovszki.xbank;

import android.content.Context;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreditCardsActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate{

    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    Switch switchStatus;
    SeekBar seekBarAtm;
    SeekBar seekBarPos;
    SeekBar seekBarOnline;
    LinearLayout controls;
    TextView textViewAtmLimit;
    TextView textViewPosLimit;
    TextView textViewOnlineLimit;
    TextView textViewStatus1;
    TextView textViewStatus2;
    TextView textViewCardNumber;
    LinearLayout linearLayoutSeparator;
    Button buttonSubmit;

    boolean loginState = false;
    boolean answer;
    Boolean valasz = false;
    ListView ListViewCreditCards;
    String userFirstName="";
    int userId;
    ArrayList<DataModelCreditCards> creditCardList;
    private SwipeRefreshLayout SwipeRefreshLayout;


    //MySQL adatbázishoz

    PerformNetworkRequest request;

    // listview-hez kellő változók
    ArrayList<DataModelCreditCards> dataModels;
    ListView listView;
    private static CustomAdapterCreditCards adapter;


    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    private SimpleAdapter sa;

    int positionForwarded;

    int seekbarStep = 10000;
    int seekbarMin = 10000;
    int seekbarMax = 500000;

    DrawerLayout drawerLayout;
    String userLastLoginTime;
    String userLastName;
    boolean ignore = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleCreditcards)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_credit_cards);

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
                getCreditCards(userId); // elinditom a kártyaadatok bekérését, és amikor megérkezett, akkor építem fel a listview-et
            }
        });


        /*SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        String wasRecreatedOnce = sp.getString("bankkartyalista_frissiteshez_szukseges_ujrainditas_egyszer_mar_lefutott", "null");

        if (isNetworkAvailable()){ // ha engedélyezve van a helymeghatározás
            if (wasRecreatedOnce == "igen") { // a program elindítása utáni első árfolyamlista megjelenítés után x mp után frissítem a listát. X-et úgy választom meg, hogy addigra másr biztos legyen friss koordináta.
            }
            else{
                sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("bankkartyalista_frissiteshez_szukseges_ujrainditas_egyszer_mar_lefutott", "igen");
                editor.apply();
                getCreditCards(userId);
            }
        }*/

        getCreditCards(userId); // elinditom a kártyaadatok bekérését, és amikor megérkezett, akkor építem fel a listview-et


        listView = (ListView) findViewById(R.id.ListViewCreditCards);
        dataModels = new ArrayList<>();
        // átkerült a taskcompletition-részbe
        //dataModels.add(new DataModelCreditCards("1234", "master", "2018-12", "Aktiv", 150000, 150000, 150000));
        //dataModels.add(new DataModelCreditCards("1234", "master", "2018-12", "Aktiv", 150000, 150000, 150000));
        //dataModels.add(new DataModelCreditCards("1234", "master", "2018-12", "Aktiv", 150000, 150000, 150000));
        //adapter = new CustomAdapterCreditCards(dataModels, this); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben
        //listView.setAdapter(adapter);
        ListViewCreditCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                //Toast.makeText(CreditCardsActivity.this, "Position " + position + "Getview: " + view.getTag(), Toast.LENGTH_SHORT).show();

                //controls.setVisibility(controls.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                //linearLayoutSeparator.setVisibility(controls.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);

                textViewAtmLimit.setTextColor(getResources().getColor(R.color.midgrey2));
                textViewPosLimit.setTextColor(getResources().getColor(R.color.midgrey2));
                textViewOnlineLimit.setTextColor(getResources().getColor(R.color.midgrey2));
                textViewStatus1.setTextColor(getResources().getColor(R.color.midgrey2));
                switchStatus.setChecked(dataModels.get(position).status.equals("Active"));
                buttonSubmit.setEnabled(false);

                if (position == positionForwarded){
                    controls.setVisibility(controls.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                    linearLayoutSeparator.setVisibility(linearLayoutSeparator.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                    textViewStatus2.setTextColor(dataModels.get(position).status.equals(Constants.ACTIVE) ? getResources().getColor(R.color.midgrey2) : getResources().getColor(R.color.holo_red_dark));
                    textViewStatus2.setText(dataModels.get(position).status.equals(Constants.ACTIVE) ? getString(R.string.active) : getString(R.string.inactive));
                }
                else{
                    controls.setVisibility(View.VISIBLE);
                    linearLayoutSeparator.setVisibility(View.VISIBLE);
                    textViewStatus2.setTextColor(switchStatus.isChecked() ? getResources().getColor(R.color.holo_red_dark) : getResources().getColor(R.color.midgrey2));
                    textViewStatus2.setText(switchStatus.isChecked() ? getString(R.string.active) : getString(R.string.inactive));
                    textViewStatus1.setTextColor(getResources().getColor(R.color.midgrey2));
                }
                positionForwarded = position;

                textViewCardNumber.setText(
                        dataModels.get(position).cardNumber.substring(0,4) + "-" +
                        dataModels.get(position).cardNumber.toString().substring(4,8) + "-" +
                        dataModels.get(position).cardNumber.toString().substring(8,12) + "-" +
                        dataModels.get(position).cardNumber.toString().substring(12,16) + " " +
                        dataModels.get(position).cardType  + " " + getString(R.string.card));
                textViewStatus1.setText(getString(R.string.card_status));
                textViewStatus2.setText(dataModels.get(position).status.equals(Constants.ACTIVE) ? getString(R.string.active) : getString(R.string.inactive));
                textViewStatus2.setTextColor(dataModels.get(position).status.equals(Constants.ACTIVE) ? getResources().getColor(R.color.holo_red_dark) : getResources().getColor(R.color.midgrey2));
                switchStatus.setChecked(dataModels.get(position).status.equals(Constants.ACTIVE));

                seekBarAtm.setMax((seekbarMax - seekbarMin) / seekbarStep);
                seekBarAtm.setProgress(dataModels.get(position).limitAtm / seekbarStep);
                textViewAtmLimit.setText("Atm limit:" + " " + dataModels.get(position).limitAtm + " " + "Ft");

                seekBarPos.setMax((seekbarMax - seekbarMin) / seekbarStep);
                seekBarPos.setProgress(dataModels.get(position).limitPos / seekbarStep);
                textViewPosLimit.setText("Pos limit:" + " " + dataModels.get(position).limitPos + " " + "Ft");

                seekBarOnline.setMax((seekbarMax - seekbarMin) / seekbarStep);
                seekBarOnline.setProgress(dataModels.get(position).limitOnline / seekbarStep);
                textViewOnlineLimit.setText("Online limit:" + " " + dataModels.get(position).limitOnline + " " + "Ft");


                switchStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        //dataModels.get(positionForwarded).setStatus(switchStatus.isChecked() ? "active" : "inactive");
                        //textViewStatus1.setText("A kártya állapota:" + " ");
                        textViewStatus2.setText(switchStatus.isChecked() ? getString(R.string.active) : getString(R.string.inactive));
                        textViewStatus1.setTextColor(getResources().getColor(R.color.black));
                        textViewStatus2.setTextColor(switchStatus.isChecked() ? getResources().getColor(R.color.holo_red_dark) : getResources().getColor(R.color.midgrey2));
                        buttonSubmit.setEnabled(true);

                    }
                });
            }
        });

        seekBarAtm.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewAtmLimit.setText(getString(R.string.limit_atm) + " " + (i * seekbarStep + seekbarMin) + " " + getString(R.string.forint_short));
                //textViewAtmLimit.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textViewAtmLimit.setTextColor(getResources().getColor(R.color.black));
                buttonSubmit.setEnabled(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewAtmLimit.setTextColor(getResources().getColor(R.color.black));
            }
        });

        seekBarPos.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewPosLimit.setText(getString(R.string.limit_pos) + " " + (i * seekbarStep + seekbarMin) + " " + getString(R.string.forint_short));
                //textViewPosLimit.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textViewPosLimit.setTextColor(getResources().getColor(R.color.black));
                buttonSubmit.setEnabled(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewPosLimit.setTextColor(getResources().getColor(R.color.black));
            }
        });

        seekBarOnline.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewOnlineLimit.setText(getString(R.string.limit_online) + " " + (i * seekbarStep + seekbarMin) + " " + getString(R.string.forint_short));
                //textViewOnlineLimit.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
                //textViewOnlineLimit.setTextColor(getResources().getColor(R.color.red));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                textViewOnlineLimit.setTextColor(getResources().getColor(R.color.black));
                buttonSubmit.setEnabled(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textViewOnlineLimit.setTextColor(getResources().getColor(R.color.black));
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataModels.get(positionForwarded).setStatus(switchStatus.isChecked() ? Constants.ACTIVE : Constants.INACTIVE);
                dataModels.get(positionForwarded).setLimitAtm(seekBarAtm.getProgress() * seekbarStep + seekbarMin);
                dataModels.get(positionForwarded).setLimitPos(seekBarPos.getProgress() * seekbarStep + seekbarMin);
                dataModels.get(positionForwarded).setLimitOnline(seekBarOnline.getProgress() * seekbarStep + seekbarMin);

                updateCreditCards(
                        dataModels.get(positionForwarded).card_id,
                        dataModels.get(positionForwarded).status,
                        dataModels.get(positionForwarded).limitAtm,
                        dataModels.get(positionForwarded).limitPos,
                        dataModels.get(positionForwarded).limitOnline,
                        dataModels.get(positionForwarded).user_id
                        );

                getCreditCards(userId);
                Intent intent = new Intent(CreditCardsActivity.this, DoneAnimation.class);
                startActivity(intent);

                controls.setVisibility(View.INVISIBLE);
                linearLayoutSeparator.setVisibility(View.INVISIBLE);
                textViewAtmLimit.setTextColor(getResources().getColor(R.color.midgrey2));
                textViewPosLimit.setTextColor(getResources().getColor(R.color.midgrey2));
                textViewOnlineLimit.setTextColor(getResources().getColor(R.color.midgrey2));
                textViewStatus1.setTextColor(getResources().getColor(R.color.midgrey2));
            }
        });

    }

    /**
     * Új bankkártya adatok kiírása
     * Aszinkron művelet!
     *
     */
    private void updateCreditCards(int creditCardId, String status, int limitAtm, int limitPos, int limitOnline, int user_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_CREDIT_CARDS_ID, String.valueOf(creditCardId));
            params.put(Constants.COL_CREDIT_CARDS_STATUS, status);
            params.put(Constants.COL_CREDIT_CARDS_LIMIT_ATM, String.valueOf(limitAtm));
            params.put(Constants.COL_CREDIT_CARDS_LIMIT_POS, String.valueOf(limitPos));
            params.put(Constants.COL_CREDIT_CARDS_LIMIT_ONLINE, String.valueOf(limitOnline));
            params.put(Constants.COL_CREDIT_CARDS_ID_USER, String.valueOf(user_id));
            request = new PerformNetworkRequest(Constants.URL_UPDATE_CREDIT_CARDS, params, Constants.CODE_POST_REQUEST, this);
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
        ListViewCreditCards = findViewById(R.id.ListViewCreditCards);
        switchStatus = findViewById(R.id.SwitchStatus);
        seekBarAtm = findViewById(R.id.SeekBarAtm);
        seekBarPos = findViewById(R.id.SeekBarPos);
        seekBarOnline = findViewById(R.id.SeekBarOnline);
        controls = findViewById(R.id.LinLayControls);
        textViewAtmLimit = findViewById(R.id.TextViewAtmLimit);
        textViewPosLimit = findViewById(R.id.TextViewPosLimit);
        textViewOnlineLimit = findViewById(R.id.TextViewOnlineLimit);
        textViewStatus1 = findViewById(R.id.TextViewStatus1);
        textViewStatus2 = findViewById(R.id.TextViewStatus2);
        textViewCardNumber = findViewById(R.id.TextViewCardNumber);
        linearLayoutSeparator = findViewById(R.id.LinLaySeparator);
        buttonSubmit = findViewById(R.id.ButtonSubmit);
        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        drawerLayout = findViewById(R.id.drawer_layout);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mobilbankmainactivity_without_name, menu); // elemek hozzáadása a toolbarhoz
        menuitem_logout_3 = menu.findItem(R.id.logout_toolbar_icon_3);
        //menuitem_toolbar_name = menu.findItem(R.id.logout_toolbar_name);
        loginState = Utility.getLoginState(CreditCardsActivity.this);

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
                    Utility.saveLoginState(CreditCardsActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(CreditCardsActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            CreditCardsActivity.this.finish();
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
        //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        String returnedMessage ="";
        try {
            JSONObject object2 = new JSONObject(result);
            returnedMessage = object2.getString(Constants.RESPONSE_MESSAGE);
        }
        catch(JSONException e){
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
        switch (returnedMessage) {
            case Constants.RESPONSE_MESSAGE_CREDITCARDS_SUCCESSFUL:
                dataModels.clear();
                final JSONArray creditcardListJSON;
                try {
                    JSONObject object = new JSONObject(result);
                    creditcardListJSON = object.getJSONArray(Constants.RESPONSE_CREDIT_CARDS);
                    if (!object.getBoolean(Constants.RESPONSE_ERROR)) {
                        for (int i = 0; i < creditcardListJSON.length(); i++) {
                            JSONObject obj = creditcardListJSON.getJSONObject(i);
                            dataModels.add(new DataModelCreditCards(
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
                            /*dataModels.add(new DataModelCreditCards(
                                    obj.getString(Constants.COL_CREDIT_CARDS_NUMBER),
                                    obj.getString(Constants.COL_CREDIT_CARDS_TYPE),
                                    getString(R.string.validto) + ": " + obj.getString(Constants.COL_CREDIT_CARDS_EXPIRE_DATE),
                                    obj.getString(Constants.COL_CREDIT_CARDS_STATUS),
                                    obj.getInt(Constants.COL_CREDIT_CARDS_LIMIT_ATM),
                                    obj.getInt(Constants.COL_CREDIT_CARDS_LIMIT_POS),
                                    obj.getInt(Constants.COL_CREDITCARDS_LIMIT_ONLIN)
                            ));*/
                        }
                        adapter = new CustomAdapterCreditCards(dataModels, this); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben

                        listView.setAdapter(adapter);
                        buttonSubmit.setEnabled(false);

                        //recreate();
                    }
                } catch (JSONException e) {
                    Toast.makeText(this, getString(R.string.no_creditcard), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); // itt értékes infót kapok arról, hogy mi nem jó!!!
                }
            break;
            case Constants.RESPONSE_MESSAGE_CREDITCARDS_UPDATES_SUCCESSFUL:{
                Toast.makeText(this, getString(R.string.creditcard_modified_succesfully), Toast.LENGTH_SHORT).show();
            }
            break;
        }
        SwipeRefreshLayout.setRefreshing(false);

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
        MenuItem navMenuCreditCards = navMenu.findItem(R.id.Manage_credit_cards);
        navMenuCreditCards.setEnabled(false);

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
                                intent = new Intent(CreditCardsActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(CreditCardsActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(CreditCardsActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(CreditCardsActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(CreditCardsActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(CreditCardsActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(CreditCardsActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(CreditCardsActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(CreditCardsActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(CreditCardsActivity.this, PinCodeChangeActivity.class);
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

                                intent = new Intent(CreditCardsActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(CreditCardsActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(CreditCardsActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(CreditCardsActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                CreditCardsActivity.this.finish();
                                                //finish();
                                            }
                                        }, 3000);
                                    }

                                    @Override
                                    public void onNo(String title) {

                                    }
                                });
                                cdd.show();
                                cdd.show();
                                break;

                            default: // van egy üres sor a navigation drawer legalján, amiatt, hogy oda is kerüljön elválasztóvonal. Ennek az üres sornak a lekezelése miatt kell ide az ignore, hogy ha arra kattintunk, ne történjen semmi
                                ignore = true;
                                intent = new Intent(CreditCardsActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
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
