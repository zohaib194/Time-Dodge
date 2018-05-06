package no.xillez.kentwh.mobilelab3;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.CountDownTimer;

import java.util.ArrayList;

public class GameObject extends ShapeDrawable
{
    // Ball properties
    protected PointF position = new PointF(0.0f, 0.0f);
    protected PointF velocity = new PointF(0.0f, 0.0f);
    protected PointF acceleration = new PointF(0.0f, 0.0f);

    protected GameObjectCollisionCallback collisionCallback = null;

    protected ArrayList<PointF> collisions = new ArrayList<>();

    protected CollisionState backgroundCollState = new CollisionState(false, false, false, false);

    protected boolean hasCollided = false;

    // Common/often used variables for effects
    protected Ball.BallEffectCallback interactionCallback = null;
    protected boolean hasEffect = false;
    protected CountDownTimer effectDisTimer = new CountDownTimer(5000, 1)
    {
        @Override
        public void onTick(long millisUntilFinished)
        {

        }

        @Override
        public void onFinish()
        {
            // Reset effects
            triggerEffect(0, null);
        }
    };

    /**
     * Constructor
     * @param shape is to be drawn.
     */
    GameObject(Shape shape)
    {
        super(shape);
    }

    /**
     * Update the velocity and position according to collision state with gameObject.
     * @param dt delta time.
     * @param gameObject is to be updated.
     */
    public void update(float dt, GameObject gameObject)
    {
        // Find new velocity based on acceleration (in landscape mode, x and y is swapped)
        this.velocity.x += this.acceleration.y * 4.0f * dt;
        this.velocity.y += this.acceleration.x * 4.0f * dt;

        // Loop through all ball collisions and add affect
        for (PointF vec : this.collisions)
        {
            // Update velocity
            this.velocity.x += vec.x;
            this.velocity.y += vec.y;
        }

        // We ran through these collisions, clear the list
        this.collisions.clear();

        // Find the size of the object
        float size = (this.getBounds().right - this.getBounds().left) / 2.0f;

        // Set velocity according to collision state
        this.setVelocity(((this.backgroundCollState.left || this.backgroundCollState.right) ? this.velocity.x * -1 * 0.50f : this.velocity.x),
                ((this.backgroundCollState.top || this.backgroundCollState.bottom) ? this.velocity.y * -1 * 0.50f : this.velocity.y));

        // Update position with velocity and collision on x-axis and y-axis
        this.setPosition(new PointF(
                ((this.backgroundCollState.left) ? gameObject.getBounds().left + size :
                        ((this.backgroundCollState.right) ? gameObject.getBounds().right - size :
                                this.position.x + this.velocity.x)),
                ((this.backgroundCollState.top) ? gameObject.getBounds().top + size :
                        ((this.backgroundCollState.bottom) ? gameObject.getBounds().bottom - size :
                                this.position.y + this.velocity.y))));
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
    }

    /**
     * Check the collision with background.
     * @param gameObject is the background.
     * @return the collision state.
     */
    protected CollisionState checkCollisionWithinSquareBounds(GameObject gameObject)
    {
        // Save background collision state for later updating
        this.backgroundCollState = new CollisionState(
                // Going left        Ball going to pass background's left?
                (this.velocity.x < 0 && this.getBounds().left + this.velocity.x < gameObject.getBounds().left),
                // Going up          Ball going to pass background's top?
                (this.velocity.y < 0 && this.getBounds().top + this.velocity.y < gameObject.getBounds().top),
                // Going down        Ball going to pass background's down?
                (this.velocity.y > 0 && this.getBounds().bottom + this.velocity.y > gameObject.getBounds().bottom),
                // Going right       Ball right hits background's right?
                (this.velocity.x > 0 && this.getBounds().right + this.velocity.x > gameObject.getBounds().right));

        return this.backgroundCollState;
    }

    /**
     * Check collision with gameObject(debris).
     * @param gameObject is debris.
     * @param saveCollResult collision state.
     * @param radiiAddition additional radius around the ball to check collision with.
     * @return if true is collided.
     */
    protected boolean checkCollisionWithOutsideRadius(GameObject gameObject, boolean saveCollResult, float radiiAddition)
    {
        // Find radii of objects
        float thisRadius = (this.getBounds().width() / 2) + radiiAddition;
        float gameObjRadius = (gameObject.getBounds().width() / 2) ;

        // Find vector between objects
        PointF vector = new PointF((this.getPosition().x - gameObject.getPosition().x),
                (this.getPosition().y - gameObject.getPosition().y));

        // Calc the distance between objects
        float diff = (float) (Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2)) - thisRadius - gameObjRadius);

        // If any interesting results
        if (diff <= 0 && saveCollResult)
        {
            vector = new PointF(vector.x * 0.1f, vector.y * 0.1f);

            // Save collision state for later updating
            if (!this.collisions.contains(vector))
                this.collisions.add(vector);

            // Make other gameObject reason collision and update later
            gameObject.saveCollision(new PointF(-vector.x, -vector.y));
        }

        return (diff <= 0);
    }

    /**
     * Check if the debris is inside the bonus radius.
     * @param gameObject is the debris.
     * @return true if the debris is inside the bonus radius.
     */
    protected boolean checkIfInsideBonusRadius(GameObject gameObject){
        float radiiAddition = 10.0f;
        if(checkCollisionWithOutsideRadius(gameObject, false,0.0f)){
            this.hasCollided = true;
        }
        return (checkCollisionWithOutsideRadius(gameObject, false, radiiAddition));
    }

    public void triggerEffect(int effect, SpecItem item)
    {

    }

    /**
     * Get position.
     * @return position.
     */
    public PointF getPosition()
    {
        return this.position;
    }

    /**
     * Set position
     * @param position is to be set.
     */
    public void setPosition(PointF position)
    {
        this.position = position;
    }

    /**
     * Set position
     * @param x coordinate.
     * @param y coordinate.
     */
    public void setPosition(float x, float y)
    {
        this.position.x = x;
        this.position.y = y;
    }

    /**
     * Set velocity
     * @param velocity is to be set.
     */
    public void setVelocity(PointF velocity)
    {
        this.velocity = velocity;
    }

    /**
     * Set velocity
     * @param x coordinate.
     * @param y coordinate.
     */
    public void setVelocity(float x, float y)
    {
        this.velocity.x = x;
        this.velocity.y = y;
    }

    /**
     * Set acceleration.
     * @param x coordinate.
     * @param y coordinate.
     */
    public void setAcceleration(float x, float y)
    {
        this.acceleration.x = x;
        this.acceleration.y = y;
    }

    /**
     * Save the collision coordination.
     * @param dir is where collision occurred.
     */
    public void saveCollision(PointF dir)
    {
        this.collisions.add(dir);
    }

    /**
     * Callback for collision.
     * @param callback interface of methods to be triggered at collision.
     */
    public void registerCollisionCallback(GameObjectCollisionCallback callback)
    {
        this.collisionCallback = callback;
    }

    /**
     * Interface of method to be trigger at collision.
     */
    interface GameObjectCollisionCallback
    {
        void triggerGameOver();
        void triggerVibration();
        void triggerSound();
    }
}
