package tamas.verovszki.xbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {

    String selectedLanguage = ""; // a nyelv-kiválasztás kezeléséhez kell
    LinearLayout Linear_Layout_SplashScreen_1; // az üdvözlőképernyő szövegét tartalmazza
    Animation animBounce; // (le)pattogó animáció
    Animation animFadeOut; // elhalványuló animáció

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        init(); // erőforrások inicializálása
        getSavedApplicationLanguage(); // ha van elmentve korábban kiválasztott nyelv, akkor azt beolvassuk
        setLocalisation(); // lokalizáció beállítása
        saveLanguage();// elmenti a nyelv-beállítást savedPreferences-be(ha nincs mentett állapot, akkor a rendszer alapértelmezett nyelvét menti el)
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
                Utility.CallNextActivity(SplashScreenActivity.this, MainActivity.class); // következő activity indítása
                finish(); // jelen activity befejezése
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        /**
         * Ha véget ért a leeső animáció, némi késleltetéssel (ld: offset az animációban) utána indul az elhalványulás.
         * Ha annak is vége, akkor elindul a következő activity, ez pedig befejeződik
         */

        /* TÖRÖLTEM
        animFadeOut.setAnimationListener(new Animation.AnimationListener() { // elejét kézzel írtam be, majd zárójel -> new -> Ani -> enter
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                // Utility.CallNextActivity(SplashScreenActivity.this, MainActivity.class); // következő activity indítása
                // finish(); // jelen activity befejezése
                // overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });*/

    }

    /**
     * Ha van elmentve korábban kiválasztott nyelv, akkor azt beolvassuk. Ha nincs, akkor kiolvassuk az rendszer alapértelmezett nyelvét.
     * Ha a kiolvasott alapértelmezett nyelv se nem angol, se nem magyar, akkor angol lesz az applikáció nyelve
     * */
    public void getSavedApplicationLanguage(){
        SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        selectedLanguage = sp.getString("valasztott_nyelv", "üres");
        if (selectedLanguage.equals("üres")){
                String getDefaultLanguage = (String) Locale.getDefault().getDisplayLanguage(); // ha nincs elmentve semmi, akkor kiolvassuk a rendszer alapértelmezett nyelvét
                switch (getDefaultLanguage) { // Ez a switch azért kell, mert amikor a default lokalizációt kiolvasom, nem "hu"-t vagy "en"-t kapok, hanem "magyar"-t vagy "English"-t.
                    case "magyar":
                        selectedLanguage = "hu";
                        break;
                    case "English":
                        selectedLanguage = "en";
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
        Locale setLocaleTo = new Locale(selectedLanguage);
        Locale.setDefault(setLocaleTo);
        Configuration config = getBaseContext().getResources().getConfiguration(); // kiolvassuk a jelenlegi configurációt
        config.locale = setLocaleTo; // a kiolvasott configurációban módosítjuk a lokalizációt
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics()); // módosítások mentése
    }

    /**
     * Elmenti SharedPreferences változóba a kiválasztott nyelvet
     */
    public void saveLanguage(){
        SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("valasztott_nyelv", selectedLanguage);
        editor.apply();
    }

    /**
     * Ez a metódus 2 részből áll.
     * Tartalmaz egy visszaszámláló időzítőt, az időzítő letelte után pedig elindítja a következő activity-t.
     * Ennél a visszaszámlálónál lehetőség lenne a visszaszámlálás alatt bizonyos időközönként elindítani valamilyen utasítást is a jelzett helyen.
     * Ez egy absztrakt osztály !?
     * @param delay : ennyi ideig tart a visszaszámlálás

    public void callNextActivity(int delay){
        new CountDownTimer(delay, 1000) {
            public void onTick(long millisUntilFinished) {
                // Toast.makeText(SplashScreenActivity.this, (millisUntilFinished / 1000) + "seconds remaining", Toast.LENGTH_SHORT).show();
                // a meghatározott időközönként végrehajtódik, amit ide írok
            }
            public void onFinish() { // a visszaszámláló lejáratakor hajtódik végre
                Utility.CallNextActivity(SplashScreenActivity.this, MainActivity.class);  // következő activity hívása
            }
        }.start();
        finish();
    }
     */

    /**
     * erőforrások inicializálása
     */
    public void init(){
        Linear_Layout_SplashScreen_1 = (LinearLayout) findViewById(R.id.Linear_Layout_SplashScreen_1);
        animBounce = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bouncing_splashscreen_text);
        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out_splashscreen_text);
    }
}
