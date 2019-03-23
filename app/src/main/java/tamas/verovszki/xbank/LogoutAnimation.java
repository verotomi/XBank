package tamas.verovszki.xbank;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;

import com.airbnb.lottie.LottieAnimationView;

public class LogoutAnimation extends Activity { // át kellett írni AppCompatActivity-ról Activityre

    // Lottie-animációhoz
    private LottieAnimationView LottieLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // fejléc kiiktatása
        setContentView(R.layout.logout_animation);

        LottieLogout = findViewById(R.id.LottieLogout);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        LottieLogout.setSpeed((float) 1.5);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                Utility.CallNextActivity(LogoutAnimation.this, MainActivity.class);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out); // átmenetes animáció a két activity között
            }
        }, 2400);

    }

}
