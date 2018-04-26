package no.xillez.kentwh.mobilelab3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.FragmentContainer;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class GameActivity extends AppCompatActivity implements Ball.BallCollideCallback, GameOverFragment.OnFragmentInteractionListener
{
    private static final String LOG_TAG_INFO = "Xillez_GameActivity [INFO]";
    private static final String LOG_TAG_WARN = "Xillez_GameActivity [WARN]";

    private GameCanvas gameCanvas;
    private View fragmentView;
    private Boolean gameOver = false;
    // SensorManagers
    private SensorManager sensorManager;
    private Sensor sensor;

    // Vibrator
    private Vibrator vibrator;

    // Media player variables
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG_INFO, "App starting up!");

        // Set view
        Log.i(LOG_TAG_INFO, "Setting view!");
        setContentView(R.layout.activity_game);
        gameCanvas = findViewById(R.id.game_gamecanvas01);

        // Set screen orientation
        Log.i(LOG_TAG_INFO, "Setting screen orientation!");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Get Vibrator
        Log.i(LOG_TAG_INFO, "Tying to find vibrator!");
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null)
            Log.i(LOG_TAG_WARN, "Vibrator is null!");

        // Make a media play to play boop sound
        Log.i(LOG_TAG_INFO, "Trying to get media player!");
        mediaPlayer = MediaPlayer.create(this, R.raw.boop);

        // Get accelerometer
        Log.i(LOG_TAG_INFO, "Finding acceleration sensor (accelerometer)!");
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Register the sensor listener
        Log.i(LOG_TAG_INFO, "Tying to register sensor!");
        sensorManager.registerListener(gameCanvas, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        // Give sensor and vibrator to Canvas
        Log.i(LOG_TAG_INFO, "Passing on sensor and vibrator to canvas!");
        gameCanvas.setSensor(sensor);
        gameCanvas.registerCollisionCallback_OnBall(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Un-register sensor listener
        Log.i(LOG_TAG_INFO, "App paused, un-registering sensor listener");
        sensorManager.unregisterListener(gameCanvas);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Re-register sensor listener
        Log.i(LOG_TAG_INFO, "App un-paused, registering sensor listener");
        sensorManager.registerListener(gameCanvas, sensor, SensorManager.SENSOR_DELAY_GAME);

        // Log first drawing even after resume
        if (gameCanvas.isLoggingFirstDrawEvent()) gameCanvas.setLoggingFirstDrawEvent(true);
    }

    @Override
    public void triggerGameOver() {
        if(gameOver == true) {
            return;
        }
        gameOver = true;

        FrameLayout frameLayout = findViewById(R.id.game_framelayout01);
        frameLayout.setVisibility(View.VISIBLE);

        fragmentView = findViewById(R.id.game_gameover01);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(fragmentView,
                        View.SCALE_X, 0f, 1.075f))
                .with(ObjectAnimator.ofFloat(fragmentView,
                        View.SCALE_Y, 0f, 1.075f));
        set.setDuration(750);

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                AnimatorSet set2 = new AnimatorSet();
                set2
                        .play(ObjectAnimator.ofFloat(fragmentView,
                                View.SCALE_X, 1.075f, 1f))
                        .with(ObjectAnimator.ofFloat(fragmentView,
                                View.SCALE_Y, 1.075f, 1f));
                set2.setDuration(300);

                set2.start();
            }
        });

        set.start();
    }

    @Override
    public void triggerVibration()
    {
        if(gameOver == true){
            return;
        }
        // Vibrator exists? Vibrate!
        if (vibrator != null)
            vibrator.vibrate(50);
    }

    @Override
    public void triggerSound()
    {
        if(gameOver == true){
            return;
        }
        // Media player exists? Play sound!
        if (mediaPlayer != null)
            mediaPlayer.start();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
