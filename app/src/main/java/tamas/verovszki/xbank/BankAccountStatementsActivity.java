package tamas.verovszki.xbank;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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


import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BankAccountStatementsActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate{

    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    Spinner spinnerSourceAccount;
    TextView TextViewSourceAccount;
    LinearLayout LinLay0;


    boolean loginState = false;
    boolean answer;
    Boolean valasz = false;
    ListView ListViewStatements;
    String userFirstName="";
    int userId;
    ArrayList<DataModelStatements> statementList;
    private SwipeRefreshLayout SwipeRefreshLayout;
    boolean vanAtadvaErtek = false;


    //MySQL adatbázishoz

    PerformNetworkRequest request;

    // listview-hez kellő változók
    ArrayList<DataModelStatements> dataModels;
    private static CustomAdapterStatements adapter;

    List<String> bankAccountNumberIdList = new ArrayList<String>();
    List<String> bankAccountCurrencyList = new ArrayList<String>();
    String selectedBankAccountNumberId="";
    int spinnerSelectedItemNo = 0;
    boolean _ignore2 = false; // ezzel kezelem azt, hogy a ugyanaz a bankszámla legyen kiválasztva a spinnerben az átutalás után is

    // letöltéshez kell
    private long downloadID;

    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    private SimpleAdapter sa;

    DrawerLayout drawerLayout;
    String userLastLoginTime;
    String userLastName;
    boolean ignore = false;
    boolean isBankAccount= true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleBankAccountStatements)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_bank_account_statements);


        giveAccessRightToOtherApplicationsToReadFiles(); // ez kell ahhoz, hogy a letöltött pdf-ek megnyithatók legyenek

        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));// fájl letöltéshez kell

        getBundle(); // átadott változók beolvasása
        init();
        navDrawer();


        setSupportActionBar(toolbar); // Toolbar megjelenítése

        getAccountBalances(userId);

        /**
         * Képernyő lehúzás hatására frissítés
         */
        SwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.darkgreen3, R.color.darkgreen);
        SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                    SwipeRefreshLayout.setRefreshing(true);
                    //recreate();

            if (!selectedBankAccountNumberId.equals("")) {
                getStatements(userId, selectedBankAccountNumberId); // elinditom a kivonatok bekérését, és amikor megérkezett, akkor építem fel a listview-et
            }
            else{
                //Toast.makeText(BankAccountStatementsActivity.this, "Nem található bankszámla kivonat!", Toast.LENGTH_SHORT).show();
                SwipeRefreshLayout.setRefreshing(false);
            }
                    //SwipeRefreshLayout.setRefreshing(false);

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
                getStatements(userId);
            }
        }*/

        // ez nem kell ide, mert amikor a számlaszám-választó spinner beállításra kerül, akkor is meghívódik a getStatements
        // getStatements(userId, selectedBankAccountNumberId); // elinditom a kártyaadatok bekérését, és amikor megérkezett, akkor építem fel a listview-et

        dataModels = new ArrayList<>();
        // átkerült a taskcompletition-részbe
        //dataModels.add(new DataModelStatements("1234", "master", "2018-12", "Aktiv", 150000, 150000, 150000));
        //dataModels.add(new DataModelStatements("1234", "master", "2018-12", "Aktiv", 150000, 150000, 150000));
        //dataModels.add(new DataModelStatements("1234", "master", "2018-12", "Aktiv", 150000, 150000, 150000));
        //adapter = new CustomAdapterStatements(dataModels, this); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben
        //listView.setAdapter(adapter);

        ListViewStatements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                //Toast.makeText(BankAccountStatementsActivity.this, "Position " + position + dataModels.get(position).getFilename(), Toast.LENGTH_SHORT).show();

                File file=new File(getExternalFilesDir(null),dataModels.get(position).getFilename());
                if(file.exists()){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    File file2 = new File(getExternalFilesDir(null),dataModels.get(position).getFilename());
                    intent.setDataAndType( Uri.fromFile( file2 ), "application/pdf" );
                    startActivity(intent);
                }
                else{
                    beginDownload(dataModels.get(position).getFilename());

                }
                //controls.setVisibility(controls.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
                //linearLayoutSeparator.setVisibility(controls.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
            }
        });

        /**
         * Korábban összeakadt a click és a longclick (mindkettő elindult), ezért:
         * - elmentem az m1 változóba a listenert
         * - amig tart a longclick, letiltom a clicket.
         * - a longclick végén visszaállítom m1-ből a listenert.
         */
        final ListView.OnItemClickListener m1 = ListViewStatements.getOnItemClickListener();
        ListViewStatements.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                ListViewStatements.setOnItemClickListener(null);
                final File file = new File(getExternalFilesDir(null),dataModels.get(position).getFilename());
                if (file.exists()) {

                    /*
                    CustomDialogClass cdd = new CustomDialogClass(BankAccountStatementsActivity.this, "Törli a telefonról ezt a kivonatot?");
                    cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                        @Override
                        public void onYes(String title) {
                            if (file.delete()) {
                                Toast.makeText(BankAccountStatementsActivity.this, "Kivonat törölve!", Toast.LENGTH_SHORT).show();
                                System.out.println("file Deleted !");
                            } else {
                                Toast.makeText(BankAccountStatementsActivity.this, "A kivonat törölése nem sikerült!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    cdd.show();*/


                    if (Utility.askYesOrNoWithQuestion2(BankAccountStatementsActivity.this, getString(R.string.delete_statement))){
                        if (file.delete()) {
                            Toast.makeText(BankAccountStatementsActivity.this, R.string.statement_deleted, Toast.LENGTH_SHORT).show();

                            Intent intent2 = new Intent(BankAccountStatementsActivity.this, DoneAnimation.class);
                            startActivity(intent2);

                            //System.out.println("file Deleted !");
                            getStatements(userId, selectedBankAccountNumberId); // elinditom a kivonatok bekérését, és amikor megérkezett, akkor építem fel a listview-et
                        } else {
                            //System.out.println("file not Deleted :");
                        }
                    }

                }

                ListViewStatements.setOnItemClickListener(m1);
                return true;
            }
        });

        spinnerSourceAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelectedItemNo = i;
                if (!_ignore2){
                    selectedBankAccountNumberId = bankAccountNumberIdList.get(i);
                    getStatements(userId, selectedBankAccountNumberId); // elinditom a kivonatok bekérését, és amikor megérkezett, akkor építem fel a listview-et
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
    }

    private void giveAccessRightToOtherApplicationsToReadFiles() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        ListViewStatements = findViewById(R.id.ListViewStatements);
        spinnerSourceAccount = findViewById(R.id.SpinnerSourceAccount);
        TextViewSourceAccount = findViewById(R.id.TextViewSourceAccount);
        drawerLayout = findViewById(R.id.drawer_layout);
        LinLay0 = findViewById(R.id.LinLay0);


        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayoutStatements);

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
            CustomDialogClass cdd = new CustomDialogClass(BankAccountStatementsActivity.this);
            cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                @Override
                public void onYes(String title) {
                    menuitem_logout_3.setEnabled(false);
                    menuitem_logout_3.setVisible(false);
                    //menuitem_toolbar_name.setTitle("");
                    //menuitem_toolbar_name.setVisible(false);
                    loginState = false;
                    Utility.saveLoginState(BankAccountStatementsActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(BankAccountStatementsActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            BankAccountStatementsActivity.this.finish();
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
    private void getStatements(int user_id, String user_bank_account_id) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_ACCOUNT_STATEMENTS_ID_USER, String.valueOf(user_id));
            params.put(Constants.COL_ACCOUNT_STATEMENTS_ID_BANK_ACCOUNT, user_bank_account_id);
            request = new PerformNetworkRequest(Constants.URL_READ_BANK_ACCOUNT_STATEMENTS, params, Constants.CODE_POST_REQUEST, this);
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
                JSONArray accountList;
                try {
                    JSONObject object = new JSONObject(result);
                    accountList = object.getJSONArray(Constants.RESPONSE_ACCOUNT_BALANCES);
                    List<String> list = new ArrayList<String>();
                    if (!object.getBoolean("error")) {
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
                LinLay0.setVisibility(View.VISIBLE);
                break;
            case "Accountlist query was not succesfull!":
                Toast.makeText(this, "Önnek nincs aktív bankszámlája!", Toast.LENGTH_SHORT).show();
                isBankAccount = false;
                break;


            case "Bank account statement query was succesfull!":{
                //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
                dataModels.clear();
                final JSONArray statementListJSON;
                try {
                    //JSONObject object = new JSONObject(request.getResult());
                    JSONObject object = new JSONObject(result);
                    statementListJSON = object.getJSONArray("bankaccountstatements");
                    if (!object.getBoolean("error")) {
                        dataModels.clear();
                        for (int i = 0; i < statementListJSON.length(); i++) {
                            JSONObject obj = statementListJSON.getJSONObject(i);
                            dataModels.add(new DataModelStatements(
                                    obj.getInt(Constants.COL_ACCOUNT_STATEMENTS_ID),
                                    obj.getInt(Constants.COL_ACCOUNT_STATEMENTS_ID_USER),
                                    obj.getInt(Constants.COL_ACCOUNT_STATEMENTS_ID_BANK_ACCOUNT),
                                    obj.getString(Constants.COL_ACCOUNT_STATEMENTS_NUMBER),
                                    obj.getString(Constants.COL_ACCOUNT_STATEMENTS_FILENAME)
                            ));
                        }
                        adapter = new CustomAdapterStatements(dataModels, this); // itt a getactivity() azért kell, mert fragment-ben vagyunk, nem activityben
                        ListViewStatements.setAdapter(adapter);

                        //recreate();
                    }
                } catch (JSONException e) {
                    //Toast.makeText(this, "Nem található bankszámla kivonat!", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); // itt értékes infót kapok arról, hogy mi nem jó!!!
                }
                //getAccountBalances(userId);
                //resetFields();
                break;
            }
            case "Bank account statement query was not succesfull!":{
                //Toast.makeText(this, "Valami hiba történt, vagy a felhasználónak nincs bankszámlája", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, getString(R.string.transfer_ok), Toast.LENGTH_SHORT).show();
                //isBankAccount = false;
                Toast.makeText(this, "Nem található bankszámla kivonat!", Toast.LENGTH_SHORT).show();
                ListViewStatements.setAdapter(null);
                break;
            }
            default:
                //Toast.makeText(this, "Nem található bankszámla kivonat!", Toast.LENGTH_SHORT).show();
                break;
        }
        SwipeRefreshLayout.setRefreshing(false);

    }

    // add items into spinner dynamically
    public void addItemsOnSpinnerSourceAccount(List<String> listOfStatements) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_transfer_money, listOfStatements);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item_xbank);
        spinnerSourceAccount.setAdapter(dataAdapter);
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
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                getStatements(userId, selectedBankAccountNumberId); // elinditom a kivonatok bekérését, és amikor megérkezett, akkor építem fel a listview-etf
                Toast.makeText(BankAccountStatementsActivity.this, R.string.download_completed, Toast.LENGTH_SHORT).show();

                Intent intent2 = new Intent(BankAccountStatementsActivity.this, DoneAnimation.class);
                startActivity(intent2);
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete); // leiratkozás aa letöltés figyelésről
    }

    /**
     * fájl letöltés
     */
    private void beginDownload(String filename){
        File file=new File(getExternalFilesDir(null),filename);
        /*
        Create a DownloadManager.Request with all the information necessary to start the download
         */
        DownloadManager.Request request= null;// Set if download is allowed on roaming network
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            //Toast.makeText(this, Constants.URL_FOR_DOWNLOAD + filename, Toast.LENGTH_SHORT).show();
            String sss = Constants.URL_FOR_DOWNLOAD + filename;
            request = new DownloadManager.Request(Uri.parse(Constants.URL_FOR_DOWNLOAD + filename))
                    .setTitle("Bank account statement file")// Title of the Download Notification
                    .setDescription("Downloading")// Description of the Download Notification
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                    .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                    .setRequiresCharging(false)// Set if charging is required to begin the download
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setAllowedOverRoaming(true);
        }
        DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadID = downloadManager.enqueue(request);// enqueue puts the download request in the queue.
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
            try {
                selectedBankAccountNumberId = (b.getString(Constants.SHAREDPREFERENCES_ACCOUNT_NUMBER_POSITION));
                if (selectedBankAccountNumberId.equals(null)){
                    vanAtadvaErtek = false;
                }
                else{
                    vanAtadvaErtek = true;
                }
            }catch(Exception e){
                selectedBankAccountNumberId ="";
                vanAtadvaErtek = false;
            }
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
        MenuItem navMenuAccountStatementDownload = navMenu.findItem(R.id.Account_statement_download);
        navMenuAccountStatementDownload.setEnabled(false);

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
                                intent = new Intent(BankAccountStatementsActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(BankAccountStatementsActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(BankAccountStatementsActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(BankAccountStatementsActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(BankAccountStatementsActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(BankAccountStatementsActivity.this, BankAccountHistoryActivity.class);

                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(BankAccountStatementsActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(BankAccountStatementsActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(BankAccountStatementsActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(BankAccountStatementsActivity.this, PinCodeChangeActivity.class);
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

                                intent = new Intent(BankAccountStatementsActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(BankAccountStatementsActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(BankAccountStatementsActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(BankAccountStatementsActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                BankAccountStatementsActivity.this.finish();
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
                                intent = new Intent(BankAccountStatementsActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
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
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }
}