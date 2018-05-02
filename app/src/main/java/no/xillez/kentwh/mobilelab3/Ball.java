package no.xillez.kentwh.mobilelab3;

import android.graphics.PointF;
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

    // Variables for effects
    private boolean ignoreCollisions = true;

    // Whether or not collision sound should be played!
    boolean playCollSound = true;
    CountDownTimer playCollSoundTimer = new CountDownTimer(250, 1) {
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

    @Override
    public void update(float dt, GameObject background)
    {
        // Find new velocity based on acceleration (in landscape mode, x and y is swapped)
        velocity.x += (acceleration.y * dt);
        velocity.y += (acceleration.x * dt);

        // Update color if changed
        this.getPaint().setColor(color);

        // Disable collision for any effects that require it
        if (!ignoreCollisions)
        {
            // Loop through all ball collisions and add affect
            for (PointF vec : collisions)
            {
                // Update velocity
                velocity.x += vec.x;
                velocity.y += vec.y;
            }
        }

        // We ran through these collisions, clear the list
        collisions.clear();

        // Set velocity according to collision state
        this.setVelocity(((backgroundCollState.left || backgroundCollState.right) ? velocity.x * -1 * 0.50f : velocity.x),
                ((backgroundCollState.top || backgroundCollState.bottom) ? velocity.y * -1 * 0.50f : velocity.y));

        // Update position with velocity and collision on x-axis and y-axis
        this.setPosition(new PointF(
                ((backgroundCollState.left) ? background.getBounds().left + radius :
                    ((backgroundCollState.right) ? background.getBounds().right - radius :
                        position.x + velocity.x)),
                ((backgroundCollState.top) ? background.getBounds().top + radius :
                    ((backgroundCollState.bottom) ? background.getBounds().bottom - radius :
                        position.y + velocity.y))));

        // Did we collide? if so make GameActivity vibrate phone
        if (backgroundCollState.left || backgroundCollState.right || backgroundCollState.top || backgroundCollState.bottom)
        {
            // trigger vibration
            collisionCallback.triggerVibration();

            // Trigger gameover
            collisionCallback.triggerGameOver();

            // If no collision previous update, play sound
            if (playCollSound)
            {
                collisionCallback.triggerSound();
                playCollSound = false;
            }
            playCollSoundTimer.cancel();
            playCollSoundTimer.start();
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
