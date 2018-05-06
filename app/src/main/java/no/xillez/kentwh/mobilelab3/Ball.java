package no.xillez.kentwh.mobilelab3;

import android.graphics.PointF;
import android.graphics.drawable.shapes.OvalShape;
import android.os.CountDownTimer;

public class Ball extends GameObject
{
    protected int radius = 0;
    protected int color = 0;
    private int currentEffect = 0;

    // Variables specific to effects
    private boolean ignoreCollisions = false;
    private boolean enableShield = false;

    // Whether or not collision sound should be played!
    private boolean playCollSound = true;

    // Counter to keep from spamming user with collision sounds
    private final CountDownTimer playCollSoundTimer = new CountDownTimer(250, 1) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            // Allow the collision sound to play
            playCollSound = true;
        }
    };

    /**
     *  Constructor of Ball class. Ball is always a oval shape
     */
    Ball()
    {
        super(new OvalShape());
    }

    /**
     * Updates the state of the ball during game play.
     *
     * @param dt - time since last frame
     * @param background - the background game-object
     */
    @Override
    public void update(float dt, GameObject background)
    {
        // Find new velocity based on acceleration (in landscape mode, x and y is swapped)
        velocity.x += (acceleration.y * dt);
        velocity.y += (acceleration.x * dt);

        // Set color to yellow if effect is active
        this.getPaint().setColor(((this.hasEffect) ? 0xFFFFFF00 : this.color));

        // Disable collision for any effects that require it
        if (!this.ignoreCollisions)
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
                ((backgroundCollState.left) ? background.getBounds().left + this.radius :
                    ((backgroundCollState.right) ? background.getBounds().right - this.radius :
                        position.x + velocity.x)),
                ((backgroundCollState.top) ? background.getBounds().top + this.radius :
                    ((backgroundCollState.bottom) ? background.getBounds().bottom - this.radius :
                        position.y + velocity.y))));

        // Did we collide? if so make GameActivity vibrate phone
        if (backgroundCollState.left || backgroundCollState.right || backgroundCollState.top || backgroundCollState.bottom)
        {
            // trigger vibration
            collisionCallback.triggerVibration();

            // Trigger game over
            collisionCallback.triggerGameOver();

            // If no collision previous update, play sound
            if (this.playCollSound)
            {
                collisionCallback.triggerSound();
                this.playCollSound = false;
            }
            this.playCollSoundTimer.cancel();
            this.playCollSoundTimer.start();
        }

        // Update position and collision box
        this.setBounds((int) position.x - this.radius, (int) position.y - this.radius, (int) position.x + this.radius, (int) position.y + this.radius);
    }

    /**
     * checkCollisionWithOutsideRadius - Checks for circular collision with objects radii.
     * It adds radiiAddition to check for collision with added this.radius from object. e
     * And dependent on the state of "saveCollResult" (bool), it'll save the collision result in object.
     *
     * @param gameObject - game object to check against
     * @param saveCollResult - whether to save collision or not
     * @param radiiAddition - this.radius to add to calculating collision
     * @return Whether a collision occurred or not
     */
    @Override
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
        // Received special item
        else if (diff <= 0 && gameObject instanceof SpecItem)
        {
            // Trigger the special item's effect
            if (!this.hasEffect)
                this.triggerEffect(((SpecItem) gameObject).getEffect(), ((SpecItem) gameObject));
        }

        return (diff <= 0);
    }

    /**
     * triggers some effect on the ball.
     *
     * @param effect - what effect to apply to the ball
     * @param item - what item is giving the effect
     */
    @Override
    public void triggerEffect(int effect, SpecItem item)
    {
        // No effect (effect for default behaviour)
        if (effect == 0)
        {
            hasEffect = false;
            this.ignoreCollisions = false;
            this.enableShield = false;
            if (currentEffect == 1)
                interactionCallback.triggerShield(false);
            else if (currentEffect == 2)
                interactionCallback.triggerDebrisSizeGrowth(false);
        }
        // Shield effect
        else if (effect == 1)
        {
            hasEffect = true;
            this.ignoreCollisions = true;
            this.enableShield = true;
            interactionCallback.triggerShield(true);
            interactionCallback.triggerItemPoint();
            effectDisTimer.start();
        }
        // Debris growth effect
        else if (effect == 2)
        {
            hasEffect = true;
            interactionCallback.triggerDebrisSizeGrowth(true);
            interactionCallback.triggerItemPoint();
            effectDisTimer.start();
        }

        // Remove current item picked up
        if (item != null)
            interactionCallback.triggerSpecItemDeSpawn(item);

        // Set current effect the ball has for more efficient disabling
        currentEffect = effect;
    }

    /**
     * Setter for ball color
     *
     * @param color - integer for color
     */
    public void setColor(int color)
    {
        this.color = color;
    }

    /**
     * Setter for ball radius
     *
     * @param radius - radius of ball
     */
    public void setRadius(int radius)
    {
        this.radius = radius;
    }

    /**
     * Function used to register callback. Used to callback if certain events happen.
     *
     * Events:
     * - SpecialItem De-spawn
     * - Giving item point
     * - Shield Effect
     * - Debris growth
     *
     * @param callback - Object implementing
     */
    public void registerCollisionCallback(BallEffectCallback callback)
    {
        this.interactionCallback = callback;
    }

    /**
     * Interface for effect callback
     */
    interface BallEffectCallback
    {
        void triggerSpecItemDeSpawn(SpecItem item);
        void triggerItemPoint();

        void triggerShield(boolean enable);
        void triggerDebrisSizeGrowth(boolean enable);

    }
}
