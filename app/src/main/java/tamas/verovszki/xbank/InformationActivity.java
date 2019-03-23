package tamas.verovszki.xbank;

import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {

    TextView Text_View_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // képernyő-forgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleInformation)); // Fejléc átállítása a kiválasztott nyelver. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_information);

        init();

        // Toolbar + visszanyíl - többek között be kell állítani hozzá a manifestben a szülő Activityt + kell egy Toolbar view az XML-be.
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_4);
        setSupportActionBar(myToolbar);
        //ActionBar ab = getSupportActionBar();
        //ab.setDisplayHomeAsUpEnabled(true);

        Text_View_1.setMovementMethod(new ScrollingMovementMethod());

        Text_View_1.setText(
                "\n" +
                getString(R.string.copyright_vt) + "\n" +
                "\n\n\n" +
                getString(R.string.information_text_1) +
                "\n" +
                getString(R.string.information_text_2));
    }

    public void init(){
        Text_View_1 = findViewById(R.id.Text_View_1);
    }
}
