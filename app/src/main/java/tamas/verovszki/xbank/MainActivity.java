package tamas.verovszki.xbank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageButton ImageButton11,ImageButton12,ImageButton21,ImageButton22,ImageButton31,ImageButton32;
    private Toolbar toolbar;
    MenuItem menuitem_logout_1;
    boolean loginState = false;
    boolean answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        init();

        SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        String loginStateisLoggedIn = sp.getString("bejelentkezve", "nem");
        if (loginStateisLoggedIn.equals("igen")){
            loginState = true;
        }
        else{
            loginState = false;
        }

        ImageButton11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(MainActivity.this, "Belépés a Netbankba!", Toast.LENGTH_SHORT).show();

                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));

                Utility.CallNextActivity(MainActivity.this, LoginActivity.class);
                //finish();
            }
        });
        ImageButton12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));

                Utility.CallNextActivity(MainActivity.this, SelectLanguageActivity.class);
                // finish();
                //Toast.makeText(MainActivity.this, "Nyelvválasztás!", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));

                Toast.makeText(MainActivity.this, "Fiók- és ATM lista!", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));

                Toast.makeText(MainActivity.this, "Árfolyamok megtekintése", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton31.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));

                Toast.makeText(MainActivity.this, "Kapcsolatfelvételi lehetőségek", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));

                Toast.makeText(MainActivity.this, "Információk", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mainactivity, menu); // elemek hozzáadása a toolbarhoz

        menuitem_logout_1 = menu.findItem(R.id.logout_toolbar_icon_1);

        if (loginState){
            menuitem_logout_1.setVisible(true);
            menuitem_logout_1.setEnabled(true);
        }
        else{
            menuitem_logout_1.setEnabled(false);
            menuitem_logout_1.setVisible(false);
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!

        int id = item.getItemId();

        if (id == R.id.logout_toolbar_icon_1){
            if (Utility.askForConfirmExit(MainActivity.this)){
                menuitem_logout_1.setEnabled(false);
                menuitem_logout_1.setVisible(false);

                SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("bejelentkezve", "nem");
                editor.apply();
                loginState = false;

                // Toast.makeText(this, "Vissza a főmenübe!", Toast.LENGTH_SHORT).show();
                // Új Activity
                /*Intent intent = new Intent(MainActivity.this, MainActivity.class); // új Activity példányosítása
                startActivity(intent); // Új Activity elindítása
                finish();*/
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void vibrateForXMillisec(int milliSec){
        Vibrator v = (Vibrator) getSystemService(MainActivity.VIBRATOR_SERVICE);
        v.vibrate(milliSec);
    }

    public void init(){
        ImageButton11 = (ImageButton) findViewById(R.id.ImageButton11);
        ImageButton12 = (ImageButton) findViewById(R.id.ImageButton12);
        ImageButton21 = (ImageButton) findViewById(R.id.ImageButton21);
        ImageButton22 = (ImageButton) findViewById(R.id.ImageButton22);
        ImageButton31 = (ImageButton) findViewById(R.id.ImageButton31);
        ImageButton32 = (ImageButton) findViewById(R.id.ImageButton32);
    }
}
