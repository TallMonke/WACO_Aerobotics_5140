package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Wheel Gun Test", group="Linear OpMode")

public class WheelFireTest extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx leftWheelDCMotor = null;
    private DcMotorEx rightWheelDCMotor = null;
    private double targetVelocity = 0.0;
    private double currentVelocity = 0.0;
    private double velocityStep = 0.1;
    private double maxVelocity = 2160.0;

    @Override
    public void runOpMode() {

        // drive motors
        leftWheelDCMotor = hardwareMap.get(DcMotorEx.class, "driveFL");
        rightWheelDCMotor = hardwareMap.get(DcMotorEx.class, "driveFR");

        // Direction of individual wheels.
        leftWheelDCMotor.setDirection(DcMotorEx.Direction.REVERSE);
        rightWheelDCMotor.setDirection(DcMotorEx.Direction.FORWARD);

        leftWheelDCMotor.setMotorEnable();
        rightWheelDCMotor.setMotorEnable();

        // Run using encoders to get the velocity
        leftWheelDCMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightWheelDCMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // Pressing Left Bumper button turns on wheel motors
            if(gamepad1.left_bumper){
                leftWheelDCMotor.setVelocity(targetVelocity);
                rightWheelDCMotor.setVelocity(targetVelocity);
            }
            else{
                leftWheelDCMotor.setVelocity(0);
                rightWheelDCMotor.setVelocity(0);
            }

            // target velocity gets bigger with A button
            if(gamepad1.a && currentVelocity <= maxVelocity){
                targetVelocity += velocityStep;
            }

            // target velocity get small with B button
            if(gamepad1.b && currentVelocity <= maxVelocity){
                targetVelocity -= velocityStep;
            }

            // Show the elapsed game time and wheel Velocity.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("L Wheel Velocity", leftWheelDCMotor.getVelocity());
            telemetry.addData("R Wheel Velocity", rightWheelDCMotor.getVelocity());
            telemetry.addData("Current Velocity: ", currentVelocity );
            telemetry.addData("Target Velocity: ", targetVelocity );
            telemetry.update();
        }
    }
}
