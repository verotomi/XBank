package tamas.verovszki.xbank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    String selectedLanguage = "";
    LinearLayout Linear_Layout_SplashScreen_1;
    Animation animBounce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSavedApplicationLanguage(); // ha van elmentve korábban kiválasztott nyelv, akkor azt beolvassuk
        setLocalisation();
        saveLanguage();// elmenti a nyelv-beállítást (ha nincs mentett állapot, akkor a rendszer alapértelmezett nyelvét menti el)


        Linear_Layout_SplashScreen_1 = (LinearLayout) findViewById(R.id.Linear_Layout_SplashScreen_1);

        animBounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bouncing_splashscreen_text);
        Linear_Layout_SplashScreen_1.startAnimation(animBounce);

        waitForXSec(getResources().getInteger(R.integer.delayForSplashScreen));
    }

    public void getSavedApplicationLanguage(){ // ha van elmentve korábban kiválasztott nyelv, akkor azt beolvassuk. Ha nincs, akkor kiolvassuk az alapértelmezettet.
        SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        selectedLanguage = sp.getString("valasztott_nyelv", "empty");
        if (selectedLanguage.equals("empty")){
                String getDefaultLanguage = (String) Locale.getDefault().getDisplayLanguage(); // ha nincs mentve, akkor kiolvassuk a jelenlegit -- NEM MŰKÖDIK!!!!!
                switch (getDefaultLanguage) { // Ez a switch azért kell, mert amikor a default lokalizációt kiolvasom, nem "hu"-t vagy "en"-t kapok, hanem "magyar"-t vagy "English"-t.
                    case "magyar":
                        selectedLanguage = "hu";
                        break;
                    case "English":
                        selectedLanguage = "en";
                        break;
                    default:
                        selectedLanguage = "en"; // ha nincs elmentett nyelv és az alapértelmeztt nyelv nem magyar!
                        break;
                }
        }
        // Toast.makeText(this, selectedLanguage, Toast.LENGTH_SHORT).show(); // Teszt jelleggel van itt, később kiszedni!
    }

    public void setLocalisation(){ // Lokalizáció - Applikációnként kell beállítani, nem pedig Activity-nként. Viszont csak egy új Activity elindításakor lép életbe (Többek között újra be kell tölteni a resource/string értékeket.)
        Locale setLocaleTo = new Locale(selectedLanguage);
        Locale.setDefault(setLocaleTo);
        Configuration config = getBaseContext().getResources().getConfiguration(); // kiolvassuk a jelenlegi configot
        config.locale = setLocaleTo; // a kiolvasott configban módosítjuk a lokalizációt
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics()); // módosítások mentése
    }

    public void saveLanguage(){
        SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("valasztott_nyelv", selectedLanguage);
        editor.apply();
    }

    public void callNextActivity(){ // Új Activity hívása
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class); // új Activity példányosítása
        startActivity(intent); // Új Activity elindítása
        finish();
    }

    public void waitForXSec(int millis) { // x db hibás kód utáni letiltás y ideig
        final int waitingTime = getResources().getInteger(R.integer.delayForSplashScreen); // in milliseconds
        new CountDownTimer(waitingTime, 1000) { // Visszaszámlálás (absztrakt osztály)
            public void onTick(long millisUntilFinished) {
                // a meghatározott időközönként végrehajtódik, amit ide írok
            }
            public void onFinish() { // számláló lejáratakor hajtódik végre

                callNextActivity();  // Következő Activity hívása
            }
        }.start();
    }
}
