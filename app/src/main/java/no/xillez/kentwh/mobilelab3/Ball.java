package no.xillez.kentwh.mobilelab3;

import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Vibrator;

/**
 * Created by kent on 10.03.18.
 */

public class Ball extends ShapeDrawable
{
    // Ball properties
    private PointF position = new PointF(0.0f, 0.0f);
    private PointF velocity = new PointF(0.0f, 0.0f);
    private PointF acceleration = new PointF(0.0f, 0.0f);
    private int diameter = 0;
    private int color = 0;
    private CollisionBox collBox = new CollisionBox(0, 0, 0, 0);
    private BallCollideCallback callback = null;

    // Last collision state
    boolean prevCollState = false;

    // Vibrator
    private Vibrator vibrator;

    Ball()
    {
        super(new OvalShape());
    }

    void update(float dt, CollisionBox collBox)
    {
        // Find new velocity based on acceleration (in landscape mode x and y is switched)
        velocity.x += acceleration.y * 4.0f * dt;
        velocity.y += acceleration.x * 4.0f * dt;

        // Update color if changed
        this.getPaint().setColor(color);

        // Get the collision state
        CollisionState collState = isCollidingWithin(collBox);
        this.setVelocity(((collState.left || collState.right) ? 0.0f : velocity.x), ((collState.top || collState.bottom) ? 0.0f : velocity.y));

        // Update position with velocity and collision on x-axis and y-axis
        this.setPosition(((collState.left) ? collBox.left :
                                ((collState.right) ? collBox.right - diameter : position.x + velocity.x)),
                         ((collState.top) ? collBox.top :
                                ((collState.bottom) ? collBox.bottom - diameter : position.y + velocity.y)));


        // Did we collide? if so make GameActivity vibrate phone
        if (collState.left || collState.right || collState.top || collState.bottom)
        {
            // trigger vibration
            callback.triggerVibration();


            // If no collision previous update, play sound
            if (!prevCollState) callback.triggerSound();
            prevCollState = true;
        }
        else prevCollState = false;

        // Update position and collision box
        this.setBounds((int) position.x, (int) position.y, (int) position.x + diameter, (int) position.y + diameter);
        this.updateCollBox();
    }

    private CollisionState isCollidingWithin(CollisionBox collBox)
    {                           // Going left        Ball going to pass background left?
        return new CollisionState((velocity.x < 0 && this.collBox.left + velocity.x < collBox.left),
                                // Going up          Ball going to pass background top?
                                  (velocity.y < 0 && this.collBox.top + velocity.y < collBox.top),
                                // Going down        Ball going to pass backgrounds down?
                                  (velocity.y > 0 && this.collBox.bottom + velocity.y > collBox.bottom),
                                // Going right       Ball right hits backgrounds right
                                  (velocity.x > 0 && this.collBox.right + velocity.x > collBox.right));
    }

    public PointF getPosition()
    {
        return position;
    }

    void setPosition(PointF position)
    {
        this.position = position;
    }

    void setPosition(float x, float y)
    {
        this.position.x = x;
        this.position.y = y;
    }

    public PointF getVelocity()
    {
        return velocity;
    }

    void setVelocity(PointF velocity)
    {
        this.velocity = velocity;
    }

    private void setVelocity(float x, float y)
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

    void setAcceleration(float x, float y)
    {
        this.acceleration.x = x;
        this.acceleration.y = y;
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    int getDiameter()
    {
        return diameter;
    }

    void setDiameter(int diameter)
    {
        this.diameter = diameter;
    }

    public CollisionBox getCollBox()
    {
        return collBox;
    }

    public void setCollBox(CollisionBox collBox)
    {
        this.collBox = collBox;
    }

    public void setCollBox(int left, int top, int bottom, int right)
    {
        this.collBox.left = left;
        this.collBox.top = top;
        this.collBox.bottom = bottom;
        this.collBox.right = right;
    }

    void updateCollBox()
    {
        this.collBox.left = position.x;
        this.collBox.top = position.y;
        this.collBox.bottom = position.y + diameter;
        this.collBox.right = position.x + diameter;
    }

    void setVibrator(Vibrator vibrator)
    {
        this.vibrator = vibrator;
    }

    public void registerCallback(BallCollideCallback callback)
    {
        this.callback = callback;
    }

    interface BallCollideCallback
    {
        void triggerVibration();
        void triggerSound();
    }
}
