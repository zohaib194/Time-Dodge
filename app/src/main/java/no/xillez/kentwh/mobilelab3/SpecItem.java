package no.xillez.kentwh.mobilelab3;

import android.graphics.drawable.shapes.RectShape;

/**
 * Created by kent on 10.03.18.
 */

public class SpecItem extends GameObject
{
    protected int size = 0;
    protected int color = 0;

    SpecItem()
    {
        super(new RectShape());
    }

    @Override
    public void update(float dt, GameObject gameObject)
    {
        // Update color if changed
        this.getPaint().setColor(color);

        // If we have collision information, move back to limit duplication next frame
        if (collisions.size() > 0)
        {
            // TODO: Add affects if nr of collisions are over 0!
        }

        // Update position and collision box
        this.setBounds((int) position.x - size, (int) position.y - size, (int) position.x + size, (int) position.y + size);
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }
}
