package org.firstinspires.ftc.teamcode.mechanisms;

import android.graphics.Color;
import android.graphics.ColorSpace;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class BallColorSensor {
    private RevColorSensorV3 colorSensor = null;

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
        colorSensor = hardwareMap.get(RevColorSensorV3.class, "colorSensor");
        colorSensor.setGain(0.5f);
    }

    public DetectedColor getColor(){
        NormalizedRGBA color = colorSensor.getNormalizedColors();

        tm.addData("Detected Red: ", color.red );
        tm.addData("Detected Blue: ", color.blue );
        tm.addData("Detected Green: ", color.green );

        // TODO: Base the color detection on a threshold value (%) around the color
        /*
        if(color >= 158.0 && color <= 165.0) {
            tm.addData("Color Detected", "Green" );
            return DetectedColor.GREEN;
        }
        else if(color >= 190.0 && color <= 230.0) {
            tm.addData("Color Detected", "Purple" );
            return DetectedColor.PURPLE;
        }
        else if(color >= 166.0 && color <= 175.0) {
            tm.addData("Color Detected", "BLACK" );
            return DetectedColor.BLACK;
        }
*/
        return DetectedColor.UNKNOWN;
    }
}
