package no.xillez.kentwh.mobilelab3;

/**
 * Created by kent on 10.03.18.
 */

public class CollisionBox
{
    public float left, top, bottom, right;

    public CollisionBox(float x, float y, float z, float w)
    {
        this.left = x;
        this.top = y;
        this.bottom = z;
        this.right = w;
    }
}
