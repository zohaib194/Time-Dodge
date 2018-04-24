package no.xillez.kentwh.mobilelab3;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.shapes.OvalShape;
import android.os.CountDownTimer;
import android.os.Vibrator;

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

    public void update(float dt, GameObject gameObject)
    {
        // Find new velocity based on acceleration (in landscape mode x and y is switched)
        velocity.x += acceleration.y * 4.0f * dt;
        velocity.y += acceleration.x * 4.0f * dt;

        // Update color if changed
        this.getPaint().setColor(color);

        // Get the collision state an bounce if hit
        //boolean collState = isColliding(gameObject);

        if (isColliding(gameObject))
        {
            // Find vector between objects
            //PointF vector = new PointF(this.getPosition().x - gameObject.getPosition().x,
            //        this.getPosition().y - gameObject.getPosition().y);

            // Find the vector from other object to me
            PointF negVector = new PointF(gameObject.getPosition().x - this.getPosition().x,
                    gameObject.getPosition().y - this.getPosition().y);

            // Find the dotproduct of
            //float dotProd = dot(this.getVelocity(), vector);

            velocity.x += negVector.x;
            velocity.y += negVector.y;


            //this.setVelocity(new PointF(((collState.left || collState.right) ? velocity.x * -1 * 0.60f : velocity.x),
            //        ((collState.top || collState.bottom) ? velocity.y * -1 * 0.60f : velocity.y)));

            // Update position with velocity and collision on x-axis and y-axis
            /*this.setPosition(new PointF(((collState.left) ? gameObject.getBounds().left + radius :
                        ((collState.right) ? gameObject.getBounds().right - radius :
                                position.x + velocity.x)),
                ((collState.top) ? gameObject.getBounds().top  + radius :
                        ((collState.bottom) ? gameObject.getBounds().bottom - radius :
                                position.y + velocity.y))));*/
        }

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
