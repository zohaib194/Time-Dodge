package no.xillez.kentwh.mobilelab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by kent on 10.03.18.
 */

public class GameCanvas extends View implements SensorEventListener//, GameObject.GameObjectInteractionCallback
{
    // Logging variables
    private static final String LOG_TAG_INFO = "CustomCanvas [INFO]";
    private static final String LOG_TAG_WARN = "CustomCanvas [WARN]";
    private static final String LOG_TAG_ERROR = "CustomCanvas [ERROR]";
    private boolean logDrawing = true;

    // Canvas variables
    private Point wSize;
    private int MARGIN = 5;
    private float dt = -1.0f;
    private float dt_altered = -1.0f;
    private Long curr_time;
    private Long prev_time;
    private float spawnTime = 0.0f;
    private float additiveGameTime = 0.0f;


    private Long points = 0L;
    private CountDownTimer pointGiver = new CountDownTimer(2000, 1)
    {
        @Override
        public void onTick(long millisUntilFinished) {}

        @Override
        public void onFinish()
        {
            points++;
            Log.i("PointsTest", "" + points);
            this.start();
        }
    };

    // Sensors
    private Sensor sensor;

    // Background variables
    private GameObject background;

    // Ball
    private Ball ball;

    // Debris
    private ArrayList<Debris> debris = new ArrayList<>();

    public GameCanvas(Context context, AttributeSet attr)
    {
        super(context, attr);

        // Get screen dimensions
        Log.i(LOG_TAG_INFO, "Getting screen size!");
        wSize = new Point();
        wSize.set(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);

        // Setup background
        Log.i(LOG_TAG_INFO, "Making the background!");
        makeBackground();

        // Setup ball
        Log.i(LOG_TAG_INFO, "Making the ball!");
        makeBall();

        // Make a debris at first to keep player active in the beginning
        Log.i(LOG_TAG_INFO, "Making a debris to keep player active!");
        makeDebris();

        pointGiver.start();

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

        // Use data from sensor to update game objects
        ball.setAcceleration(event.values[0] * 3.0f, event.values[1] * 3.0f);

        //Update things indirectly affected by sensor change.
        update();

    }

    private void update() {

        // Get time since last frame
        curr_time = System.currentTimeMillis();
        dt = (curr_time - prev_time) / 1000.0f;
        prev_time = curr_time;

        additiveGameTime += dt * 0.5f;
        spawnTime += additiveGameTime * 0.5f;

        dt_altered = dt * (float)Math.sqrt(Math.pow(ball.velocity.x, 2.0f) + Math.pow(ball.velocity.y, 2.0f));

        if (spawnTime > 100 && debris.size() < 10)
        {
            makeDebris();
            spawnTime = 0;
        }

        // Record all collisions for all game objects
        ball.checkCollisionWithinSquareBounds(background);

        for (Debris go : debris)
        {
            if (go.isOutside())
            {
                go.setPosition((wSize.x / 2) + (float) (Math.cos(Math.random() * 2.0f * Math.PI) * ((wSize.x / 2) * 1.5f)),
                        (wSize.y / 2) + (float) (Math.sin(Math.random() * 2.0f * Math.PI) * ((wSize.x / 2) * 1.5f)));

                //Calculate the unit vector (of length 1) in direction of the ball
                final PointF unNormalizedVelocity = new PointF(ball.getPosition().x - go.getPosition().x,ball.getPosition().y - go.getPosition().y);
                final PointF normalizedVector = new PointF(
                        unNormalizedVelocity.x /(unNormalizedVelocity.x + unNormalizedVelocity.y),
                        unNormalizedVelocity.y /(unNormalizedVelocity.x + unNormalizedVelocity.y)
                );

                // Set position based on unit vector and ball movement.
                go.setVelocity(
                        normalizedVector.x * dt_altered,
                        normalizedVector.x * dt_altered
                );
            }

            ball.checkCollisionWithOutsideRadius(go);
            for (Debris go2 : debris)
                if(go != go2) {
                    go.checkCollisionWithOutsideRadius(go2);
                }
            go.checkCollisionWithinSquareBounds(background);
        }

        // Update ball
        ball.update(dt, background);

        // Update all debris
        for (Debris go : debris)
            go.update(dt_altered, background);

        // New data is available, current UI/frame is invalid.
        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (logDrawing)
            Log.i(LOG_TAG_INFO, "Updating and drawing the background and ball on canvas!");

        // Draw background, ball and debris
        background.draw(canvas);
        ball.draw(canvas);
        for (Debris go : debris)
            go.draw(canvas);

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
        background = new GameObject(new RectShape());
        background.getPaint().setColor(Color.DKGRAY);
        background.setBounds(MARGIN, MARGIN, wSize.x - MARGIN, wSize.y - MARGIN);
    }

    private void makeBall()
    {
        // Set ball's color, position, velocity, radius and collision box
        ball = new Ball();
        ball.setRadius(25);
        ball.setPosition(new PointF(wSize.x / 2, wSize.y / 2));
        ball.setVelocity(new PointF(0.0f, 0.0f));
        ball.setColor(Color.GREEN);
    }

    private void makeDebris()
    {
        // Set ball's color, position, velocity, radius and collision box
        Debris debri = new Debris();
        debri.setRadius(25);
        debri.setPosition(new PointF((wSize.x / 2) + (float) (Math.cos(Math.random() * 2.0f * Math.PI) * ((wSize.x / 2) * 1.5f)),
                (wSize.y / 2) + (float) (Math.sin(Math.random() * 2.0f * Math.PI) * ((wSize.x / 2) * 1.5f))));
        debri.setVelocity(new PointF((ball.getPosition().x - debri.getPosition().x) * 0.025f,
                (ball.getPosition().y - debri.getPosition().y) * 0.025f));
        debri.setColor(Color.BLUE);
        debris.add(debri);
    }

    public void registerCollisionCallback(GameObject.GameObjectCollisionCallback gameActivity)
    {
        // Relay the registration of collision collisionCallback to the ball, if it exists
        if (ball != null)
            ball.registerCollisionCallback(gameActivity);
    }

    public boolean isLoggingFirstDrawEvent()
    {
        return logDrawing;
    }

    public void setLoggingFirstDrawEvent(boolean logDrawing)
    {
        this.logDrawing = logDrawing;
    }

    public void setPrevTime(Long time)
    {
        this.prev_time = time;
    }

    public Long getPoints()
    {
        return points;
    }

    public void stopPointGiving()
    {
        pointGiver.cancel();
    }

    public void startPointGiving()
    {
        pointGiver.start();
    }

    /*@Override
    public void triggerRespawn(GameObject gameObject)
    {
        gameObject.respawn(new PointF((wSize.x / 2) + (float) (Math.cos(Math.random() * 2 * Math.PI) * (wSize.y / 4)),
                (wSize.y / 2) + (float) (Math.cos(Math.random() * 2 * Math.PI) * (wSize.y / 4))),
        new PointF((ball.getPosition().x - debri.getPosition().x) * 0.05f,
                (ball.getPosition().y - debri.getPosition().y) * 0.05f));
    }*/
}
