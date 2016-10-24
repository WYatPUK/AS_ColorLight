package com.example.a37754.cl1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by 37754 on 2016/10/21.
 */

public class LEDView extends ImageView {
    final String LEDView_msg = "LEDView_MSG";

    Paint paint;
    Point PixelXY;
    static boolean Cycle_On = true;
    long Start_Time;
    double quanshu = 4;
    long Cycle_Time = 3000;
    long Release_Time = 1000;

    final int ColorAt[] = {0xff000000, 0xff886600, 0xffff0000, 0xffff8800, 0xffffff00, 0xff00ff00,
            0xff0000ff, 0xff9900ff, 0xff444444, 0xffffffff};
    public static ArrayList<Integer> ColorArray;
    public static int ColorStatue;

    Bitmap mBitmap;

    public LEDView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        paint = new Paint();
        PixelXY = new Point();
        Start_Time = System.currentTimeMillis();
        Cycle_On = true; //注释掉，resume时不旋转，好看一点
        ColorArray = new ArrayList<Integer>();
        for (int i=0; i<11; i++) {
            ColorArray.add(ColorAt[0]);
        }
        Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.p_3d);
        ColorStatue = Color.GREEN;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        PixelXY.x = this.getWidth();
        PixelXY.y = this.getHeight();
        //canvas.drawColor(Color.GRAY);
        paint.setColor(Color.BLUE);
        paint.setColor(Color.YELLOW);
        Point p = new Point();
        if (Cycle_On) {
            //Cycle!
            p = this.Cycle_Point(System.currentTimeMillis() - Start_Time);
        } else {
            p.x = (int) (PixelXY.x*0.05);
            p.y = PixelXY.y/2;
        }
        int R = R_Point(System.currentTimeMillis() - Start_Time);
        for (int i=0; i<11; i++) {
            paint.setColor(ColorArray.get(i));
            Point z = Num_Point(p, 0.4625*i - 0.7418);
            canvas.drawCircle(z.x, z.y, R, paint);
        }
        paint.setColor(ColorStatue);
        if (System.currentTimeMillis() - Start_Time < Cycle_Time) {
            canvas.drawCircle(PixelXY.x / 2, PixelXY.y / 2, 50, paint);
        } else if (System.currentTimeMillis() - Start_Time < Cycle_Time + Release_Time) {
            canvas.drawCircle(PixelXY.x / 2, PixelXY.y / 2 * (1 + (0.2f * (System.currentTimeMillis() - Start_Time - Cycle_Time)) / (Release_Time)), 50, paint);
        } else {
            canvas.drawCircle(PixelXY.x / 2, PixelXY.y / 2 * 1.2f, 50, paint);
        }
        if (LEDVM.telIn) {
            //canvas.drawText(LEDVM.telNum, PixelXY.x/2, PixelXY.y/2, paint);
        } else {
            //canvas.drawText("None", PixelXY.x/2, PixelXY.y/2, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        this.invalidate();
        return true;
    }

    private Point Cycle_Point(long t) {
        Point point = new Point();
        double R;
        if (t < Cycle_Time) {
            R = (PixelXY.x)*0.05;
            point.x = (int) (PixelXY.x/2 - R*(Math.cos(Math.PI*quanshu*t/(Cycle_Time + Release_Time))));
            point.y = (int) (PixelXY.y/2 - R*(Math.sin(Math.PI*quanshu*t/(Cycle_Time + Release_Time))));
        } else if (t < Release_Time + Cycle_Time) {
            R = (PixelXY.x)*(0.35*(t - Cycle_Time)/Release_Time + 0.05);
            point.x = (int) (PixelXY.x/2 - R*(Math.cos(Math.PI*quanshu*t/(Cycle_Time + Release_Time))));
            point.y = (int) (PixelXY.y/2 - R*(Math.sin(Math.PI*quanshu*t/(Cycle_Time + Release_Time))));
        } else {
            point.x = (int) ((PixelXY.x)*0.05);
            point.y = (int) ((PixelXY.y)/2);
            Cycle_On = false;
        }
        return point;
    }

    private int R_Point(long t) {
        if (t < Cycle_Time) {
            return 10;
        } else if (t < Cycle_Time + Release_Time) {
            return (int) (30.0*(t - Cycle_Time)/Release_Time + 10);
        } else {
            return 40;
        }
    }

    private Point Num_Point(Point x, double theta) {
        Point y = new Point();
        int yx = x.x - PixelXY.x/2;
        int yy = x.y - PixelXY.y/2;
        y.x = (int) (yx*Math.cos(theta) - yy*Math.sin(theta));
        y.y = (int) (yy*Math.cos(theta) + yx*Math.sin(theta));
        y.x += PixelXY.x/2;
        y.y += PixelXY.y/2;
        return y;
    }
}
