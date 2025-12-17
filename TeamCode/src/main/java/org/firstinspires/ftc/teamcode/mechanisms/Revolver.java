package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Revolver {
    //revolver
    private DcMotor revolverDrive = null;
    private double revolverDrivePower = 0.5;
    public enum RevolverDirection{
        FORWARD,
        BACKWARD,
        STOP
    }
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

        //revolver DCmotor
        revolverDrive = hardwareMap.get(DcMotor.class, "par0");
    }

    /**
     * Sets the direction of the revolver: FORWARD, BACKWARD, STOP
     *
     * @param direction spin direction of the sweeper mechanism
     */
    public void setSweepDirection( RevolverDirection direction ){
        final double REVOLVER_POWER_MAX = 0.5;

        switch(direction){
            case FORWARD:
                revolverDrivePower = REVOLVER_POWER_MAX;
                break;
            case BACKWARD:
                revolverDrivePower = -REVOLVER_POWER_MAX;
                break;
            case STOP:
                revolverDrivePower = 0.0;
                break;
        }
    }

    /**
     * Performs the actions for Revolver sorting mechanism
     */
    public void run(){
        revolverDrive.setPower(revolverDrivePower);

        tm.addData("Sorter (D-Pad Left/Right): ", revolverDrive.getPower());
    }
}

