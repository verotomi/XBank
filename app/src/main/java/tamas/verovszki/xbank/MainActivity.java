package tamas.verovszki.xbank;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    LinearLayout LinLay1, LinLay2, LinLay3, LinLay4, LinLay5, LinLay6;
    ImageView ImageButton11,ImageButton12,ImageButton21,ImageButton22,ImageButton31,ImageButton32;
    private Toolbar toolbar;

    private LocationManager locationManager;
    private LocationListener locationListener;
    boolean doubleBackToExitPressedOnce = false;
    private String isGetCurrentLocationStarted = "nem"; // ezzel a változóval figyelem, hogy már elindult-e egyszer a helyzet-lekérdezés, mert enélkül ahányszor előtérbe kerül a mainactivity, annyiszor elindul egy ujabb helymeghatározó listener

    public static Activity mainActivity; // ahhoz kell, hogy máshonnan be tudjam zárni ezt az activityt


    /**
     * ez azért kellett, hogy a nyelvválasztásból vissza tudjak térni rendesen.
     * Először ugy hivtam meg a nyelvválasztást, hogy nem fejeztem be finish-el a mainactivity-t. A nyelvválasztás submit gombja után meg meghívtam ujra a mainactivityt.
     * Ez nem volt jó, mert ha a nyelvválasztásból a visszanyíllal jöttem vissza, nem pedig a submit gombbal, akkor egy ujabb mainactivity intent keletkezett, ami nem jó.
     * Utána ugy hivtam meg a nyelvválasztást, hogy finish-el befejeztem a mainactivity-t. Ez sem volt jó, mert íyg a nyelvválasztásnaál a visszanyíllal kiléptünk a
     * programból nem volt hova visszatérni, a stack-ben már nem szerepelt a mainactivity
     * Ezután kitaláltam, hogy a a nyelvválasztás meghivásakor nem finish-elem a mainactivityt, a nyelvválasztásnál a submit gomb pedig csak simán finisheli a
     * nyelvválasztást, így visszatér a program a stack-ben szereplő mainactivityhez. Ez működött is, de így nem lépett életbe a nyelvváltoztatás.
     * Ujra kellett tehát inditani a mainactivity-t. Először az onResume()-ben probáltam azt ujrainditást, de nem működött jól. Utána az onPause()-ben, de ott sem volt
     * jó. Ezután arájöttem, hogy az onRestart() a jó hely erre. Így már működik is, habár ugy veszem észre, picit lassult a program. Habár a lassulást az emulátoron nem veszem észre
     */
    @Override
    protected void onRestart() {
        // ujra akarta rajzoltatni az activity_main xml-t, de egyik módszerrel sem sikerült.
        //
        // LinLay3.invalidate();
        // findViewById(android.R.id.content).invalidate();
        // LinLay1.requestLayout();
        // LinLay1.measure(0,0);
        // LinLay3.requestLayout();
        // setContentView(R.layout.activity_main);
        // LinLay2.removeAllViews();
        // LinLay2.refreshDrawableState();
        // LinLay2.setVisibility(LinLay2.GONE);
        // LinLay2.setVisibility(LinLay2.VISIBLE);
        // TextView31 = findViewById(R.id.TextView31);
        // TextView31.requestLayout();
        /* ez a kicsillagozott rész elvileg ugyanazt csinálná, mint a recereate(), de ez sem működött
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
        overridePendingTransition(0, 0);*/

        // egyedül a recreate-vel tudom ujra rajzolni a mainactivity-t (ami a nyelv választás miatt kell), viszont ez  mindig elinditott egy ujabb helymeghatarozas-hívást.
        // ezt sikerült kiküszöbölnöm egy változóval, amit átállitok, ha már egyszer elindult a helymeghatározás, és utána elmantem sharedpreferencesbe, majd ha kell, kiolvasom.
        // az activity recreate() miatt kellett a sharedpreferences mentés

        //recreate();
        startActivity(getIntent());
        finish();
        overridePendingTransition(R.anim.anim_fade_in_4, R.anim.anim_fade_out_4); // átmenetes animáció a két activity között
        //        overridePendingTransition(0, 0);
        super.onRestart();
    }

    @SuppressLint("ClickableViewAccessibility") // ez amiatt kellett ide, mert a LinLay-ek onTouch listenerjeinél overrideolnom kellett volna a performclick()-et, de nem sikerült. (A performclick override azért kellett volna, hogy a látássérültek is tudják használni az appot)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleMainmenu)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_main);

        mainActivity = this; // ahhoz kell, hogy máshonnan be tudjam zárni ezt az activityt


        /*
        // Hide Status Bar
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = getWindow().getDecorView();
            // Hide Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }*/

        // végül ezek ide nem kellenek, csak a getcurrentlocation metodusnál kell figyelni a változót!
        //ha a splashscreen-ről kerülünk ide, akkor az isGetCurrentLocationStarted "nem" lesz, ha csak ujraindul a mainactivity, akkor meg "igen" lesz
        // igy csak 1x fog futni a helymeghatározós listener
        //SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        //String isGetCurrentLocationStarted = sp.getString("helymeghatarozas_elinditva", "null");
        //Toast.makeText(this, "1. " + isGetCurrentLocationStarted, Toast.LENGTH_SHORT).show();

        // requestPermissionForGetCurrentLocation(); // Első alkalommal engedélyt kérünk a helymeghatározáshoz KISZEDVE, ÁTHELYEZVE MÁSHOVA

        getCurrentLocation();

        init();

        setSupportActionBar(toolbar); // toolbar megjelenítése
        // loginState = Utility.getLoginState(MainActivity.this);// megnézi, hogy be vagyunk-e jelentkezve? - Ez nem kell ide, itt még nem lesz "belépve" állapot


        LinLay1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        ImageButton11.setBackground(getResources().getDrawable(R.drawable.entertonetbankpressed));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // RELEASED
                        ImageButton11.setBackground(getResources().getDrawable(R.drawable.entertonetbank85));
                        Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                        Utility.CallNextActivity(MainActivity.this, LoginActivity.class);
                        overridePendingTransition(R.anim.anim_fade_in_4, R.anim.anim_fade_out_4); // átmenetes animáció a két activity között
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        LinLay2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        ImageButton12.setBackground(getResources().getDrawable(R.drawable.languagepressed));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // RELEASED
                        ImageButton12.setBackground(getResources().getDrawable(R.drawable.language85));
                        Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                        Utility.CallNextActivity(MainActivity.this, SelectLanguageActivity.class);
                        overridePendingTransition(R.anim.anim_fade_in_4, R.anim.anim_fade_out_4); // átmenetes animáció a két activity között
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        LinLay3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        ImageButton21.setBackground(getResources().getDrawable(R.drawable.branchandatmlistpressed));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // RELEASED
                        ImageButton21.setBackground(getResources().getDrawable(R.drawable.branchandatmlist85));
                        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString(Constants.SHAREDPREFERENCES_DISTANCE_REFRESH, "nem");
                        editor.apply();
                        Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                        Utility.CallNextActivity(MainActivity.this, BranchAndAtmListActivity.class);
                        overridePendingTransition(R.anim.anim_fade_in_4, R.anim.anim_fade_out_4); // átmenetes animáció a két activity között
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        LinLay4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        ImageButton22.setBackground(getResources().getDrawable(R.drawable.rateofexchangepressed));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // RELEASED
                        ImageButton22.setBackground(getResources().getDrawable(R.drawable.rateofexchange85));
                        Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                        Utility.CallNextActivity(MainActivity.this, ExchangeRatesActivity.class);
                        overridePendingTransition(R.anim.anim_fade_in_4, R.anim.anim_fade_out_4); // átmenetes animáció a két activity között
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        LinLay5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        ImageButton31.setBackground(getResources().getDrawable(R.drawable.contactpressed));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // RELEASED
                        ImageButton31.setBackground(getResources().getDrawable(R.drawable.contact85));
                        Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                        Utility.CallNextActivity(MainActivity.this, ContactActivity.class);
                        overridePendingTransition(R.anim.anim_fade_in_4, R.anim.anim_fade_out_4); // átmenetes animáció a két activity között
                        return true; // if you want to handle the touch event
                }
                return false;
            }
        });

        LinLay6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // PRESSED
                        ImageButton32.setBackground(getResources().getDrawable(R.drawable.informationpressed));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // RELEASED
                        ImageButton32.setBackground(getResources().getDrawable(R.drawable.information85));
                        Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                        Utility.CallNextActivity(MainActivity.this, InformationActivity.class);
                        overridePendingTransition(R.anim.anim_fade_in_4, R.anim.anim_fade_out_4); // átmenetes animáció a két activity között
                        return true; // if you want to handle the touch event
            }
                return false;
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mainactivity, menu); // elemek hozzáadása a toolbarhoz

        /* //kiszedtem. Itt még ne legyen "bejelentkezve" állapo
        menuitem_logout_1 = menu.findItem(R.id.logout_toolbar_icon_1);

        if (loginState){
            menuitem_logout_1.setVisible(true);
            menuitem_logout_1.setEnabled(true);
        }
        else{
            menuitem_logout_1.setEnabled(false);
            menuitem_logout_1.setVisible(false);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!

        /* kiszedtem, mert ide nem kell még "bejelentkezve" állapot
        int id = item.getItemId();

        if (id == R.id.logout_toolbar_icon_1){
            if (Utility.askForConfirmExit(MainActivity.this)){
                menuitem_logout_1.setEnabled(false);
                menuitem_logout_1.setVisible(false);

                Utility.setLoginState(MainActivity.this, false); // elmenti a "kilépve" állapotot
                loginState = false;

                // Toast.makeText(this, "Vissza a főmenübe!", Toast.LENGTH_SHORT).show();
                // Új Activity
                /*Intent intent = new Intent(MainActivity.this, MainActivity.class); // új Activity példányosítása
                startActivity(intent); // Új Activity elindítása
                finish();
            }
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void init() {
        // eeleinte csak az imagebutton-képre kattintva lehetett kiválasztani, mit akarunk.
        // átírtam, hogy az egész egységre lehessen kattintani (kép + alatta szöveg)
        LinLay1 = (LinearLayout) findViewById(R.id.LinearLayout11);
        LinLay2 = (LinearLayout) findViewById(R.id.LinearLayout12);
        LinLay3 = (LinearLayout) findViewById(R.id.LinearLayout21);
        LinLay4 = (LinearLayout) findViewById(R.id.LinearLayout22);
        LinLay5 = (LinearLayout) findViewById(R.id.LinearLayout31);
        LinLay6 = (LinearLayout) findViewById(R.id.LinearLayout32);

        ImageButton11 = findViewById(R.id.ImageButton11);
        ImageButton12 = findViewById(R.id.ImageButton12);
        ImageButton21 = findViewById(R.id.ImageButton21);
        ImageButton22 = findViewById(R.id.ImageButton22);
        ImageButton31 = findViewById(R.id.ImageButton31);
        ImageButton32 = findViewById(R.id.ImageButton32);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
    }



    /* kiszedve, mert átkerült máshova
    public boolean isSavedLocation(){
        SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        String savedLongitude = sp.getString("aktualis_longitude", "null");
        String savedLatitude = sp.getString("aktualis_latitude", "null");
        if (savedLatitude.equals("null") || savedLongitude.equals("null")){
            return false;
        }
        else{
            return true;
        }
    }*/

    private void requestPermissionForGetCurrentLocation(){
        // Utility.askForConfirmGetLocation(MainActivity.this); // ki akakrtam rakni Utilitybe de nem sikerültl, lefagy a program!
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},1); // ACCESS_FINE_LOCATION -> Alt+Enter -> import static Constant

    }

    /**
     * helymeghatározás
     * arra jutottam, hogy a helymeghatározás 3. ága ferlesleges volt, ezért kicsillagoztam
     * a Toast b is lefut egyszer és a c is lefutott egymás után
     *   mintinme és mindistance értékeket le kellett venni 0-ra , így ha minden igaz, elindításkor rögtön lekéri a helyzetünket, és változáskor is azonnal
     *   UPDATE: mintime= 0 jól működött emulátoron, de a telefonomon túl sokszor kérte le az adatokat, szinte mp-enként többször is. Ezért feljebb raktam az értékét!
     *   Ráadásul az engedélykérést is előbbre kell vennem, valszeg be kell rakni egy új üres activity-t hozzá! Ami nem túl nagy baj, mert később nem fog látszódni, ha már
     *   meg van az engedély.
     */
    public void getCurrentLocation(){
        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        String isGetCurrentLocationStarted = sp.getString(Constants.SHAREDPREFERENCES_LOCATION_STARTED, "null");
            //Toast.makeText(this, "1.5 " + isGetCurrentLocationStarted, Toast.LENGTH_SHORT).show();
            locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    //Toast.makeText(GetCurrentLocation.this, "Location :" + location, Toast.LENGTH_SHORT).show();
                    //Log.d("Location:", location.toString());

                    saveCurrentLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

        //Toast.makeText(this, "2. " + isGetCurrentLocationStarted, Toast.LENGTH_SHORT).show();
        if (isGetCurrentLocationStarted.equals("igen")) { // ez azért van itt, hogy a program futása során ez csak 1x induljon el, ne pedig minden alkalommal, amikor a mainactivity ujraindul
            //Toast.makeText(this, "If", Toast.LENGTH_SHORT).show();
        }
        else{
            //Toast.makeText(this, "else", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT < 23) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                //Toast.makeText(this, "a", Toast.LENGTH_SHORT).show();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
            }
            else{
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1); // - ezt utólag ki kellett szedjem, mert 2x kérdezte meg
                }
                else{
                    //Toast.makeText(this, "b", Toast.LENGTH_SHORT).show();
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
                }
            }
            // itt mentjük el a sharedpreferencesbe azt, hogy egyszer már el lett inditva a helymeghatározás-kérés, így többször már nem fog elindulni
            sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
            isGetCurrentLocationStarted = "igen";
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(Constants.SHAREDPREFERENCES_LOCATION_STARTED, isGetCurrentLocationStarted);
            editor.apply();
        }
    }

    /**
     * ez is a helymeghatározás része!
     * időközben rájöttem, hogy enélkül is működik a helymeghatározás!
     * @param requestCode
     * @param permissions
     * @param grantResults
     *
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this, "c", Toast.LENGTH_SHORT).show();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, locationListener);
            }

        }
    }*/

    /**
     * ha sikerült a helymeghatározás, elmentem sharedPreferencesbe a latitude-longitude értékeket, hogy ki tudjam majd olvasni onnan ott, ahol kell
     * @param location
     */
    public void saveCurrentLocation(Location location){

        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(Constants.SHAREDPREFERENCES_LONGITUDE_PREVIOUS, sp.getString(Constants.SHAREDPREFERENCES_LONGITUDE_CURRENT, "" + 0));
        editor.putString(Constants.SHAREDPREFERENCES_LATITUDE_PREVIOUS, sp.getString(Constants.SHAREDPREFERENCES_LATITUDE_CURRENT, "" + 0));

        editor.putString(Constants.SHAREDPREFERENCES_LONGITUDE_CURRENT, "" + location.getLongitude());
        editor.putString(Constants.SHAREDPREFERENCES_LATITUDE_CURRENT, "" + location.getLatitude());
        editor.apply();

        //Toast.makeText(this, "Saved " + szamlalo++ + " times", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

        // A kilépés-megkérdezés helyett beraktam a 2x visszanyíl-ra kilépést
        /*new AlertDialog.Builder(this)
                .setTitle(R.string.really_exit)
                //.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null) // érdekesség: előre definiált szöveg. Nem r.string.xxx, hanem android.R.String.xxx
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        Utility.setLoginState(MainActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                        // MainActivity.super.onBackPressed(); // ha ez nincs itt, akkor nem menti el a "kilépve" állapotot az emulátoron! A telefonon meg egyáltalán nem menti el :( Megoldás: onDestroy()-ba raktam ki a "kilépve" változó mentését.
                        Process.killProcess(Process.myPid());
                    }
                }).create().show();*/

        // kilépés 2x vissza-gombra!
        if (doubleBackToExitPressedOnce) {
            Utility.saveLoginState(MainActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
            Process.killProcess(Process.myPid());
            //super.onBackPressed();
            //return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.back_button_double_press, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);



    }



    @Override
    protected void onDestroy() {
        //Utility.saveLoginState(MainActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
        super.onDestroy();
    }
}

