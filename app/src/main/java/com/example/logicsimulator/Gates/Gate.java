package com.example.logicsimulator.Gates;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public abstract class Gate {
    protected Paint gatePaint;
    protected Paint connectionPaint;
    protected Point inputPoint1;
    protected Point inputPoint2;
    protected Point outputPoint;
    protected Point bodyPoint;
    protected boolean isDragging;
    protected Point lastTouchPoint;

    Gate(Point inputPoint1, Point inputPoint2, Point outputPoint, Point bodyPoint) {
        this.inputPoint1 = inputPoint1;
        this.inputPoint2 = inputPoint2;
        this.outputPoint = outputPoint;
        this.bodyPoint = bodyPoint;

        gatePaint = new Paint();
        gatePaint.setColor(Color.BLACK);
        gatePaint.setStyle(Paint.Style.STROKE);
        gatePaint.setStrokeWidth(2f);

        connectionPaint = new Paint();
        connectionPaint.setColor(Color.BLACK);
        connectionPaint.setStrokeWidth(2f);

        isDragging = false;
        lastTouchPoint = new Point();
    }

    public abstract void setInputA (int option);

    public abstract void setInputB (int option);

    public abstract void  setOutput (int option);

    public abstract boolean calculateOutput();

    public abstract void drawGate(Canvas canvas);

    public boolean isInsideBounds(int x, int y) {
        // Check if the touch event is inside the gate bounds
        return x >= inputPoint1.x && x <= outputPoint.x-20 &&
                y >= Math.min(inputPoint1.y-50, outputPoint.y-50) &&
                y <= Math.max(inputPoint1.y+100, outputPoint.y+100);
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public void setLastTouchPoint(Point point) {
        lastTouchPoint = point;
    }

    public Point getLastTouchPoint() {
        return lastTouchPoint;
    }

    public void moveGate(int dx, int dy) {
        // Update the positions of the gate and points
        inputPoint1.x += dx;
        inputPoint1.y += dy;
        inputPoint2.x += dx;
        inputPoint2.y += dy;
        outputPoint.x += dx;
        outputPoint.y += dy;
        bodyPoint.x += dx;
        bodyPoint.y += dy;
    }

    public abstract boolean checkNullInputs();
}
