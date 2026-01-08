package org.firstinspires.ftc.teamcode.mechanisms;

public enum DetectedColor {
    GREEN(0),
    PURPLE(1),
    BLUE(2),
    RED(3),
    UNKNOWN(-1);

    private final int color;

    DetectedColor(int color) {
        this.color = color;
    }

    public int getColor() { return this.color; }

    public String toString(){
        switch(this.color){
            case GREEN:
                return "GREEN";
            case PURPLE:
                return "PURPLE";
            case BLUE:
                return "BLUE";
            case RED:
                return "RED";
            default:
                return "UNKNOWN";
        }
    }
}
