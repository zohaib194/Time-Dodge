package no.xillez.kentwh.mobilelab3;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.CountDownTimer;

import java.util.ArrayList;

/**
 * Created by kent on 19.04.18.
 */

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

    private Paint paint = new Paint();

    // Common/often used variables for effects
    protected Ball.BallEffectCallback interactionCallback = null;
    protected boolean hasEffect = false;
    protected CountDownTimer effectDissTimer = new CountDownTimer(5000, 1)
    {
        @Override
        public void onTick(long millisUntilFinished)
        {

        }

        @Override
        public void onFinish()
        {
            // Reset effects
            triggerEffect(0);
        }
    };


    GameObject(Shape shape)
    {
        super(shape);
    }

    public void update(float dt, GameObject gameObject)
    {
        // Find new velocity based on acceleration (in landscape mode, x and y is swapped)
        velocity.x += acceleration.y * 4.0f * dt;
        velocity.y += acceleration.x * 4.0f * dt;

        // Loop through all ball collisions and add affect
        for (PointF vec : collisions)
        {
            // Update velocity
            velocity.x += vec.x;
            velocity.y += vec.y;
        }

        // We ran through these collisions, clear the list
        collisions.clear();

        // Find the size of the object
        float size = (this.getBounds().right - this.getBounds().left) / 2.0f;

        // Set velocity according to collision state
        this.setVelocity(((backgroundCollState.left || backgroundCollState.right) ? velocity.x * -1 * 0.50f : velocity.x),
                ((backgroundCollState.top || backgroundCollState.bottom) ? velocity.y * -1 * 0.50f : velocity.y));

        // Update position with velocity and collision on x-axis and y-axis
        this.setPosition(new PointF(
                ((backgroundCollState.left) ? gameObject.getBounds().left + size :
                        ((backgroundCollState.right) ? gameObject.getBounds().right - size :
                                position.x + velocity.x)),
                ((backgroundCollState.top) ? gameObject.getBounds().top + size :
                        ((backgroundCollState.bottom) ? gameObject.getBounds().bottom - size :
                                position.y + velocity.y))));
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
    }

    protected CollisionState checkCollisionWithinSquareBounds(GameObject gameObject)
    {
        // Save background collision state for later updating
        backgroundCollState = new CollisionState(
                // Going left        Ball going to pass background's left?
                (velocity.x < 0 && this.getBounds().left + velocity.x < gameObject.getBounds().left),
                // Going up          Ball going to pass background's top?
                (velocity.y < 0 && this.getBounds().top + velocity.y < gameObject.getBounds().top),
                // Going down        Ball going to pass background's down?
                (velocity.y > 0 && this.getBounds().bottom + velocity.y > gameObject.getBounds().bottom),
                // Going right       Ball right hits background's right?
                (velocity.x > 0 && this.getBounds().right + velocity.x > gameObject.getBounds().right));;

        return backgroundCollState;
    }

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
            if (!collisions.contains(vector))
                collisions.add(vector);

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
           hasCollided = true;
        }
        return (checkCollisionWithOutsideRadius(gameObject, false, radiiAddition));
    }

    /*protected PointF norm(PointF vec)
    {
        float length = (float) Math.sqrt(Math.pow(vec.x, 2) + Math.pow(vec.y, 2));
        return new PointF(vec.x / length, vec.y / length);
    }*/

    public void triggerEffect(int effect) {}

    public PointF getPosition()
    {
        return position;
    }

    public void setPosition(PointF position)
    {
        this.position = position;
    }

    public void setPosition(float x, float y)
    {
        this.position.x = x;
        this.position.y = y;
    }

    public PointF getVelocity()
    {
        return velocity;
    }

    public void setVelocity(PointF velocity)
    {
        this.velocity = velocity;
    }

    public void setVelocity(float x, float y)
    {
        this.velocity.x = x;
        this.velocity.y = y;
    }

    public PointF getAcceleration()
    {
        return acceleration;
    }

    public void setAcceleration(PointF acceleration)
    {
        this.acceleration = acceleration;
    }

    public void setAcceleration(float x, float y)
    {
        this.acceleration.x = x;
        this.acceleration.y = y;
    }

    public void saveCollision(PointF dir)
    {
        collisions.add(dir);
    }

    public void registerCollisionCallback(GameObjectCollisionCallback callback)
    {
        this.collisionCallback = callback;
    }

    interface GameObjectCollisionCallback
    {
        void triggerGameOver();
        void triggerVibration();
        void triggerSound();
    }
}
