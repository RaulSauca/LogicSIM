package com.example.logicsimulator.Gates;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class OutputGate extends Gate {

    private boolean isDragging = false;
    private Point lastTouchPoint = new Point();

    private Paint gatePaint;
    private Paint pointPaint;
    private Paint truePaint;
    private Paint defaultPaint;

    private static Context context;


    OutputGate(Point inputPoint1, Point inputPoint2, Point outputPoint, Point bodyPoint, Context context) {
        super(inputPoint1, inputPoint2, outputPoint, bodyPoint);
        this.context = context;
        init();
    }


    public OutputGate(Context context){
        super(new Point(100, 220),new Point(100, 380),new Point(390, 300),new Point(400, 300));
        this.context = context;
        init();
    }


    private void init() {

        defaultPaint = new Paint();
        defaultPaint.setColor(Color.GRAY);

        truePaint = new Paint();
        truePaint.setColor(Color.YELLOW);

        pointPaint = new Paint();
        pointPaint.setColor(Color.WHITE);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStrokeWidth(6f);

        gatePaint = defaultPaint;

    }

    @Override
    public void setInputA(int option) {
        //NOT APPLICABLE
    }

    @Override
    public void setInputB(int option) {
        //NOT APPLICABLE
    }

    @Override
    public void setOutput(int option) {
        this.gatePaint = defaultPaint;
    }

    @Override
    public boolean calculateOutput() {
        return false;
    }

    @Override
    public void drawGate(Canvas canvas) {

        // Draw Body
        canvas.drawCircle(bodyPoint.x -10, bodyPoint.y-130, 110f, gatePaint);

        // Draw output point
        canvas.drawCircle(outputPoint.x, outputPoint.y, 20f, pointPaint);
    }

    @Override
    public boolean checkNullInputs() {
        return false;
    }

    public void changeState(boolean value){
        if(value){
            this.gatePaint = truePaint;
        }
        else{
            this.gatePaint = defaultPaint;
        }
    }

}