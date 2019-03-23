package tamas.verovszki.xbank;

import android.app.Activity;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;

import com.airbnb.lottie.LottieAnimationView;

public class DoneAnimation extends Activity { // át kellett írni AppCompatActivity-ról Activityre

    // Lottie-animációhoz
    private LottieAnimationView LottieDone;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  // fejléc kiiktatása
        setContentView(R.layout.done_animation);

            LottieDone = findViewById(R.id.LottieDone);
            getWindow().setBackgroundDrawableResource(R.color.transparent);
            LottieDone.setSpeed((float) 2);

            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, 1500);

    }

}
