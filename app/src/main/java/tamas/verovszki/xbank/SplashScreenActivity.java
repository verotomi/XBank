package tamas.verovszki.xbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SplashScreenActivity extends AppCompatActivity {

    String selectedLanguage = ""; // a nyelv-kiválasztás kezeléséhez kell
    LinearLayout Linear_Layout_SplashScreen_1; // az üdvözlőképernyő szövegét tartalmazza
    Animation animBounce; // (le)pattogó animáció
    Animation animFadeOut; // elhalványuló animáció
    private LocationManager locationManager;
    private LocationListener locationListener;
    Location location;
    //private int szamlalo;

    //MySQL adatbázishoz kell

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        db = new DatabaseHelper(SplashScreenActivity.this); // mySQL-hez kell

        resetFlags(); // magyarázatot lásda a metódusnál!

        copySQLiteDatabaseFromAssetsToData(); // adatbázis bemásolása az assets mappából a data mappába, ha nem létezik. (arra az esetre, ha a felhasználó törölné a program adatait)

        //copyCurrenciesFromMySqlToSqlite(); // átraktam az ondestroyba, mert emulátoron lassú volt és emiatt lefagyott. Telefonon ment így is
        //copyForeignCurrenciesFromMySqlToSqlite();// átraktam az ondestroyba, mert emulátoron lassú volt és emiatt lefagyott. Telefonon ment így is

        init(); // erőforrások inicializálása

        getSavedApplicationLanguage(); // ha van elmentve korábban kiválasztott nyelv, akkor azt beolvassuk
        setLocalisation(); // lokalizáció beállítása
        saveLanguage();// elmenti a nyelv-beállítást savedPreferences-be(ha nincs mentett állapot, akkor a rendszer alapértelmezett nyelvét menti el)
        requestPermissionForGetCurrentLocation(); // engedélyt kérünk a helymeghatározásra (ha még nincs engedélyezve) Ez fogja a következő activityt elindítani!
        //animationFallDownText(); // leeső szöveg  + elhalványulás animáció + új activity indítása
    }

    /**
     *  leeső szöveg  + elhalványulás animáció + új activity indítása
     */
    private void animationFallDownText() {
        Linear_Layout_SplashScreen_1.startAnimation(animBounce); // "beeső" felirat animáció
        // callNextActivity(getResources().getInteger(R.integer.delayForSplashScreen)); // x idő múlva elindítja a soron következő activity-t.

        /**
         * Ha a felülről leeső animáció lefutott, akkor indul az elhalványulós animáció
         */
        animBounce.setAnimationListener(new Animation.AnimationListener() { // elejét kézzel írtam be, majd zárójel -> new -> Ani -> enter
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                // Linear_Layout_SplashScreen_1.startAnimation(animFadeOut); // elhalványuló felirat animáció indítása TÖRÖLTEM
                Utility.CallNextActivity(SplashScreenActivity.this, MainActivity.class);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // átmenetes animáció a két activity között
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * adatbázis bemásolása az assets mappából a data mappába, ha még nem létezne (arra az esetre, ha a felhasználó törölné a program adatait
     */
    public void copySQLiteDatabaseFromAssetsToData() {
        DataAdapter mDbHelper = new DataAdapter(SplashScreenActivity.this);
        mDbHelper.createDatabase();
        mDbHelper.open();
        //Cursor testdata = mDbHelper.getTestData(); teszteléshez kellett
        mDbHelper.close();
    }

    /**
     * UPDATE: más SharedPreferences változó(kat) is itt resetelek.
     *  amikor a splashscreen megjelenik, "nem"-re állitom a mentett helymeghatározás-elinditva változót, mert különben mindig "igaz" lenne az értéke
     *  és nem indulna el. A mainactivity-ben van elindítva a helymeghatározás, és enélkül a változó nélkül annyiszor indulna el a helymeghatározás,
     *  ahányszor újra előtérbe kerül a mainactivity (habár.... lehet, hogy erre nincs is szükség? Volt egy olyan bug,, hogy több példány futott a mainactivityből,
     *  de már kijavítottam.
     */
    public void resetFlags() {
        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.SHAREDPREFERENCES_LOCATION_STARTED, "nem");
        editor.putString(Constants.SHAREDPREFERENCES_EXCHANGE_RATE_REFRESH, "nem");
        editor.putString(Constants.SHAREDPREFERENCES_DISTANCE_REFRESH, "nem");
        editor.apply();
    }

    /**
     * Ha van elmentve korábban kiválasztott nyelv, akkor azt beolvassuk. Ha nincs, akkor kiolvassuk az rendszer alapértelmezett nyelvét.
     * Ha a kiolvasott alapértelmezett nyelv se nem angol, se nem magyar, akkor angol lesz az applikáció nyelve
     * */
    public void getSavedApplicationLanguage(){
        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        selectedLanguage = sp.getString(Constants.SHAREDPREFERENCES_LANGUAGE, "üres");
        if (selectedLanguage.equals("üres")){
                String getDefaultLanguage = (String) Locale.getDefault().getDisplayLanguage(); // ha nincs elmentve semmi, akkor kiolvassuk a rendszer alapértelmezett nyelvét
                switch (getDefaultLanguage) { // Ez a switch azért kell, mert amikor a default lokalizációt kiolvasom, nem "hu"-t vagy "en"-t kapok, hanem "magyar"-t vagy "English"-t.
                    case "magyar":
                        selectedLanguage = "hu";
                        break;
                    case "English":
                        selectedLanguage = "en";
                        break;
                    case "Deutsch":
                        selectedLanguage = "de";
                        break;
                    default:
                        selectedLanguage = "en"; // ha nincs elmentett nyelv és az alapértelmeztt nyelv nem magyar, akkor angol lesz az applikáció nyelve
                        break;
                }
        }
        // Toast.makeText(this, selectedLanguage, Toast.LENGTH_SHORT).show(); // Teszt jelleggel van itt, később kiszedni!
    }

    /**
     * Lokalizáció beállítása. Applikáció-szinten fog beállítódni, nem pedig activity szinten.
     * Viszont csak egy új Activity elindításakor lép életbe (Mert többek között újra be kell tölteni a resource/string értékeket.)
     */
    public void setLocalisation(){
        /*Locale setLocaleTo = new Locale(selectedLanguage);
        Locale.setDefault(setLocaleTo);
        Configuration config = this.getResources().getConfiguration(); // kiolvassuk a jelenlegi configurációt
        config.locale = setLocaleTo; // a kiolvasott configurációban módosítjuk a lokalizációt
        this.getResources().updateConfiguration(config, this.getResources().getDisplayMetrics()); // módosítások mentése
        this.createConfigurationContext(config);*/
        setApplicationLanguage(selectedLanguage);
    }

    /**
     * Elmenti SharedPreferences változóba a kiválasztott nyelvet
     */
    public void saveLanguage(){
        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.SHAREDPREFERENCES_LANGUAGE, selectedLanguage);
        editor.apply();
    }

    /**
     * erőforrások inicializálása
     */
    public void init(){
        Linear_Layout_SplashScreen_1 = (LinearLayout) findViewById(R.id.Linear_Layout_SplashScreen_1);
        animBounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bouncing_splashscreen_text);
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_splashscreen_text);
    }

    /**
     * UPDATE: átalakítottam a programot, így erre már nincs szükség.
     * Leellenőrzi, hogy van-e már engedély adva helymeghatározáshoz? Az eredmény függvényében más-más activity kerül meghívásra
     * @return
     *
    public boolean checkGrantedPermissonForGetLocation(){
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE); // ide NEM kellett a getContext(), mert ott van a this !
        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Toast.makeText(this, "True", Toast.LENGTH_SHORT).show();
            return true;
        } catch (SecurityException e) {
            //Toast.makeText(this, "False", Toast.LENGTH_SHORT).show();
            return false;
        }
    }*/

    /**
     * engedélyt kérünk a helymeghatározásra (ha még nincs engedélyezve)
     */
    private void requestPermissionForGetCurrentLocation(){
        // Utility.askForConfirmGetLocation(MainActivity.this); // ki akartam rakni Utilitybe de nem sikerült, lefagy a program! Nem baj, jó ez itt, ugyis csak egyszer kell hjasználni
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},1); // ACCESS_FINE_LOCATION -> Alt+Enter -> import static Constant
    }


    /**
     * Ez a metódus fogja elindítaani a következő activity-t!
     * Megvizsgáljuk, hogy van-e engedély a adva a helymeghatározáshoz. Lehetőség van ennek függvényében kétfelé ágaztatni a program továbbhaladásának a menetét
     * Én azonban - ha igen a válasz, ha nem - ugyanúhgy folytatom a programot és a kellő helyen (atm és fióklista felépítése) nézem meg, hogy engedélyezve van-e a helymeghatározás vagy sem?
     * Ezért er a onRequestPermissionsResult nálam csak egyfajta triggerként szolgál, ennek hatására lép tovább a program. Tehát ezzel sikerült elérnem, hogy amig a felhasználó nem válaszol
     * arra, hogy engedélyezi e a helymeghatározást vagy sem, addig nincs továbblépés. A onRequestPermissionsResult használata nélkül tovább lépne a program, még a felhasználó válasza előtt.
     * Így, hogy ezt sikerült megcsinálnom, kiszedhettem a programból a GetPermissionForRequestLocation activity-t, ami ugyanezt csinálta volrna, de gombnyomásra ment csak tovább, ami nem tetszett.
     * UPDATE: időközben betettem egy figyelmeztetést az egyik ágba!
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(ACCESS_FINE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        animationFallDownText();
                    } else {
                        Toast.makeText(this, getString(R.string.no_location_permission), Toast.LENGTH_SHORT).show();
                        animationFallDownText();
                    }
                }
            }
        }
    }

    /**
     * Ez mehetne az ondestroyba is. Amugy azért van itt , és nem a programtörzsben, mert az emulátoron olyan lassu volt ugy, hogy le is állt.
     */
    @Override
    protected void onStop() {
        // átraktam az ExchangeRates Activitybe. Ha ott marad, itt majd törölni lehet az ide tartozó metódusokat is
        //copyCurrenciesFromMySqlToSqlite(); - Töröltem!
        //copyForeignCurrenciesFromMySqlToSqlite(); - Töröltem!
        super.onStop();
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
        applicationRes.updateConfiguration(applicationConf, applicationRes.getDisplayMetrics());
    }
}
