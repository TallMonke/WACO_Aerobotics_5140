package org.firstinspires.ftc.teamcode.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp(name = "CR_Servo", group = "tests")

/*
    Program only works if Servo is plugged into a port named 'sweeper' && controller is connected as Gamepad.1 (Start + A-Button)
    Right now assuming using the DECODE_2025_3d_printed hardware configuration
 */

public class CR_Servo extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private CRServo posServo = null;              //Initalization of the Servo we are Powering
    private double servoPower = 0.0;              //Power the Servo will spin to when you click start. Range -1.0 to 1.0 *This is the Variable the program is affecting
    private double mimPower = -0.5;
    private double maxPower = 0.5;

    private final double servoPowerStep = 0.001;  //Amount Power will increase everytime you hit a or b

    @Override
    public void runOpMode() {                       //the Servo name in hardware on the controle hub
        posServo = hardwareMap.get(CRServo.class, "CRServo");

                                                    // Wait for the driver to press START
        telemetry.addData("Check if Servo is in 'sweeper' named port ", "Initialized :");
        telemetry.addData("Servo Port: ", posServo.getPortNumber());
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            if(gamepad1.right_bumper) {                  //if Pressing Left Bumper button turns Servo other way
                servoPower = 0.0;
                posServo.setPower(0.0);
            }

            if(gamepad1.a && servoPower < 0.5){
                servoPower += servoPowerStep;
                posServo.setPower(servoPower);
            }                                           // if a-button pressed, increases Power / if b-button pressed decreases Power
            if(gamepad1.b && servoPower > -0.5){
                servoPower -= servoPowerStep;
                posServo.setPower(servoPower);
            }
            if(gamepad1.right_trigger > 0.85){
                servoPower = mimPower;
                posServo.setPower(mimPower);
            }
            if(gamepad1.left_trigger > 0.85){
                servoPower = maxPower;
                posServo.setPower(maxPower);
            }

            // Print out the elapsed game time, controles, Servo port, and Servo Power.
            telemetry.addData("Servo Controlled for", "Run Time: " + runtime.toString());
            telemetry.addData("Right Bumper", "Stop Power");
            telemetry.addData("A Button", "Increase Power");
            telemetry.addData("B Button", "Decrease Power");
            telemetry.addData("Right Trigger", "Set Max Power");
            telemetry.addData("Left Trigger", "Set Min Power");
            telemetry.addData("Servo power: ", posServo.getPower());
            telemetry.addData("Target Power", servoPower);
            telemetry.update();
        }
    }
}
