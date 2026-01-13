package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.MecanumDrive;

/**
 * Object to define and run the drive motors for a mechanum wheeled robot base.
 */
public class DriveTrain {
    IMU imu = null;

    /**
     * Motor objects for the drive train
     */
    private DcMotor leftBack = null;
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor rightBack = null;

    /**
     * Telemetry object for logging in the driver station
     */
    Telemetry tm;

    /**
     * The amount to reduce the speed by. 0.0-1.0
     */
    double speedReduction = 0.5;

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

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
                        RevHubOrientationOnRobot.UsbFacingDirection.UP
                )
        ));

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        leftFront = hardwareMap.get(DcMotor.class, "perp");
        leftBack = hardwareMap.get(DcMotor.class, "par0");
        rightFront = hardwareMap.get(DcMotor.class, "par1");
        rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        // set wheel motor direction.
        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);
    }

    /**
     * Sets the speed to reduce the speed by. 0.0-1.0
     * @param speed Velocity to set the wheels to
     */
    public void setSpeedReduction(double speed){
        speedReduction = speed;
    }

    public void displayTelemetry(){
        if(leftFront != null || leftBack != null || rightFront != null || rightBack != null) {
            tm.addData("Front L/R: ", "%4.2f, %4.2f", leftFront.getPower(), rightFront.getPower());
            tm.addData("Back L/R: ", "%4.2f, %4.2f", leftBack.getPower(), rightBack.getPower());
        }
    }

    /**
     * Normalizes the axial, lateral and yaw directions and applies them to the wheels
     * to move the robot.
     *
     * @param axial Forward and backward movement
     * @param lateral Left/Right movement
     * @param yaw Turning movement
     */
    public void run(double axial, double lateral, double yaw){
        double max = 0.0;

        // Combine the joystick requests for each axis-motion to determine each wheel's power.
        // Set up a variable for each drive wheel to save the power level for telemetry.
        // Multiply by speed reduction variable
        double frontLeftPower  = (axial + lateral + yaw) * speedReduction;
        double frontRightPower = (axial - lateral - yaw) * speedReduction;
        double backLeftPower   = (axial - lateral + yaw) * speedReduction;
        double backRightPower  = (axial + lateral - yaw) * speedReduction;

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
    }

    /**
     * Rotates the robot by the given degrees.
     *
     * @param degrees Amount of degrees to rotate the robot
     */
    public void rotate(double degrees){
        imu.resetYaw();

        // Example for FTC/FRC environment using a simple PID logic
        double currentAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES); // Get current heading from IMU
        double error = currentAngle - degrees;

        // Keep error within -180 to 180 range
        while (error > 180) error -= 360;
        while (error <= -180) error += 360;

        double kP = 0.07; // Proportional constant (tune for your robot)

        while (Math.abs(error) > 1.5) { // Threshold of 1 degree
            tm.addData("ROTATE", error);
            tm.update();

            double power = error * kP;

            // Mecanum rotation: Left side same, Right side opposite
            leftFront.setPower(power);
            leftBack.setPower(power);
            rightFront.setPower(-power);
            rightBack.setPower(-power);

            currentAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            error = currentAngle - degrees;

            // Re-normalize error
            while (error > 180) error -= 360;
            while (error <= -180) error += 360;
        }

        stopDrive();
    }

    /**
     * Stops the motors by setting power to 0.0
     */
    public void stopDrive(){
        leftFront.setPower(0.0);
        rightFront.setPower(0.0);
        leftBack.setPower(0.0);
        rightBack.setPower(0.0);
    }
}
