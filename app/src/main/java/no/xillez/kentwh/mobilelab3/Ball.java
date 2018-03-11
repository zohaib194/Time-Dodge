package no.xillez.kentwh.mobilelab3;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.NonNull;

/**
 * Created by kent on 10.03.18.
 */

public class Ball extends ShapeDrawable
{
    private Point position;
    private Point velocity;
    private int radius;
    private int color;
    public CollisionBox collBox;

    public Ball()
    {
        super(new OvalShape());
    }

    public void update(float dt)
    {
        // Update the color to GREEN
        this.getPaint().setColor(color);
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

    public Point getVelocity()
    {
        return velocity;
    }

    public void setVelocity(Point velocity)
    {
        this.velocity = velocity;
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
