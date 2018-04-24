package no.xillez.kentwh.mobilelab3;

import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class GameActivity extends AppCompatActivity implements GameObject.GameObjectCollideCallback
{
    private static final String LOG_TAG_INFO = "Xillez_GameActivity [INFO]";
    private static final String LOG_TAG_WARN = "Xillez_GameActivity [WARN]";

    private GameCanvas gameCanvas;

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
        gameCanvas = new GameCanvas(getApplicationContext());
        setContentView(gameCanvas);

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
        sensorManager.registerListener(gameCanvas, sensor, SensorManager.SENSOR_DELAY_GAME);

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
    public void triggerVibration()
    {
        // Vibrator exists? Vibrate!
        if (vibrator != null)
            vibrator.vibrate(50);
    }

    @Override
    public void triggerSound()
    {
        // Media player exists? Play sound!
        if (mediaPlayer != null)
            mediaPlayer.start();
    }
}
