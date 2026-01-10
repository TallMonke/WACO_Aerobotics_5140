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

        if(this.color == GREEN.ordinal()) {
            return "GREEN";
        }
        else if(this.color == PURPLE.ordinal()) {
            return "PURPLE";
        }
        else if(this.color == BLUE.ordinal()) {
            return "BLUE";
        }
        else if(this.color == RED.ordinal()) {
            return "RED";
        }

        return "UNKNOWN";
    }
}
