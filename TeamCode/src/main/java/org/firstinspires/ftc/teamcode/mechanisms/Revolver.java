package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Revolver {
    //revolver
    private ServoImplEx revolverDrive = null;
    private double revolverPosition = 0.0;

    // Step size for the 6 position revolver to hit each spot
    double revolverStep = 0.036;
    Telemetry tm;

    /**
     * Initializes the Revolver sorting mechanism
     *
     * @param hardwareMap Initialized hardware map from the Op Mode
     * @param telemetry Telemetry object from the Op Mode
     */
    public Revolver(HardwareMap hardwareMap, Telemetry telemetry){
        if(telemetry == null) {
            return;
        }

        if(hardwareMap == null) {
            telemetry.addData("Error", "Hardware map is null");
            return;
        }
        tm = telemetry;

        //revolver servo
        revolverDrive = hardwareMap.get(ServoImplEx.class, "revolverServo");
        revolverDrive.setDirection(Servo.Direction.FORWARD);
        revolverDrive.setPosition(1.0);
        revolverPosition = revolverDrive.getPosition();
    }

    /**
     * Sets the direction of the revolver: FORWARD, BACKWARD, STOP
     *
     * @param direction spin direction of the sweeper mechanism
     */
    public void setSweepDirection( double position ){
        revolverPosition = position;
        revolverDrive.setPosition(revolverPosition);
    }

    public void stepUp(){
        revolverPosition += revolverStep;
        revolverDrive.setPosition(revolverPosition);
    }
    public void stepDown(){
        revolverPosition -= revolverStep;
        revolverDrive.setPosition(revolverPosition);
    }

    /**
     * Performs the actions for Revolver sorting mechanism
     */
    public void run(){
        tm.addData("Sorter (D-Up/D-Down): ", revolverDrive.getPosition());
    }
}

