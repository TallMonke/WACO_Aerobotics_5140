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

    public static final double NEW_P = 2.5;
    public static final double NEW_I = 0.1;
    public static final double NEW_D = 0.2;
    public static final double NEW_F = 0.5;

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx leftWheelDCMotor = null;
    private DcMotorEx rightWheelDCMotor = null;
    private double wheelVelocity  = 0.0;
    private final double wheelVelocityStep = 10.0;

    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        // drive motors

        leftWheelDCMotor = hardwareMap.get(DcMotorEx.class, "driveFL");
        rightWheelDCMotor = hardwareMap.get(DcMotorEx.class, "driveFR");

        // Direction of individual wheels.
        leftWheelDCMotor.setDirection(DcMotor.Direction.REVERSE);
        rightWheelDCMotor.setDirection(DcMotor.Direction.FORWARD);

        leftWheelDCMotor.setMotorEnable();
        rightWheelDCMotor.setMotorEnable();

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // Change coefficients using methods included with DcMotorEx class.
        PIDFCoefficients pidfNew = new PIDFCoefficients(NEW_P, NEW_I, NEW_D, NEW_F);
        leftWheelDCMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfNew);
        rightWheelDCMotor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfNew);

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // Pressing Left Bumper button turns on wheel motors
            if(gamepad1.left_bumper){
                leftWheelDCMotor.setVelocity(wheelVelocity);
                rightWheelDCMotor.setVelocity(wheelVelocity);
            }
            else{
                leftWheelDCMotor.setVelocity(0);
                rightWheelDCMotor.setVelocity(0);
            }

            // open hand at push of A-button
            if(gamepad1.a && wheelVelocity <= 1.0){
                wheelVelocity += wheelVelocityStep;
            }

            //close hand at push of A-button
            if(gamepad1.b && wheelVelocity >= 0.0){
                wheelVelocity -= wheelVelocityStep;
            }

            // Show the elapsed game time and wheel Velocity.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("L Wheel Velocity", leftWheelDCMotor.getVelocity());
            telemetry.addData("R Wheel Velocity", rightWheelDCMotor.getVelocity());
            telemetry.addData("Wheel Velocity: ", wheelVelocity );
            telemetry.update();
        }
    }
}
