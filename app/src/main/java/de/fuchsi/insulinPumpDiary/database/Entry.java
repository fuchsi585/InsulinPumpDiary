package de.fuchsi.insulinPumpDiary.database;

import java.io.Serializable;
import java.util.Arrays;

public class Entry implements Serializable {
    public static final String NEW    = "NEW";
    public static final String UPDATE = "UPDATE";
    public static final String DELETE = "DELETE";
    private static final long serialVersionUID = 1L;

    private long     id;
    private String   name;
    private String   basalRateArrayString;
    private double[] basalRateArray;
    private double   basalRateSum;
    private boolean  active;

    public Entry(long id, String name, String basalRateArrayString, boolean active){
        this.id         = id;
        this.name       = name;
        this.basalRateArrayString = basalRateArrayString;
        this.active   = active;
        basalString2Array();
        calcBasalSum();
    }

    public long getId(){
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getBasalRateArrayString(){
        return basalRateArrayString;
    }
    public void setRateString(String sRate){
        basalRateArrayString = sRate;
        basalString2Array();
    }
    public void setBasalRateArray(double[] rate){
        this.basalRateArray = rate;
        basalRateArrayString = Arrays.toString(basalRateArray);
    }
    public double[] getBasalRateArray(){
        return basalRateArray;
    }
    public boolean getActive (){
        return active;
    }
    public void setActive(boolean active){
        this.active = active;
    }
    public double getBasalRateSum(){
        return basalRateSum;
    }
    /*
     * helper functions
     */
    private void basalString2Array(){
        String[] list = basalRateArrayString.substring(1,basalRateArrayString.length()-1).split(",");
        basalRateArray = new double[list.length];

        for (int i=0; i < list.length; i++){
            basalRateArray[i] = Double.valueOf(list[i]);
        }
    }
    private void calcBasalSum(){
        basalRateSum = 0;
        for (int i=0; i < basalRateArray.length; i++){
            basalRateSum += basalRateArray[i];
        }
        basalRateSum = basalRateSum * 100;
        basalRateSum = Math.round(basalRateSum);
        basalRateSum = basalRateSum / 100;
    }
}
