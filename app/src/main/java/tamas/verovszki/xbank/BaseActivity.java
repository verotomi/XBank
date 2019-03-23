package tamas.verovszki.xbank;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity implements LogoutListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApp) getApplication()).registerSessionListener(this);
        ((MyApp) getApplication()).startUserSession();

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        ((MyApp) getApplication()).onUserInteracted();
    }

    @Override
    public void onSessionLogout() {
        if (MyApp.isActivityVisible()){
            //startActivity(new Intent(this, MainActivity.class));
            //finish();

            // ez a handler kellett ahhoz, hogy a dialogbox és a countdowntimer működjön itt ebben az osztályban
            Message message = mHandler.obtainMessage(0, "parameter");
            message.sendToTarget();
        }
        else{
            Utility.saveLoginState(BaseActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
            finish();
            //Process.killProcess(Process.myPid()); // nem működik jól
            //finishAndRemoveTask(); // nem működik jól
            this.finishAffinity();
        }


    }
    CountDownTimer cdt;
    CustomDialogClass cdd;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override

        public void handleMessage(Message message) {
            // This is where you do your work in the UI thread.
            // Your worker tells you in the message what to do.
            //CustomDialogClass cdd = new CustomDialogClass(BaseActivity.this, getString(R.string.text_remove));
            cdd = new CustomDialogClass(BaseActivity.this, getString(R.string.security_timeout_expired));
            cdd.setCustomObjectListener2(new CustomDialogClass.MyCustomObjectListener2() {
                @Override
                public void onYes(String title) {
                    //Toast.makeText(BaseActivity.this, "Yes", Toast.LENGTH_SHORT).show();
                    cdt.cancel();
                    ((MyApp) getApplication()).startUserSession();
                }
                @Override
                public void onNo(String title) {
                    Utility.saveLoginState(BaseActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    //Toast.makeText(BaseActivity.this, "No", Toast.LENGTH_SHORT).show();
                    cdt.cancel();
                    startActivity(new Intent(BaseActivity.this, MainActivity.class));
                    finish();
                }
            });
            cdd.show();

            cdt = new CountDownTimer(10 * 1000, 1000) { // Visszaszámlálás (absztrakt osztály) ??
                public void onTick(long millisUntilFinished) { // meghatározott időközönként végrehajtódik ez a metódus
                    //Toast.makeText(BaseActivity.this, "Tick", Toast.LENGTH_SHORT).show();
                    //cdd.yes.setText("T: " + millisUntilFinished*1000);
                    cdd.TextViewquestion.setText(getString(R.string.security_timeout_expired));
                    cdd.TextViewMessage.setVisibility(View.VISIBLE);
                    cdd.TextViewMessage.setText( R.string.want_to_prolong);
                    cdd.no.setText(getString(R.string.no) + " (" +String.valueOf((int) Math.ceil(millisUntilFinished/1000 + 1))+ ")");
                }

                public void onFinish() { // számláló lejáratakor hajtódik végre
                    Utility.saveLoginState(BaseActivity.this, false); // elmentjük a "kilépve" állapotot SharedPrefferences-be
                    //Toast.makeText(BaseActivity.this, "Finish", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(BaseActivity.this, MainActivity.class));
                    finish();
                }
            }.start();

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        MyApp.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApp.activityPaused();
    }


}
