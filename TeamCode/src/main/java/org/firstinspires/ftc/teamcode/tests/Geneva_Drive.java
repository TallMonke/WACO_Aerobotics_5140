package org.firstinspires.ftc.teamcode.tests;

import static java.lang.Math.round;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name= "Geneva_Drive", group = "tests")
/*
    Make a Dc Motor move only 1 rotation at the push of a button. It will 0 upon initialization
 */

public class Geneva_Drive extends LinearOpMode {
    private DcMotorEx genevaDrive = null;       //initialization of the motor we are moving
    private int ticksPerRev = 448;              //how many ticks of the motor it takes to go one revolution *motor ticks multiplied by gearbox*
    private double maxMotorPower = 1.0;         //The power the motor runs at.
    private double proportionalGain = 0.0013;    //rate motor will accelerate and decelerate to reach a more accurate revolution. * Overshoot: decrease / Undershoot: increase *
    private int tolerance = 1;                  //how far from exact target pos is acceptable to say "we made it"
    private int startDecel = 165;               //when to start the deceleration *value is a percent of the way though the total distance: start pos - target pos (smaller = closer to start : bigger = closer to target)
    private boolean moving = false;
    private ElapsedTime timer;
    private int targetPosition;
    private int startPosition;

/**
In order to pass in anything other than 1 revolution we need to convert how many revolution:(float) into ticks of the motor:(int)
this number will need to be passed into the while loop for the target Position
 */
    private int revolutions = 1;                                  //How many revolution you want the motor to do. *can be fraction like 1.25 or 4.5* (needs to be float because converting double to int is to hard for the computer. float to int is much easier.
    private boolean isPressed = false;


    public void runOpMode(){
        genevaDrive = hardwareMap.get(DcMotorEx.class, "genevaDrive");
        genevaDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);            //reset the encoder values upon initialization
        genevaDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);                 //runs the motor using the encoder wire
        genevaDrive.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);    //When the motor stops it will use a break so is does not drift

        telemetry.addData("Initialization :","");
        telemetry.addData("Is the motor in the right port and named rightBack","");
        telemetry.addData("looking in Motor Port: ", genevaDrive.getPortNumber());
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            if (gamepad1.left_trigger > 0.85 && !moving) {                      ///Left Trigger - clockwise rotation
                timer = new ElapsedTime();
                startPosition = genevaDrive.getCurrentPosition();
                targetPosition = startPosition + (ticksPerRev * revolutions);                            //Need changed so we can do things like half rev and 2 rev
                telemetry.addData("Left Trigger","Active");
                telemetry.update();
                moving = true;                                                                          //set mode that motor is moving and should calculate how to go to target position
            }

            if (gamepad1.right_trigger > 0.85 && !moving) {                     ///Right Trigger - counterclockwise rotation
                timer = new ElapsedTime();
                startPosition = genevaDrive.getCurrentPosition();
                targetPosition = startPosition - (ticksPerRev * revolutions);                                           //Need changed so we can do things like half rev and 2 rev
                telemetry.addData("Right Trigger","Active");
                telemetry.update();
                moving = true;                                                                          //set mode that motor is moving and should calculate how to go to target position
            }
            MotorToPosition();                                                      //will activate function when: moving = true

            if (!moving){                     //if motor is NOT moving then print off what controls for the program are
                telemetry.addData("Left Trigger to","Clockwise Rotation");
                telemetry.addData("Right Trigger to","Counterclockwise Rotation");
                telemetry.addData("Revolution away from initialisation", getMotorRevs());
                telemetry.addData("A button to ","Increase Revolutions");
                telemetry.addData("B button to ","Decrease Revolutions");
                telemetry.addData("Revolution", revolutions);
                telemetry.update();
            }

            /**
             * Need to add in a and b buttons to cycle through revolutions. 0.5 - infinity increments of 0.5 (a increase : b decrees)
             */
            if (gamepad1.a && revolutions >= 1 && !isPressed){
                revolutions += 1;
                isPressed = true;
                sleep(250);
                isPressed = false;
            }
            if (gamepad1.b && revolutions > 1){
                revolutions -= 1;
                isPressed = true;
                sleep(250);
                isPressed = false;
            }
        }
    }

    private double getMotorRevs() {                                                 //how many revolution the motor is away from when it was initialized
        return genevaDrive.getCurrentPosition() / ticksPerRev;
    }

    private void MotorToPosition() {                                                //Move DC motor to position but add in a deceleration before reaching target
        if (moving) {                                                                       //if a trigger was pushed
            int currentPosition = genevaDrive.getCurrentPosition();                         //the current motor pos
            int targetDistance = targetPosition - currentPosition;                          //how far is left to go till target pos

            int totalDistance = Math.abs(targetPosition - startPosition);
            int distanceTraveled = Math.abs(currentPosition - startPosition);
            int slowZone = totalDistance - startDecel;                           //when to start the deceleration based of the target position *value is a set tick amount back from total distance

            double power;                                                   //set the power to max when current position is less than the slowZone


            if (distanceTraveled < slowZone) {                            // DECELERATION ZONE           *if we are not at Max Power Zone we are in deceleration zone
                power = maxMotorPower * Math.signum(targetDistance);                                       //apply gain to how far we have to go
            }
            else {
                double powerMag = Math.abs(targetDistance) * proportionalGain;
                powerMag = Range.clip(powerMag, 0, maxMotorPower);               //make sure power does not exceed max set power
                power = powerMag * Math.signum(targetDistance);
            }

            if (timer.seconds() > 2.0){
                telemetry.addData("Im Stuck", timer.milliseconds());
                telemetry.update();
                genevaDrive.setPower(power * -1.0);
                sleep(150);
                timer.reset();
            }

            genevaDrive.setPower(power);                                            //set power each part calculates to motor

            if (Math.abs(targetDistance) <= tolerance) {                                              //how far from exact target pos is acceptable to say "we made it"
                genevaDrive.setPower(0);
                moving = false;                                               //set moving back to false so we can do it again
            }

            telemetry.addData("Rotation","Active");                     //telemetry saying we are in this part
            telemetry.addData("Power",genevaDrive.getPower());
            telemetry.update();
        }
    }
}
