package no.xillez.kentwh.mobilelab3;

import android.graphics.Canvas;
import android.graphics.drawable.shapes.RectShape;

/**
 * Created by kent on 10.03.18.
 */

public class SpecItem extends GameObject
{
    protected int size = 0;
    protected int color = 0;
    protected int effect = 0;

    SpecItem()
    {
        super(new RectShape());
    }

    public void update(float dt, GameObject gameObject) {}

    @Override
    public void draw(Canvas canvas)
    {
        // Update color if changed
        this.getPaint().setColor(color);

        // Update position and collision box
        this.setBounds((int) position.x - size, (int) position.y - size, (int) position.x + size, (int) position.y + size);

        super.draw(canvas);
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

    public int getEffect()
    {
        return effect;
    }

    public void setEffect(int effect)
    {
        this.effect = effect;
    }
}
