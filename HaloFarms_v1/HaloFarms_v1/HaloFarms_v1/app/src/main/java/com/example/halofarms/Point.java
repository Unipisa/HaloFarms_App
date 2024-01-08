package com.example.halofarms;
import java.util.ArrayList;

/**
 * Class representing point to analyze inside Field with all its attributes
 */
public class Point {
    // global unique id for point in json format
    private String jsonPoint;
    // flag that indicates if the point is to analyze
    private boolean analyze;
    // attributes of point
    private float ec, sar, ph, cec;
    // local id of point: 0, 1, 2, 3..
    private int zoneId;
    // corners of square containing point
    private String square;
    // coordinates of point
    private String suggestedPoint;
    // for every attribute there is an heat map with 4 colors
    private ArrayList<Integer> ecColors, sarColors, phColors, cecColors;
    public Point() {
    }
    public Point(String square, int zoneId, String suggestedPoint) {
        this.analyze = false;
        this.square = square;
        this.zoneId = zoneId;
        this.suggestedPoint = suggestedPoint;
        this.ecColors = new ArrayList<>();
        this.sarColors = new ArrayList<>();
        this.phColors = new ArrayList<>();
        this.cecColors = new ArrayList<>();
    }

    public String getJsonPoint() {
        return jsonPoint;
    }

    public void setJsonPoint(String jsonPoint) {
        this.jsonPoint = jsonPoint;
    }

    public boolean isAnalyze() {
        return analyze;
    }

    public void setAnalyze(boolean analyze) {
        this.analyze = analyze;
    }

    public float getEc() {
        return ec;
    }

    public void setEc(float ec) {
        this.ec = ec;
    }

    public float getSar() {
        return sar;
    }

    public void setSar(float sar) {
        this.sar = sar;
    }

    public float getPh() {
        return ph;
    }

    public void setPh(float ph) {
        this.ph = ph;
    }

    public float getCec() {
        return cec;
    }

    public void setCec(float cec) {
        this.cec = cec;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public String getSquare() {
        return square;
    }

    public void setSquare(String square) {
        this.square = square;
    }

    public String getSuggestedPoint() {
        return suggestedPoint;
    }

    public void setSuggestedPoint(String suggestedPoint) {
        this.suggestedPoint = suggestedPoint;
    }

    public ArrayList<Integer> getEcColors() {
        return ecColors;
    }

    public void setEcColors(ArrayList<Integer> ecColors) {
        this.ecColors = ecColors;
    }

    public ArrayList<Integer> getSarColors() {
        return sarColors;
    }

    public void setSarColors(ArrayList<Integer> sarColors) {
        this.sarColors = sarColors;
    }

    public ArrayList<Integer> getPhColors() {
        return phColors;
    }

    public void setPhColors(ArrayList<Integer> phColors) {
        this.phColors = phColors;
    }

    public ArrayList<Integer> getCecColors() {
        return cecColors;
    }

    public void setCecColors(ArrayList<Integer> cecColors) {
        this.cecColors = cecColors;
    }
}
