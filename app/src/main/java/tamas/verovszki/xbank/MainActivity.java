package tamas.verovszki.xbank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    LinearLayout LinLay1, LinLay2, LinLay3, LinLay4, LinLay5, LinLay6;
    // ImageButton ImageButton11,ImageButton12,ImageButton21,ImageButton22,ImageButton31,ImageButton32;
    private Toolbar toolbar;
    MenuItem menuitem_logout_1;
    boolean loginState = false;
    boolean answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        setSupportActionBar(toolbar); // toolbar megjelenítése
        // loginState = Utility.getLoginState(MainActivity.this);// megnézi, hogy be vagyunk-e jelentkezve? - Ez nem kell ide, itt még nem lesz "belépve" állapot

        LinLay1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(MainActivity.this, "Belépés a Netbankba!", Toast.LENGTH_SHORT).show();
                Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                Utility.CallNextActivity(MainActivity.this, LoginActivity.class);
                finish();
            }
        });


        LinLay2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                Utility.CallNextActivity(MainActivity.this, SelectLanguageActivity.class);
                finish();
                //Toast.makeText(MainActivity.this, "Nyelvválasztás!", Toast.LENGTH_SHORT).show();
            }
        });
        LinLay3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                Utility.CallNextActivity(MainActivity.this, BranchAndAtmListActivity.class);
                //Toast.makeText(MainActivity.this, "Fiók- és ATM lista!", Toast.LENGTH_SHORT).show();
            }
        });
        LinLay4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                Utility.CallNextActivity(MainActivity.this, ExchangeRatesActivity.class);
                //Toast.makeText(MainActivity.this, "Árfolyamok megtekintése", Toast.LENGTH_SHORT).show();
            }
        });
        LinLay5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                Utility.CallNextActivity(MainActivity.this, ContactActivity.class);
                //Toast.makeText(MainActivity.this, "Kapcsolatfelvételi lehetőségek", Toast.LENGTH_SHORT).show();
            }
        });
        LinLay6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(MainActivity.this, getResources().getInteger(R.integer.vibrateLength));
                Utility.CallNextActivity(MainActivity.this, InformationActivity.class);
                //Toast.makeText(MainActivity.this, "Információk", Toast.LENGTH_SHORT).show();
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

    public void init(){
        // eeleinte csak az imagebutton-képre kattintva lehetett kiválasztani, mit akarunk.
        // átírtam, hogy az egész egységre lehessen kattintani (kép + alatta szöveg)
        LinLay1 = (LinearLayout) findViewById(R.id.LinearLayout11);
        LinLay2 = (LinearLayout) findViewById(R.id.LinearLayout12);
        LinLay3 = (LinearLayout) findViewById(R.id.LinearLayout21);
        LinLay4 = (LinearLayout) findViewById(R.id.LinearLayout22);
        LinLay5 = (LinearLayout) findViewById(R.id.LinearLayout31);
        LinLay6 = (LinearLayout) findViewById(R.id.LinearLayout32);

        //ImageButton11 = (ImageButton) findViewById(R.id.ImageButton11);
        //ImageButton12 = (ImageButton) findViewById(R.id.ImageButton12);
        //ImageButton21 = (ImageButton) findViewById(R.id.ImageButton21);
        //ImageButton22 = (ImageButton) findViewById(R.id.ImageButton22);
        //ImageButton31 = (ImageButton) findViewById(R.id.ImageButton31);
        //ImageButton32 = (ImageButton) findViewById(R.id.ImageButton32);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
    }


}
