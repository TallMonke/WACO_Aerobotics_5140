package org.firstinspires.ftc.teamcode.tests;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Power_A_DC_Motor", group = "tests")

/*
    Program only works if DC_Motor is plugged into a port named 'sweeper' && controller is connected as Gamepad.1 (Start + A-Button)
    Right now assuming using the DECODE_2025_3d_printed hardware configuration
 */

public class Power_A_DC_Motor extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx sweeperMotor = null;          //Initalization of the motor we are powering
    private double sweeperPower = 0.5;              //power the motor will spin when you click start. *This is the Variable the program is affecting
    private final double sweeperPowerStep = 0.0001;  //Amount power will increase everytime you hit a or b

    @Override
    public void runOpMode() {                       //the Motor name in hardware on the controle hub
        sweeperMotor = hardwareMap.get(DcMotorEx.class, "rightBack");

                                                    // Wait for the game to start (driver presses START)
        telemetry.addData("Check if motor is in 'sweeper' named port ", "Initialized :");
        telemetry.addData("Motor Port: ", sweeperMotor.getPortNumber());
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

                                                    //if Pressing Left Bumper button turns motor
            if(gamepad1.left_bumper && sweeperPower < 1.0){
                sweeperMotor.setPower(sweeperPower);
            }
            else{                                   //else turn off motor
                sweeperMotor.setPower(0.0);
            }

                                                    //if pushing Right Bumper button turns other way
            if(gamepad1.right_bumper && sweeperPower > 0.0){
                sweeperMotor.setPower(-sweeperPower);
            }
            else{                                   //else turn off motor
                sweeperMotor.setPower(0.0);
            }


            if(gamepad1.a && sweeperPower < 0.99){
                sweeperPower += sweeperPowerStep;
            }                                       // if a-button pressed, increases power / if b-button pressed decreases power
            if(gamepad1.b && sweeperPower > 0.1){
                sweeperPower -= sweeperPowerStep;
            }

            // Print out the elapsed game time, controles, motor port, and motor power.
            telemetry.addData("Left Bumper Hold", "Spin Forward");
            telemetry.addData("Right Bumper Hold", "Spin Reverse");
            telemetry.addData("A Button", "Increase Power");
            telemetry.addData("B Button", "Decrease Power");
            telemetry.addData("Motor Power: ", sweeperMotor.getPower());
            telemetry.addData("SetPower", sweeperPower);
            telemetry.update();
        }
    }
}
