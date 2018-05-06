package no.xillez.kentwh.mobilelab3;

public class CollisionState
{
    public boolean left, top, bottom, right;

    /**
     * Constructor for 'CollisionState' class. This is a simple container class.
     *
     * @param left - the left bounds of the object.
     * @param top - the top bounds of the object.
     * @param bottom - the bottom bounds of the object.
     * @param right - the rights bounds of the object.
     */
    public CollisionState(boolean left, boolean top, boolean bottom, boolean right)
    {
        this.left = left;
        this.top = top;
        this.bottom = bottom;
        this.right = right;
    }
}
