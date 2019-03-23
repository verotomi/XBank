package tamas.verovszki.xbank;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity implements PerformNetworkRequest.TaskDelegate {

    private Button Button_1, Button_2, Button_3, Button_4, Button_5, Button_6, Button_7, Button_8, Button_9, Button_0, Button_back, Button_enter;
    private TextView Text_View_Enter_PIN_Code;
    private TextView Text_View_Enter_ID;
    private TextView Text_View_Stars_Pin;
    private TextView Text_View_Stars_Id;
    private TextView Text_View_Dialogue;
    private TextView Text_View_Fingerprint;
    private String enteredPincode = "";
    private String enteredId = "";
    private String starsPin = "";
    private String stringId = "";
    boolean loginState = false;
    boolean enteredIdMatches = false;
    boolean banned = false;
    private int wrongAttempts = 0;
    private Toolbar toolbar;
    private SwipeRefreshLayout SwipeRefreshLayout;
    private LinearLayout LinLayId;
    private LinearLayout LinLayPin;


    // animációkhoz
    Animation animFadeIn2; // elhalványuló animáció
    Animation animFadeOut2; // elhalványuló animáció
    int animationCounter = 0; // pinkód-kérés animáció csak 1x fusson le

    // Ujjlenyomat-azonossításhoz
    // Declare a string variable for the key we’re going to use in our fingerprint authentication
    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    FingerprintHandler helper;

    // Lottie-animációhoz
    private LottieAnimationView LottieFingerPrint;

    //  Bejelentkezéshez, intent-átadáshoz, sharedppreferences mentéshez
    private String userFirstName;
    private int userId;
    private String userLastName;
    private String userLastLoginTime;
    private int savedPinCode;
    private int savedId;

    //MySQL adatbázishoz

    PerformNetworkRequest request;

    //SQLite adatbázishoz
    private DatabaseHelper db;

    private long remainingTimeBanned; // hogy el tudjam menteni, ha háttérbe kerül az activity

    SharedPreferences sp;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loginactivity, menu); // elemek hozzáadása a toolbarhoz

        return true;
    }

    /*
     * Itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        /*if (id == R.id.back){
            finish();
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }


    /*
     * Toolbarban található visszanyíl kezelése
     * Ez nem fog kelleni, kicseréltem a visszanyilat
     * @return
     */
    /* kiszedtem a visszanyílat a toolbarból igy ez sem kell!
    @Override
    public boolean onSupportNavigateUp() {
        Utility.CallNextActivity(LoginActivity.this, MainActivity.class);
        finish();
        return true;
        // return super.onSupportNavigateUp(); ez magától íródott be
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // elforgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitleLogin)); // Fejléc átállítása a kiválasztott nyelvre. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_login);

        init();

        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        remainingTimeBanned = Long.parseLong(sp.getString(Constants.SHAREDPREFERENCES_BANNED_REMAINING_TIME, "0"));
        if (remainingTimeBanned > 0) {
            banned(remainingTimeBanned / 1000 + 1);
        }


        //Text_View_Enter_ID.startAnimation(animFadeIn2);
        LinLayId.startAnimation(animFadeIn2);
        //Text_View_Enter_PIN_Code.setVisibility(View.INVISIBLE);
        LinLayPin.setVisibility(View.INVISIBLE);


        /**
         * Ujjlenyomat-azonosításhoz kell
         */
        // If you’ve set your app’s minSdkVersion to anything lower than 23, then you’ll need to verify that the device is running Marshmallow
        // or higher before executing any fingerprint-related code
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Get an instance of KeyguardManager and FingerprintManager//
            keyguardManager =
                    (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager =
                    (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            //textView = (TextView) findViewById(R.id.textview);

            //Check whether the device has a fingerprint sensor//
            // ki kellett szedni, mert a GenyMotion Galaxy S8 leállt itt. Gondolom, nem volt engedélyezve/beállítva az ujjlenyomat
            /*if (!fingerprintManager.isHardwareDetected()) {
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                //Toast.makeText(this, "Your device doesn't support fingerprint authentication", Toast.LENGTH_SHORT).show();
                //textView.setText("Your device doesn't support fingerprint authentication");
            }*/

            //Check whether the user has granted your app the USE_FINGERPRINT permission//
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // If your app doesn't have this permission, then display the following text//
                //Toast.makeText(this, "Please enable the fingerprint permission", Toast.LENGTH_SHORT).show();
                //textView.setText("Please enable the fingerprint permission");
            }

            //Check that the user has registered at least one fingerprint//
            // ki kellett szedni, mert a GenyMotion Galaxy S8 leállt itt. Gondolom, nem volt engedélyezve/beállítva az ujjlenyomat
            /*if (!fingerprintManager.hasEnrolledFingerprints()) {
                // If the user hasn’t configured any fingerprints, then display the following message//
                // Toast.makeText(this, "No fingerprint configured. Please register at least one fingerprint in your device's Settings", Toast.LENGTH_SHORT).show();
                //textView.setText("No fingerprint configured. Please register at least one fingerprint in your device's Settings");
            }*/

            //Check that the lockscreen is secured//
            if (!keyguardManager.isKeyguardSecure()) {
                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                //Toast.makeText(this, "Please enable lockscreen security in your device's Settings", Toast.LENGTH_SHORT).show();
                //textView.setText("Please enable lockscreen security in your device's Settings");
            } else {
                try {
                    generateKey();
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }

                if (initCipher()) {
                    //If the cipher is initialized successfully, then create a CryptoObject instance//
                    cryptoObject = new FingerprintManager.CryptoObject(cipher);

                    // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                    // for starting the authentication process (via the startAuth method) and processing the authentication process events//

                    // ha van elmentett user_id a sharedpreferences-ben ( tehát már lett megadva user_id + Pin-kod, amivel vissza tudtuk kapni mysql-ből
                    // a felhasználó id-jét, akkor engedélyezzük az ujjlenyomatos azonosítást. Anélkül nem, mert nem tudnánk mi alapján kikeresni a felhasználót a mysql-ből

                    sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
                    userId = Integer.parseInt(sp.getString(Constants.SHAREDPREFERENCES_USER_ID, ""));
                    try {
                        savedId = Integer.parseInt(sp.getString(Constants.SHAREDPREFERENCES_ID_ENTERED, "0"));
                        savedPinCode = Integer.parseInt(sp.getString(Constants.SHAREDPREFERENCES_PINCODE_USER, "0"));
                    } catch (Exception e) {
                    }

                    if (userId != 0) {
                        helper = new FingerprintHandler(this);
                        Text_View_Fingerprint.setVisibility(View.VISIBLE);
                        LottieFingerPrint.setVisibility(View.VISIBLE);
                        helper.startAuth(fingerprintManager, cryptoObject);
                        helper.setCustomObjectListener(new FingerprintHandler.MyCustomObjectListener() {
                            @Override
                            public void onFingerprintAuthorized(String title) {
                                tryToLogin(String.valueOf(savedId), String.valueOf(savedPinCode));
                                //Toast.makeText(LoginActivity.this, title, Toast.LENGTH_SHORT).show();
                            }

                        });

                    }
                }
            }
        }

        setSupportActionBar(toolbar); // Toolbar megjelenítése
        loginState = Utility.getLoginState(LoginActivity.this); // be vagyunk jelentkezve?

        /**
         * megnézni, hogy ide egyáltalán eljut-e valamikor is a program ??
         */
        /*if (loginState) { // ha már be vagyunk jelentkezve, akkor átugorjuk a bejelentkezési kérést! Mivel ezt lekezelem, ezért feleslegessé vált ezen az activityn a kilépés gomb!
            try {
                helper.stopAuth();
            } catch (Exception e) {
            }
            Utility.CallNextActivity(LoginActivity.this, MobilBankMainActivity.class);
            overridePendingTransition(R.anim.anim_fade_in_4, R.anim.anim_fade_out_4); // átmenetes animáció a két activity között
        }*/

        /**
         * A gombok figyelése és kezelése
         */
        Button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (enteredId.length() < 8 && !banned) {
                    if (!enteredIdMatches) {
                        enteredId += "1";
                        stringId += "1";
                        Text_View_Stars_Id.setText(stringId);
                        Text_View_Dialogue.setText(R.string.text_not_logged_in);
                    }
                } else if (enteredPincode.length() < 6 && !banned && enteredIdMatches) {
                    enteredPincode += "1";
                    starsPin += "*";
                    Text_View_Stars_Pin.setText(starsPin);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (enteredId.length() < 8 && !banned) {                    if (!enteredIdMatches) {
                        enteredId += "2";
                        stringId += "2";
                        Text_View_Stars_Id.setText(stringId);
                        Text_View_Dialogue.setText(R.string.text_not_logged_in);
                    }
                } else if (enteredPincode.length() < 6 && !banned && enteredIdMatches) {
                    enteredPincode += "2";
                    starsPin += "*";
                    Text_View_Stars_Pin.setText(starsPin);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (enteredId.length() < 8 && !banned) {
                    if (!enteredIdMatches) {
                        enteredId += "3";
                        stringId += "3";
                        Text_View_Stars_Id.setText(stringId);
                        Text_View_Dialogue.setText(R.string.text_not_logged_in);
                    }
                } else if (enteredPincode.length() < 6 && !banned && enteredIdMatches) {
                    enteredPincode += "3";
                    starsPin += "*";
                    Text_View_Stars_Pin.setText(starsPin);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (enteredId.length() < 8 && !banned) {
                    if (!enteredIdMatches) {
                        enteredId += "4";
                        stringId += "4";
                        Text_View_Stars_Id.setText(stringId);
                        Text_View_Dialogue.setText(R.string.text_not_logged_in);
                    }
                } else if (enteredPincode.length() < 6 && !banned && enteredIdMatches) {
                    enteredPincode += "4";
                    starsPin += "*";
                    Text_View_Stars_Pin.setText(starsPin);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (enteredId.length() < 8 && !banned) {
                    if (!enteredIdMatches) {
                        enteredId += "5";
                        stringId += "5";
                        Text_View_Stars_Id.setText(stringId);
                        Text_View_Dialogue.setText(R.string.text_not_logged_in);
                    }
                } else if (enteredPincode.length() < 6 && !banned && enteredIdMatches) {
                    enteredPincode += "5";
                    starsPin += "*";
                    Text_View_Stars_Pin.setText(starsPin);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (enteredId.length() < 8 && !banned) {
                    if (!enteredIdMatches) {
                        enteredId += "6";
                        stringId += "6";
                        Text_View_Stars_Id.setText(stringId);
                        Text_View_Dialogue.setText(R.string.text_not_logged_in);
                    }
                } else if (enteredPincode.length() < 6 && !banned && enteredIdMatches) {
                    enteredPincode += "6";
                    starsPin += "*";
                    Text_View_Stars_Pin.setText(starsPin);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (enteredId.length() < 8 && !banned) {
                    if (!enteredIdMatches) {
                        enteredId += "7";
                        stringId += "7";
                        Text_View_Stars_Id.setText(stringId);
                        Text_View_Dialogue.setText(R.string.text_not_logged_in);
                    }
                } else if (enteredPincode.length() < 6 && !banned && enteredIdMatches) {
                    enteredPincode += "7";
                    starsPin += "*";
                    Text_View_Stars_Pin.setText(starsPin);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (enteredId.length() < 8 && !banned) {
                    if (!enteredIdMatches) {
                        enteredId += "8";
                        stringId += "8";
                        Text_View_Stars_Id.setText(stringId);
                        Text_View_Dialogue.setText(R.string.text_not_logged_in);
                    }
                } else if (enteredPincode.length() < 6 && !banned && enteredIdMatches) {
                    enteredPincode += "8";
                    starsPin += "*";
                    Text_View_Stars_Pin.setText(starsPin);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (enteredId.length() < 8 && !banned) {
                    if (!enteredIdMatches) {
                        enteredId += "9";
                        stringId += "9";
                        Text_View_Stars_Id.setText(stringId);
                        Text_View_Dialogue.setText(R.string.text_not_logged_in);
                    }
                } else if (enteredPincode.length() < 6 && !banned && enteredIdMatches) {
                    enteredPincode += "9";
                    starsPin += "*";
                    Text_View_Stars_Pin.setText(starsPin);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (enteredId.length() < 8 && !banned) {
                    if (!enteredIdMatches) {
                        enteredId += "0";
                        stringId += "0";
                        Text_View_Stars_Id.setText(stringId);
                        Text_View_Dialogue.setText(R.string.text_not_logged_in);
                    }
                } else if (enteredPincode.length() < 6 && !banned && enteredIdMatches) {
                    enteredPincode += "0";
                    starsPin += "*";
                    Text_View_Stars_Pin.setText(starsPin);
                    Text_View_Dialogue.setText(R.string.text_not_logged_in);
                }
            }
        });
        Button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if ((enteredId.length() > 0 && !banned) && !enteredIdMatches) {
                    enteredId = enteredId.substring(0, enteredId.length() - 1); // utolsó bevitt karakter törlése
                    stringId = stringId.substring(0, stringId.length() - 1);
                    Text_View_Stars_Id.setText(stringId);
                } else if (enteredPincode.length() > 0 && !banned) {
                    enteredPincode = enteredPincode.substring(0, enteredPincode.length() - 1); // utolsó bevitt karakter törlése
                    starsPin = starsPin.substring(0, starsPin.length() - 1);
                    Text_View_Stars_Pin.setText(starsPin);
                }
            }
        });
        Button_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(LoginActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!banned) {
                    if (enteredId.length() == 8) { // 6--nál nagyobb nem lehet, ld: számgombok
                        enteredIdMatches = true;
                        //Text_View_Stars_Pin.setVisibility(view.VISIBLE);
                        LinLayPin.setVisibility(view.VISIBLE);
                        if (animationCounter == 0) {
                            //Text_View_Enter_PIN_Code.setVisibility(View.VISIBLE);
                            //LinLayPin.setVisibility(view.VISIBLE);
                            LinLayPin.startAnimation(animFadeIn2);
                            animationCounter++;
                        }
                        if (enteredPincode.length() >= 4) {
                            tryToLogin(enteredId, enteredPincode);
                        } else {
                            if (enteredPincode.length() > 0) {
                                Text_View_Dialogue.setText(getString(R.string.text_pin_code_rule));
                            }
                            starsPin = "";
                            enteredPincode = "";
                            Text_View_Stars_Pin.setText(starsPin);
                        }
                    } else {

                            //Text_View_Dialogue.setText(getString(R.string.text_pin_code_rule));
                            Text_View_Dialogue.setText(getString(R.string.text_id_code_rule));

                        stringId = "";
                        enteredId = "";
                        Text_View_Stars_Id.setText(stringId);
                    }
                }
            }
        });

        Text_View_Stars_Id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                CustomDialogClass cdd = new CustomDialogClass(LoginActivity.this, getString(R.string.text_remove));
                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                    @Override
                    public void onYes(String title) {
                        if (enteredIdMatches) {
                            Text_View_Enter_PIN_Code.startAnimation(animFadeOut2);
                            //Text_View_Enter_PIN_Code.setVisibility(View.INVISIBLE);
                            LinLayPin.setVisibility(View.INVISIBLE);
                            Text_View_Stars_Pin.startAnimation(animFadeOut2);
                            Text_View_Stars_Pin.setVisibility(view.INVISIBLE);
                            animationCounter = 0;
                        }
                        stringId = "";
                        enteredId = "";
                        enteredIdMatches = false;
                        Text_View_Stars_Id.setText(stringId);
                        starsPin = "";
                        enteredPincode = "";
                        Text_View_Stars_Pin.setText(starsPin);
                    }

                    @Override
                    public void onNo(String title) {

                    }
                });
                cdd.show();

                /*
                if (Utility.askYesOrNoWithQuestion(LoginActivity.this, getString(R.string.text_remove))){
                    if (enteredIdMatches){
                        Text_View_Enter_PIN_Code.startAnimation(animFadeOut2);
                        Text_View_Enter_PIN_Code.setVisibility(View.INVISIBLE);
                        Text_View_Stars_Pin.startAnimation(animFadeOut2);
                        Text_View_Stars_Pin.setVisibility(view.INVISIBLE);
                        animationCounter = 0;
                    }
                    stringId = "";
                    enteredId = "";
                    enteredIdMatches = false;
                    Text_View_Stars_Id.setText(stringId);
                    starsPin = "";
                    enteredPincode = "";
                    Text_View_Stars_Pin.setText(starsPin);
                }*/
            }
        });
    }

    /**
     * sikeres mysql azonosításkor fut le
     * itt történik a belépés a MobilBank részre
     *
     * @param id
     */
    public void logInUserById(int id) {
        Text_View_Dialogue.setVisibility(View.INVISIBLE);
        loginState = true;
        stringId = "";
        enteredId = "";
        Text_View_Stars_Id.setText(stringId);
        //Text_View_Enter_ID.setVisibility(View.INVISIBLE);
        LinLayId.setVisibility(View.INVISIBLE);
        starsPin = "";
        enteredPincode = "";
        Text_View_Stars_Pin.setText(stringId);
        //Text_View_Enter_PIN_Code.setVisibility(View.INVISIBLE);
        LinLayPin.setVisibility(View.INVISIBLE);
        updateLastLoginTime(id); // utolsó bejelentkezés idejét frissítjük mysqsl-ben (aszinkron!!)

        try {
            helper.stopAuth();
        } catch (Exception e) {
        }

        Utility.saveLoginState(LoginActivity.this, true); // bejelentkezve változó beállítása, mentése sharedpreferences-be
        Intent intent = new Intent(LoginActivity.this, MobilBankMainActivity.class);
        intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
        intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
        intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
        intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
        startActivity(intent);
        finish();
        //Utility.CallNextActivity(LoginActivity.this, MobilBankMainActivity.class);
        overridePendingTransition(R.anim.anim_fade_in_4, R.anim.anim_fade_out_4); // átmenetes animáció a két activity között
        MainActivity.mainActivity.finish(); // MainActivity bezárása
    }

    /**
     * Hibás bejelentkezési kísérlet esetén fut le
     * Itt történik a tiltás is, x db sikertelen próbálkozás után
     */
    public void accessDenied() {
        Text_View_Dialogue.setText(getString(R.string.text_wrong_pin_code));
        wrongAttempts++;
        if (wrongAttempts == getResources().getInteger(R.integer.maxWrongAttempts)) {
            banned(getResources().getInteger(R.integer.timeToWait));
            wrongAttempts = 0;
        }
        starsPin = "";
        enteredPincode = "";
        Text_View_Stars_Pin.setText(starsPin);
    }

    /**
     * x db hibás kód utáni letiltás y ideig
     * Ez a metódus tartalmaz egy visszaszámláló időzítőt, az időzítő letelte után pedig elindítja a következő activity-t. ????
     * Ennél a visszaszámlálónál lehetőség lenne a visszaszámlálás alatt bizonyos időközönként elindítani valamilyen utasítást is a jelzett helyen.
     */
    public void banned(long seconds) {
        banned = true;

        new CountDownTimer(seconds * 1000, 1000) { // Visszaszámlálás (absztrakt osztály) ??
            public void onTick(long millisUntilFinished) { // meghatározott időközönként végrehajtódik ez a metódus
                remainingTimeBanned = millisUntilFinished;
                Text_View_Dialogue.setText(getString(R.string.text_wrong_pin_code) + " " + getString(R.string.text_banned_1) + (millisUntilFinished / 1000) + " " + getString(R.string.text_banned_2));
            }

            public void onFinish() { // számláló lejáratakor hajtódik végre
                Text_View_Enter_PIN_Code.startAnimation(animFadeIn2);
                Text_View_Dialogue.setText(R.string.text_not_logged_in);
                banned = false;
            }
        }.start();
    }

    public void init() {
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
        Text_View_Enter_PIN_Code = (TextView) findViewById(R.id.Text_View_Enter_PIN_Code);
        Text_View_Enter_ID = (TextView) findViewById(R.id.Text_View_Enter_Id);
        Text_View_Stars_Pin = (TextView) findViewById(R.id.Text_View_Stars_Pin);
        Text_View_Stars_Id = (TextView) findViewById(R.id.Text_View_Stars_Id);
        Text_View_Dialogue = (TextView) findViewById(R.id.Text_View_Dialogue);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        Text_View_Fingerprint = findViewById(R.id.Text_View_Fingerprint);
        LottieFingerPrint = findViewById(R.id.LottieFingerprint);
        animFadeIn2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_in_2);
        animFadeOut2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_out_2);
        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        LinLayId = findViewById(R.id.LinLayId);
        LinLayPin = findViewById(R.id.LinLayPin);

    }

    /**
     * Ujjlenyomat-azonosításhoz kell
     *
     * @throws FingerprintException
     */
    //Create the generateKey method that we’ll use to gain access to the Android keystore and generate the encryption key//
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            keyGenerator.init(new

                    //Specify the operation(s) this key can be used for//
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generate the key//
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }

        /**
         * Képernyő lehúzás hatására frissítés
         */
        SwipeRefreshLayout.setColorSchemeResources(R.color.green, R.color.darkgreen3, R.color.darkgreen);
        SwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SwipeRefreshLayout.setRefreshing(true);

                //recreate();
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);

                /*(new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getRecurringTransfers(userId);

                        SwipeRefreshLayout.setRefreshing(false);
                    }
                },3000);*/
            }
        });

    }

    /**
     * Ujjlenyomat-azonosításhoz kell
     *
     * @return
     */
    //Create a new method that we’ll use to initialize our cipher//
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    /**
     * ujjlenyomat-azonosításhozo kell
     */
    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }

    /**
     * ha lefutott az aszinkron mysql-kommunikáció, ide kerül az eredménye, valamint lefut ez a metódus.
     *
     * @param result - a mysql lekérdezés eredménye JSON formátumban
     */
    public void taskCompletionResult(String result) {
        //Toast.makeText(this, "" + request.getResult(), Toast.LENGTH_SHORT).show();
        JSONArray userList;
        try {
            //JSONObject object = new JSONObject(request.getResult());
            JSONObject object = new JSONObject(result);
            userList = object.getJSONArray(Constants.RESPONSE_USERS);
            if (!object.getBoolean(Constants.RESPONSE_ERROR)) {
                JSONObject obj = userList.getJSONObject(0);
                userFirstName = obj.getString(Constants.COL_USERS_FIRSTNAME);
                userLastName = obj.getString(Constants.COL_USERS_LASTNAME);
                userId = obj.getInt(Constants.COL_USERS_ID);
                savedId = obj.getInt(Constants.COL_USERS_MOBILEBANK_ID);
                savedPinCode = obj.getInt(Constants.COL_USERS_PINCODE);
                userLastLoginTime = obj.getString(Constants.COL_USERS_LAST_LOGIN);
                // Toast.makeText(LoginActivity.this, "Üdvözlöm " + userFirstName + "!", Toast.LENGTH_SHORT).show();
                saveUserData(userFirstName, String.valueOf(userId), String.valueOf(savedId), String.valueOf(savedPinCode)); // elmentjük Sharedpreferencesbe a legutolsó sikeres belépéshez kapcsolódó user_id-t, ezt fogjuk használni az ujjlenyomatos azonosításhoz.
                logInUserById(obj.getInt(Constants.RESPONSE_ID));
            }
        } catch (JSONException e) {
            accessDenied(); // hibás pinkód vagy user_id, vagy nem volt sikeres a kommunikáció a mysql adatbázissal
            e.printStackTrace();
        }
    }


    /**
     * Bejelentkezési próbálkozás a számgombokkal megadott adatok alapján
     * Aszinkron művelet!
     *
     * @param
     * @param
     */
    private void tryToLogin(String user, String pincode) {
        if (Utility.IsNetworkAvailable(this)) {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_USERS_MOBILEBANK_ID, user);
            params.put(Constants.COL_USERS_PINCODE, pincode);
            request = new PerformNetworkRequest(Constants.URL_TRY_TO_LOGIN, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            SwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * MySQL-ben frissítjük az utolsó bejelentkezés idejét.
     * Aszinkron művelet!
     */
    private void updateLastLoginTime(int id) {
        if (Utility.IsNetworkAvailable(this)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("id", String.valueOf(id));
            request = new PerformNetworkRequest(Constants.URL_UPDATE_LASTLOGINTIME, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
        } else {
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            SwipeRefreshLayout.setRefreshing(false);
        }


    }

    /**
     * A mindenkori legutolsó pinkódos bejelentkezést elmentjük sharedpreferences-be
     * Ha van ilyen elémentett érték, akkor megjelenik az ujjlenyomatos azonosítási lehetőség, ha nincs, akkor nem jelenik meg
     * Ez azért van, mert az ujjlenyomatos azonosításhoz is kell id + firstname
     */
    public void saveUserData(String userFirstName, String userId, String enteredId, String pincode) {
        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
        editor.putString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
        editor.putString(Constants.SHAREDPREFERENCES_PINCODE_USER, String.valueOf(pincode));
        editor.putString(Constants.SHAREDPREFERENCES_ID_ENTERED, String.valueOf(enteredId)); // ezt asszem lehet törölni, mert nem használom. MÉGSEM!!! :)
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.SHAREDPREFERENCES_BANNED_REMAINING_TIME, String.valueOf(remainingTimeBanned));
        editor.apply();
        super.onDestroy();
    }
}

