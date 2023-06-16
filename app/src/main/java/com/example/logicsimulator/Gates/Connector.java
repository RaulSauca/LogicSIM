package com.example.logicsimulator.Gates;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class Connector {
    private Boolean carriedValue;

    private Point startPoint;
    private Point endPoint;

    private Paint connectorPaint;

    private Paint defaultPaint;
    private Paint truePaint;
    private Paint falsePaint;

    public Connector(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;

        init();
    }

    private void init(){

        carriedValue = false;

        connectorPaint = new Paint();
        connectorPaint.setColor(Color.BLUE);
        connectorPaint.setStyle(Paint.Style.FILL);
        connectorPaint.setStrokeWidth(30f);

        defaultPaint = new Paint();
        defaultPaint.setColor(Color.BLUE);
        defaultPaint.setStyle(Paint.Style.FILL);
        defaultPaint.setStrokeWidth(30f);

        truePaint = new Paint();
        truePaint.setColor(Color.GREEN);
        truePaint.setStyle(Paint.Style.FILL);
        truePaint.setStrokeWidth(30f);

        falsePaint = new Paint();
        falsePaint.setColor(Color.RED);
        falsePaint.setStyle(Paint.Style.FILL);
        falsePaint.setStrokeWidth(30f);
    }

    public void drawConnector(Canvas canvas) {

        canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, connectorPaint);
        canvas.drawCircle(startPoint.x, startPoint.y, 20f, connectorPaint);
        canvas.drawCircle(endPoint.x, endPoint.y, 20f, connectorPaint);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Connector){
            return (this.startPoint.equals(((Connector) o).startPoint) && this.endPoint.equals(((Connector) o).endPoint)) || (this.startPoint.equals(((Connector) o).endPoint) && this.endPoint.equals(((Connector) o).startPoint));
        }
        return false;
    }

    public boolean getCarriedValue() {
        if(this.carriedValue == null)
            return false;
        return  this.carriedValue;
    }

    public void setCarriedValue(int option) {
        switch (option){
            case 0:
                this.carriedValue = null;
                break;
            case 1:
                this.carriedValue = true;
                break;
            case 2:
                this.carriedValue = false;
                break;
            default:
                break;
        }
        setConnectorPaint(this.carriedValue);
    }

    public Point getStartPoint() {
        return this.startPoint;
    }

    public Point getEndPoint(){
        return this.endPoint;
    }

    public void setConnectorPaint(Boolean status){
        if(status == null) {
            this.connectorPaint = defaultPaint;
            return;
        }
        if (status)
            this.connectorPaint = truePaint;
        else
            this.connectorPaint = falsePaint;
    }

    public void resetConnectorPaint(){
        this.connectorPaint = defaultPaint;
    }
}
