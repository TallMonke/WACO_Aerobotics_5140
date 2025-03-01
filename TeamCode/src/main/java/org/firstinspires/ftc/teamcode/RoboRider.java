package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="RoboRider", group="Linear OpMode")

public class RoboRider extends LinearOpMode
{

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor driveFR = null;
    private DcMotor driveBR = null;
    private DcMotor driveFL = null;
    private DcMotor driveBL = null;
    private DcMotor hArm = null;
    private DcMotor vArm = null;
    private DcMotor hand = null;

    private Servo brush = null;
    private Servo bucket = null;

    double bucketPos = 0.5;
    double maxSpeed = 1.0;
    double mediumSpeed = 0.5;
    double slowSpeed = 0.1;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        driveFR = hardwareMap.get(DcMotor.class, "driveFR");
        driveBR = hardwareMap.get(DcMotor.class, "driveBR");
        driveFL = hardwareMap.get(DcMotor.class, "driveFL");
        driveBL = hardwareMap.get(DcMotor.class, "driveBL");
        hArm = hardwareMap.get(DcMotor.class, "hArm");
        vArm = hardwareMap.get(DcMotor.class, "vArm");
        hand = hardwareMap.get(DcMotor.class, "hand");

        brush = hardwareMap.get(Servo.class, "brush");
        bucket = hardwareMap.get(Servo.class, "bucket");

        driveBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hand.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        while(driveBL.getCurrentPosition() != 0 &&
                driveFL.getCurrentPosition() != 0 &&
                driveBR.getCurrentPosition() != 0 &&
                driveFR.getCurrentPosition() != 0 &&
                hand.getCurrentPosition() != 0) {
            idle();
        }

        driveBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        driveFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hand.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        driveBR.setDirection(DcMotor.Direction.REVERSE);
        driveFR.setDirection(DcMotor.Direction.REVERSE);

        waitForStart();
        runtime.reset();

        double brushPos = 0.0 ;
        double brushStepSize = 0.01;
        double brushMax = 0.43;
        double brushMin = 0.0;
        while (opModeIsActive()) {
            // Servos
            // Brush

            // 1 close, 0 open

            if (gamepad2.right_trigger > 0 && gamepad2.right_bumper) {
                brush.setPosition(brushMin);
            }
            else if(gamepad2.right_trigger > 0) {

                brushPos -= brushStepSize;

                if (brushPos < brushMin) {
                    brushPos = brushMin;
                }
                brush.setPosition(brushPos);

            }
            else if(gamepad2.right_bumper) {
                brushPos += brushStepSize;

                if (brushPos > brushMax) {
                    brushPos = brushMax;
                }
                brush.setPosition(brushPos);
            }
            else {
                brush.setPosition(brushPos);
            }

            //Bucket
            if (gamepad2.left_bumper && gamepad2.left_trigger > 0) {
                bucketPos = 0.38;
            }
            else if(gamepad2.left_trigger > 0) {
                bucketPos += 0.001;
            }
            else if(gamepad2.left_bumper) {
                bucketPos -= 0.001;
            }

            bucket.setPosition(bucketPos);

            // Motors
            // Horizontal Arm
            if (gamepad2.left_stick_y > 0) {
                hArm.setPower(- 1);
            }
            else if(gamepad2.left_stick_y < 0) {
                hArm.setPower(11);
            }
            else {
                hArm.setPower(0);
            }

            // Vertical Arm
            if (gamepad2.right_stick_y > 0) {
                vArm.setPower(1);
            }
            else if(gamepad2.right_stick_y < 0) {
                vArm.setPower(-1);
            }
            else {
                vArm.setPower(0);
            }

            // Hand
            if (gamepad2.a && gamepad2.b) {
                hand.setPower(0);
            }
            else if(gamepad2.b) {
                /*hand.setTargetPosition(hand.getCurrentPosition()+1);
                hand.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                while (hand.getCurrentPosition() <= hand.getTargetPosition()) {
                    hand.setPower(0.25);
                }*/
                hand.setPower(0.25);

            }
            else if(gamepad2.a) {
                hand.setPower(-0.25);
            }
            else {
                hand.setPower(0);
            }

            //Right Bumper sets speed to 100%, Left Bumper sets speed to 25%. Defult 50%
            double speedAlter;

            if (gamepad1.right_trigger > 0 && gamepad1.left_trigger > 0) {
                speedAlter = mediumSpeed;
            }
            else if (gamepad1.right_trigger > 0) {
                speedAlter = maxSpeed;
            }
            else if (gamepad1.left_trigger > 0) {
                speedAlter = slowSpeed;
            }
            else {
                speedAlter = mediumSpeed;
            }

            // Setup a variable for each drive wheel to save power level for telemetry
            double PowerFL;
            double PowerBL;
            double PowerFR;
            double PowerBR;

            // Choose to drive using either Tank Mode, or POV Mode
            // Comment out the method that's not used.  The default below is POV.

            // POV Mode uses left stick to go forward, backwards, and strafe. Right stick to turn.
            // - This uses basic math to combine motions and is easier to drive straight.

            double  drive = -gamepad1.left_stick_y * speedAlter;
            double  strafe = gamepad1.left_stick_x * speedAlter;
            double turning = gamepad1.right_stick_x * speedAlter;
            /*double turn = 0;

            if (turning == 0) {
                turn = 0;
            }
            else   if (turning < 0) {
                turn = turning;
            }
            else if (turning > 0) {
                turn = 1;
            }*/

            PowerFL   = Range.clip(strafe + drive + turning, -1.0f, 1.0f) ;
            PowerBL   = Range.clip(-strafe + drive + turning,-1.0f, 1.0f) ;
            PowerFR   = Range.clip(drive - strafe - turning, -1.0f, 1.0f) ;
            PowerBR   = Range.clip(drive + strafe - turning, -1.0f, 1.0f) ;

            // Send calculated power to wheels
            driveFL.setPower(PowerFL);
            driveBL.setPower(PowerBL);
            driveFR.setPower(PowerFR);
            driveBR.setPower(PowerBR);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "FL (%.2f), FR (%.2f)", PowerFL, PowerFR);
            telemetry.addData("Motors", "BL (%.2f), BR (%.2f)", PowerBL, PowerBR);
            telemetry.addData("Encoder", "FL (%.2f), FR (%.2f)", driveFL.getPower(), driveFR.getPower());
            telemetry.addData("Encoder", "BL (%.2f), BR (%.2f)", driveBL.getPower(), driveBR.getPower());
            telemetry.addData("Triggers", "RT (%.2f), LT (%.2f)", gamepad1.right_trigger, gamepad1.left_trigger);
            telemetry.addData("Brush", "Power (%.2f)", brush.getPosition());
            telemetry.addData("Bucket", "Position: (%.2f)", bucket.getPosition());
            telemetry.addData("Right Stick x", "Position: (%.2f)", gamepad1.right_stick_x);

            telemetry.update();
        }
    }
}

