package no.xillez.kentwh.mobilelab3;

/**
 * Created by kent on 12.03.18.
 */

public class CollisionState
{
    public boolean left, top, bottom, right;

    public CollisionState(boolean left, boolean top, boolean bottom, boolean right)
    {
        this.left = left;
        this.top = top;
        this.bottom = bottom;
        this.right = right;
    }
}
