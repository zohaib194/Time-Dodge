package no.xillez.kentwh.mobilelab3;

import android.app.Service;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class GameActivity extends AppCompatActivity
{

    private static final String LOG_TAG_INFO = "Xillez_GameActivity [INFO]";
    private static final String LOG_TAG_WARN = "Xillez_GameActivity [WARN]";

    private GameCanvas gameCanvas;

    // SensorManagers
    private SensorManager sensorManager;
    public Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Set view
        Log.i(LOG_TAG_INFO, "Setting view!");
        gameCanvas = new GameCanvas(getApplicationContext());
        setContentView(gameCanvas);

        Log.i(LOG_TAG_INFO, "Setting screen orientation!");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Get accelerometer
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(gameCanvas, sensor, SensorManager.SENSOR_DELAY_GAME);

        // Give it to Canvas
        gameCanvas.setSensor(sensor);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(gameCanvas);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(gameCanvas, sensor, SensorManager.SENSOR_DELAY_GAME);
    }
}
