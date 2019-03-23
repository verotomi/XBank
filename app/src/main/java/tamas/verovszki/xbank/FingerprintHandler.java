package tamas.verovszki.xbank;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;


@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    // You should use the CancellationSignal method whenever your app can no longer process user input, for example when your app goes
    // into the background. If you don’t use this method, then other apps will be unable to access the touch sensor, including the lockscreen!//

    private CancellationSignal cancellationSignal;
    private Context context;
    //private String userFirstName; // utolag irtam be ide
    //private int userId; // utolag irtam be ide

    // Step 2 - This variable represents the listener passed in by the owning object
    // The listener must implement the events interface and passes messages up to the parent.
    private MyCustomObjectListener listener;

    public FingerprintHandler(Context mContext) {
        context = mContext;
        // set null or default listener or accept as argument to constructor
        this.listener = null;
    }

    // Assign the listener implementing events interface that will receive the events
    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.listener = listener;
    }

    //Implement the startAuth method, which is responsible for starting the fingerprint authentication process//

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    public void stopAuth() {
        if(cancellationSignal != null && !cancellationSignal.isCanceled()){
            cancellationSignal.cancel();
        }
        //manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    //onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//

    public void onAuthenticationError(int errMsgId, CharSequence errString) {

        //I’m going to display the results of fingerprint authentication as a series of toasts.
        //Here, I’m creating the message that’ll be displayed if an error occurs//

        // !!!*** Ezt én utólag kiszedtem, mert néha megjelent, holott nem kellett volna
        //Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_SHORT).show();
    }

    @Override

    //onAuthenticationFailed is called when the fingerprint doesn’t match with any of the fingerprints registered on the device//

    public void onAuthenticationFailed() {
        Utility.vibrateForXMillisec(context, 15);
        Toast.makeText(context, context.getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
    }

    @Override

    //onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
    //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        //Utility.vibrateForXMillisec(context, 15);
        Toast.makeText(context, context.getString(R.string.authentication_help) + "\n" + helpString, Toast.LENGTH_SHORT).show();
    }@Override

    /**
     * Sikeres ujjlenyomat-azonosítást követően  meghívjuk a következő activityt, átadva neki a nevet és az id-t.
     */
    //onAuthenticationSucceeded is called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {

        // Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show();

        listener.onFingerprintAuthorized(context.getString(R.string.fingerprint_authorized)); // <---- fire listener here
        Utility.vibrateForXMillisec(context, 15);
        // Utility.saveLoginState(context, true); // átraktam a login acticitybe, mert így akkor is bejelentkezett (másodszorra), ha nem volt net

        //getSavedUserId();
        /*Utility.saveLoginState(context, true); // bejelentkezve változó beállítása, mentése
        Intent intent = new Intent(context, MobilBankMainActivity.class);
        intent.putExtra("user_firstname", userFirstName);
        intent.putExtra("user_id", "" + userId);
        context.startActivity(intent);
        ((LoginActivity)context).finish();

        //Utility.CallNextActivity(context, MobilBankMainActivity.class);

        // Nem működik.
        //Intent i = new Intent((Activity) context, MobilBankMainActivity.class);
        //startActivityForResult(i, 1);
        // startActivityForResult(MobilBankMainActivity.class, 1);
        //((Activity) context).finish(); // login activity bezárása classból - nem működik*/



        /*
        // login activity bezárása classból
        if(context instanceof Activity){
            ((Activity)context).finish(); }*/
    }

    /**
     * id és firstname kiolvasása sharedpreferencesből
     * ide lehet hogy be kéne rakni egy ellnőrzést, hogy ha nincs user-id, akkor ne lehessen belépni! Mert az user_id alapján fogok tudni bármit is csinálni.
     *
    public void getSavedUserId(){
        SharedPreferences sp = context.getSharedPreferences("mentett_adatok", Context.MODE_PRIVATE);
        String savedUserId = sp.getString("user_id", "üres");
        if (savedUserId.equals("üres")){
            userId = 0;
        }
        else{
            userId = Integer.parseInt(savedUserId);
        }
        String savedUserFirstname = sp.getString("user_firstname", "üres");
        if (savedUserFirstname.equals("üres")){
            userFirstName = "";
        }
        else{
            userFirstName = savedUserFirstname;
        }
    }*/

    public interface MyCustomObjectListener {
        // These methods are the different events and
        // need to pass relevant arguments related to the event triggered
        public void onFingerprintAuthorized(String title);
    }


}