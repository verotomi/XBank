package tamas.verovszki.xbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class BranchAndAtmListActivity extends AppCompatActivity implements Tab1Branches.OnFragmentInteractionListener,Tab2Atms.OnFragmentInteractionListener{

    MenuItem menuitem_BranchAndAtm_3;
    Location location;
    LocationManager locationManager;

    private android.support.v4.widget.SwipeRefreshLayout SwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleBranchAndAtm)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_branch_and_atm_list);

        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.text_branches)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.text_atms)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Toolbar + visszanyíl - többek között be kell állítani hozzá a manifestben a szülő Activityt + kell egy Toolbar view az XML-be.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_5);
        setSupportActionBar(myToolbar);
        myToolbar.setOverflowIcon(getDrawable(R.drawable.three_dots_white_small_20)); // menü(3 pont) ikon cseréje fehérre
        //ActionBar ab = getSupportActionBar(); Kiszedtem, nem kell vissanyíl a toolbarba
        //ab.setDisplayHomeAsUpEnabled(true); Kiszedtem, nem kell vissanyíl a toolbarba

        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String wasRecreatedOnce = sp.getString(Constants.SHAREDPREFERENCES_DISTANCE_REFRESH, "null");

        if (checkGrantedPermissonForGetLocation()){ // ha engedélyezve van a helymeghatározás
            if (wasRecreatedOnce == "igen") { // a program elindítása utáni első árfolyamlista megjelenítés után x mp után frissítem a listát. X-et úgy választom meg, hogy addigra másr biztos legyen friss koordináta.
            }
            else{
                //Toast.makeText(this, "Az árfolyamok frissítésre kerültek!", Toast.LENGTH_SHORT).show();
                sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(Constants.SHAREDPREFERENCES_DISTANCE_REFRESH, "igen");
                editor.apply();
                recreateAfterDelay(5000);
            }
        }

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);
        final MyPagerAdapterBranchesAndAtmList adapter = new MyPagerAdapterBranchesAndAtmList(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        // viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

                //recreate();;
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);
            }
        });

    }

    @Override
    public void onBFragmentInteraction(Uri uri) {

    }

    /**
     * végül nem használom, máshogy lett megoldva
     */
    /*
    public void restartThisActivity() {
        final int waitingTime = getResources().getInteger(R.integer.timeToWait);

        new CountDownTimer(waitingTime*1000, 1000) { // Visszaszámlálás (absztrakt osztály)
            public void onTick(long millisUntilFinished) { // meghatározott időközönként végrehajtódik ez a metódus
                Toast.makeText(BranchAndAtmListActivity.this, "Tick", Toast.LENGTH_SHORT).show();
            }
            public void onFinish() { // számláló lejáratakor hajtódik végre
                Toast.makeText(BranchAndAtmListActivity.this, "Restart", Toast.LENGTH_SHORT).show();
                finish();
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }.start();
    }*/

    /**
     * végül nem használom, máshogy lett megoldva
     */
    /*public void restartThisActivity2() { // x db hibás kód utáni letiltás x ideig
        final int waitingTime = getResources().getInteger(R.integer.timeToWait);

        new CountDownTimer(waitingTime*1000, 1000) { // Visszaszámlálás (absztrakt osztály)
            public void onTick(long millisUntilFinished) { // meghatározott időközönként végrehajtódik ez a metódus
                Toast.makeText(BranchAndAtmListActivity.this, "Tick", Toast.LENGTH_SHORT).show();
            }
            public void onFinish() { // számláló lejáratakor hajtódik végre
                SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);

                if ((sp.getString("aktualis_latitude", ("" + 0))).equals(sp.getString("elozo_latitude", ("" + 0))) && (sp.getString("aktualis_longitude", ("" + 0))).equals(sp.getString("elozo_longitude", ("" + 0)))){
                    Toast.makeText(BranchAndAtmListActivity.this, "If", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(BranchAndAtmListActivity.this, "Else", Toast.LENGTH_SHORT).show();
                    finish();
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        }.start();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (checkGrantedPermissonForGetLocation()) {
            getMenuInflater().inflate(R.menu.menu_branchandatmlistactivity, menu); // elemek hozzáadása a toolbarhoz
            MenuCompat.setGroupDividerEnabled(menu, true); // nem jó, hibát jelez (megjavult. Át kellett állnom 28API-ra, majd kézzel berakni 2 dependencie-t
            //menuitem_BranchAndAtm_3 = menu.findItem(R.id.order1);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!

        int id = item.getItemId();

        if (id == R.id.order1){
            saveOrderSelection("address");
            //recreate();
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
            return true;
        }
        if (id == R.id.order2){
            saveOrderSelection("distance");
            //recreate();
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveOrderSelection(String orderType){
        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.SHAREDPREFERENCES_BRANCHES_AND_ATMS_ORDER_TYPE, orderType);
        editor.apply();
    }

    public boolean checkGrantedPermissonForGetLocation(){
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE); // ide NEM kellett a getContext(), mert ott van a this !
        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return true;
        } catch (SecurityException e) {
            //dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
            return false;
        }
    }

    /**
     *
     * @param delay
     * @return
     */
    public boolean recreateAfterDelay(int delay){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //recreate();        //Do something after x ms
                if (MyApp.isActivityVisible()) {
                    Toast.makeText(BranchAndAtmListActivity.this, getString(R.string.distance_data_updated), Toast.LENGTH_SHORT).show();
                    startActivity(getIntent());
                    finish();
                    overridePendingTransition(0, 0);
                }
            }
        }, delay); // in milliseconds
        return true;
    }

    @Override
    protected void onDestroy() {
        /* Ezt innen át kellett raknom a Main menübe, oda, ahol a BranchandATM activity elindítása történik
        SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
        editor.putString("tavolsagfrissiteshez_szukseges_ujrainditas_egyszer_mar_lefutott", "nem");
        editor.apply();*/
        super.onDestroy();
    }
}