package no.xillez.kentwh.mobilelab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;
import android.view.View;

/**
 * Created by kent on 10.03.18.
 */

public class CustomCanvas extends View
{
    private static final String LOG_TAG_INFO = "Xillez_CustomCanvas [INFO]";
    private static final String LOG_TAG_WARN = "Xillez_CustomCanvas [WARN]";

    private Point wSize;
    private int MARGIN = 5;

    private ShapeDrawable background;
    private CollisionBox backCollBox;

    private Ball ball;

    public CustomCanvas(Context context)
    {
        super(context);
        wSize = new Point();

        // Get screen dimensions
        wSize.set(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);

        // Setup background
        Log.i(LOG_TAG_INFO, "Building background!");
        makeBackground();
    }

    private void update()
    {

    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        // Update everything before we draw it.
        update();

        // Draw background
        Log.i(LOG_TAG_INFO, "Drawing on canvas!");
        background.draw(canvas);

        // TODO: DRAW BALL!
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void makeBackground()
    {
        // Make new rectangle shape, set it's color, position and collision box
        background = new ShapeDrawable(new RectShape());
        background.getPaint().setColor(Color.DKGRAY);
        background.setBounds(MARGIN, MARGIN, wSize.x - MARGIN, wSize.y - MARGIN);
        backCollBox = new CollisionBox(MARGIN, MARGIN, wSize.x - MARGIN, wSize.y - MARGIN);
    }
}
