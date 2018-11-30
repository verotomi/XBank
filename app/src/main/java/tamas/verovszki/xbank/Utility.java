package tamas.verovszki.xbank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by verov on 2018.11.22..
 */

class Utility {
    static boolean answer;

    public static boolean askForConfirmExit(final Context context){

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(R.string.text_confirm_exit)
        //Text_View_Dialogue.setText(getString(R.string.text_wrong_pin_code)); //
                . setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        answer = true;
                        Toast.makeText(context, "" + answer, Toast.LENGTH_SHORT).show();
                        //return;
                    }
                })
                .setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        answer = false;
                        Toast.makeText(context, "" + answer, Toast.LENGTH_SHORT).show();
                        //return;
                    }
                })
                .setCancelable(true) // ne lehessen klépni az alertből
                .show();
        Toast.makeText(context, "Show után vagyok", Toast.LENGTH_SHORT).show();
        return answer;
    }

    public static void CallNextActivity(Context context, Class newActivityName) {

        Intent intent = new Intent(context, newActivityName); // új Activity példányosítása
        context.startActivity(intent); // Új Activity elindítása
    }

    public static boolean getLoginState(Context context){
        SharedPreferences sp = context.getSharedPreferences("mentett_adatok", context.MODE_PRIVATE); // nem értem, hogy miért, de csak ez a fomrátum működik (a 'context. ...'-ra gondolok)
        String loggedIn = sp.getString("bejelentkezve", "nem");
        //String loggedIn = getSharedPreferences(context).getString("bejelentkezve", "");
        Toast.makeText(context, "Mentett változó :" + loggedIn, Toast.LENGTH_SHORT).show();
        if (loggedIn.equals("igen")){
            return true;
        }
        else{
            return false;
        }
    }

    public static void setLoginState(Context context, Boolean loginState) { // át kell venni a context-et is!
        String textForEditor="";
        SharedPreferences sp = context.getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (loginState){
            textForEditor="igen";
        }
        else{
            textForEditor="nem";
        }
        editor.putString("bejelentkezve", textForEditor);
        editor.apply();
    }

    public static void vibrateForXMillisec(Context context, int milliSec){ // át kell venni a context-et is!
        Vibrator v = (Vibrator) context.getSystemService(MainActivity.VIBRATOR_SERVICE); // nem értem, hogy miért, de csak ez a fomrátum működik (a 'context. ...'-ra gondolok)
        v.vibrate(milliSec);
    }


}
