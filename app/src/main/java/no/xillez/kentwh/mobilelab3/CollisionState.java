package no.xillez.kentwh.mobilelab3;

class CollisionState
{
    final boolean left;
    final boolean top;
    final boolean bottom;
    final boolean right;

    /**
     * Constructor for 'CollisionState' class. This is a simple container class.
     *
     * @param left - the left bounds of the object.
     * @param top - the top bounds of the object.
     * @param bottom - the bottom bounds of the object.
     * @param right - the rights bounds of the object.
     */
    CollisionState(boolean left, boolean top, boolean bottom, boolean right)
    {
        this.left = left;
        this.top = top;
        this.bottom = bottom;
        this.right = right;
    }
}
