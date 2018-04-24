package no.xillez.kentwh.mobilelab3;

import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.CountDownTimer;
import android.os.Vibrator;

/**
 * Created by kent on 10.03.18.
 */

public class Ball extends GameObject
{
    protected int radius = 0;
    protected int color = 0;

    // Whether or not collision sound should be played!
    boolean playCollSound = true;
    CountDownTimer cdt = new CountDownTimer(100, 1) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            playCollSound = true;
        }
    };

    // Vibrator
    protected Vibrator vibrator;

    Ball()
    {
        super(new OvalShape());
    }

    public void update(float dt, GameObject gameObject)
    {
        // Find new velocity based on acceleration (in landscape mode, x and y is swapped)
        velocity.x += acceleration.y * 4.0f * dt;
        velocity.y += acceleration.x * 4.0f * dt;

        // Update color if changed
        this.getPaint().setColor(color);



        // Get the collision state and bounce if hit
        CollisionState collState = checkCollisionWithinSquareBounds(gameObject);

        // Did we collide? if so make GameActivity vibrate phone
        if (collState.left || collState.right || collState.top || collState.bottom)
        {
            // trigger vibration
            callback.triggerVibration();

            // If no collision previous update, play sound
            if (playCollSound)
            {
                callback.triggerSound();
                playCollSound = false;
            }
            cdt.cancel();
            cdt.start();
        }

        // Update position and collision box
        this.setBounds((int) position.x - radius, (int) position.y - radius, (int) position.x + radius, (int) position.y + radius);
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public int getRadius()
    {
        return radius;
    }

    public void setRadius(int radius)
    {
        this.radius = radius;
    }

    public void setVibrator(Vibrator vibrator)
    {
        this.vibrator = vibrator;
    }
}
