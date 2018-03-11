package no.xillez.kentwh.mobilelab3;

import android.app.Service;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class GameActivity extends AppCompatActivity// implements SensorEventListener
{

    private static final String LOG_TAG_INFO = "Xillez_GameActivity [INFO]";
    private static final String LOG_TAG_WARN = "Xillez_GameActivity [WARN]";

    private GameCanvas gameCanvas;

    // SensorManagers
    private SensorManager sensorManager;
    public Sensor sensor;

    /*private TextView xView;
    private TextView yView;
    private TextView zView;*/

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG_INFO, "App starting up!");

        // Set view
        Log.i(LOG_TAG_INFO, "Setting view!");
        gameCanvas = new GameCanvas(getApplicationContext());
        setContentView(gameCanvas);

        Log.i(LOG_TAG_INFO, "Setting screen orientation!");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Get accelerometer
        Log.i(LOG_TAG_INFO, "Finding acceleration sensor (accelerometer)!");
        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Log.i(LOG_TAG_INFO, "Tying to register sensor!");
        sensorManager.registerListener(gameCanvas, sensor, SensorManager.SENSOR_DELAY_GAME);

        // Give it to Canvas
        Log.i(LOG_TAG_INFO, "Passing on sensor to canvas!");
        gameCanvas.setSensor(sensor);

        /*this.xView = findViewById(R.id.game_textview01);
        this.yView = findViewById(R.id.game_textview02);*/
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i(LOG_TAG_INFO, "App paused, un-registering sensor listener");
        sensorManager.unregisterListener(gameCanvas);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.i(LOG_TAG_INFO, "App un-paused, registering sensor listener");
        sensorManager.registerListener(gameCanvas, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    /*@Override
    public void onSensorChanged(SensorEvent event)
    {
        xView.setText("" + event.values[0]);
        yView.setText("" + event.values[1]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }*/
}
