package no.xillez.kentwh.mobilelab3;

import android.graphics.Point;

/**
 * Created by kent on 10.03.18.
 */

public class Ball
{
    Point position;
    Point velocity;
    float radius;
    CollisionBox collBox;

    public Ball(Point position, Point velocity, float radius)
    {
        this.position = position;
        this.velocity = velocity;
        this.radius = radius;
        this.collBox = new CollisionBox(position.x, position.y, position.x + radius, position.y + radius);
    }
}
