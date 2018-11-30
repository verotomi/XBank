package tamas.verovszki.xbank;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button Button_1,Button_2,Button_3,Button_4,Button_5,Button_6,Button_7,Button_8,Button_9,Button_0, Button_back, Button_enter, Button_logout;
    private TextView Text_View_Enter_PIN_Code;
    private TextView Text_View_Stars;
    private TextView Text_View_Dialogue;
    private String enteredPincode="", stars="";
    final String storedPincode="123456";
    boolean loginState = false;
    boolean banned = false;
    private int wrongAttempts = 0;
    private Toolbar toolbar;
    //MenuItem menuitem_logout_2; kiszedtem utólag, ide nem kell kijelentkező ikon

    /*@Override // nem működik
    public boolean onSupportNavigateUp() { // ez a pár sor is kell az AppBar visszanyílhoz,
        finish();
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loginactivity, menu); // elemek hozzáadása a toolbarhoz

        /* kiszedtem, mert mégsem kell ide kilépés ikon
        menuitem_logout_2 = menu.findItem(R.id.logout_toolbar_icon_2);

        loginState = Utility.getLoginState(LoginActivity.this);
        if (loginState){
            menuitem_logout_2.setVisible(true);
            menuitem_logout_2.setEnabled(true);
        }
        else{
            menuitem_logout_2.setEnabled(false);
            menuitem_logout_2.setVisible(false);
        }*/
        return true;
    }

    /**
     * Itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /* kiszedtem, mert ide nem kell még "bejelentkezve" állapot
        int id = item.getItemId();

        if (id == R.id.logout_toolbar_icon_2){ // ha a kilépés ikonra kattintottunk
            if (Utility.askForConfirmExit(LoginActivity.this)){ // "biztos, hogy ki akarsz lépni?" alert
                menuitem_logout_2.setEnabled(false);
                menuitem_logout_2.setVisible(false);
                Utility.setLoginState(LoginActivity.this, false); // elmenti a "kilépve" állapotot
                loginState = false;
                Toast.makeText(this, "Vissza a főmenübe!", Toast.LENGTH_SHORT).show();
                // Új Activity
                Utility.CallNextActivity(LoginActivity.this, MainActivity.class);
                finish();
            }
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    /**
     * Toolbarban található visszanyíl kezelése
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        Utility.CallNextActivity(LoginActivity.this, MainActivity.class);
        finish();
        return true;
        // return super.onSupportNavigateUp(); ez magától íródott be
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // elforgatás tiltása
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
        setSupportActionBar(toolbar); // Toolbar megjelenítése
        loginState = Utility.getLoginState(LoginActivity.this); // be vagyunk jelentkezve?
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // visszanyíl megjelenítése a toolbarban
        if (loginState){ // ha már be vagyunk jelentkezve, akkor átugorjuk a pin-kód bekérést! MIvel ezt lekezelem, ezért feleslegessé vált ezen az activityn a kilépés gomb!
            Utility.CallNextActivity(LoginActivity.this, NetBankMainActivity.class);
            finish(); // ez fontos, kell ide!!!
        }

        Button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState == false && enteredPincode.length() < 6 && !banned) {
                    enteredPincode += "1";
                    stars += "*";
                    Text_View_Stars.setText(stars);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState == false && enteredPincode.length() < 6 && !banned) {
                    enteredPincode += "2";
                    stars += "*";
                    Text_View_Stars.setText(stars);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState == false && enteredPincode.length() < 6 && !banned) {
                    enteredPincode += "3";
                    stars += "*";
                    Text_View_Stars.setText(stars);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState == false && enteredPincode.length() < 6 && !banned) {
                    enteredPincode += "4";
                    stars += "*";
                    Text_View_Stars.setText(stars);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState == false && enteredPincode.length() < 6 && !banned) {
                    enteredPincode += "5";
                    stars += "*";
                    Text_View_Stars.setText(stars);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState == false && enteredPincode.length() < 6 && !banned) {
                    enteredPincode += "6";
                    stars += "*";
                    Text_View_Stars.setText(stars);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState == false && enteredPincode.length() < 6 && !banned) {
                    enteredPincode += "7";
                    stars += "*";
                    Text_View_Stars.setText(stars);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState == false && enteredPincode.length() < 6 && !banned) {
                    enteredPincode += "8";
                    stars += "*";
                    Text_View_Stars.setText(stars);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState == false && enteredPincode.length() < 6 && !banned) {
                    enteredPincode += "9";
                    stars += "*";
                    Text_View_Stars.setText(stars);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState == false && enteredPincode.length() < 6 && !banned) {
                    enteredPincode += "0";
                    stars += "*";
                    Text_View_Stars.setText(stars);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState == false && enteredPincode.length() > 0 && !banned) {
                    enteredPincode = enteredPincode.substring(0, enteredPincode.length() - 1); // utolsó bevitt karakter törlése
                    stars = stars.substring(0, stars.length() - 1);
                    Text_View_Stars.setText(stars);
                }
            }
        });
        Button_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!banned) {
                    if (enteredPincode.length() >= 4) { // 6--nál nagyobb nem lehet, ld: számgombok
                        if (enteredPincode.equals(storedPincode)) { // itt eredetileg ==-t írtam, de azzal nem működött:(
                            //Text_View_Dialogue.setText(getString(R.string.text_logged_in));
                            loginState = true;
                            stars = "";
                            enteredPincode = "";
                            Text_View_Stars.setText(stars);
                            Text_View_Enter_PIN_Code.setVisibility(View.INVISIBLE);
                            //Button_logout.setVisibility(View.VISIBLE); // ez még a régi gomb, amit majd ki kell szednem
                            //setLogoutIconState(); // "kijelentkezés" ikon láthatóságának a beállítása. Utolag kiszedtem ebből az activityből a kijelentkezést
                            Utility.setLoginState(LoginActivity.this, true); // bejelentkezve változó beállítása, mentése
                            Utility.CallNextActivity(LoginActivity.this, NetBankMainActivity.class);
                            finish(); // ez fontos, kell ide!!!
                        } else {
                            Text_View_Dialogue.setText(getString(R.string.text_wrong_pin_code));
                            wrongAttempts++;
                            if (wrongAttempts == getResources().getInteger(R.integer.maxWrongAttempts)) {
                                banned();
                                wrongAttempts = 0;
                                // Text_View_Dialogue.setText(R.string.text_not_logged_in); // átraktam a banned metódusba!
                            }
                            stars = "";
                            enteredPincode = "";
                            Text_View_Stars.setText(stars);
                        }
                    } else {
                        if (loginState == false) {
                            Text_View_Dialogue.setText(getString(R.string.text_pin_code_rule));
                        }
                        stars = "";
                        enteredPincode = "";
                        Text_View_Stars.setText(stars);
                    }
                }
            }
        });
        Button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // ez a gomb már nem fog kelleni!
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (loginState) {
                    loginState = false;
                    Text_View_Enter_PIN_Code.setVisibility(View.VISIBLE);
                    Text_View_Dialogue.setText(getString(R.string.text_not_logged_in));
                    Button_logout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    /**
     * Ez a metódus tartalmaz egy visszaszámláló időzítőt, az időzítő letelte után pedig elindítja a következő activity-t.
     * Ennél a visszaszámlálónál lehetőség lenne a visszaszámlálás alatt bizonyos időközönként elindítani valamilyen utasítást is a jelzett helyen.
     * Ez egy absztrakt osztály !?
     */
    public void banned() { // x db hibás kód utáni letiltás x ideig
        final int waitingTime = getResources().getInteger(R.integer.timeToWait);
        banned = true;

        new CountDownTimer(waitingTime*1000, 1000) { // Visszaszámlálás (absztrakt osztály)
            public void onTick(long millisUntilFinished) { // meghatározott időközönként végrehajtódik ez a metódus
                Text_View_Dialogue.setText(getString(R.string.text_wrong_pin_code) + " " + getString(R.string.text_banned_1) + (millisUntilFinished / 1000) + " " + getString(R.string.text_banned_2));
            }
            public void onFinish() { // számláló lejáratakor hajtódik végre
                Text_View_Dialogue.setText(R.string.text_not_logged_in);
                banned = false;
            }
        }.start();
    }

    /* @Override // ez sem működik :(
    public boolean onOptionsItemSelected(MenuItem item) { // ez a pár sor is kell az AppBar visszanyílhoz,
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true; //gondolom a return miatt nem kell a break
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void init(){
        Button_1 = (Button) findViewById(R.id.Button_1);
        Button_2 = (Button) findViewById(R.id.Button_2);
        Button_3 = (Button) findViewById(R.id.Button_3);
        Button_4 = (Button) findViewById(R.id.Button_4);
        Button_5 = (Button) findViewById(R.id.Button_5);
        Button_6 = (Button) findViewById(R.id.Button_6);
        Button_7 = (Button) findViewById(R.id.Button_7);
        Button_8 = (Button) findViewById(R.id.Button_8);
        Button_9 = (Button) findViewById(R.id.Button_9);
        Button_0 = (Button) findViewById(R.id.Button_0);
        Button_back = (Button) findViewById(R.id.Button_back);
        Button_enter = (Button) findViewById(R.id.Button_enter);
        Button_logout = (Button) findViewById(R.id.Button_logout);
        Text_View_Enter_PIN_Code = (TextView) findViewById(R.id.Text_View_Enter_PIN_Code); //töröltem
        Text_View_Stars = (TextView) findViewById(R.id.Text_View_Stars);
        Text_View_Dialogue = (TextView) findViewById(R.id.Text_View_Dialogue);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        // setTitle(R.string.app_label); // cimke frissitése. Lokalizáció miatt kell
    }

    /*public void setLogoutIconState(){ nem kell ide logout ikon
        if (loginState){
            menuitem_logout_2.setVisible(true);
            menuitem_logout_2.setEnabled(true);
        }
        else{
            menuitem_logout_2.setEnabled(false);
            menuitem_logout_2.setVisible(false);
        }
    }*/
}
