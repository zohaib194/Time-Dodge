package no.xillez.kentwh.mobilelab3;

import android.graphics.Point;

/**
 * Created by user on 10.03.18.
 */

public class Ball
{
    Point position;
    Point velocity;
    Point size;

    public Ball(Point position, Point velocity, Point size)
    {
        this.position = position;
        this.velocity = velocity;
        this.size = size;
    }
}
