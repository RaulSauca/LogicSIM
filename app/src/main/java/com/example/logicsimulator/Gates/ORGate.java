package com.example.logicsimulator.Gates;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ORGate extends Gate {
    private Boolean inputA;
    private Boolean inputB;
    private Boolean output;
    private static final float BAR_WIDTH_PERCENT = 0.6f;
    private static final float BAR_HEIGHT_PERCENT = 0.2f;
    private static final float BAR_OFFSET_PERCENT = 0.4f;

    private boolean isDragging = false;
    private Point lastTouchPoint = new Point();

    private Bitmap body;
    private Paint gatePaint;
    private Paint pointPaint;
    private Paint connectionPaint;

    private static Context context;


    ORGate(Point inputPoint1, Point inputPoint2, Point outputPoint, Point bodyPoint, Context context) {
        super(inputPoint1, inputPoint2, outputPoint, bodyPoint);
        this.context = context;
        init();
    }


    public ORGate(Context context){
        super(new Point(100, 220),new Point(100, 380),new Point(400, 300),new Point(400, 300));
        this.context = context;
        init();
    }


    private void init() {

        int resourceId = context.getResources().getIdentifier("orgate", "drawable", context.getPackageName());
        InputStream inputStream = context.getResources().openRawResource(resourceId);

        File file = new File(context.getFilesDir(), "orgate.png");

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            // Get the file path
            String filePath = file.getAbsolutePath();

            // Use the filePath in the decodeFile method
            // decodeFile(filePath);
            body = BitmapFactory.decodeFile(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int desiredWidth = (int) (body.getWidth() / 0.6); // Scale to half the width
        int desiredHeight = (int) (body.getHeight() / 0.6); // Scale to half the height

        body = Bitmap.createScaledBitmap(body, desiredWidth, desiredHeight, false);

        gatePaint = new Paint();
        gatePaint.setColor(Color.WHITE);
        gatePaint.setStyle(Paint.Style.FILL);
        gatePaint.setStrokeWidth(4f);

        pointPaint = new Paint();
        pointPaint.setColor(Color.WHITE);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStrokeWidth(6f);

        connectionPaint = new Paint();
        connectionPaint.setColor(Color.BLUE);
        connectionPaint.setStyle(Paint.Style.STROKE);
        connectionPaint.setStrokeWidth(2f);
    }

    @Override
    public void setInputA(int option) {
        switch (option) {
            case 0:
                this.inputA = null;
                break;
            case 1:
                this.inputA = true;
                break;
            case 2:
                this.inputA = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void setInputB(int option) {
        switch (option) {
            case 0:
                this.inputB = null;
                break;
            case 1:
                this.inputB = true;
                break;
            case 2:
                this.inputB = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void setOutput(int option) {
        switch (option) {
            case 0:
                this.output = null;
                break;
            case 1:
                this.output = true;
                break;
            case 2:
                this.output = false;
                break;
            default:
                break;
        }
    }

    @Override
    public boolean calculateOutput() {
        this.output = this.inputA || this.inputB;
        return this.output;
    }

    @Override
    public void drawGate(Canvas canvas) {

        canvas.drawBitmap(body, bodyPoint.x -320, bodyPoint.y-123, null);

        // Draw input points
        canvas.drawCircle(inputPoint1.x, inputPoint1.y, 20f, pointPaint);
        canvas.drawCircle(inputPoint2.x, inputPoint2.y, 20f, pointPaint);

        // Draw output point
        canvas.drawCircle(outputPoint.x, outputPoint.y, 20f, pointPaint);
    }

    @Override
    public boolean checkNullInputs() {
        if(this.inputA !=null && this.inputB != null) {
            return true;
        }
        return false;
    }

    public Boolean getInputA() {
        return inputA;
    }

    public Boolean getInputB() {
        return inputB;
    }

    public Boolean getOutput() {
        return output;
    }

    public void setInputA(Boolean inputA) {
        this.inputA = inputA;
    }

    public void setInputB(Boolean inputB) {
        this.inputB = inputB;
    }

    public void setOutput(Boolean output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "ANDGate{" +
                "inputA=" + inputA +
                ", inputB=" + inputB +
                ", output=" + output +
                '}';
    }
}
