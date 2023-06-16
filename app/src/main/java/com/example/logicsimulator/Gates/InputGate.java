package com.example.logicsimulator.Gates;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

public class InputGate extends Gate {
    private Boolean output;

    private boolean isDragging = false;
    private Point lastTouchPoint = new Point();

    private Paint gatePaint;
    private Paint pointPaint;
    private Paint truePaint;
    private Paint falsePaint;

    private static Context context;


    InputGate(Point inputPoint1, Point inputPoint2, Point outputPoint, Point bodyPoint, Context context) {
        super(inputPoint1, inputPoint2, outputPoint, bodyPoint);
        this.context = context;
        init();
    }


    public InputGate(Context context){
        super(new Point(100, 220),new Point(100, 380),new Point(390, 300),new Point(400, 300));
        this.context = context;
        init();
    }


    private void init() {

        output = false;

        gatePaint = new Paint();
        gatePaint.setColor(Color.RED);

        falsePaint = new Paint();
        falsePaint.setColor(Color.RED);

        truePaint = new Paint();
        truePaint.setColor(Color.GREEN);

        pointPaint = new Paint();
        pointPaint.setColor(Color.WHITE);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStrokeWidth(6f);

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
        //NOT APPLICABLE
    }

    @Override
    public boolean calculateOutput() {
        return false;
    }

    @Override
    public void drawGate(Canvas canvas) {

        //canvas.drawBitmap(body, bodyPoint.x -330, bodyPoint.y-110, null);
        int centerX = bodyPoint.x -150;
        int centerY = bodyPoint.y;
        int rhombusSize = 250;
        // Create a Path object to define the rhombus shape
        Path path = new Path();
        path.moveTo(centerX, centerY - (rhombusSize / 2)); // Top point
        path.lineTo(centerX + (rhombusSize / 2), centerY); // Right point
        path.lineTo(centerX, centerY + (rhombusSize / 2)); // Bottom point
        path.lineTo(centerX - (rhombusSize / 2), centerY); // Left point
        path.close(); // Close the path to form a closed shape


        // Draw the filled rhombus shape on the canvas
        canvas.drawPath(path, gatePaint);

        // Draw output point
        canvas.drawCircle(outputPoint.x, outputPoint.y, 20f, pointPaint);
    }

    @Override
    public boolean checkNullInputs() {
        return false;
    }

    public Boolean getOutput() {
        return output;
    }

    public void setOutput(Boolean output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "ANDGate{" +
                ", output=" + output +
                '}';
    }

    public void changeState(){
        if(this.gatePaint.getColor() == falsePaint.getColor()){
            this.gatePaint = truePaint;
            this.output = true;
        }
        else{
            this.gatePaint = falsePaint;
            this.output = false;
        }
    }
}
