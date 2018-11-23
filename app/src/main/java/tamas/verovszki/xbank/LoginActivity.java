package tamas.verovszki.xbank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    MenuItem menuitem_logout_2;

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

        // setTitle(R.string.app_label); // cimke frissitése. Lokalizáció miatt kell
    }

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

    public void vibrateForXMillisec(int milliSec){
        Vibrator v = (Vibrator) getSystemService(MainActivity.VIBRATOR_SERVICE);
        v.vibrate(milliSec);
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

    /*@Override // nem működik
    public boolean onSupportNavigateUp() { // ez a pár sor is kell az AppBar visszanyílhoz,
        finish();
        return true;
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loginactivity, menu); // elemek hozzáadása a toolbarhoz

        menuitem_logout_2 = menu.findItem(R.id.logout_toolbar_icon_2);
        if (loginState){
            menuitem_logout_2.setVisible(true);
            menuitem_logout_2.setEnabled(true);
        }
        else{
            menuitem_logout_2.setEnabled(false);
            menuitem_logout_2.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!

        int id = item.getItemId();

        if (id == R.id.logout_toolbar_icon_2){
            if (Utility.askForConfirmExit(LoginActivity.this)){
                menuitem_logout_2.setEnabled(false);
                menuitem_logout_2.setVisible(false);

                SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("bejelentkezve", "nem");
                editor.apply();
                loginState = false;

                Toast.makeText(this, "Vissza a főmenübe!", Toast.LENGTH_SHORT).show();
                // Új Activity
                Utility.CallNextActivity(LoginActivity.this, MainActivity.class);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // elforgatás tiltása
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        String loginStateisLoggedIn = sp.getString("bejelentkezve", "nem");
        if (loginStateisLoggedIn.equals("igen")){
            loginState = true;
        }
        else{
            loginState = false;
        }

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true); // visszanyíl az AppBar-ba

        init();

        Button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
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

                            SharedPreferences sp = getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("bejelentkezve", "igen");
                            editor.apply();

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
            public void onClick(View view) {
                vibrateForXMillisec(getResources().getInteger(R.integer.vibrateLength));
                if (loginState) {
                    loginState = false;
                    Text_View_Enter_PIN_Code.setVisibility(View.VISIBLE);
                    Text_View_Dialogue.setText(getString(R.string.text_not_logged_in));
                    Button_logout.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
