package tamas.verovszki.xbank;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.constraint.Constraints.TAG;
import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static android.support.v4.content.ContextCompat.getCodeCacheDir;
import static tamas.verovszki.xbank.BuildConfig.DEBUG;

/**
 * Created by verov on 2018.11.22..
 */

class Utility {
    static boolean answer;
    static private boolean answer2 = false;


    /**
     * Ez az askForConfirmExit2 metódus azért készült, mert az askForConfirmExit nem működött jól; már akkor visszatért a visszatérési értékkel, még mielőtt ki lett volna választva az igen vagy a nem.
     * A Handler-es részt ki lehet venni a metódusból és egy szinttel feljebb rakni, ha szükség lenne rá.
     * @param context
     * @return
     */
    static public boolean askForConfirmExit2(Context context)
    {
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message message)
            {
                throw new RuntimeException();
            }
        };

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.text_confirm_exit);
        //alert.setMessage("Message");
        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                answer2 = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                answer2 = false;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.show();

        try{ Looper.loop(); }
        catch(RuntimeException e){}

        return answer2;
    }

    /**
     * Ez az askForConfirmExit metódus nem működött jól, már akkor visszatért a visszatérési értékkel, még mielőtt ki lett volna választva az igen vagy a nem.
     * Ezért készült egy askForConfirmExit2 !!
     *
     * @param context
     * @return
     */

    static public boolean askForConfirmExit(final Context context){ // itt a final csak a Toast-nál lévő Ccontext miatt kell
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.text_confirm_exit)
        //Text_View_Dialogue.setText(getString(R.string.text_wrong_pin_code)); //
                . setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        answer = true;
                        Toast.makeText(context, "Answer: " + answer, Toast.LENGTH_SHORT).show();
                        //return;
                    }
                })
                .setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        answer = false;
                        Toast.makeText(context, "Answer: " + answer, Toast.LENGTH_SHORT).show();
                        //return;
                    }
                })
                .setCancelable(true) // ne lehessen kilépni az alertből
                .show();
        //Toast.makeText(context, "Show után vagyok", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "Return előtti sor", Toast.LENGTH_SHORT).show();
        return answer;

        /*AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Ki akar lépni?");
        builder1.setCancelable(false);
        builder1.setPositiveButton( // "igen" válasz esetén
                "Igen",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        answer = true;
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton( // "nem" válasz esetén
                "Nem",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        answer = false;
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
        return answer;*/
    }

    public static void CallNextActivity(Context context, Class newActivityName) {
        Intent intent = new Intent(context, newActivityName); // új Activity példányosítása
        context.startActivity(intent); // Új Activity elindítása
    }

    public static boolean getLoginState(Context context){
        SharedPreferences sp = context.getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, context.MODE_PRIVATE); // nem értem, hogy miért, de csak ez a fomrátum működik (a 'context. ...'-ra gondolok)
        String loggedIn = sp.getString(Constants.SHAREDPREFERENCES_LOGGED_IN, "nem");
        //String loggedIn = getSharedPreferences(context).getString("bejelentkezve", "");
        //Toast.makeText(context, "Mentett változó :" + loggedIn, Toast.LENGTH_SHORT).show();
        if (loggedIn.equals("igen")){
            return true;
        }
        else{
            return false;
        }
    }

    public static void saveLoginState(Context context, Boolean loginState) { // át kell venni a context-et is!
        String textForEditor="";
        SharedPreferences sp = context.getSharedPreferences(Constants.SHAREDPREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (loginState){
            textForEditor="igen";
        }
        else{
            textForEditor="nem";
        }
        editor.putString(Constants.SHAREDPREFERENCES_LOGGED_IN, textForEditor);
        editor.apply();
    }

    public static void vibrateForXMillisec(Context context, int milliSec){ // át kell venni a context-et is!
        Vibrator v = (Vibrator) context.getSystemService(MainActivity.VIBRATOR_SERVICE); // nem értem, hogy miért, de csak ez a fomrátum működik (a 'context. ...'-ra gondolok)
        v.vibrate(milliSec);
    }

    /*ki akakartam rakni ide az Utilitybe, de nem működik. lefagy tőle a program!
    static public boolean askForConfirmGetLocation(final Context context){ // itt a final csak a Toast-nál lévő Context miatt kell
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message message)
            {
                throw new RuntimeException();
            }
        };
        Toast.makeText(context, "Itt vagyok", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions((Activity) context, new String[]{ACCESS_FINE_LOCATION},1); // ACCESS_FINE_LOCATION -> Alt+Enter -> import static Constant // contexten Alt+Enter -> cast 1st parameter
        try{ Looper.loop(); }
        catch(RuntimeException e){}
        return true;
    }*/

    static public boolean askYesOrNoWithQuestion(Context context, String question)
    {
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message message)
            {
                throw new RuntimeException();
            }
        };

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(question);
        //alert.setMessage("Message");
        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                answer2 = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                answer2 = false;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.show();

        try{ Looper.loop(); }
        catch(RuntimeException e){}

        return answer2;
    }

    static public boolean askYesOrNoWithQuestion2(Context context, String question)
    {
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message message)
            {
                throw new RuntimeException();
            }
        };

        CustomDialogClass cdd = new CustomDialogClass((Activity)context, question); // cast-olni kellett a context-et :o :)
        cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
            @Override
            public void onYes(String title) {
                answer2 = true;
                handler.sendMessage(handler.obtainMessage());
            }

            @Override
            public void onNo(String title) {

            }
        });
        cdd.show();

        try{ Looper.loop(); }
        catch(RuntimeException e){}

        return answer2;
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Activity activity) {
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        InputMethodManager imm = (InputMethodManager)   activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    // Hide Status Bar
    public static void hideStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT < 16) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        else {
            View decorView = activity.getWindow().getDecorView();
            // Hide Status Bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * NEm működik
     * @param context
     * @param navView
     * @param menuResId
     * @param colorRes
     */
    public static void setMenuTextColor(final Context context, final NavigationView navView, final int menuResId, final int colorRes) {
        navView.post(new Runnable() {
            @Override
            public void run() {
                View settingsMenuItem =  navView.findViewById(menuResId);
                if (settingsMenuItem instanceof TextView) {
                    if (DEBUG) {
                        Log.i(TAG, "setMenuTextColor textview");
                    }
                    TextView tv = (TextView) settingsMenuItem;
                    tv.setTextColor(ContextCompat.getColor(context, colorRes));
                } else { // you can ignore this branch, because usually there is not the situation
                    Menu menu = navView.getMenu();
                    MenuItem item = menu.findItem(menuResId);
                    SpannableString s = new SpannableString(item.getTitle());
                    s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, colorRes)), 0, s.length(), 0);
                    item.setTitle(s);
                }

            }
        });
    }

    /**
     * Nav drawer item colors
     * @return
     */
    public static ColorStateList navigationDrawerColors(){
        // FOR NAVIGATION VIEW ITEM TEXT COLOR
        int[][] state = new int[][] {
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] color = new int[] {
                Color.GRAY,
                Color.BLACK,
                Color.BLACK,
                Color.BLACK
        };
        ColorStateList cs1 = new ColorStateList(state, color);
        return cs1;
    }

    /**
     *
     * @return
     */
    public static boolean IsNetworkAvailable(Context mcontext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
