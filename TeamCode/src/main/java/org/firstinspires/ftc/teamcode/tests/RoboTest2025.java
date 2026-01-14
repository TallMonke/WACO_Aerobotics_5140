package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Robo Test 2025", group = "tests")

public class RoboTest2025 extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    // Drive Motors
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor frontLeftDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backRightDrive = null;

    //Arm Motors
    private DcMotor liftLeft = null;
    private DcMotor liftRight = null;
    private DcMotor extend = null;
    private Servo grip = null;

    //hand
    private boolean isHandOpen = false;
    private double handClosed = 1;
    private double handOpen  = 0.39;

    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        // drive motors
        frontLeftDrive = hardwareMap.get(DcMotor.class, "driveFL");
        backLeftDrive = hardwareMap.get(DcMotor.class, "driveBL");
        frontRightDrive = hardwareMap.get(DcMotor.class, "driveFR");
        backRightDrive = hardwareMap.get(DcMotor.class, "driveBR");

        //arm motors
        liftLeft = hardwareMap.get(DcMotor.class, "liftLeft");
        liftRight = hardwareMap.get(DcMotor.class, "liftRight");
        extend = hardwareMap.get(DcMotor.class, "extend");
        grip = hardwareMap.get(Servo.class, "grip");

        // Direction of individual wheels.
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        //Direction of Lift motors.
        liftLeft.setDirection(DcMotor.Direction.FORWARD);
        liftRight.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_x;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_y;
            double yaw     =  -gamepad1.right_stick_x;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double frontLeftPower  = -axial + lateral + yaw;
            double frontRightPower = -axial - lateral - yaw;
            double backLeftPower   = axial - lateral + yaw;
            double backRightPower  = axial + lateral - yaw;


            // left bumper and trigger to controll the liftLeft
            // right bumper and trigger to extend and retract
            float triggerDecend = -gamepad1.left_trigger;
            float triggerExtend = gamepad1.right_trigger;

            // send power to arm
            liftLeft.setPower(triggerDecend);
            liftRight.setPower(triggerDecend);
            extend.setPower(triggerExtend);

            // liftLeft arm up
            if(gamepad1.left_bumper){
                liftLeft.setPower(1.0);
                liftRight.setPower(1.0);
            }
            else{
                liftLeft.setPower(0);
                liftRight.setPower(0);
            }

            // extend the arm out
            if(gamepad1.right_bumper){
                extend.setPower(-1.0);
            }
            else{
                extend.setPower(0);
            }

            // open hand at push of A-button
            if(gamepad1.a && isHandOpen==true){
                grip.setPosition(handOpen);
                telemetry.addData("A-Button", " pushed");
                isHandOpen = false;
            }

            //close hand at push of A-button
            else if(gamepad1.a && isHandOpen==false){
                grip.setPosition(handClosed);
                telemetry.addData("A-Button", " pushed");
                isHandOpen = true;
            }

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
            frontLeftDrive.setPower(frontLeftPower);
            frontRightDrive.setPower(frontRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.update();
        }
    }}
