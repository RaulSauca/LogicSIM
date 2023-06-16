package com.example.logicsimulator.Gates;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GateView extends View implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private static List<Gate> gates;
    private static List<Connector> connectors;
    private static List<InputGate> inputGates;
    private static List<OutputGate> outputGates;
    private Point selectedPoint1;
    private Point selectedPoint2;
    private Paint pointPaint;

    private GestureDetector gestureDetector;
    private Context context = getContext();

    public GateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        gestureDetector = new GestureDetector(context, this);
        gestureDetector.setOnDoubleTapListener(this);
        init();
    }

    public GateView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GateView(Context context) {
        this(context, null, 0);
    }

    private void init() {

        gates = new ArrayList<>();
        connectors = new ArrayList<>();
        inputGates = new ArrayList<>();
        outputGates = new ArrayList<>();

        pointPaint = new Paint();
        pointPaint.setColor(Color.GRAY);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(2f);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        invalidate();
        if (!(gates == null))
            for (Gate gate : gates) {
                if (gate != null)
                    gate.drawGate(canvas);
            }
        if (!(connectors == null))
            for (Connector connector : connectors) {
                if (connector != null)
                    connector.drawConnector(canvas);
            }
        if (!(inputGates == null))
            for (InputGate inputGate : inputGates) {
                if (inputGate != null)
                    inputGate.drawGate(canvas);
            }

        if (!(outputGates == null))
            for (OutputGate outputGate : outputGates) {
                if (outputGate != null)
                    outputGate.drawGate(canvas);
            }

        if (!(selectedPoint1 == null))
            canvas.drawCircle(selectedPoint1.x, selectedPoint1.y, 20f, pointPaint);
        if (!(selectedPoint2 == null))
            canvas.drawCircle(selectedPoint2.x, selectedPoint2.y, 20f, pointPaint);

        evaluateCircuit();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (gates == null)
            return false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Check if the touch event is within any gate bounds
                for (InputGate inputGate : inputGates) {
                    if (inputGate.isInsideBounds(x, y)) {
                        inputGate.changeState();
                        inputGate.setDragging(true);
                        inputGate.setLastTouchPoint(new Point(x, y));
                        return true;
                    }
                }
                for (Gate gate : gates) {
                    if (gate.isInsideBounds(x, y)) {
                        gate.setDragging(true);
                        gate.setLastTouchPoint(new Point(x, y));
                        return true; // Consume the touch event
                    }
                }

                for (OutputGate outputGate : outputGates) {
                    if (outputGate.isInsideBounds(x, y)) {
                        outputGate.setDragging(true);
                        outputGate.setLastTouchPoint(new Point(x, y));
                        return true;
                    }
                }

                Point touchedPoint = getSelectedPoint(x, y);
                if (touchedPoint != null) {
                    togglePointSelection(touchedPoint);
                    if (selectedPoint1 != null && selectedPoint2 != null) {
                        // Both points are selected, call your function
                        Connector connector = new Connector(selectedPoint1, selectedPoint2);
                        Boolean alreadyConnected = true;
                        for (Connector oldConnector : connectors) {
                            if (oldConnector.equals(connector)) {
                                connectors.remove(oldConnector);
                                resetValues();
                                evaluateCircuit();
                                alreadyConnected = false;
                                selectedPoint1 = null;
                                selectedPoint2 = null;
                                invalidate();
                                return true;
                            }
                        }
                        if(checkIllegalConnection()) {

                            if (alreadyConnected) {
                                connectors.add(connector);
                                resetValues();
                                evaluateCircuit();
                            }
                            selectedPoint1 = null;
                            selectedPoint2 = null;
                        }
                    }
                    invalidate(); // Redraw the view
                    return true; // Consume the touch event
                }
                break;

            case MotionEvent.ACTION_MOVE:
                for (InputGate inputGate : inputGates) {
                    if (inputGate.isDragging()) {
                        // Calculate the distance moved
                        int dx = x - inputGate.getLastTouchPoint().x;
                        int dy = y - inputGate.getLastTouchPoint().y;

                        // Update the positions of the gate and points
                        inputGate.moveGate(dx, dy);

                        // Update the last touch point
                        inputGate.setLastTouchPoint(new Point(x, y));
                        invalidate(); // Redraw the view
                        return true; // Consume the touch event
                    }
                }
                for (Gate gate : gates) {
                    if (gate.isDragging()) {
                        // Calculate the distance moved
                        int dx = x - gate.getLastTouchPoint().x;
                        int dy = y - gate.getLastTouchPoint().y;

                        // Update the positions of the gate and points
                        gate.moveGate(dx, dy);

                        // Update the last touch point
                        gate.setLastTouchPoint(new Point(x, y));
                        invalidate(); // Redraw the view
                        return true; // Consume the touch event
                    }
                }
                for (OutputGate outputGate : outputGates) {
                    if (outputGate.isDragging()) {
                        // Calculate the distance moved
                        int dx = x - outputGate.getLastTouchPoint().x;
                        int dy = y - outputGate.getLastTouchPoint().y;

                        // Update the positions of the gate and points
                        outputGate.moveGate(dx, dy);

                        // Update the last touch point
                        outputGate.setLastTouchPoint(new Point(x, y));
                        invalidate(); // Redraw the view
                        return true; // Consume the touch event
                    }
                }
                break;

            case MotionEvent.ACTION_UP:

            case MotionEvent.ACTION_CANCEL:
                for (InputGate inputGate : inputGates) {
                    inputGate.setDragging(false);
                }
                for (Gate gate : gates) {
                    gate.setDragging(false);
                }
                for (OutputGate outputGate : outputGates) {
                    outputGate.setDragging(false);
                }
                break;
        }

        return super.onTouchEvent(event);
    }

    private boolean checkIllegalConnection() {

        for (Gate gate : gates){
            if((gate.inputPoint1.equals(selectedPoint1) && gate.inputPoint2.equals(selectedPoint2)) || (gate.inputPoint2.equals(selectedPoint1) && gate.inputPoint1.equals(selectedPoint2))){
                Toast.makeText(context, "You cannot connect a gate to itself", Toast.LENGTH_SHORT).show();
                selectedPoint2 = null;
                return false;
            }
            if((gate.inputPoint1.equals(selectedPoint1) && gate.outputPoint.equals(selectedPoint2)) || (gate.outputPoint.equals(selectedPoint1) && gate.inputPoint1.equals(selectedPoint2))){
                Toast.makeText(context, "You cannot connect a gate to itself", Toast.LENGTH_SHORT).show();
                selectedPoint2 = null;
                return false;
            }
            if((gate.inputPoint2.equals(selectedPoint1) && gate.outputPoint.equals(selectedPoint2)) || (gate.outputPoint.equals(selectedPoint1) && gate.inputPoint2.equals(selectedPoint2))){
                Toast.makeText(context, "You cannot connect a gate to itself", Toast.LENGTH_SHORT).show();
                selectedPoint2 = null;
                return false;
            }
        }


        for (InputGate inputGate : inputGates){
            for (Gate gate : gates){
                if((inputGate.outputPoint.equals(selectedPoint1) && gate.outputPoint.equals(selectedPoint2)) || (inputGate.outputPoint.equals(selectedPoint2) && gate.outputPoint.equals(selectedPoint1))){
                    Toast.makeText(context, "You cannot connect an input to an output", Toast.LENGTH_SHORT).show();
                    selectedPoint2 = null;
                    return false;
                }
            }
        }

        for (OutputGate outputGate : outputGates){
            for (Gate gate : gates){
                if((outputGate.outputPoint.equals(selectedPoint1) && gate.inputPoint1.equals(selectedPoint2)) || (outputGate.outputPoint.equals(selectedPoint2) && gate.inputPoint1.equals(selectedPoint1))){
                    Toast.makeText(context, "You cannot connect two inputs", Toast.LENGTH_SHORT).show();
                    selectedPoint2 = null;
                    return false;
                }
                if((outputGate.outputPoint.equals(selectedPoint1) && gate.inputPoint2.equals(selectedPoint2)) || (outputGate.outputPoint.equals(selectedPoint2) && gate.inputPoint2.equals(selectedPoint1))){
                    Toast.makeText(context, "You cannot connect two inputs", Toast.LENGTH_SHORT).show();
                    selectedPoint2 = null;
                    return false;
                }
            }
        }

        for (OutputGate outputGate : outputGates){
            for (Connector connector : connectors){
                if((outputGate.outputPoint.equals(selectedPoint1)) && (connector.getEndPoint().equals(outputGate.outputPoint) || connector.getStartPoint().equals(outputGate.outputPoint))){
                    Toast.makeText(context, "You can connect the output with only one connection", Toast.LENGTH_SHORT).show();
                    selectedPoint1 = null;
                    return false;
                }
                if((outputGate.outputPoint.equals(selectedPoint2)) && (connector.getEndPoint().equals(outputGate.outputPoint) || connector.getStartPoint().equals(outputGate.outputPoint))){
                    Toast.makeText(context, "You can connect the output with only one connection", Toast.LENGTH_SHORT).show();
                    selectedPoint2 = null;
                    return false;
                }
            }
        }

        for (Gate gate : gates){
            for (Connector connector : connectors){
                if((gate.inputPoint1.equals(selectedPoint1)) && (connector.getEndPoint().equals(gate.inputPoint1) || connector.getStartPoint().equals(gate.inputPoint1))){
                    Toast.makeText(context, "You can connect the input with only one connection", Toast.LENGTH_SHORT).show();
                    selectedPoint1 = null;
                    return false;
                }
                if((gate.inputPoint1.equals(selectedPoint2)) && (connector.getEndPoint().equals(gate.inputPoint1) || connector.getStartPoint().equals(gate.inputPoint1))){
                    Toast.makeText(context, "You can connect the input with only one connection", Toast.LENGTH_SHORT).show();
                    selectedPoint2 = null;
                    return false;
                }if((gate.inputPoint2.equals(selectedPoint1)) && (connector.getEndPoint().equals(gate.inputPoint2) || connector.getStartPoint().equals(gate.inputPoint2))){
                    Toast.makeText(context, "You can connect the input with only one connection", Toast.LENGTH_SHORT).show();
                    selectedPoint1 = null;
                    return false;
                }
                if((gate.inputPoint2.equals(selectedPoint2)) && (connector.getEndPoint().equals(gate.inputPoint2) || connector.getStartPoint().equals(gate.inputPoint2))){
                    Toast.makeText(context, "You can connect the input with only one connection", Toast.LENGTH_SHORT).show();
                    selectedPoint2 = null;
                    return false;
                }if((gate.outputPoint.equals(selectedPoint1)) && (connector.getEndPoint().equals(gate.outputPoint) || connector.getStartPoint().equals(gate.outputPoint))){
                    Toast.makeText(context, "You can connect the output with only one connection", Toast.LENGTH_SHORT).show();
                    selectedPoint1 = null;
                    return false;
                }
                if((gate.outputPoint.equals(selectedPoint2)) && (connector.getEndPoint().equals(gate.outputPoint) || connector.getStartPoint().equals(gate.outputPoint))){
                    Toast.makeText(context, "You can connect the output with only one connection", Toast.LENGTH_SHORT).show();
                    selectedPoint2 = null;
                    return false;
                }
            }
        }

        for (InputGate inputGate : inputGates){
            for(InputGate inputGate1 : inputGates){
                if ((inputGate.outputPoint.equals(selectedPoint1) && inputGate1.outputPoint.equals(selectedPoint2)) || (inputGate.outputPoint.equals(selectedPoint2) && inputGate1.outputPoint.equals(selectedPoint1))){
                    Toast.makeText(context, "You can connect two inputs", Toast.LENGTH_SHORT).show();
                    selectedPoint2 = null;
                    return false;
                }
            }
        }

        return true;
    }

    private void resetValues() {
        for(Connector connector : connectors){
            connector.setCarriedValue(0);
        }
        for(Gate gate : gates){
                gate.setInputA(0);
                gate.setInputB(0);
                gate.setOutput(0);
        }
        for (OutputGate outputGate : outputGates){
                outputGate.setOutput(0);
        }
    }

    private Point getSelectedPoint(int x, int y) {
        // Iterate over your list of points and check if the touch event is inside the bounds of any point
        for (InputGate inputGate : inputGates) {
            if (isInsidePointBounds(inputGate.outputPoint, x, y)) {
                return inputGate.outputPoint;
            }
        }

        for (Gate gate : gates) {
            if (isInsidePointBounds(gate.inputPoint1, x, y)) {
                return gate.inputPoint1;
            }
            if (isInsidePointBounds(gate.inputPoint2, x, y)) {
                return gate.inputPoint2;
            }
            if (isInsidePointBounds(gate.outputPoint, x, y)) {
                return gate.outputPoint;
            }
        }

        for (OutputGate outputGate : outputGates) {
            if (isInsidePointBounds(outputGate.outputPoint, x, y)) {
                return outputGate.outputPoint;
            }
        }

        return null; // No point selected
    }

    private boolean isInsidePointBounds(Point point, int x, int y) {
        // Check if the touch event is inside the bounds of the point
        int pointRadius = 60; // Adjust the radius as needed
        return x >= point.x - pointRadius && x <= point.x + pointRadius &&
                y >= point.y - pointRadius && y <= point.y + pointRadius;
    }

    private void togglePointSelection(Point point) {
        if (selectedPoint1 == point) {
            selectedPoint1 = null; // Deselect point
        } else if (selectedPoint2 == point) {
            selectedPoint2 = null; // Deselect point
        } else if (selectedPoint1 == null) {
            selectedPoint1 = point; // Select point
        } else if (selectedPoint2 == null) {
            selectedPoint2 = point; // Select point
        }
    }

    public void deleteSelectedGate() {
    }

    public String returnCircuitRaw() {
        StringBuilder circuitRaw = new StringBuilder();
        int type = 0;
        for (Gate gate : gates) {
            if (gate instanceof ANDGate)
                type = 1;
            else if (gate instanceof ORGate)
                type = 2;
            else if (gate instanceof  NOTGate)
                type = 3;
            circuitRaw.append(type + "=");
            circuitRaw.append(gate.inputPoint1.x + "_" + gate.inputPoint1.y + "-");
            circuitRaw.append(gate.inputPoint2.x + "_" + gate.inputPoint2.y + "-");
            circuitRaw.append(gate.outputPoint.x + "_" + gate.outputPoint.y + "-");
            circuitRaw.append(gate.bodyPoint.x + "_" + gate.bodyPoint.y + "/");
        }
        type = 4;
        for (InputGate inputGate : inputGates){
            circuitRaw.append(type + "=");
            circuitRaw.append(inputGate.inputPoint1.x + "_" + inputGate.inputPoint1.y + "-");
            circuitRaw.append("0_0" + "-");
            circuitRaw.append(inputGate.outputPoint.x + "_" + inputGate.outputPoint.y + "-");
            circuitRaw.append(inputGate.bodyPoint.x + "_" + inputGate.bodyPoint.y + "/");
        }
        type = 5;
        for (OutputGate outputGate : outputGates){
            circuitRaw.append(type + "=");
            circuitRaw.append(outputGate.inputPoint1.x + "_" + outputGate.inputPoint1.y + "-");
            circuitRaw.append(outputGate.inputPoint2.x + "_" + outputGate.inputPoint2.y + "-");
            circuitRaw.append(outputGate.outputPoint.x + "_" + outputGate.outputPoint.y + "-");
            circuitRaw.append(outputGate.bodyPoint.x + "_" + outputGate.bodyPoint.y + "/");
        }
        circuitRaw.append("|");
        for (Connector connector : connectors) {
            circuitRaw.append(connector.getStartPoint().x + "_" + connector.getStartPoint().y + "-");
            circuitRaw.append(connector.getEndPoint().x + "_" + connector.getEndPoint().y + "/");
        }
        circuitRaw.append("#");

        System.out.println(circuitRaw);
        String circuit = circuitRaw.toString();
        return circuit;
    }

    public void buildLoadedCircuit(String rawCircuit) {
        /*
           1,2,3,4 - Gate type
           = gate start
           _ space x and y of a point
           - space the points
           / finished the object
           | gates done, starting with connectors
           # circuit done
        */

        gates = new ArrayList<>();
        connectors = new ArrayList<>();
        inputGates = new ArrayList<>();
        outputGates = new ArrayList<>();

        connectors = new ArrayList<>();

        ArrayList type = new ArrayList<Integer>();
        String[] rawCircuitByObjects = rawCircuit.split("/");
        List<Point> points = new ArrayList<>();
        for (String rawObject : rawCircuitByObjects) {
            if (rawObject.valueOf(rawObject.charAt(0)).equalsIgnoreCase("|"))
                break;
            else {
                type.add(Integer.parseInt(String.valueOf(rawObject.charAt(0))));
                rawObject = rawObject.substring(2);
                String[] rawCircuitByPoint = rawObject.split("-");
                for (String rawCoordinates : rawCircuitByPoint) {
                    String[] coordinates = rawCoordinates.split("_");
                    points.add(new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
                }
            }
        }

        int pointSpot = 0;
        for (int i = 0; i < type.size(); i++) {
            switch (Integer.parseInt(String.valueOf(type.get(i)))) {
                case 1:
                    Gate andGate = new ANDGate((Point) points.get(pointSpot), (Point) points.get(pointSpot + 1), (Point) points.get(pointSpot + 2), (Point) points.get(pointSpot + 3), context);
                    System.out.println(points.get(pointSpot));
                    System.out.println(points.get(pointSpot+1));
                    System.out.println(points.get(pointSpot+2));
                    System.out.println(points.get(pointSpot+3));
                    System.out.println(andGate.inputPoint1);
                    System.out.println(andGate.inputPoint2);
                    System.out.println(andGate.outputPoint);
                    System.out.println(andGate.bodyPoint);
                    gates.add(andGate);
                    pointSpot += 4;
                    break;
                case 2:
                    Gate orGate = new ORGate((Point) points.get(pointSpot), (Point) points.get(pointSpot + 1), (Point) points.get(pointSpot + 2), (Point) points.get(pointSpot + 3), context);
                    gates.add(orGate);
                    pointSpot += 4;
                    break;
                case 3:
                    Gate notGate = new NOTGate((Point) points.get(pointSpot), (Point) points.get(pointSpot + 1), (Point) points.get(pointSpot + 2), (Point) points.get(pointSpot + 3), context);
                    gates.add(notGate);
                    pointSpot += 4;
                    break;
                case 4:
                    InputGate inputGate = new InputGate((Point) points.get(pointSpot), (Point) points.get(pointSpot + 1), (Point) points.get(pointSpot + 2), (Point) points.get(pointSpot + 3), context);
                    inputGates.add(inputGate);
                    pointSpot += 4;
                    break;
                case 5:
                    OutputGate outputGate = new OutputGate((Point) points.get(pointSpot), (Point) points.get(pointSpot + 1), (Point) points.get(pointSpot + 2), (Point) points.get(pointSpot + 3), context);
                    outputGates.add(outputGate);
                    pointSpot += 4;
                    break;
                default:
                    break;
            }
        }

        points = new ArrayList<Point>();
        int index = rawCircuit.indexOf("|");
        rawCircuit = rawCircuit.substring(index + 1);

        rawCircuitByObjects = rawCircuit.split("/");
        for (String rawObject : rawCircuitByObjects) {
            if (rawObject.valueOf(rawObject.charAt(0)).equalsIgnoreCase("#"))
                break;
            else {
                String[] rawPoint = rawObject.split("-");
                for (String rawCoordinates : rawPoint) {
                    String[] coordinates = rawCoordinates.split("_");
                    points.add(new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
                }
            }
        }

        Point[] connectorPoint = new Point[2];
        List pointsFound = new ArrayList<Boolean>();
        pointsFound.add(false);
        pointsFound.add(false);
        int nPointsFound = 0;
        for(Point point : points){
            System.out.println(point);

            for (InputGate inputGate : inputGates){
                if(inputGate.outputPoint.equals(point)){
                    pointsFound.set(nPointsFound, true);
                    connectorPoint[nPointsFound] = inputGate.outputPoint;
                    nPointsFound +=1;
                    if(((Boolean) pointsFound.get(0)) && ((Boolean) pointsFound.get(1))){
                        connectors.add(new Connector(connectorPoint[0], connectorPoint[1]));
                        connectorPoint = new Point[2];
                        pointsFound.set(0, false);
                        pointsFound.set(1, false);
                        nPointsFound = 0;
                        break;
                    }
                }
            }

            for (Gate gate : gates){
                if(gate.inputPoint1.equals(point)){
                    pointsFound.set(nPointsFound, true);
                    connectorPoint[nPointsFound] = gate.inputPoint1;
                    nPointsFound +=1;
                    if(((Boolean) pointsFound.get(0)) && ((Boolean) pointsFound.get(1))){
                        connectors.add(new Connector(connectorPoint[0], connectorPoint[1]));
                        connectorPoint = new Point[2];
                        pointsFound.set(0, false);
                        pointsFound.set(1, false);
                        nPointsFound = 0;
                        break;
                    }
                }
                if(gate.inputPoint2.equals(point)){
                    pointsFound.set(nPointsFound, true);
                    connectorPoint[nPointsFound] = gate.inputPoint2;
                    nPointsFound +=1;
                    if(((Boolean) pointsFound.get(0)) && ((Boolean) pointsFound.get(1))){
                        connectors.add(new Connector(connectorPoint[0], connectorPoint[1]));
                        connectorPoint = new Point[2];
                        pointsFound.set(0, false);
                        pointsFound.set(1, false);
                        nPointsFound = 0;
                        break;
                    }
                }
                if(gate.outputPoint.equals(point)){
                    pointsFound.set(nPointsFound, true);
                    connectorPoint[nPointsFound] = gate.outputPoint;
                    nPointsFound +=1;
                    if(((Boolean) pointsFound.get(0)) && ((Boolean) pointsFound.get(1))){
                        connectors.add(new Connector(connectorPoint[0], connectorPoint[1]));
                        connectorPoint = new Point[2];
                        pointsFound.set(0, false);
                        pointsFound.set(1, false);
                        nPointsFound = 0;
                        break;
                    }
                }

            }

            for (OutputGate outputGate : outputGates){
                if(outputGate.outputPoint.equals(point)){
                    pointsFound.set(nPointsFound, true);
                    connectorPoint[nPointsFound] = outputGate.outputPoint;
                    nPointsFound +=1;
                    if(((Boolean) pointsFound.get(0)) && ((Boolean) pointsFound.get(1))){
                        connectors.add(new Connector(connectorPoint[0], connectorPoint[1]));
                        connectorPoint = new Point[2];
                        pointsFound.set(0, false);
                        pointsFound.set(1, false);
                        nPointsFound = 0;
                        break;
                    }
                }
            }

        }
        System.out.println(rawCircuit);
        System.out.println(points);
        resetValues();
        evaluateCircuit();
    }

    public void evaluateCircuit() {
        for (InputGate inputGate : inputGates) {
            for (Connector connector : connectors) {
                if (connector.getStartPoint().equals(inputGate.outputPoint) || connector.getEndPoint().equals(inputGate.outputPoint)) {
                    connector.setCarriedValue(toOption(inputGate.getOutput()));
                }
            }
        }

        for (Gate gate : gates) {
            if (gate instanceof NOTGate) {
                for (Connector connector : connectors) {
                    if (connector.getStartPoint().equals(gate.inputPoint1) || connector.getEndPoint().equals(gate.inputPoint1)) {
                        gate.setInputA(toOption(connector.getCarriedValue()));
                    }
                    if (connector.getStartPoint().equals(gate.outputPoint) || connector.getEndPoint().equals(gate.outputPoint)) {
                        if(gate.checkNullInputs()) {
                            gate.calculateOutput();
                            connector.setCarriedValue(toOption(((NOTGate) gate).getOutput()));
                        }
                    }
                }
            }
            else {
                for (Connector connector : connectors){
                    if (connector.getStartPoint().equals(gate.inputPoint1) || connector.getEndPoint().equals(gate.inputPoint1)){
                        gate.setInputA(toOption(connector.getCarriedValue()));
                    }
                    if (connector.getStartPoint().equals(gate.inputPoint2) || connector.getEndPoint().equals(gate.inputPoint2)){
                        gate.setInputB(toOption(connector.getCarriedValue()));
                    }
                    if (connector.getStartPoint().equals(gate.outputPoint) || connector.getEndPoint().equals(gate.outputPoint)){
                        System.out.println(gate.checkNullInputs());
                        if(gate.checkNullInputs()) {
                            gate.calculateOutput();
                            connector.setCarriedValue(toOption(gate.calculateOutput()));
                        }
                    }
                }
            }
        }

        for (OutputGate outputGate : outputGates) {
            for (Connector connector : connectors) {
                if (connector.getStartPoint().equals(outputGate.outputPoint) || connector.getEndPoint().equals(outputGate.outputPoint)) {
                    outputGate.changeState(connector.getCarriedValue());
                }
            }
        }
    }

    private int toOption(Boolean output) {
        if (output == null)
            return 0;
        else
            if (output.booleanValue() == true)
                return 1;
            else
                return 2;
    }

    public void addGate(Gate gate) {
        gates.add(gate);
        performClick();
    }

    public void addInputGate(InputGate inputGate) {
        inputGates.add(inputGate);
        performClick();
    }

    public void addOutputGate(OutputGate outputGate) {
        outputGates.add(outputGate);
        performClick();
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        System.out.println("rap taptap");
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        deleteGateAt(x,y);
        return false;
    }

    private void deleteGateAt(int x, int y) {
        selectedPoint1 = null;
        selectedPoint2 = null;

        for (Gate gate : gates) {
            if (gate.isInsideBounds(x, y)) {
                Iterator<Connector> iterator = connectors.iterator();
                while (iterator.hasNext()) {
                    Connector connector = iterator.next();
                    if((connector.getStartPoint().equals(gate.inputPoint1)||connector.getEndPoint().equals(gate.inputPoint1)) || (connector.getStartPoint().equals(gate.inputPoint2)||connector.getEndPoint().equals(gate.inputPoint2)) || (connector.getStartPoint().equals(gate.outputPoint)||connector.getEndPoint().equals(gate.outputPoint))){
                        iterator.remove();
                    }
                }
                gates.remove(gate);
                resetValues();
                evaluateCircuit();
                return;
            }
        }

        for (InputGate inputGate : inputGates) {
            if(inputGate.isInsideBounds(x,y)){
                Iterator<Connector> iterator = connectors.iterator();
                while (iterator.hasNext()) {
                    Connector connector = iterator.next();
                    if((connector.getStartPoint().equals(inputGate.outputPoint)||connector.getEndPoint().equals(inputGate.outputPoint))){
                        iterator.remove();
                    }
                }
                inputGates.remove(inputGate);
                resetValues();
                evaluateCircuit();
                return;
            }
        }

        for (OutputGate outputGate : outputGates) {
            if(outputGate.isInsideBounds(x,y)){
                Iterator<Connector> iterator = connectors.iterator();
                while (iterator.hasNext()) {
                    Connector connector = iterator.next();
                    if((connector.getStartPoint().equals(outputGate.outputPoint)||connector.getEndPoint().equals(outputGate.outputPoint))){
                        iterator.remove();
                    }
                }
                outputGates.remove(outputGate);
                resetValues();
                evaluateCircuit();
                return;
            }
        }
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}