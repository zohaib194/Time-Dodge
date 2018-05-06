package no.xillez.kentwh.mobilelab3;

import android.graphics.Canvas;
import android.graphics.drawable.shapes.RectShape;

/**
 * SpecItem handles pickups that alter the behaviour of the game.
 *
 * Created by kent on 10.03.18.
 */
class SpecItem extends GameObject
{
    /**
     * Width and height of the square to draw.
     */
    private int size = 0;

    /**
     * Color of the square to draw.
     */
    private int color = 0;

    /**
     * Identifies the effect for the game.
     */
    private int effect = 0;

    /**
     * Constructs the shape of the object to draw.
     */
    SpecItem()
    {
        super(new RectShape());
    }

    /**
     *Overrides super to prevent unnecessary check for collision and position updates.
     *
     * @param dt delta time since last frame.
     * @param gameObject Item affecting the update.
     */
    @Override
    public void update(float dt, GameObject gameObject) {}


    /**
     * Drawn square with size with and height in specified color.
     *
     * @param canvas Part of the UI to draw on.
     */
    @Override
    public void draw(Canvas canvas)
    {
        // Update color if changed
        this.getPaint().setColor(this.color);

        // Update position and collision box
        this.setBounds(
                (int) this.position.x - this.size, (int) this.position.y - this.size,
                (int) this.position.x + this.size, (int) this.position.y + this.size);

        // Draw my self after defining my variables
        super.draw(canvas);
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    void setSize(int size)
    {
        this.size = size;
    }

    int getEffect()
    {
        return effect;
    }

    void setEffect(int effect)
    {
        this.effect = effect;
    }
}
