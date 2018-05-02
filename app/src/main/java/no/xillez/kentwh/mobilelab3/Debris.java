package no.xillez.kentwh.mobilelab3;

import android.graphics.PointF;
import android.graphics.drawable.shapes.OvalShape;

/**
 * Created by kent on 10.03.18.
 */

public class Debris extends GameObject
{
    protected int radius = 0;
    protected int color = 0;

    boolean isOutside = false;
    boolean hasBeenInside = false;

    Debris()
    {
        super(new OvalShape());
    }

    @Override
    public void update(float dt, GameObject gameObject)
    {
        // Find new velocity based on acceleration (in landscape mode x and y is switched)
        velocity.x += acceleration.y * dt;
        velocity.y += acceleration.x * dt;

        // Update color if changed
        this.getPaint().setColor(color);

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
        if (!hasBeenInside && !(backgroundCollState.left || backgroundCollState.top || backgroundCollState.right || backgroundCollState.bottom))
            hasBeenInside = !(backgroundCollState.left || backgroundCollState.top || backgroundCollState.right || backgroundCollState.bottom);

        // have I been inside yet and am I collision now (past the edge of the screen)
        isOutside = (hasBeenInside && (backgroundCollState.left || backgroundCollState.top || backgroundCollState.right || backgroundCollState.bottom));

        // Update position with new velocity
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        // Update position and collision box
        this.setBounds((int) position.x - radius, (int) position.y - radius, (int) position.x + radius, (int) position.y + radius);
    }

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

    public boolean isOutside()
    {
        return isOutside;
    }

    public void setOutside(boolean outside)
    {
        isOutside = outside;
    }
}
