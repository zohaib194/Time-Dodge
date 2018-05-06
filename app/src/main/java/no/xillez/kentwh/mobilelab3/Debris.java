package no.xillez.kentwh.mobilelab3;

import android.graphics.PointF;
import android.graphics.drawable.shapes.OvalShape;

public class Debris extends GameObject
{
    protected int radius = 0;
    protected int color = 0;

    boolean isOutside = false;
    boolean hasBeenInside = false;

    /**
     * Constructor for 'Debris' class. Debris is always an oval shape.
     */
    Debris()
    {
        super(new OvalShape());
    }

    /**
     * Updates all game related variables.
     *
     * @param dt - time since last frame.
     * @param gameObject - the current game-object to check against.
     */
    @Override
    public void update(float dt, GameObject gameObject)
    {
        // Find new velocity based on acceleration (in landscape mode x and y is switched)
        velocity.x += acceleration.y * dt;
        velocity.y += acceleration.x * dt;

        // Update color if changed
        this.getPaint().setColor(this.color);

        // If we have collision information, move back to limit duplication next frame
        if (collisions.size() > 0)
        {
            position.x += -velocity.x;
            position.y += -velocity.y;
        }

        // Loop through all ball collisions and add affect
        for (PointF vec : collisions)
        {
            // Update velocity
            velocity.x = (velocity.x * 0.75f) + vec.x;
            velocity.y = (velocity.y * 0.75f) + vec.y;
        }

        // We ran through these collisions, clear the list
        collisions.clear();

        // Have I been inside before and am I inside now (not past the edge of the screen).
        if (!this.hasBeenInside && !(backgroundCollState.left || backgroundCollState.top || backgroundCollState.right || backgroundCollState.bottom))
            this.hasBeenInside = !(backgroundCollState.left || backgroundCollState.top || backgroundCollState.right || backgroundCollState.bottom);

        // have I been inside yet and am I collision now (past the edge of the screen)
        this.isOutside = (this.hasBeenInside && (backgroundCollState.left || backgroundCollState.top || backgroundCollState.right || backgroundCollState.bottom));

        // Update position with new velocity
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        // Update position and collision box
        this.setBounds((int) position.x - this.radius, (int) position.y - this.radius, (int) position.x + this.radius, (int) position.y + this.radius);
    }

    /**
     * Checks the square collision state of the current frame with the given game object.
     *
     * @param gameObject - the game object to check against.
     * @return Whether or not a collision happened.
     */
    @Override
    protected CollisionState checkCollisionWithinSquareBounds(GameObject gameObject)
    {
        // Save background collision state for later updating
        backgroundCollState = new CollisionState(
                // Going left        Ball passed background's left?
                (velocity.x < 0 && this.getBounds().right < gameObject.getBounds().left),
                // Going up          Ball passed background's top?
                (velocity.y < 0 && this.getBounds().bottom < gameObject.getBounds().top),
                // Going down        Ball passed background's down?
                (velocity.y > 0 && this.getBounds().top > gameObject.getBounds().bottom),
                // Going right       Ball passed background's right?
                (velocity.x > 0 && this.getBounds().left > gameObject.getBounds().right));

        return backgroundCollState;
    }

    /**
     * Getter for debris color.
     *
     * @return The color of the debris.
     */
    public int getColor()
    {
        return this.color;
    }

    /**
     * Setter for debris color.
     *
     * @param color - color to apply to the debris.
     */
    public void setColor(int color)
    {
        this.color = color;
    }

    /**
     * Setter for debris' radius.
     *
     * @param radius - the size of the debris radius.
     */
    public void setRadius(int radius)
    {
        this.radius = radius;
    }

    /**
     * Getter for whether the debris is outside of the screen or not.
     *
     * @return whether the debris is outside of the screen or not.
     */
    public boolean isOutside()
    {
        return this.isOutside;
    }
}
