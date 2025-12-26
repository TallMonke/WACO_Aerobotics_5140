package org.firstinspires.ftc.teamcode.mechanisms;

import android.graphics.Color;
import android.graphics.ColorSpace;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Object to wrap a color sensor and retrieve if the color is PURPLE or GREEN
 */
public class BallColorSensor {
    private RevColorSensorV3 colorSensor = null;

    public enum DetectedColor {
        GREEN,
        PURPLE,
        UNKNOWN
    }

    // Telemetry object for logging in the driver station
    Telemetry tm = null;

    // ControLHub configuration name for the color sensor
    final private String SENSOR_NAME = "colorSensor";


    /**
     * Initializes a the hardware for a RevColorSensorV3
     *
     * @param hardwareMap HardwareMap from the ControlHub
     * @param telemetry Telemetry object from the ControlHub
     */
    public BallColorSensor(HardwareMap hardwareMap, Telemetry telemetry){
        if(telemetry == null) {
            return;
        }

        if(hardwareMap == null) {
            telemetry.addData("Error", "Hardware map is null");
            return;
        }

        tm = telemetry;
        colorSensor = hardwareMap.get(RevColorSensorV3.class, SENSOR_NAME);
        colorSensor.setGain(1.0f);
    }

    /**
     * Current distance reported from the color sensor in centimeters
     *
     * @return detected distance in CM
     */
    public double getDistance(){
        return colorSensor.getDistance(DistanceUnit.CM);
    }

    /**
     * Retrieves the detected color (PURPLE, GREEN) from the sensor, if it is <3.0cm away.
     * UNKNOWN is returned if it is >3.0cm away or color is not known. Uses the normalized
     * hue value for color detection.
     *
     * @return DectectedColor.GREEN, DectectedColor.PURPLE, DectectedColor.UNKNOWN
     */
    public DetectedColor getColor(){
        NormalizedRGBA normColor = colorSensor.getNormalizedColors();

        float[] hsv = {0,0,0};
        Color.colorToHSV( normColor.toColor(), hsv);
        double hue = hsv[0]; // Hue is the most useful for color identification
        double saturation = hsv[1];
        double value = hsv[2];

        tm.addData("Detected Distance: ", getDistance() );
        tm.addData("Detected Hue: ", hue );

        if( getDistance() < 3.0 ) {
            if (hue > 220 && hue < 250) {
                return DetectedColor.PURPLE;
            } else if (hue > 140 && hue < 180) {
                return DetectedColor.GREEN;
            }
        }

        return DetectedColor.UNKNOWN;
    }
}
