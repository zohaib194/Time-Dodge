package no.xillez.kentwh.mobilelab3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class GameActivity extends AppCompatActivity implements GameObject.GameObjectCollisionCallback, GameOverFragment.OnFragmentInteractionListener
{
    private static final String LOG_TAG_INFO = "GameActivity [INFO]";
    private static final String LOG_TAG_WARN = "GameActivity [WARN]";

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
        setContentView(R.layout.activity_game);         // TODO: exception thrown on screen blackout. FIX THIS! PRIORITY OVER 9000.
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
        gameCanvas.registerCollisionCallback(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        // Un-register sensor listener
        Log.i(LOG_TAG_INFO, "App paused, un-registering sensor listener");
        sensorManager.unregisterListener(gameCanvas);
        gameCanvas.stopPointGiving();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // Re-register sensor listener
        Log.i(LOG_TAG_INFO, "App un-paused, registering sensor listener");
        sensorManager.registerListener(gameCanvas, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        // Log first drawing even after resume
        if (gameCanvas.isLoggingFirstDrawEvent()) gameCanvas.setLoggingFirstDrawEvent(true);

        gameCanvas.setPrevTime(System.currentTimeMillis());
        gameCanvas.startPointGiving();
    }

    @Override
    public void triggerGameOver() {
        gameCanvas.stopPointGiving();

        if(gameOver == true) {
            return;
        }

        gameOver = true;

        FrameLayout frameLayout = findViewById(R.id.game_framelayout01);
        frameLayout.setVisibility(View.VISIBLE);

        //FragmentManager fragmentManager = getSupportFragmentManager();
        //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GameOverFragment frag = (GameOverFragment) getSupportFragmentManager().findFragmentById(R.id.game_gameover01);

        frag.setNewScore(gameCanvas.getPoints());
        frag.setBonus(gameCanvas.getBonus());
        frag.setUserName(getIntent().getStringExtra(getString(R.string.preference_username)));
        frag.setBestScore(getIntent().getLongExtra(getString(R.string.preference_bestscore), 0l));
        frag.removeCurrentScoreFromHighscore();

       // fragmentTransaction.commit();

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

                set2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        frag.animateScoreScreen();
                    }
                });
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
            vibrator.vibrate(75);
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
