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

    private boolean hasHadEffect = false;

    // Variables specific to effects
    private boolean ignoreCollisions = false;
    private boolean enableShield = false;


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

        if (!hasHadEffect)
        {
            triggerEffect(1);
            hasHadEffect = true;
        }

        // Set color to yellow if effect is active
        this.getPaint().setColor(((this.hasEffect) ? 0xFFFFFF00 : color));

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

        // Activate shields on canvas
        if (enableShield)
            interactionCallback.triggerShield(true);

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

        // If any interesting results and it's not any special items
        if (diff <= 0 && saveCollResult && !(gameObject instanceof SpecItem))
        {
            vector = new PointF(vector.x * 0.1f, vector.y * 0.1f);

            // Save collision state for later updating
            if (!collisions.contains(vector))
                collisions.add(vector);

            // Make other gameObject reason collision and update later
            gameObject.saveCollision(new PointF(-vector.x, -vector.y));
        }
        // Received special item=
        else if (gameObject instanceof SpecItem)
        {
            // Trigger the special item's effect
            this.triggerEffect(((SpecItem) gameObject).getEffect());
        }

        return (diff <= 0);
    }

    @Override
    public void triggerEffect(int effect)
    {
        // No effect (effect for default behaviour)
        if (effect == 0)
        {
            hasEffect = false;
            ignoreCollisions = false;
            enableShield = false;
            interactionCallback.triggerShield(false);
        }
        // Shield effect
        else if (effect == 1)
        {
            hasEffect = true;
            ignoreCollisions = true;
            enableShield = true;
            effectDissTimer.start();
        }
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

    public void registerCollisionCallback(BallEffectCallback callback)
    {
        this.interactionCallback = callback;
    }

    interface BallEffectCallback
    {
        void triggerShield(boolean draw);
    }

}
