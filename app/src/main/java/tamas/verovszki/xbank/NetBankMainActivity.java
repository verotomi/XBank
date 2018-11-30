package tamas.verovszki.xbank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class NetBankMainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    boolean loginState = false;
    boolean answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_bank_main);

        init();
        setSupportActionBar(toolbar); // Toolbar megjelenítése
        loginState = Utility.getLoginState(NetBankMainActivity.this); // "belépve" állapot beolvasása SharedPreferencesből
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true); // visszanyíl megjelenítése a toolbarban . Ide nem kell visszanyíl. ott van a kilépés ikon!
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_netbankmainactivity, menu); // elemek hozzáadása a toolbarhoz
        menuitem_logout_3 = menu.findItem(R.id.logout_toolbar_icon_3);
        loginState = Utility.getLoginState(NetBankMainActivity.this);

        if (loginState){
            menuitem_logout_3.setVisible(true);
            menuitem_logout_3.setEnabled(true);
        }
        else{
            menuitem_logout_3.setEnabled(false);
            menuitem_logout_3.setVisible(false);
        }
        // Toast.makeText(this, "loginstate: " + loginState, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!

        int id = item.getItemId();

        if (id == R.id.logout_toolbar_icon_3){
            if (Utility.askForConfirmExit(NetBankMainActivity.this)){
                menuitem_logout_3.setEnabled(false);
                menuitem_logout_3.setVisible(false);

                Utility.setLoginState(NetBankMainActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                loginState = false;

                // Toast.makeText(this, "Vissza a főmenübe!", Toast.LENGTH_SHORT).show();
                // Új Activity
                Utility.CallNextActivity(NetBankMainActivity.this, MainActivity.class);

                /* átraktam külső metódusba :)
                Intent intent = new Intent(NetBankMainActivity.this, MainActivity.class); // új Activity példányosítása
                startActivity(intent); // Új Activity elindítása
                 */
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Toolbarban található visszanyíl kezelése
     * @return
     */
    /* töröltem, merrt ide mégsem kell visszanyíl! Ha ki akarunk lépni, ott a logout ikon
    @Override
    public boolean onSupportNavigateUp() {
        // ide majd kéne egy "Biztos ki akarsz lépni?" kérdés, és utána visszaa főmenübe (nem pedig a logout képernyőre)
        if (Utility.askForConfirmExit(NetBankMainActivity.this)){
            menuitem_logout_3.setEnabled(false);
            menuitem_logout_3.setVisible(false);
            Utility.setLoginState(NetBankMainActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
            loginState = false;
            Toast.makeText(this, "Vissza a főmenübe!", Toast.LENGTH_SHORT).show();
            Utility.CallNextActivity(NetBankMainActivity.this, MainActivity.class);
            finish();
        }
        return true;
        // return super.onSupportNavigateUp(); ez magától íródott be
    }*/

    public void init(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
    }
}
