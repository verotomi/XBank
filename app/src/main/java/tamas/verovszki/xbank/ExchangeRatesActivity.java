package tamas.verovszki.xbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

/**
 * EZ MÁR NEM IGAZ: Jelenleg az activityben 2 helyen fut le az árfolyam-szinkronizálás. Az egyik a SplashsScreen-en, hogy mire a felhasználó az árfolyammegtekintéshez ér, már legyen neki friss adat.
 * Ezen kívül itt is lefut, ha a menüből kiválasztjuk a frissítést. Ehhez a komplett műveletet át kellett másolnom a SplashScreenől ide is.
 *
 */
public class ExchangeRatesActivity extends AppCompatActivity implements Tab1Currencies.OnFragmentInteractionListener,Tab2ForeignCurrencies.OnFragmentInteractionListener, PerformNetworkRequest.TaskDelegate {

    //MySQL adatbázishoz kell
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    PerformNetworkRequest request;

    //SQLite adatbázishoz kell
    private DatabaseHelper db;

    private android.support.v4.widget.SwipeRefreshLayout SwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleExchangeRates)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_exchange_rates);

        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.currency)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.foreign_currency)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        db = new DatabaseHelper(this); // mySQL-hez kell

        // Toolbar + visszanyíl - többek között be kell állítani hozzá a manifestben a szülő Activityt + kell egy Toolbar view az XML-be.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_4);
        setSupportActionBar(myToolbar);
        myToolbar.setOverflowIcon(getDrawable(R.drawable.three_dots_white_small_20)); // menü(3 pont) ikon cseréje fehérre
        //ActionBar ab = getSupportActionBar();
        // ab.setDisplayHomeAsUpEnabled(true);

        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String wasRecreatedOnce = sp.getString(Constants.SHAREDPREFERENCES_EXCHANGE_RATE_REFRESH, "null");

        if (isNetworkAvailable()) {
            if (wasRecreatedOnce == "igen") { // a program elindítása utáni első árfolyamlista megjelenítés után x mp után frissítem a listát. X-et úgy választom meg, hogy addigra másr biztos legyen friss koordináta.
            } else {
                //Toast.makeText(this, "Az árfolyamok frissítésre kerültek!", Toast.LENGTH_SHORT).show();
                //copyCurrenciesFromMySqlToSqlite();
                //copyForeignCurrenciesFromMySqlToSqlite();
                copyBothCurrenciesFromMySqlToSqlite();
                sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constants.SHAREDPREFERENCES_EXCHANGE_RATE_REFRESH, "igen");
                editor.apply();
                //recreateAfterDelay(5000);
            }
        }

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final MyPagerAdapterExchangeRates adapter = new MyPagerAdapterExchangeRates(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        // viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

        // érdekesség: ha ide írnék valamilyen utasítást, az folyamatosan futna, amig ez z activity fut!

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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

                if (Utility.IsNetworkAvailable(ExchangeRatesActivity.this)){
                    copyBothCurrenciesFromMySqlToSqlite();                }
        else{
                Toast.makeText(ExchangeRatesActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                SwipeRefreshLayout.setRefreshing(false);
                }

                /*(new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getRecurringTransfers(userId);

                        SwipeRefreshLayout.setRefreshing(false);
                    }
                },3000);*/
            }
        });

    }

    /**
     * Összevontam a kettőt
     * Valuták beolvasása MySQL adatbázisból, majd a beolvasott adatokkal frissitjük az SQLite adatbázist.
     */
    /*private void copyCurrenciesFromMySqlToSqlite() {
        request = new PerformNetworkRequest(Constants.URL_READ_CURRENCIES, DatabaseHelper.TABLE_NAME_CURRENCIES,"currencies", null, CODE_GET_REQUEST, this);
        request.execute();
    }*/

    /**
     * Összevontam a kettőt
     * Devizák beolvasása MySQL adatbázisból, majd a beolvasott adatokkal frissitjük az SQLite adatbázist.
     */
    /*private void copyForeignCurrenciesFromMySqlToSqlite() {
        request = new PerformNetworkRequest(Constants.URL_READ_FOREIGNCURRENCIES, DatabaseHelper.TABLE_NAME_FOREIGN_CURRENCIES,"foreigncurrencies", null, CODE_GET_REQUEST, this);
        request.execute();
    }*/

    /**
     * Valuta + deviza beolvasása MySQL adatbázisból, majd a beolvasott adatokkal frissitjük az SQLite adatbázist.
     */
    private void copyBothCurrenciesFromMySqlToSqlite() {
        request = new PerformNetworkRequest(Constants.URL_READ_BOTH_CURRENCIES,null, CODE_GET_REQUEST, this);
        request.execute();
    }

    public void taskCompletionResult(String result) {
        JSONArray currencyList;
        JSONArray foreigncurrencyList;
        try {
            JSONObject object = new JSONObject(result);
            int a = 0;
            a = a+1;
            currencyList = object.getJSONArray(Constants.TABLE_NAME_CURRENCIES);
            foreigncurrencyList = object.getJSONArray(Constants.TABLE_NAME_FOREIGN_CURRENCIES);
            int b = 0;
            b = b+1;
            if (!object.getBoolean(Constants.RESPONSE_ERROR)) {
                //Toast.makeText(ExchangeRatesActivity.this, "Adatok frissítve - " + tableName, Toast.LENGTH_SHORT).show(); // végül ez sem kell ide
                for (int i = 0; i < currencyList.length(); i++) {
                    JSONObject obj1 = currencyList.getJSONObject(i);
                    //db.updateCurrencies(DatabaseHelper.TABLE_NAME_CURRENCIES, obj.getInt("id"),obj.getString("name"), obj.getDouble("buy"), obj.getDouble("sell"),obj.getString("date"));
                    //refreshCurrencyList(tableName, object.getJSONArray("currencies"));
                    db.updateCurrencies(Constants.TABLE_NAME_CURRENCIES, obj1.getInt(Constants.COL_CURRENCIES_ID),obj1.getString(Constants.COL_CURRENCIES_NAME), obj1.getDouble(Constants.COL_CURRENCIES_BUY), obj1.getDouble(Constants.COL_CURRENCIES_SELL),obj1.getString(Constants.COL_CURRENCIES_VALIDFROM));
                }
                for (int i = 0; i < foreigncurrencyList.length(); i++) {
                    JSONObject obj2 = foreigncurrencyList.getJSONObject(i);
                    //db.updateCurrencies(DatabaseHelper.TABLE_NAME_CURRENCIES, obj.getInt("id"),obj.getString("name"), obj.getDouble("buy"), obj.getDouble("sell"),obj.getString("date"));
                    //refreshCurrencyList(tableName, object.getJSONArray("currencies"));
                    db.updateCurrencies(Constants.TABLE_NAME_FOREIGN_CURRENCIES, obj2.getInt(Constants.COL_FOREIGNCURRENCIES_ID),obj2.getString(Constants.COL_FOREIGNCURRENCIES_NAME), obj2.getDouble(Constants.COL_FOREIGNCURRENCIES_BUY), obj2.getDouble(Constants.COL_FOREIGNCURRENCIES_SELL),obj2.getString(Constants.COL_FOREIGNCURRENCIES_VALIDFROM));
                }
                Toast.makeText(ExchangeRatesActivity.this, getString(R.string.exchange_rates_refreshed), Toast.LENGTH_SHORT).show();

                //recreate();        //Do something after x ms
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_exchangeratesactivity, menu); // elemek hozzáadása a toolbarhoz
            //menuitem_BranchAndAtm_3 = menu.findItem(R.id.order1);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!
        int id = item.getItemId();

        /**
         * Az újrafrissítés-menüelem kiválasztására elindul a friss adatok lekérése a szerverről és az átmásolása a helyi adatbázisba.
         * Emellett elindul egy időzítő, aminek a letelte után újrarajzoljuk az activity-t, így frissülnek az árfolyamok.
         * A késleltetési idővel lehet még jászani.
         * Valójában jobb lenne figyelni ( vagy értesítést kapni) , ha az adatbázis-beolvasás-frissaítés lefutott, és akkor rajzolni ujra az activity-t, de azt nem tudom, hogyan kell.
         */
        if (id == R.id.refreshExchangeRates){
            //copyCurrenciesFromMySqlToSqlite();
            //copyForeignCurrenciesFromMySqlToSqlite();
            copyBothCurrenciesFromMySqlToSqlite();
            //recreateAfterDelay(3000);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * ezt már nem használom
     * @param delay
     * @return
     */
    public boolean recreateAfterDelay(int delay){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               // Ezt a két sort átraktam az onPostExecuteba innen.
               Toast.makeText(ExchangeRatesActivity.this, getString(R.string.exchange_rates_refreshed), Toast.LENGTH_SHORT).show();
               //recreate();        //Do something after x ms
               startActivity(getIntent());
               finish();
               overridePendingTransition(0, 0);
            }
        }, delay); // in milliseconds
        return true;
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

    private void setApplicationLanguage(String newLanguage) {
        Resources activityRes = getResources();
        Configuration activityConf = activityRes.getConfiguration();
        Locale newLocale = new Locale(newLanguage);
        activityConf.setLocale(newLocale);
        activityRes.updateConfiguration(activityConf, activityRes.getDisplayMetrics());

        Resources applicationRes = getApplicationContext().getResources();
        Configuration applicationConf = applicationRes.getConfiguration();
        applicationConf.setLocale(newLocale);
        applicationRes.updateConfiguration(applicationConf,
                applicationRes.getDisplayMetrics());
    }

}
