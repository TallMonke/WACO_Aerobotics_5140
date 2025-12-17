package org.firstinspires.ftc.teamcode.mechanisms;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class BallColorSensor {
    private NormalizedColorSensor colorSensor = null;

    public enum DetectedColor {
        GREEN(Color.parseColor("#00FF00")),
        PURPLE(Color.parseColor("#800080")),
        BLACK(Color.parseColor("#000000")),
        UNKNOWN(Color.parseColor("#FFFFFF"));

        private int color = Color.parseColor("#FFFFFF");

        DetectedColor(int colorHexValue){
            color = colorHexValue;
        }

        int getColor(){ return color; }
    }

    Telemetry tm = null;

    public BallColorSensor(HardwareMap hardwareMap, Telemetry telemetry){
        if(telemetry == null) {
            return;
        }

        if(hardwareMap == null) {
            telemetry.addData("Error", "Hardware map is null");
            return;
        }

        tm = telemetry;
        colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorSensor");
        colorSensor.setGain(0.5f);
    }

    public DetectedColor getColor(){
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        float normRed,
                normGreen,
                normBlue;

        normRed = colors.red / colors.alpha;
        normGreen = colors.green / colors.alpha;
        normBlue = colors.blue / colors.alpha;

        tm.addData("Normalized Colors: ", "%d, %d, %d",
                normRed,
                normGreen,
                normBlue);
        tm.addData("Normalized Color (Hex): ", colors.toString());

        // TODO: Base the color detection on a threshold value (%) around the color
        if(colors.toColor() == DetectedColor.GREEN.getColor()) {
            tm.addData("Color Detected", "Green" );
            return DetectedColor.GREEN;
        }
        else if(colors.toColor() == DetectedColor.PURPLE.getColor()) {
            tm.addData("Color Detected", "Purple" );
            return DetectedColor.PURPLE;
        }
        else if(colors.toColor() == DetectedColor.BLACK.getColor()) {
            tm.addData("Color Detected", "BLACK" );
            return DetectedColor.BLACK;
        }

        return DetectedColor.UNKNOWN;
    }
}
