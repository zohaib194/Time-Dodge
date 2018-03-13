package no.xillez.kentwh.mobilelab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.View;

/**
 * Created by kent on 10.03.18.
 */

public class GameCanvas extends View implements SensorEventListener
{
    // Logging variables
    private static final String LOG_TAG_INFO = "Xillez_CustomCanvas [INFO]";
    private static final String LOG_TAG_WARN = "Xillez_CustomCanvas [WARN]";
    private static final String LOG_TAG_ERROR = "Xillez_CustomCanvas [ERROR]";
    private boolean logDrawing = true;

    // Canvas variables
    private Point wSize;
    private int MARGIN = 5;
    private float dt = -1.0f;
    private Long curr_time;
    private Long prev_time;

    // Sensors
    private Sensor sensor;

    // Background variables
    private ShapeDrawable background;
    private CollisionBox backCollBox;

    //Ball variables
    private Ball ball;

    public GameCanvas(Context context)
    {
        super(context);
        wSize = new Point();

        // Get screen dimensions
        Log.i(LOG_TAG_INFO, "Getting screen size!");
        wSize.set(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);

        // Setup background
        Log.i(LOG_TAG_INFO, "Making the background!");
        makeBackground();

        // Setup ball
        Log.i(LOG_TAG_INFO, "Making the ball!");
        ball = new Ball();
        makeBall();

        // Ready prev_time for delta time calculation
        prev_time = System.currentTimeMillis();
    }

    public void setSensor(Sensor sensor)
    {
        this.sensor = sensor;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        // Sensor isn't set, return
        if (sensor == null)
        {
            Log.e(LOG_TAG_ERROR, "No sensor avaliable!");
            return;
        }

        // Use data from sensor to update velocity
        ball.setAcceleration(event.values[0], event.values[1]);

        // New data is available, current UI/frame is invalid
        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (logDrawing)
            Log.i(LOG_TAG_INFO, "Updating and drawing the background and ball on canvas!");

        // Get time since last frame
        curr_time = System.currentTimeMillis();
        dt = (curr_time - prev_time) / 1000.0f;
        prev_time = curr_time;

        // Is dt a valid value, if so, update ball
        if (dt >= 0.0f)
            ball.update(dt, backCollBox);

        // Draw background and ball
        background.draw(canvas);
        ball.draw(canvas);

        // Disable draw logging after first time
        if (logDrawing)
            logDrawing = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void makeBackground()
    {
        // Make new rectangle shape, set it's color, position and collision box
        background = new ShapeDrawable(new RectShape());
        background.getPaint().setColor(Color.DKGRAY);
        background.setBounds(MARGIN, MARGIN, wSize.x - MARGIN, wSize.y - MARGIN);
        backCollBox = new CollisionBox(MARGIN, MARGIN, wSize.y - MARGIN, wSize.x - MARGIN);
    }

    private void makeBall()
    {
        // Set ball's color, position, velocity, radius and collision box
        ball.setDiameter(50);
        ball.setPosition(new PointF(wSize.x / 2 - ball.getDiameter() / 2, wSize.y / 2 - ball.getDiameter() / 2));
        ball.setVelocity(new PointF(0.0f, 0.0f));
        ball.setColor(Color.GREEN);
        ball.updateCollBox();
    }

    public void registerCollisionCallback_OnBall(GameActivity gameActivity)
    {
        // Relay the registration of collision callback to the ball, if it exists
        if (ball != null)
            ball.registerCallback(gameActivity);
    }

    public boolean isLoggingFirstDrawEvent()
    {
        return logDrawing;
    }

    public void setLoggingFirstDrawEvent(boolean logDrawing)
    {
        this.logDrawing = logDrawing;
    }
}
