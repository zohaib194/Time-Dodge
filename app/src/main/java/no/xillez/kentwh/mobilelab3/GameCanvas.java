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
        // If the sensor exists, use data from it to update velocity
        if (sensor != null)
            ball.setVelocity(event.values[0], event.values[1]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void update(float dt)
    {
        ball.update(dt);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // Get time since last frame
        curr_time = System.currentTimeMillis();
        dt = (curr_time - prev_time) / 1000.0f;
        prev_time = curr_time;

        // Not updateing with negative time? if not update everything
        Log.i(LOG_TAG_INFO, "Updating all data that need to be updated!");
        if (dt >= 0.0f)
            update(dt);

        // Draw background and ball
        Log.i(LOG_TAG_INFO, "Drawing background and ball on canvas!");
        background.draw(canvas);
        ball.draw(canvas);
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
        backCollBox = new CollisionBox(MARGIN, MARGIN, wSize.x - MARGIN, wSize.y - MARGIN);
    }

    private void makeBall()
    {
        // Set ball's color, position, velocity, radius and collision box
        ball.setRadius(50);
        ball.setPosition(new Point(wSize.x / 2 - ball.getRadius() / 2, wSize.y / 2 - ball.getRadius() / 2));
        ball.setVelocity(new PointF(0.0f, 0.0f));
        ball.setColor(Color.GREEN);
    }
}
