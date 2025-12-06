package org.firstinspires.ftc.teamcode.mechanisms;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.ColorSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class BallColorSensor {
    //private NormalizedColorSensor colorSensor = null;
    ColorSensor colorSensor = null;

    public enum DetectedColor {
        GREEN,
        PURPLE,
        BLACK,
        UNKNOWN
    }
    public void init(HardwareMap hwMap){
        colorSensor = hwMap.get(ColorSensor.class, "colorSensor");
        //colorSensor = hwMap.get(NormalizedColorSensor.class, "colorSensor");
        //colorSensor.setGain(0.5f);
    }

    public DetectedColor getColor(Telemetry telemetry){
        float hsvValues[] = {0f,0f,0f};
/*
        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        float normRed,
                normGreen,
                normBlue;

        normRed = colors.red / colors.alpha;
        normGreen = colors.green / colors.alpha;
        normBlue = colors.blue / colors.alpha;
*/
        Color.RGBToHSV(colorSensor.red()*8, colorSensor.green()*8, colorSensor.blue() * 8, hsvValues );
        telemetry.addData("Revolvor colorSensor: ", "%d, %d, %d",
                colorSensor.red(),
                colorSensor.green(),
                colorSensor.blue());
        telemetry.addData("Revolvor hsvValues: ", hsvValues[0] );

        return DetectedColor.UNKNOWN;
    }
}
