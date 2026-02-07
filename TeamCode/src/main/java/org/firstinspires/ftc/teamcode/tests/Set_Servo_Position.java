package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
@TeleOp(name = "Set_Servo_Position", group = "tests")

/*
    Program only works if Servo is plugged into a port named 'sweeper' && controller is connected as Gamepad.1 (Start + A-Button)
    Right now assuming using the DECODE_2025_3d_printed hardware configuration
 */

public class Set_Servo_Position extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private Servo posServo = null;                 //Initalization of the Servo we are positioning
    private double servoPosition = 0.0;              //position the Servo will spin to when you click start. Range -1.0 to 1.0 *This is the Variable the program is affecting
    private double minPosition = 0.0;
    private double maxPosition = 1.0;

    private final double servoPositionStep = 0.001;  //Amount position will increase everytime you hit a or b

    @Override
    public void runOpMode() {                       //the Servo name in hardware on the controle hub
        posServo = hardwareMap.get(Servo.class, "ballFeed");

                                                    // Wait for the driver to press START
        telemetry.addData("Check if Servo is in 'sweeper' named port ", "Initialized :");
        telemetry.addData("Servo Port: ", posServo.getPortNumber());
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {


            if(gamepad1.left_bumper){                   //if Pressing Left Bumper button turns Servo
                posServo.setPosition(servoPosition);
            }

            if(gamepad1.a && servoPosition < 1.0){
                servoPosition += servoPositionStep;
                posServo.setPosition(servoPosition);
            }                                           // if a-button pressed, increases position / if b-button pressed decreases position
            if(gamepad1.b && servoPosition > 0.0){
                servoPosition -= servoPositionStep;
                posServo.setPosition(servoPosition);
            }
            if(gamepad1.x){
                servoPosition = minPosition;
                posServo.setPosition(minPosition);
            }
            if(gamepad1.y){
                servoPosition = maxPosition;
                posServo.setPosition(maxPosition);
            }

            // Print out the elapsed game time, controles, Servo port, and Servo position.
            telemetry.addData("Servo Controlled for", "Run Time: " + runtime.toString());
            telemetry.addData("Left Bumper", "Spin to position");
            telemetry.addData("A Button", "Increase target position");
            telemetry.addData("B Button", "Decrease target position");
            telemetry.addData("X Button", "Max position");
            telemetry.addData("Y Button", "Min position");
            telemetry.addData("Servo position: ", posServo.getPosition());
            telemetry.addData("Target Position", servoPosition);
            telemetry.update();
        }
    }
}
