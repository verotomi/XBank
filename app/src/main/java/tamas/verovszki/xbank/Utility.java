package tamas.verovszki.xbank;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by verov on 2018.11.22..
 */

class Utility {
    static boolean answer;
    public static boolean askForConfirmExit(final Context context){

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Ki akarsz-e lépni?")
                . setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        answer = true;
                        Toast.makeText(context, "" + answer, Toast.LENGTH_SHORT).show();
                        return;
                    }
                })
                .setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        answer = false;
                        Toast.makeText(context, "" + answer, Toast.LENGTH_SHORT).show();
                        return;
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

}
