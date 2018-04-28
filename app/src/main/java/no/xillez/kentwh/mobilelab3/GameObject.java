package no.xillez.kentwh.mobilelab3;

import android.graphics.PointF;
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
    //protected GameObjectInteractionCallback interactionCallback = null;

    protected ArrayList<PointF> collisions = new ArrayList<>();
    protected CollisionState backgroundCollState = new CollisionState(false, false, false, false);

    /*CountDownTimer respawnCountDownTimer = new CountDownTimer(1000, 1) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            callbackRespawn();
        }
    };*/

    GameObject(Shape shape)
    {
        super(shape);
    }

    public void update(float dt, GameObject gameObject) {}

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

    protected boolean checkCollisionWithOutsideRadius(GameObject gameObject)
    {
        // Find radii of objects
        float thisRadius = (this.getBounds().width() / 2);
        float gameObjRadius = (gameObject.getBounds().width() / 2);

        // Find vector between objects
        PointF vector = new PointF((this.getPosition().x - gameObject.getPosition().x),
                (this.getPosition().y - gameObject.getPosition().y));

        // Calc the distance between objects
        float diff = (float) (Math.sqrt(Math.pow(vector.x, 2) + Math.pow(vector.y, 2)) - thisRadius - gameObjRadius);

        // If any interesting results
        if (diff <= 0)
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

    /*public void callbackRespawn()
    {
        interactionCallback.triggerRespawn(getParentGameObject());
    }

    public void respawn(PointF pos, PointF vel)
    {
        this.setPosition(pos);
        this.setVelocity(vel);
    }*/

    /*protected float dot(PointF vec1, PointF vec2)
    {
        return ((vec1.x * vec2.x) + (vec1.y * vec2.y));
    }

    protected PointF norm(PointF vec)
    {
        float length = (float) Math.sqrt(Math.pow(vec.x, 2) + Math.pow(vec.y, 2));
        return new PointF(vec.x / length, vec.y / length);
    }*/

    public PointF getPosition()
    {
        return position;
    }

    public void setPosition(PointF position)
    {
        this.position = position;
    }

    @Deprecated
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

    /*public void registerInteractionCallback(GameObjectInteractionCallback callback)
    {
        this.interactionCallback = callback;
    }

    public GameObject getParentGameObject()
    {
        return this;
    }*/

    interface GameObjectCollisionCallback
    {
        void triggerGameOver();
        void triggerVibration();
        void triggerSound();
    }

    /*interface GameObjectInteractionCallback
    {
        void triggerRespawn(GameObject gameObject);
    }*/
}
