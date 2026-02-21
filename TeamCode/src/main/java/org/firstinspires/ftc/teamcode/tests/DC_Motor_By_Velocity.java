package org.firstinspires.ftc.teamcode.tests;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.internal.ui.FilledPolygonDrawable;

@TeleOp(name = "DC_Motor_By_Velocity", group = "tests")

/*
    Program only works if DC_Motor is plugged into a port named 'sweeper' && controller is connected as Gamepad.1 (Start + A-Button)
    Right now assuming using the DECODE_2025_3d_printed hardware configuration
 */

public class DC_Motor_By_Velocity extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx sweeperMotor = null;          //Initalization of the motor we are Velocitying
    private double direction = 1.0;
    private boolean active = false;
    private double sweeperVelocity = 800;              //Velocity the motor will spin when you click start. *This is the Variable the program is affecting
    private final double sweeperVelocityStep = 1.0;  //Amount Velocity will increase everytime you hit a or b

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
            if(gamepad1.left_bumper && sweeperVelocity < 30000.0){
                sweeperMotor.setVelocity(sweeperVelocity * direction);
            }

                                                    //if pushing Right Bumper button turns other way
            if(gamepad1.right_bumper && sweeperVelocity > 0.0) {
                sweeperMotor.setVelocity(0.0);
            }

            if(gamepad1.a && sweeperVelocity < 30000.0){
                sweeperVelocity += sweeperVelocityStep;
            }                                       // if a-button pressed, increases Velocity / if b-button pressed decreases Velocity
            if(gamepad1.b && sweeperVelocity > 0.0){
                sweeperVelocity -= sweeperVelocityStep;
            }

            if(gamepad1.y && !active){                        // change direction of motor
                active = true;
                direction = direction * -1.0;
                sleep(250);
                active = false;
            }

            // Print out the elapsed game time, controles, motor port, and motor Velocity.
            telemetry.addData("Left Bumper Press to", "Spin Motor");
            telemetry.addData("Right Bumper Press to", "Stop Motor");
            telemetry.addData("A Button", "Increase Velocity");
            telemetry.addData("B Button", "Decrease Velocity");
            telemetry.addData("Y button", "Change Desired Motor Direction then ~");
            telemetry.addData("Left bumper for direction", direction);
            telemetry.addData("Current Motor Velocity: ", sweeperMotor.getVelocity());
            telemetry.addData("Set Velocity Target", sweeperVelocity);
            telemetry.update();
        }
    }
}