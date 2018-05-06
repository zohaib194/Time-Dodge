package no.xillez.kentwh.mobilelab3;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class GameCanvas extends View implements SensorEventListener, Ball.BallEffectCallback
{
    // Logging variables
    private static final String LOG_TAG_INFO = "CustomCanvas [INFO]";
    private static final String LOG_TAG_ERROR = "CustomCanvas [ERROR]";
    private boolean logDrawing = true;

    // Canvas variables
    private Point wSize;
    private int MARGIN;
    private float dt;
    private float dt_altered;
    private Long curr_time;
    private Long prev_time;
    private float spawnTime = 0.0f;
    private float additiveGameTime = 0.0f;

    private boolean showEffect;
    private float itemSpawnTime = 0.0f;

    private Random randGen = new Random();

    // Game variables
    private Long points = 0L;
    private Long bonus = 0L;
    private Long itemPoints = 0L;
    private int debrisBonusRadius = -1;

    // Paint
    private String bonusAch = "Bonus!";
    private Paint paint = new Paint();
    private Paint scorePaint = new Paint();

    // Special Effect variables
    private PointF ballPos;
    private float radiusDiffOnBallWithEffect = 0.0f;
    private Drawable shield;
    private boolean drawShield = false;

    // Timers
    private CountDownTimer deSpawnItem = new CountDownTimer(6000, 1)
    {
        @Override
        public void onTick(long millisUntilFinished) {}

        @Override
        public void onFinish()
        {
            specItems.clear();
        }
    };

    private CountDownTimer pointGiver = new CountDownTimer(1000, 1)
    {
        @Override
        public void onTick(long millisUntilFinished) {
            if(ballPos != null) {
                ballPos.x -= 0.5f;
                ballPos.y -= 0.5f;
            }
        }

        @Override
        public void onFinish()
        {
            points++;
            bonusAch = "";
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

    // Special Items
    private ArrayList<SpecItem> specItems = new ArrayList<>();

    public GameCanvas(Context context, AttributeSet attr)
    {
        super(context, attr);

        // Set canvas variables.
        this.MARGIN = 5;
        this.dt = -1.0f;
        this.curr_time = 0L;
        this.dt_altered = -1.0f;

        this.shield = ContextCompat.getDrawable(context, R.drawable.ball_shield_effect);

        // Get screen dimensions.
        Log.i(LOG_TAG_INFO, "Getting screen size!");
        this.wSize = new Point();
        this.wSize.set(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);

        // Setup background
        Log.i(LOG_TAG_INFO, "Making the background!");
        makeBackground();

        // Setup ball
        Log.i(LOG_TAG_INFO, "Making the ball!");
        makeBall();

        // Make a debris at first to keep player active in the beginning
        Log.i(LOG_TAG_INFO, "Making a debris to keep player active!");
        makeDebris();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.showEffect = sharedPref.getBoolean("pref_effect", true);

        makeSpecItem();


        this.pointGiver.start();

        // Ready prev_time for delta time calculation
        this.prev_time = System.currentTimeMillis();


        this.paint.setColor(getResources().getColor(R.color.colorAccent));
        this.paint.setTextSize(20);

        this.scorePaint.setColor(getResources().getColor(R.color.colorAccent));
        this.scorePaint.setTextSize(30);
    }

    /**
     * Setter for sensor.
     * @param sensor to be set.
     */
    public void setSensor(Sensor sensor)
    {
        this.sensor = sensor;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        // Sensor isn't set, return
        if (this.sensor == null)
        {
            Log.e(LOG_TAG_ERROR, "No sensor avaliable!");
            return;
        }

        // Use data from sensor to update game objects
        this.ball.setAcceleration(event.values[0] * 3.0f, event.values[1] * 3.0f);

        //Update things indirectly affected by sensor change.
        update();
    }

    /**
     * Update the ball and debris position, spawn special items, draw visual effects and
     * invalidate the current frame.
     */
    private void update() {

        // Get time since last frame
        this.curr_time = System.currentTimeMillis();
        this.dt = (this.curr_time - this.prev_time) / 1000.0f;
        this.prev_time = this.curr_time;

        this.additiveGameTime += this.dt * 0.5f;
        this.spawnTime += this.additiveGameTime * 0.5f;
        this.itemSpawnTime += this.dt;

        this.dt_altered = this.dt * ((float)Math.sqrt(Math.pow(this.ball.velocity.x, 2.0f) + Math.pow(this.ball.velocity.y, 2.0f)) * 100);

        if (this.spawnTime > 100 && this.debris.size() < 10)
        {
            makeDebris();
            this.spawnTime = 0;
        }

        if (this.itemSpawnTime > 100 && this.specItems.size() < 1)
        {
            makeSpecItem();
            this.itemSpawnTime = 0;
            this.deSpawnItem.start();
        }

        // Record all collisions for all game objects
        this.ball.checkCollisionWithinSquareBounds(this.background);

        // Checks if there is debris inside bonus radius previously.
        if (this.debrisBonusRadius != -1) {
            // Check if the debris has left the bonus radius.
            if (!this.ball.checkIfInsideBonusRadius(this.debris.get(this.debrisBonusRadius))) {
                if(!this.ball.hasCollided) {     // Check if the ball has collided with debris.
                    this.ballPos = this.ball.getPosition();
                    this.bonus++;
                    if (this.showEffect) {
                        this.bonusAch = "Bonus!";
                    }
                } else {
                    this.bonusAch = "";
                }
                this.ball.hasCollided = false;
                this.debrisBonusRadius = -1;        // -1 for no debris being in the bonus radius.
            }
        }

        for (Debris go : this.debris)
        {
            if (go.isOutside())
            {
                go.setPosition((this.wSize.x / 2) + (float) (Math.cos(Math.random() * 2.0f * Math.PI) * ((this.wSize.x / 2) * 1.5f)),
                        (this.wSize.y / 2) + (float) (Math.sin(Math.random() * 2.0f * Math.PI) * ((this.wSize.x / 2) * 1.5f)));

                //Calculate the unit vector (of length 1) in direction of the ball
                final PointF unNormalizedVelocity = new PointF(this.ball.getPosition().x - go.getPosition().x,this.ball.getPosition().y - go.getPosition().y);
                final PointF normalizedVector = new PointF(
                        unNormalizedVelocity.x / (float) Math.sqrt(Math.pow(unNormalizedVelocity.x, 2.0f) + Math.pow(unNormalizedVelocity.y, 2.0f)),
                        unNormalizedVelocity.y / (float) Math.sqrt(Math.pow(unNormalizedVelocity.x, 2.0f) + Math.pow(unNormalizedVelocity.y, 2.0f))
                );

                // Set velocity based on unit vector and random number between .0 and 100
                go.setVelocity(
                        (float) (normalizedVector.x * Math.random() * 10),
                        (float) (normalizedVector.y * Math.random() * 10)
                );
            }

            this.ball.checkCollisionWithOutsideRadius(go, true, this.radiusDiffOnBallWithEffect);
            for (Debris go2 : this.debris)
                if(go != go2) {
                    go.checkCollisionWithOutsideRadius(go2, true,0.0f);
                }
            go.checkCollisionWithinSquareBounds(this.background);

            // if there is no debris inside the bonus radius previously.
            if (this.debrisBonusRadius == -1) {
                if (this.ball.checkIfInsideBonusRadius(go)) {    // Check if there is any debris inside.
                    this.debrisBonusRadius = this.debris.lastIndexOf(go);    // Save the index of debris.
                }
            }
        }

        if (this.specItems.size() > 0)
            this.ball.checkCollisionWithOutsideRadius(this.specItems.get(0), false, 0.0f);

        if (this.drawShield)
            this.shield.setBounds((int) this.ball.getPosition().x - 75, (int) this.ball.getPosition().y - 75, (int) this.ball.getPosition().x + 75, (int) this.ball.getPosition().y + 75);

        // Update ball
        this.ball.update(this.dt, this.background);

        // Update all debris
        for (Debris go : this.debris)
            go.update(this.dt_altered, this.background);

        // New data is available, current UI/frame is invalid.
        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (this.logDrawing)
            Log.i(LOG_TAG_INFO, "Updating and drawing the background and ball on canvas!");

        // Draw background, ball and debris
        this.background.draw(canvas);
        this.ball.draw(canvas);
        if (this.specItems.size() > 0)
            this.specItems.get(0).draw(canvas);
        if (this.drawShield)
            this.shield.draw(canvas);
        for (Debris go : this.debris)
            go.draw(canvas);

        // Draw the bonus text.
        if(this.ballPos != null) {
            canvas.drawText(this.bonusAch, this.ballPos.x, this.ballPos.y, this.paint);
        }

        canvas.drawText("Score: " + this.points, this.wSize.x / 2.0f - (String.valueOf("Score: " + this.points).length() * 0.5f), this.wSize.y * 0.05f, this.scorePaint);

        // Disable draw logging after first time
        if (this.logDrawing)
            this.logDrawing = false;
    }

    /**
     * Make the background.
     */
    private void makeBackground()
    {
        // Make new rectangle shape, set it's color, position and collision box
        this.background = new GameObject(new RectShape());
        this.background.getPaint().setColor(Color.DKGRAY);
        this.background.setBounds(this.MARGIN, this.MARGIN, this.wSize.x - this.MARGIN, this.wSize.y - this.MARGIN);
    }

    /**
     * Make the ball.
     */
    private void makeBall()
    {
        // Set ball's color, position, velocity, radius and collision box
        this.ball = new Ball();
        this.ball.setRadius(25);
        this.ball.setPosition(new PointF(this.wSize.x / 2, this.wSize.y / 2));
        this.ball.setVelocity(new PointF(0.0f, 0.0f));
        this.ball.setColor(Color.GREEN);
        this.ball.registerCollisionCallback(this);
    }

    /**
     * Make the debris.
     */
    private void makeDebris()
    {
        // Set ball's color, position, velocity, radius and collision box
        Debris deb = new Debris();
        deb.setRadius(25);
        deb.setPosition(new PointF((this.wSize.x / 2) + (float) (Math.cos(Math.random() * 2.0f * Math.PI) * ((this.wSize.x / 2) * 1.5f)),
                (this.wSize.y / 2) + (float) (Math.sin(Math.random() * 2.0f * Math.PI) * ((this.wSize.x / 2) * 1.5f))));

        //Calculate the unit vector (of length 1) in direction of the ball
        final PointF unNormalizedVelocity = new PointF(this.ball.getPosition().x - deb.getPosition().x,this.ball.getPosition().y - deb.getPosition().y);
        final PointF normalizedVector = new PointF(
                unNormalizedVelocity.x / (float)Math.sqrt(Math.pow(unNormalizedVelocity.x, 2) + Math.pow(unNormalizedVelocity.y, 2)),
                unNormalizedVelocity.y / (float)Math.sqrt(Math.pow(unNormalizedVelocity.x, 2) + Math.pow(unNormalizedVelocity.y, 2))
        );

        // Set velocity based on unit vector random number between 0 and 100.
        deb.setVelocity(
                (float) (normalizedVector.x * Math.random() * 10),
                (float) (normalizedVector.y * Math.random() * 10)
        );
        deb.setColor(Color.BLUE);
        this.debris.add(deb);
    }

    /**
     * Make special item.
     */
    private void makeSpecItem()
    {
        // Find position of the screen to spawn the item
        int xPos = (int) (Math.random() * this.wSize.x);
        int yPos = (int) (Math.random() * this.wSize.y);

        // Make new rectangle shape, set it's color, position and effect. Then add to list
        SpecItem item = new SpecItem();
        item.setColor(Color.MAGENTA);
        item.setPosition(xPos, yPos);
        item.setSize(30);
        item.setEffect(this.randGen.nextInt(2 - 1 + 1) + 1);      // Shield effect = 1, Debris growth = 2
        this.specItems.add(item);
    }

    /**
     * Collision callback method for GameActivity.
     * @param gameActivity is the current activity.
     */
    public void registerCollisionCallback(GameObject.GameObjectCollisionCallback gameActivity)
    {
        // Relay the registration of collision collisionCallback to the ball, if it exists
        if (this.ball != null)
            this.ball.registerCollisionCallback(gameActivity);
    }

    public boolean isLoggingFirstDrawEvent()
    {
        return this.logDrawing;
    }

    public void setLoggingFirstDrawEvent(boolean logDrawing)
    {
        this.logDrawing = logDrawing;
    }

    /**
     * Set previous time.
     * @param time is the previous time.
     */
    public void setPrevTime(Long time)
    {
        this.prev_time = time;
    }

    /**
     * Get the points.
     * @return points achieved.
     */
    public Long getPoints()
    {
        return this.points;
    }

    /**
     * Get the bonus.
     * @return number of bonus achieved.
     */
    public Long getBonus()  { return this.bonus; }

    /**
     * Get the number of pickups.
     * @return number of pickups.
     */
    public Long getItemPoints()
    {
        return this.itemPoints;
    }

    /**
     * Cancel the point giver timer.
     */
    public void stopPointGiving()
    {
        this.pointGiver.cancel();
    }

    /**
     * Start the point giver timer.
     */
    public void startPointGiving()
    {
        this.pointGiver.start();
    }

    /**
     * Special item callback for removing the item.
     * @param item is to be removed.
     */
    @Override
    public void triggerSpecItemDeSpawn(SpecItem item)
    {
        this.specItems.remove(item);
    }

    /**
     * Callback to trigger special item points.
     */
    @Override
    public void triggerItemPoint()
    {
        this.itemPoints++;
    }

    /**
     * Callback to trigger shield.
     * @param enable if true enable the shield.
     */
    @Override
    public void triggerShield(boolean enable)
    {
        this.drawShield = enable;
        this.radiusDiffOnBallWithEffect = ((enable) ? 50.0f : 0.0f);
    }

    /**
     * Callback to trigger growth of debris.
     * @param enable if true enable the growth of debris.
     */
    @Override
    public void triggerDebrisSizeGrowth(boolean enable) {

        for(Debris deb : this.debris){
            deb.setRadius(((enable) ? 50 : 25));
        }
    }
}
