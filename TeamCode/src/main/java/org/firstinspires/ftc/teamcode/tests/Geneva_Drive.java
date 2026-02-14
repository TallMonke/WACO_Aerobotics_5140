package org.firstinspires.ftc.teamcode.tests;

import static java.lang.Math.round;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name= "Geneva_Drive", group = "tests")
/*
    Make a Dc Motor move only 1 rotation at the push of a button. It will 0 upon initilization
 */

public class Geneva_Drive extends LinearOpMode {
    private DcMotorEx genevaDrive = null;       //initilization of the motor we are moving
    private int ticksPerRev = 1120;             //how many ticks of the motor it takes to go one revolution *motor ticks multiplied by gearbox*
    private double MotorRunPower = 1.0;         //The power the motor runs at.
    private float revolutions = 1;             //How many revolution you want the motor to do. *can be fraction like 1.25 or 4.5*

    private int position = Math.round(ticksPerRev * revolutions);
    private int positionCorrection = 3;

    public void runOpMode(){
        genevaDrive = hardwareMap.get(DcMotorEx.class, "rightBack");
        genevaDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);            //reset the encoder values upon initilization
        genevaDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);                 //runs the motor using the encoder wire
        genevaDrive.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);    //When the motor stops it will use a break so is does not drift

        telemetry.addData("Initialization :","");
        telemetry.addData("Is the motor is the right port and named rightBack","");
        telemetry.addData("looking in Motor Port: ", genevaDrive.getPortNumber());
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.left_trigger > 0.85){              // Move motor one revolution clockwise
                runMotorToPosition(position-positionCorrection);           //position is tickes per revolution as an int
                telemetry.addData("Step Up","Active");
            }

            if (gamepad1.right_trigger > 0.85){             // Move motor one revolution counter clockwise
                runMotorToPosition(-position+positionCorrection);            //position is tickes per revolution as an int
                telemetry.addData("Step Down","Active");
            }

            telemetry.addData("Exact Rev", getMotorRevs());
            telemetry.addData("# of Revolutions", getMotorRevs() % 1);
            telemetry.addData("Motor Power", genevaDrive.getPower());
            telemetry.update();
        }

    }

    private double getMotorRevs() {
        return genevaDrive.getCurrentPosition() / (ticksPerRev);
    }

    //Take in a position and move the motor to that position
    private void runMotorToPosition(int position) {
        genevaDrive.setTargetPosition(genevaDrive.getCurrentPosition() + position);     //Sets the Target position to be so far beyond the current position
        genevaDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);                           //Run to that target position
        genevaDrive.setPower(MotorRunPower);                                            //Run using the initilized RunPower
        while (genevaDrive.isBusy()) {                                                  //While the motor is moving output telemetry data.
            telemetry.update();
        }                                                   //It will only move on once it reaches the position
        genevaDrive.setPower(0.0);                          //once at position set motor power to 0
    }

}
