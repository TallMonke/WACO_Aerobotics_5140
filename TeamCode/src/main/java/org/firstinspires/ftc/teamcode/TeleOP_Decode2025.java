package org.firstinspires.ftc.teamcode;
import android.provider.CalendarContract;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.BallColorSensor;

@TeleOp(name="TeleOP_Decode2025", group="Linear OpMode")

public class TeleOP_Decode2025 extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftBack = null;
    private DcMotor leftFront = null;
    private DcMotor rightFront = null;
    private DcMotor rightBack = null;

    //Wheel Gun Varable
    private DcMotor leftWheelDCMotor = null;
    private DcMotor rightWheelDCMotor = null;
    private DcMotorEx leftWheelEncoder = null;
    private DcMotorEx rightWheelEncoder = null;

    //power for each distance of shot far and near
    private final double nearWheelPower = 0.27;
    private final double farWheelPower = 0.30;
    private double wheelPower  = 0.0;

    // Sweeper Variables
    private DcMotor sweeperMotor = null;
    private double sweeperPower = 0.0;
    private double sweeperSpeed = 1.0;

    //BallFeed servo
    private Servo ballFeedServo = null;
    private double ballFeedPush = 1.0;
    private double ballFeedRelease = 0.6;

    //revolver
    private DcMotor revolverDrive = null;
    private double revolverDrivePower = 0.5;

    //colorSensor
    private BallColorSensor colorSensor = null;


    @Override
    public void runOpMode() {

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

        //Wheel Gun Variables
        leftWheelDCMotor = hardwareMap.get(DcMotor.class, "par1");
        rightWheelDCMotor = hardwareMap.get(DcMotor.class, "perp");

        //Calling upon shooter motor encoder ports
        leftWheelEncoder = hardwareMap.get(DcMotorEx.class,"rightFront");
        rightWheelEncoder = hardwareMap.get(DcMotorEx.class,"sweep");

        //Resetting then enabling shooter encoders
        leftWheelEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftWheelEncoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightWheelEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightWheelEncoder.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Wheel Gun Motor Direction
        leftWheelDCMotor.setDirection(DcMotor.Direction.REVERSE);
        rightWheelDCMotor.setDirection(DcMotor.Direction.FORWARD);

        // Sweeper component initialization
        sweeperMotor = hardwareMap.get(DcMotor.class, "sweep");
        sweeperMotor.setDirection(DcMotor.Direction.REVERSE);
        ballFeedServo = hardwareMap.get(Servo.class, "BallFeed");

        //revolver DCmotor
        revolverDrive = hardwareMap.get(DcMotor.class, "par0");

        // ColorSensor Init
        colorSensor = new BallColorSensor();
        colorSensor.init(hardwareMap);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double max;
            double SpeedReduction = 0.5;

            if(gamepad1.left_trigger > 0)
            {
                SpeedReduction = 0.25;
            }
            else if(gamepad1.right_trigger > 0)
            {
                SpeedReduction = 1.0;
            }

            //BallFeed servo "x" push ball out
            if(gamepad2.x)
            {
                ballFeedServo.setPosition( ballFeedPush );
            }
            else
            {
                ballFeedServo.setPosition( ballFeedRelease );
            }

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

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

            sweeperMotor.setPower(sweeperPower);

            //Wheel Gun Test
            leftWheelDCMotor.setPower(wheelPower);
            rightWheelDCMotor.setPower(wheelPower);

            // A-Button set far Shooter Power
            if(gamepad2.a){
                wheelPower = farWheelPower;
            }

            // B button sets close shooter power
            if(gamepad2.b){
                wheelPower = nearWheelPower;
            }

            //sweeper run
            if(gamepad2.right_bumper)
            {
                sweeperPower = sweeperSpeed;
            }
            else
            {
                sweeperPower = 0.0;
            }

            //revolver run "up arrow" & "Down Arrow"
            if(gamepad2.dpad_left)
            {
                revolverDrive.setPower( revolverDrivePower );
            }
            else if (gamepad2.dpad_right)
            {
                revolverDrive.setPower( -revolverDrivePower );
            }
            else
            {
                revolverDrive.setPower( 0.0 );
            }

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Shooter A=Long / B=Short: ", wheelPower);
            telemetry.addData("Left Shooter (A=600, B=520): ", leftWheelEncoder.getVelocity());
            telemetry.addData("Right Shooter (A=600, B=520): ", rightWheelEncoder.getVelocity());
            telemetry.addData("Sweeper (Right Bumper): ", sweeperMotor.getPower());
            telemetry.addData("Ball Feeder (X Button): ", ballFeedServo.getPosition());
            telemetry.addData("Sorter (D-Pad Left/Right): ", revolverDrive.getPower());
            telemetry.addData("Front Drive L/R Motor: ", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back L/R Motor: ", "%4.2f, %4.2f", backLeftPower, backRightPower);
            BallColorSensor.DetectedColor color = colorSensor.getColor(telemetry);
            telemetry.update();
        }
    }
}