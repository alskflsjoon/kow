package com.estsoft.mblockchain.kow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by joeylee on 2016-11-23.
 */

class Paper extends View {

    public Boolean clearCanvas = false;

    Paint paint = new Paint();
    Path path = new Path();

    float y;
    float x;

    public Paper(Context context) {
        super(context);
    }

    public Paper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {


        if (clearCanvas) {

            path = new Path();
            Paint clearPaint = new Paint();
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawRect(0, 0, 0, 0, clearPaint);
            clearCanvas = false;

        } else {

            paint.setStrokeWidth(8);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);


        }
        canvas.drawPath(path, paint);

    }

    public void clearCanvas() {

        clearCanvas = true;
        invalidate();

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        x = event.getX();
        y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        invalidate();


        return true;
    }
}
