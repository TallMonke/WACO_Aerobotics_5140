package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DriveTrain {
    private DcMotor leftBack = null;
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor rightBack = null;

    Telemetry tm;

    double SpeedReduction = 0.5;

    /**
     * Initializes the drive base mechanisms for the given hardware map.
     * @param hardwareMap Robot hardware to utilize for this mechanism
     * @param telemetry Logging object for this Op Mode
     */
    public DriveTrain(HardwareMap hardwareMap, Telemetry telemetry){
        if(telemetry == null) {
            return;
        }

        if(hardwareMap == null) {
            telemetry.addData("Error", "Hardware map is null");
            return;
        }

        tm = telemetry;

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        // set wheel motor direction.
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);
    }

    public void setSpeedReduction(double speed){
        SpeedReduction = speed;
    }

    public void run(double axial, double lateral, double yaw){
        double max = 0.0;

        // Combine the joystick requests for each axis-motion to determine each wheel's power.
        // Set up a variable for each drive wheel to save the power level for telemetry.
        // Multiply by speed reduction variable
        double frontLeftPower  = (axial + lateral + yaw) * SpeedReduction;
        double frontRightPower = (axial - lateral - yaw) * SpeedReduction;
        double backLeftPower   = (axial - lateral + yaw) * SpeedReduction;
        double backRightPower  = (axial + lateral - yaw) * SpeedReduction;

        // Normalize the values so no wheel power exceeds 100%
        // This ensures that the robot maintains the desired motion.
        max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        max = Math.max(max, Math.abs(backLeftPower));
        max = Math.max(max, Math.abs(backRightPower));

        if (max > 1.0) {
            frontLeftPower  /= max;
            frontRightPower /= max;
            backLeftPower   /= max;
            backRightPower  /= max;
        }

        // Send calculated power to wheels
        leftFront.setPower(frontLeftPower);
        rightFront.setPower(frontRightPower);
        leftBack.setPower(backLeftPower);
        rightBack.setPower(backRightPower);

        tm.addData("Front Drive L/R Motor: ", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
        tm.addData("Back L/R Motor: ", "%4.2f, %4.2f", backLeftPower, backRightPower);
    }

}
