package tamas.verovszki.xbank;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.content.Intent;


import java.util.HashMap;

public class PinCodeChangeActivity extends BaseActivity implements PerformNetworkRequest.TaskDelegate {
    private Toolbar toolbar;
    MenuItem menuitem_logout_3;
    MenuItem menuitem_toolbar_name;
    boolean loginState = false;
    String userFirstName="";
    int userId;
    String userPinCode;
    Boolean valasz = false;

    private Button Button_1,Button_2,Button_3,Button_4,Button_5,Button_6,Button_7,Button_8,Button_9,Button_0, Button_back, Button_enter;
    private TextView Text_View_Enter_Current_PinCode;
    private TextView Text_View_New_Pincode;
    private TextView Text_View_Re_Enter_PIN_Code;
    private TextView Text_View_Stars_Current_Pincode;
    private TextView Text_View_Stars_New_Pincode;
    private TextView Text_View_Stars_Re_Enter_Pincode;
    private TextView Text_View_Dialogue;
    private String enteredPincodeCurrent="";
    private String enteredPincodeNew="";
    private String enteredPincodeReEntered="";
    private String starsPinCurrent ="";
    private String starsPinNew ="";
    private String starsPinReEntered ="";
    private boolean currentPinEntered;
    private boolean newPinEntered;
    private SwipeRefreshLayout SwipeRefreshLayout;
    private LinearLayout LinLayPin1;
    private LinearLayout LinLayPin2;
    private LinearLayout LinLayPin3;
    private LinearLayout LinLay0;



    Animation animFadeIn2; // elhalványuló animáció
    Animation animFadeOut2; // elhalványuló animáció
    int animationCounter1 = 0; // pinkód-kérés animáció csak 1x fusson le
    int animationCounter2 = 0; // pinkód-kérés animáció csak 1x fusson le

    //MySQL adatbázishoz

    PerformNetworkRequest request;

    DrawerLayout drawerLayout;
    String userLastLoginTime;
    String userLastName;
    boolean ignore = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT); // elforgatás tiltása
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.TitlePinCodeChange)); // Fejléc átállítása a kiválasztott nyelvre. Ez még a setContentView() elé kell
        setContentView(R.layout.activity_pin_code_change);

        getBundle(); // átadott változók beolvasása
        init();

        if(Utility.IsNetworkAvailable(this)){
            LinLay0.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(this, "There is no internet connection!", Toast.LENGTH_SHORT).show();
        }

        navDrawer();

        //Utility.hideStatusBar(this); // Statusbar elrejtése

        LinLayPin1.startAnimation(animFadeIn2);
        //Text_View_New_Pincode.setVisibility(View.INVISIBLE);
        LinLayPin2.setVisibility(View.INVISIBLE);
        LinLayPin3.setVisibility(View.INVISIBLE);

        setSupportActionBar(toolbar); // Toolbar megjelenítése

        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        userPinCode = sp.getString(Constants.SHAREDPREFERENCES_PINCODE_USER, "0");


        /**
         * A gombok figyelése és kezelése
         */
        Button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!currentPinEntered){
                    if (enteredPincodeCurrent.length() < 6){
                        enteredPincodeCurrent += "1";
                        starsPinCurrent += "*";
                        Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && !newPinEntered) {
                    if (enteredPincodeNew.length() < 6) {
                        enteredPincodeNew += "1";
                        starsPinNew += "*";
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && newPinEntered) {
                    if (enteredPincodeReEntered.length() < 6) {
                        enteredPincodeReEntered += "1";
                        starsPinReEntered += "*";
                        Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        Button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!currentPinEntered){
                    if (enteredPincodeCurrent.length() < 6){
                        enteredPincodeCurrent += "2";
                        starsPinCurrent += "*";
                        Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && !newPinEntered) {
                    if (enteredPincodeNew.length() < 6) {
                        enteredPincodeNew += "2";
                        starsPinNew += "*";
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && newPinEntered) {
                    if (enteredPincodeReEntered.length() < 6) {
                        enteredPincodeReEntered += "2";
                        starsPinReEntered += "*";
                        Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        Button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!currentPinEntered){
                    if (enteredPincodeCurrent.length() < 6){
                        enteredPincodeCurrent += "3";
                        starsPinCurrent += "*";
                        Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && !newPinEntered) {
                    if (enteredPincodeNew.length() < 6) {
                        enteredPincodeNew += "3";
                        starsPinNew += "*";
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && newPinEntered) {
                    if (enteredPincodeReEntered.length() < 6) {
                        enteredPincodeReEntered += "3";
                        starsPinReEntered += "*";
                        Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        Button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!currentPinEntered){
                    if (enteredPincodeCurrent.length() < 6){
                        enteredPincodeCurrent += "4";
                        starsPinCurrent += "*";
                        Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && !newPinEntered) {
                    if (enteredPincodeNew.length() < 6) {
                        enteredPincodeNew += "4";
                        starsPinNew += "*";
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && newPinEntered) {
                    if (enteredPincodeReEntered.length() < 6) {
                        enteredPincodeReEntered += "4";
                        starsPinReEntered += "*";
                        Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        Button_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!currentPinEntered){
                    if (enteredPincodeCurrent.length() < 6){
                        enteredPincodeCurrent += "5";
                        starsPinCurrent += "*";
                        Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && !newPinEntered) {
                    if (enteredPincodeNew.length() < 6) {
                        enteredPincodeNew += "5";
                        starsPinNew += "*";
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && newPinEntered) {
                    if (enteredPincodeReEntered.length() < 6) {
                        enteredPincodeReEntered += "5";
                        starsPinReEntered += "*";
                        Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        Button_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!currentPinEntered){
                    if (enteredPincodeCurrent.length() < 6){
                        enteredPincodeCurrent += "6";
                        starsPinCurrent += "*";
                        Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && !newPinEntered) {
                    if (enteredPincodeNew.length() < 6) {
                        enteredPincodeNew += "6";
                        starsPinNew += "*";
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && newPinEntered) {
                    if (enteredPincodeReEntered.length() < 6) {
                        enteredPincodeReEntered += "6";
                        starsPinReEntered += "*";
                        Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        Button_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!currentPinEntered){
                    if (enteredPincodeCurrent.length() < 6){
                        enteredPincodeCurrent += "7";
                        starsPinCurrent += "*";
                        Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && !newPinEntered) {
                    if (enteredPincodeNew.length() < 6) {
                        enteredPincodeNew += "7";
                        starsPinNew += "*";
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && newPinEntered) {
                    if (enteredPincodeReEntered.length() < 6) {
                        enteredPincodeReEntered += "7";
                        starsPinReEntered += "*";
                        Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        Button_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!currentPinEntered){
                    if (enteredPincodeCurrent.length() < 6){
                        enteredPincodeCurrent += "8";
                        starsPinCurrent += "*";
                        Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && !newPinEntered) {
                    if (enteredPincodeNew.length() < 6) {
                        enteredPincodeNew += "8";
                        starsPinNew += "*";
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && newPinEntered) {
                    if (enteredPincodeReEntered.length() < 6) {
                        enteredPincodeReEntered += "8";
                        starsPinReEntered += "*";
                        Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        Button_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!currentPinEntered){
                    if (enteredPincodeCurrent.length() < 6){
                        enteredPincodeCurrent += "9";
                        starsPinCurrent += "*";
                        Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && !newPinEntered) {
                    if (enteredPincodeNew.length() < 6) {
                        enteredPincodeNew += "9";
                        starsPinNew += "*";
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && newPinEntered) {
                    if (enteredPincodeReEntered.length() < 6) {
                        enteredPincodeReEntered += "9";
                        starsPinReEntered += "*";
                        Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        Button_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (!currentPinEntered){
                    if (enteredPincodeCurrent.length() < 6){
                        enteredPincodeCurrent += "0";
                        starsPinCurrent += "*";
                        Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && !newPinEntered) {
                    if (enteredPincodeNew.length() < 6) {
                        enteredPincodeNew += "0";
                        starsPinNew += "*";
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
                if (currentPinEntered && newPinEntered) {
                    if (enteredPincodeReEntered.length() < 6) {
                        enteredPincodeReEntered += "0";
                        starsPinReEntered += "*";
                        Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);
                        Text_View_Dialogue.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        Button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (currentPinEntered && newPinEntered){
                    if (enteredPincodeReEntered.length() > 0){
                        enteredPincodeReEntered = enteredPincodeReEntered.substring(0, enteredPincodeReEntered.length() - 1); // utolsó bevitt karakter törlése
                        starsPinReEntered = starsPinReEntered.substring(0, starsPinReEntered.length() - 1);
                        Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);
                    }
                }
                if (currentPinEntered && !newPinEntered){
                    if (enteredPincodeNew.length() > 0){
                        enteredPincodeNew = enteredPincodeNew.substring(0, enteredPincodeNew.length() - 1); // utolsó bevitt karakter törlése
                        starsPinNew = starsPinNew.substring(0, starsPinNew.length() - 1);
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                    }
                }
                if (!currentPinEntered){
                    if (enteredPincodeCurrent.length() > 0){
                        enteredPincodeCurrent = enteredPincodeCurrent.substring(0, enteredPincodeCurrent.length() - 1); // utolsó bevitt karakter törlése
                        starsPinCurrent = starsPinCurrent.substring(0, starsPinCurrent.length() - 1);
                        Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                    }
                }


                /*if ((enteredPincodeCurrent.length() > 0) && !currentPinEntered){
                    enteredPincodeCurrent = enteredPincodeCurrent.substring(0, enteredPincodeCurrent.length() - 1); // utolsó bevitt karakter törlése
                    starsPinCurrent = starsPinCurrent.substring(0, starsPinCurrent.length() - 1);
                    Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                }
                else if (enteredPincodeNew.length() > 0){
                    enteredPincodeNew = enteredPincodeNew.substring(0, enteredPincodeNew.length() - 1); // utolsó bevitt karakter törlése
                    starsPinNew = starsPinNew.substring(0, starsPinNew.length() - 1);
                    Text_View_Stars_New_Pincode.setText(starsPinNew);
                }*/
            }
        });
        Button_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.vibrateForXMillisec(PinCodeChangeActivity.this, getResources().getInteger(R.integer.vibrateLength));
                if (enteredPincodeCurrent.length() >= 4 && enteredPincodeCurrent.length() <= 6) {
                    currentPinEntered = true;
                    //Text_View_Stars_New_Pincode.setVisibility(view.VISIBLE);
                    LinLayPin2.setVisibility(view.VISIBLE);
                    if (animationCounter1 == 0) {
                        //Text_View_New_Pincode.setVisibility(View.VISIBLE);
                        LinLayPin2.setVisibility(View.VISIBLE);
                        //Text_View_New_Pincode.startAnimation(animFadeIn2);
                        LinLayPin2.startAnimation(animFadeIn2);
                        animationCounter1++;
                    }
                    if (enteredPincodeNew.length() >= 4) {
                        newPinEntered = true;
                        //Text_View_Stars_Re_Enter_Pincode.setVisibility(view.VISIBLE);
                        LinLayPin3.setVisibility(view.VISIBLE);

                        //Toast.makeText(PinCodeChangeActivity.this, "Old: " + enteredPincodeCurrent + " New: " + enteredPincodeNew, Toast.LENGTH_SHORT).show();

                        if (animationCounter2 == 0) {
                            //Text_View_Re_Enter_PIN_Code.setVisibility(View.VISIBLE);
                            LinLayPin3.setVisibility(View.VISIBLE);
                            //Text_View_Re_Enter_PIN_Code.startAnimation(animFadeIn2);
                            LinLayPin3.startAnimation(animFadeIn2);
                            animationCounter2++;
                        }
                        if (enteredPincodeReEntered.length() > 0 && enteredPincodeReEntered.length() < 4){
                            Text_View_Dialogue.setVisibility(View.VISIBLE);
                            Text_View_Dialogue.setText(getString(R.string.text_pin_code_rule));
                            starsPinReEntered = "";
                            enteredPincodeReEntered = "";
                            Text_View_Stars_Re_Enter_Pincode.setText(starsPinReEntered);

                        }else {
                            if (enteredPincodeNew.equals(enteredPincodeReEntered) && enteredPincodeCurrent.equals(userPinCode)) {

                                changePincode(userId, enteredPincodeNew);

                                Intent intent = new Intent(PinCodeChangeActivity.this, DoneAnimation.class);
                                startActivity(intent);

                                saveUserData();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
                                        finish();
                                    }
                                }, 1500);

                                //
                                // ide jön a jelszófrissítés mysqlben
                                //
                            } else if (enteredPincodeReEntered.length() > 0) {
                                Toast.makeText(PinCodeChangeActivity.this, getString(R.string.pincode_mismatch), Toast.LENGTH_SHORT).show();

                                //recreate();
                                startActivity(getIntent());
                                finish();
                                overridePendingTransition(0, 0);
                            }
                        }
                    }
                    else if(enteredPincodeNew.length() > 0){
                        Text_View_Dialogue.setVisibility(View.VISIBLE);
                        Text_View_Dialogue.setText(getString(R.string.text_pin_code_rule));
                        starsPinNew = "";
                        enteredPincodeNew = "";
                        Text_View_Stars_New_Pincode.setText(starsPinNew);
                    }
                } else {
                    Text_View_Dialogue.setVisibility(View.VISIBLE);
                    Text_View_Dialogue.setText(getString(R.string.text_pin_code_rule));
                    starsPinCurrent = "";
                    enteredPincodeCurrent = "";
                    Text_View_Stars_Current_Pincode.setText(starsPinCurrent);
                }
            }
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mobilbankmainactivity_without_name, menu); // elemek hozzáadása a toolbarhoz
        menuitem_logout_3 = menu.findItem(R.id.logout_toolbar_icon_3);
        //menuitem_toolbar_name = menu.findItem(R.id.logout_toolbar_name);
        loginState = Utility.getLoginState(PinCodeChangeActivity.this);

        if (loginState){
            menuitem_logout_3.setVisible(true);
            menuitem_logout_3.setEnabled(true);
            //menuitem_toolbar_name.setTitle(userFirstName);
            //menuitem_toolbar_name.setVisible(true);
        }
        else{
            menuitem_logout_3.setEnabled(false);
            menuitem_logout_3.setVisible(false);
            //menuitem_toolbar_name.setTitle("");
            //menuitem_toolbar_name.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // itt kell kezelni a toolbar-gombok kattintásait. A visszanyíl kattintást a rendszer kezeli!
        int id = item.getItemId();
        if (id == R.id.logout_toolbar_icon_3){
            CustomDialogClass cdd = new CustomDialogClass(this);
            cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                @Override
                public void onYes(String title) {
                    menuitem_logout_3.setEnabled(false);
                    menuitem_logout_3.setVisible(false);
                    //menuitem_toolbar_name.setTitle("");
                    //menuitem_toolbar_name.setVisible(false);
                    loginState = false;
                    Utility.saveLoginState(PinCodeChangeActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                    Intent intent = new Intent(PinCodeChangeActivity.this, LogoutAnimation.class);
                    startActivity(intent);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                            PinCodeChangeActivity.this.finish();
                            //finish();
                        }
                    }, 3000);
                }

                @Override
                public void onNo(String title) {

                }
            });
            cdd.show();
            return true;
        }
        if (id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void init(){
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
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
        Text_View_Enter_Current_PinCode = (TextView) findViewById(R.id.Text_View_Enter_Current_PinCode);
        Text_View_New_Pincode = (TextView) findViewById(R.id.Text_View_New_Pincode);
        Text_View_Re_Enter_PIN_Code = (TextView) findViewById(R.id.Text_View_Re_Enter_PIN_Code);
        Text_View_Stars_Current_Pincode = (TextView) findViewById(R.id.Text_View_Stars_Current_Pincode);
        Text_View_Stars_New_Pincode = (TextView) findViewById(R.id.Text_View_Stars_New_Pincode);
        Text_View_Stars_Re_Enter_Pincode = (TextView) findViewById(R.id.Text_View_Stars_Re_Enter_Pincode);
        Text_View_Dialogue = (TextView) findViewById(R.id.Text_View_Dialogue);
        animFadeIn2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_in_2);
        animFadeOut2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_out_2);
        SwipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        drawerLayout = findViewById(R.id.drawer_layout);
        LinLayPin1 = findViewById(R.id.LinLayPin1);
        LinLayPin2 = findViewById(R.id.LinLayPin2);
        LinLayPin3 = findViewById(R.id.LinLayPin3);
        LinLay0 = findViewById(R.id.LinLay0);

    }

    /**
     * Pinkód csere
     * Aszinkron művelet!
     * @param id
     * @param pincode
     */
    private void changePincode(int id, String pincode) {
        if (Utility.IsNetworkAvailable(this)){
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.COL_USERS_ID, String.valueOf(id));
            params.put(Constants.COL_USERS_PINCODE, pincode);
            request = new PerformNetworkRequest(Constants.URL_UPDATE_CHANGE_PINCODE, params, Constants.CODE_POST_REQUEST, this);
            request.execute();
            //Toast.makeText(this, request.getResult(), Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            SwipeRefreshLayout.setRefreshing(false);
        }


    }


    @Override
    public void taskCompletionResult(String result) {
        Toast.makeText(this, R.string.pincode_changed_succesfully, Toast.LENGTH_SHORT).show();
    }

    public void saveUserData(){
        SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Constants.SHAREDPREFERENCES_USER_ID, ""+ userId);
        editor.putString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
        editor.putString(Constants.SHAREDPREFERENCES_PINCODE_USER, enteredPincodeNew);
        editor.apply();
    }

    public void getBundle(){
        try {
        /*Intent i = getIntent();
        userFirstName = i.getStringExtra("user_firstname");
        userId = Integer.parseInt(i.getStringExtra("user_id"));*/

            Bundle b = getIntent().getExtras();
            userId = Integer.parseInt(b.getString(Constants.SHAREDPREFERENCES_USER_ID));
            userFirstName = b.getString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME);
            userLastName = b.getString(Constants.SHAREDPREFERENCES_USER_LASTNAME);
            userLastLoginTime = b.getString(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME);
        }
        catch (Exception e){ // ez a blokk azért került ide, mert nem akart működni a bundle-s paraméter átadás. Már működik, így ez a blokk csak biztonsági szempontból maradt itt.
                SharedPreferences sp = getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
                userId = Integer.parseInt(sp.getString(Constants.SHAREDPREFERENCES_USER_ID, ""));
                userFirstName = sp.getString(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, "");
        }
    }

    public void navDrawer(){
        final NavigationView navigationView = findViewById(R.id.nav_view);
        //View hView =  navigationView.getHeaderView(0);
        View hView =  findViewById(R.id.Navigation_Header_Layout);
        TextView nav_name = (TextView)hView.findViewById(R.id.TextViewName);
        TextView nav_lastlogin = (TextView)hView.findViewById(R.id.TextViewLastLogin);
        nav_name.setText(userLastName + " " + userFirstName);
        nav_lastlogin.setText(userLastLoginTime);

        Menu navMenu = navigationView.getMenu();
        MenuItem navMenuChangePinCode = navMenu.findItem(R.id.Change_PIN_code);
        navMenuChangePinCode.setEnabled(false);
        //navMenuChangePinCode.setVisible(false);

        ColorStateList cs1 = Utility.navigationDrawerColors();
        navigationView.setItemTextColor(cs1);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        // menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        Intent intent;
                        int id = menuItem.getItemId();
                        switch (id){
                            case R.id.Overviewe:
                                intent = new Intent(PinCodeChangeActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;
                                finish();
                                break;
                            case R.id.Balance_query:
                                intent = new Intent(PinCodeChangeActivity.this, BankAccountBalancesActivity.class);
                                break;
                            case R.id.One_time_transfer:
                                intent = new Intent(PinCodeChangeActivity.this, TransferMoneyOneTimeActivity.class);
                                break;
                            case R.id.Recurring_transfers:
                                intent = new Intent(PinCodeChangeActivity.this, RecurringTransfersActivity.class);
                                break;
                            case R.id.Savings:
                                intent = new Intent(PinCodeChangeActivity.this, SavingsActivity.class);
                                break;
                            case R.id.Account_history:
                                intent = new Intent(PinCodeChangeActivity.this, BankAccountHistoryActivity.class);
                                break;
                            case R.id.Account_statement_download:
                                intent = new Intent(PinCodeChangeActivity.this, BankAccountStatementsActivity.class);
                                break;
                            case R.id.Beneficiaries:
                                intent = new Intent(PinCodeChangeActivity.this, BeneficiariesActivity.class);
                                break;
                            case R.id.Manage_credit_cards:
                                intent = new Intent(PinCodeChangeActivity.this, CreditCardsActivity.class);
                                break;
                            case R.id.Change_PIN_code:
                                intent = new Intent(PinCodeChangeActivity.this, PinCodeChangeActivity.class);
                                break;
                            case R.id.Logout:
                                //intent=null;

                                // az előzőleg kiválasztott elem úgy maradt, ezért törölni kell a kijelölést
                                // a -2 oka: navmenu utolsó eleme egy ures sor, az azelőtti meg a kilépés. Ezeknek nem kell törölni a kijelölését.
                                int size = navigationView.getMenu().size();
                                //for (int i = 0; i < size-2; i++) {
                                for (int i = 0; i < size; i++) {
                                    navigationView.getMenu().getItem(i).setChecked(false);
                                }

                                intent = new Intent(PinCodeChangeActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                ignore = true;

                                CustomDialogClass cdd = new CustomDialogClass(PinCodeChangeActivity.this);
                                cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                                    @Override
                                    public void onYes(String title) {
                                        menuitem_logout_3.setEnabled(false);
                                        menuitem_logout_3.setVisible(false);
                                        //menuitem_toolbar_name.setTitle("");
                                        //menuitem_toolbar_name.setVisible(false);
                                        loginState = false;
                                        Utility.saveLoginState(PinCodeChangeActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                                        MobilBankMainActivity.mobilBankMainActivity.finish(); // MainActivity bezárása
                                        Intent intent = new Intent(PinCodeChangeActivity.this, LogoutAnimation.class);
                                        startActivity(intent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Utility.CallNextActivity(MobilBankMainActivity.this, MainActivity.class);
                                                PinCodeChangeActivity.this.finish();
                                                //finish();
                                            }
                                        }, 3000);
                                    }

                                    @Override
                                    public void onNo(String title) {

                                    }
                                });
                                cdd.show();
                                break;

                            default: // van egy üres sor a navigation drawer legalján, amiatt, hogy oda is kerüljön elválasztóvonal. Ennek az üres sornak a lekezelése miatt kell ide az ignore, hogy ha arra kattintunk, ne történjen semmi
                                ignore = true;
                                intent = new Intent(PinCodeChangeActivity.this, MobilBankMainActivity.class); // ez csak azért kellett ide, mert szólt az Android Studio, ha ez nincs
                                break;
                        }
                        if (!ignore){
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_ID, "" + userId);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_FIRSTNAME, userFirstName);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_LASTNAME, userLastName);
                            intent.putExtra(Constants.SHAREDPREFERENCES_USER_LAST_LOGIN_TIME, userLastLoginTime);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_fade_in_5, R.anim.anim_fade_out_5); // átmenetes animáció a két activity között
                            finish();
                        }
                        ignore = false;
                        return true;
                    }
                });
        setSupportActionBar(toolbar); // Toolbar megjelenítése
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

}
