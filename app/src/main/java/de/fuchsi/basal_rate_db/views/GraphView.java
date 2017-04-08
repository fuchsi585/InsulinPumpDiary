package de.fuchsi.basal_rate_db.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.fuchsi.basal_rate_db.R;

public class GraphView extends View {
    private static final String LOG_TAG = GraphView.class.getSimpleName();

    //Config
    //private boolean scaleShowLabels = true;
    //private String scaleFontFamily = "'Ubuntu'";
    //private String scaleFontStyle = "normal";
    //private boolean pointDot = true;
    //private int pointStrokeColor = Color.rgb(255, 255, 255);

    /*
     *  Public function  to set data and cursor
     */
    private int      cursorPosition = 0;
    private double[] data;

    public double[] getData(){
        return data;
    }
    public void initializeData(double[] values){
        data = values;
        this.invalidate();
    }
    public void increaseValue(double step){
        data[cursorPosition] = data[cursorPosition] + step;
        invalidate();
    }
    public void decreaseValue(double step){
        data[cursorPosition] =  Math.max(data[cursorPosition] - step,0);
        invalidate();
    }
    public void increaseCursor(){
        cursorPosition = Math.min(cursorPosition + 1, 23);
        if (data[cursorPosition] == 0) data[cursorPosition] = data[cursorPosition-1];
        invalidate();
    }
    public void decreaseCursor(){
        cursorPosition = Math.max(cursorPosition - 1, 0);
        invalidate();
    }

    /*
     * Constructor
     */
    private Paint paint;

    public GraphView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        paint = new Paint();

        readAttributes(context, attrs);
    }
    public GraphView(Context context, AttributeSet attrs){
        super(context, attrs);
        paint = new Paint();

        readAttributes(context, attrs);
    }

    private void readAttributes(Context context, AttributeSet attr){
        TypedArray a = context.obtainStyledAttributes(attr, R.styleable.GraphView);

        scaleFontSize = (int) a.getDimension(R.styleable.GraphView_scaleFontSize, 5);
        stepLineWidth = (int) a.getDimension(R.styleable.GraphView_stepLineWidth, 6);
        scaleLineWidth= (int) a.getDimension(R.styleable.GraphView_scaleScaleWidth, 6);
        cursorEnabled      = a.getBoolean(R.styleable.GraphView_cursorEnabled, true);
        scaleShowGridLines = a.getBoolean(R.styleable.GraphView_scaleShowGridLines, true);

        a.recycle();
    }


    /*
     * onDraw method
     */

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        init();

        drawScale(canvas);
        drawSteps(canvas);
    }
    /*
     * view intialization
     */
    int    scaleHeight;
    double scaleHop;

    void init(){
        calculateDrawingSizes();
        getValueBounds();
        calculateScale();
        scaleHop = Math.floor(scaleHeight / steps);
        calculateXAxisSize();
    }

    /*
     * calculate all x-Axis values
     */
    int      widestXLabel;
    int      scaleFontSize = 30;
    double   xAxisLength;
    double   valueHop;
    double   xAxisPosY;
    double   yAxisPosX;
    String[] xLabels = {"0","","","","","","6",
                        "","","","","","12",
                        "","","","","","18",
                        "","","","","","24"};

    void calculateXAxisSize() {
        int longestText = 1;

        paint.setTextSize(scaleFontSize);

        for (int i=0; i < yLabels.size(); i ++){
            int measureText = (int) paint.measureText(yLabels.get(i),0, yLabels.get(i).length()); //xLabels[i].length();
            longestText = ((measureText > longestText) ? measureText : longestText);
        }
        longestText = longestText + 10;

        xAxisLength = getWidth() - longestText - widestXLabel;
        valueHop = Math.floor(xAxisLength/(xLabels.length - 1));

        yAxisPosX = getWidth() - widestXLabel/2 - xAxisLength;
        xAxisPosY = scaleHeight + scaleFontSize/2;
    }

    int maxSize;
    int rotateLabels;
    int labelHeight;

    void calculateDrawingSizes() {
        maxSize = getHeight();

        //Typeface font = new Typeface("Ubuntu",Typeface.NORMAL);
        //paint.setTypeface(font);
        //hier einstellen der Schriftart
        //paint.setTypeface(Typeface.create("Ubuntu",Typeface.NORMAL));
        paint.setTextSize(scaleFontSize);

        widestXLabel = (int) paint.measureText(xLabels[23],0,xLabels[23].length());

        for (int i=0; i < xLabels.length; i++){
            int textLength = (int) paint.measureText(xLabels[i],0, xLabels[i].length());
            widestXLabel = ((textLength > widestXLabel) ? textLength : widestXLabel);
        }
        if (getWidth()/xLabels.length < widestXLabel){
            rotateLabels = 45;
            if (getWidth()/xLabels.length < Math.cos(rotateLabels)*widestXLabel){
                rotateLabels = 90;
                maxSize = maxSize - widestXLabel;
            }else{
                maxSize = maxSize - (int) Math.sin(rotateLabels) * widestXLabel;
            }
        }else{
            maxSize = maxSize - scaleFontSize;
        }
        maxSize = maxSize - 5;
        labelHeight = scaleFontSize;
        maxSize = maxSize - labelHeight;
        scaleHeight = maxSize;
    }

    /*
     * draw scale
     */
    int     scaleGridLineColor = Color.argb(13,0,0,0);
    int     scaleGridLineWidth = 3;
    int     scaleLineColor     = Color.argb(26,0,0,0);
    int     scaleLineWidth     = 3;
    int     scaleFontColor     = Color.rgb(102,102,102);
    int     cursorColor        = Color.argb(45, 0, 0, 0);
    int     cursorLineWidth    = 2 * 3;
    boolean cursorEnabled;
    boolean scaleShowGridLines;
    DecimalFormat df = new DecimalFormat("0.00");

    void drawScale(Canvas ctx){
        Path path = new Path();
        paint.setStrokeWidth(scaleLineWidth);
        paint.setColor(scaleLineColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        path.moveTo((float) (getWidth() - widestXLabel/2 + 5), (float) xAxisPosY);
        path.lineTo((float) (getWidth() - (widestXLabel/2) - xAxisLength -5),(float) xAxisPosY);
        ctx.drawPath(path, paint);

        if (rotateLabels > 0){
            ctx.save();
            paint.setTextAlign(Paint.Align.RIGHT);
        }else {
            paint.setTextAlign(Paint.Align.CENTER);
        }

        for (int i=0; i < xLabels.length; i++){
            ctx.save();
            paint.setTextSize(scaleFontSize);
            paint.setColor(scaleFontColor);
            paint.setStyle(Paint.Style.FILL);
            if (rotateLabels > 0){
                ctx.translate((float)(yAxisPosX + i*valueHop),(float) (xAxisPosY + scaleFontSize));
                ctx.rotate((float)(-(rotateLabels * Math.PI/180)));
                ctx.drawText(xLabels[i],0,0, paint);
                ctx.restore();
            }else {
                ctx.drawText(xLabels[i], (float) (yAxisPosX + i * valueHop), (float) (xAxisPosY + scaleFontSize + 3), paint);
            }
            path.reset();
            //cursor

            if (cursorEnabled && i == cursorPosition){
                paint.setStrokeWidth(cursorLineWidth);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(cursorColor);
                path.moveTo((float)(yAxisPosX + i * valueHop + valueHop/2), (float)(xAxisPosY+3));
                path.lineTo((float)(yAxisPosX + i * valueHop + valueHop/2), 5);
                ctx.drawPath(path, paint);
                paint.setStyle(Paint.Style.FILL);
                ctx.drawText(String.valueOf(i), (float) (yAxisPosX + i*valueHop), (float) (xAxisPosY + scaleFontSize+3), paint);

                //show Cursor data value
                paint.setStyle(Paint.Style.FILL);
                Double cursorValue = (double)Math.round(data[i] * 100) / 100;
                ctx.drawText(df.format(cursorValue),
                        (float) (yAxisPosX + i*valueHop + valueHop/2),
                        (float) (yPos(i)),
                        paint);
            }

            paint.setStrokeWidth(scaleGridLineWidth);
            paint.setStyle(Paint.Style.STROKE);
            path.moveTo((float)(yAxisPosX + i * valueHop), (float) (xAxisPosY + 3));
            if (scaleShowGridLines && i > 0){
                paint.setColor(scaleLineColor);
                path.lineTo((float) (yAxisPosX + i*valueHop),5);
            }else {
                path.lineTo((float) (yAxisPosX + i*valueHop), (float) (xAxisPosY + 3));
            }
            ctx.drawPath(path, paint);
        }
        paint.setStrokeWidth(scaleLineWidth);
        paint.setColor(scaleLineColor);
        paint.setStyle(Paint.Style.STROKE);
        path.reset();
        path.moveTo((float)(yAxisPosX), (float) (xAxisPosY + 5));
        path.lineTo((float)(yAxisPosX), 5);
        ctx.drawPath(path, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setStrokeWidth(scaleGridLineWidth);
        for (int j = 0; j < steps; j++){
            path.reset();
            paint.setColor(scaleGridLineColor);
            paint.setStyle(Paint.Style.STROKE);
            path.moveTo((float) (yAxisPosX -3), (float) (xAxisPosY - ((j + 1) * scaleHop)));
            path.lineTo((float) (yAxisPosX + xAxisLength +  5), (float) (xAxisPosY - ((j + 1) * scaleHop)));
            ctx.drawPath(path, paint);

            paint.setTextSize(scaleFontSize);
            paint.setColor(scaleFontColor);
            paint.setStyle(Paint.Style.FILL);

            ctx.drawText(yLabels.get(j),
                    (float)(yAxisPosX - 8),
                    (float)(xAxisPosY - ((j + 1) * scaleHop) + paint.getTextSize()/3), paint);
        }
    }
    /*
     * draw line
     */

    int     pointDotStrokeWidth = 2 * 3;
    int     stepLineWidth       = 2 * 3;
    int     stepLineColor       = Color.argb(255, 255, 0, 0);
    int     stepFillColor       = Color.argb(128, 220, 220, 220);
    int     stepPointColor      = Color.argb(255, 220, 220, 220);
    int     stepPointStrokeColor= Color.argb(255, 255, 255, 255);
    boolean datasetFill         = true;
    float   pointDotRadius      = 4 * 3;

    void drawSteps(Canvas ctx) {
        Path path = new Path();
        paint.setColor(stepLineColor);
        paint.setStrokeWidth(stepLineWidth);
        paint.setStyle(Paint.Style.STROKE);

        if (data.length > 0) path.moveTo((float) (yAxisPosX), (float) (xAxisPosY - calculateOffset(data[0])));

        for (int j = 1; j < data.length; j++){
            path.lineTo((float) (xPos(j)), (float) (yPos(j - 1)));
            path.lineTo((float) (xPos(j)), (float) (yPos(j)));
        }
        path.lineTo((float) (xPos(data.length) + 1), (float) yPos(data.length - 1));
        ctx.drawPath(path, paint);

        if (datasetFill){
            path.lineTo((float) (yAxisPosX + valueHop*data.length), (float) xAxisPosY);
            path.lineTo((float) yAxisPosX, (float) xAxisPosY );
            path.close();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(stepFillColor);
            ctx.drawPath(path, paint);
        }

        //dots
        paint.setColor(stepPointColor);
        paint.setStrokeWidth(pointDotStrokeWidth);
        //paint.setAntiAlias(true);
        //paint.setStrokeCap(Paint.Cap.ROUND);
        //paint.setStyle(Paint.Style.STROKE);
        path.reset();


        for (int k=0; k < data.length; k++){
            path.addCircle((float) (yAxisPosX + (valueHop*k) + valueHop/2),
                           (float) (xAxisPosY - calculateOffset(data[k])),
                     pointDotRadius, Path.Direction.CW);
        }
        ctx.drawPath(path, paint);
    }
    /*
     * helper functions
     */

    double maxSteps;
    double minSteps;
    double maxValue;
    double minValue;
    double steps;
    double stepValue;
    double graphMin;

    List<String> yLabels;

    double xPos(int iteration){
        return yAxisPosX + (valueHop * iteration);
    }
    double yPos(int iteration){
        return xAxisPosY - calculateOffset(data[iteration]);
    }

    double calculateOffset(double val) {
        double outerValue = steps * stepValue;
        double adjustedValue = val - graphMin;
        double scalingFactor = CapValue(adjustedValue/outerValue,1,0);

        return (scaleHop*steps)*scalingFactor;
    }

    double CapValue(double valueToCap, double maxValue, double minValue) {
        if (valueToCap > maxValue) return maxValue;
        if (valueToCap < minValue) return minValue;
        return valueToCap;
    }

    void getValueBounds() {
        maxValue = Double.MIN_VALUE;
        minValue = Double.MAX_VALUE;

        for (int i = 0; i < data.length; i++){
            if (data[i] > maxValue) maxValue = data[i];
            if (data[i] < minValue) minValue = data[i];
        }

        maxSteps = Math.floor((scaleHeight / (labelHeight*0.66)));
        minSteps = Math.floor((scaleHeight / labelHeight*0.5));
    }

    void calculateScale() {
        double graphMax,graphRange,numberOfSteps,valueRange,rangeOrderOfMagnitude;
        valueRange = ((maxValue - minValue) < 0) ? 0 : maxValue - minValue;
        rangeOrderOfMagnitude = calculateOrderOfMagnitude(valueRange);

        graphMin = Math.floor(minValue / (1 * Math.pow(10,rangeOrderOfMagnitude))) * Math.pow(10, rangeOrderOfMagnitude);
        graphMin = Math.max(graphMin - 0.02, 0);
        graphMax = Math.ceil(maxValue / (1 * Math.pow(10,rangeOrderOfMagnitude))) * Math.pow(10, rangeOrderOfMagnitude);
        graphMax = graphMax + 0.02;
        graphRange = graphMax - graphMin;
        stepValue = Math.pow(10, rangeOrderOfMagnitude);
        stepValue = stepValue * 100;
        stepValue = Math.round(stepValue);
        stepValue = stepValue * 100;
        numberOfSteps = (Double.isNaN(graphRange)) ? Double.NaN : Math.round(graphRange / stepValue);

        if (data.length > 0){
            while (numberOfSteps < minSteps || numberOfSteps > maxSteps){
                if(numberOfSteps < minSteps){
                    stepValue /= 2;
                    numberOfSteps = Math.round(graphRange/stepValue);
                }else{
                    stepValue *= 2;
                    numberOfSteps = Math.round(graphRange/stepValue);
                }
            }
        }
        populateLabels(numberOfSteps, graphMin, stepValue);

        steps = numberOfSteps;
    }

    void populateLabels(double numberOfSteps, double graphMin, double stepValue) {
        List<String> tempLabel = new ArrayList<>();

        for (int i=1; i < numberOfSteps + 1; i++){
            double labVal = graphMin + stepValue*i;
            tempLabel.add(df.format(labVal));
        }
        yLabels = tempLabel;
    }

    double calculateOrderOfMagnitude(double value) {
        return Math.floor(Math.log(value) / 2.302);
    }
}
