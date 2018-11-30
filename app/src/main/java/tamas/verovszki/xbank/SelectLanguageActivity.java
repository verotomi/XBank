package tamas.verovszki.xbank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Locale;

public class SelectLanguageActivity extends AppCompatActivity {

    private Button ButtonSelectLanguageProceed;
    private RadioButton RadioButtonEnglish, RadioButtonHungarian;
    private RadioGroup RadioGroup1;
    //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_2);
    String selectedLanguage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        // Toolbar + visszanyíl - többek között be kell állítani hozzá a manifestben a szülő Activityt + kell egy Toolbar view az XML-be.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_2);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        init();
        checkCorrespondingRadioButton();

        ButtonSelectLanguageProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whichLanguageSelected(); // melyik nyelv lett kiválasztva? (radio-button gombok kiolvasása)
                saveSelectedLanguage(); // elmenti a kiválasztot nyelvet sharedPreferences-be

                // Új Activity
                Utility.CallNextActivity(SelectLanguageActivity.this, MainActivity.class);
                finish();
            }
        });
    }

    public void init(){
        ButtonSelectLanguageProceed = (Button) findViewById(R.id.ButtonSelectLanguageProceed);
        RadioButtonEnglish = (RadioButton) findViewById(R.id.RadioButtonEnglish);
        RadioButtonHungarian = (RadioButton) findViewById(R.id.RadioButtonHungarian);
        RadioGroup1 = (RadioGroup) findViewById(R.id.RadioGroup1);
    }

    public void whichLanguageSelected(){
        if (RadioButtonEnglish.isChecked()){
            selectedLanguage = "en";
        }
        else if (RadioButtonHungarian.isChecked()){
            selectedLanguage = "hu";
        }

        // Lokalizáció - Applikációnként kell beállítani, nem pedig Activity-nként. Viszont csak egy új Activity elindításakor lép életbe (Többek között újra be kell tölteni a resource/string értékeket.)
        Locale setLocaleTo = new Locale(selectedLanguage);
        Locale.setDefault(setLocaleTo);
        Configuration config = getBaseContext().getResources().getConfiguration(); // kiolvassuk a jelenlegi configot
        config.locale = setLocaleTo; // a kiolvasott configban módosítjuk a lokalizációt
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics()); // módosítások mentése
    }

    public void checkCorrespondingRadioButton(){
        SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        String chosenLanguage = sp.getString("valasztott_nyelv", "empty");
        // Toast.makeText(this, chosenLanguage, Toast.LENGTH_SHORT).show(); // Tesztelés után kiszedni
        switch (chosenLanguage){
            case "hu":
                RadioButtonHungarian.setChecked(true);
                RadioButtonEnglish.setChecked(false); // ez nem biztos, hogy kell ide
                break;
            case "en":
                RadioButtonEnglish.setChecked(true);
                RadioButtonHungarian.setChecked(false); // ez nem biztos, hogy kell ide
                break;
            /*case "empty": // asszem ez nem kell ide, itt már nem tud empty lenni
                RadioButtonEnglish.setChecked(true);
                RadioButtonHungarian.setChecked(false);
                break;*/
            default: // ez nem biztos hogy kell ide
                break;
        }
    }

    public void saveSelectedLanguage(){
        SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("valasztott_nyelv", selectedLanguage);
        editor.apply();
    }


}
