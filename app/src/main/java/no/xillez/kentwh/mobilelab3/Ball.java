package no.xillez.kentwh.mobilelab3;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

/**
 * Created by kent on 10.03.18.
 */

public class Ball extends ShapeDrawable
{
    private Point position;
    private PointF velocity;
    //private PointF acceleration;
    private int radius;
    private int color;
    public CollisionBox collBox;

    public Ball()
    {
        super(new OvalShape());
    }

    public void update(float dt)
    {
        position.x += velocity.y * dt; //acceleration * dt;
        position.y += velocity.x * dt; //acceleration * dt;

        // Update position and collision box
        this.setBounds(position.x, position.y, position.x + radius, position.y + radius);

        // Update collision box
        this.collBox = new CollisionBox(position.x, position.y, position.x + radius, position.y + radius);
    }

    public Point getPosition()
    {
        return position;
    }

    public void setPosition(Point position)
    {
        this.position = position;
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
