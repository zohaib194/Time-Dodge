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
            velocity.x = (velocity.x * 0.5f) + vec.x;
            velocity.y = (velocity.y * 0.5f) + vec.y;
        }

        // We ran through these collisions, clear the list
        collisions.clear();

        // If debris hit window edge, respawn after 1 sec
        /*if (!respawnTimerStarted && (backgroundCollState.left || backgroundCollState.top || backgroundCollState.right || backgroundCollState.bottom)) {
            this.respawnCountDownTimer.start();
            respawnTimerStarted = true;
        }*/

        // Update position with new velocity
        position.x += velocity.x;
        position.y += velocity.y;

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
}
