package org.firstinspires.ftc.teamcode.mechanisms;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Revolver {
    //revolver
    private DcMotor revolverDrive = null;
    private double revolverDrivePower = 0.5;
    //BallFeed servo
    private Servo ballFeedServo = null;

    private double ballFeedPush = 1.0;
    private double ballFeedRelease = 0.6;
    private double feedPosition = ballFeedRelease;

    Telemetry tm;

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        tm = telemetry;

        //revolver DCmotor
        revolverDrive = hardwareMap.get(DcMotor.class, "par0");
        ballFeedServo = hardwareMap.get(Servo.class, "BallFeed");
    }

    public void push()
    {
        feedPosition = ballFeedPush;
    }

    public void release()
    {
        feedPosition = ballFeedRelease;
    }
    public void run(){
        ballFeedServo.setPosition(feedPosition);

        //revolver run "up arrow" & "Down Arrow"
//        if(gamepad2.dpad_left)
//        {
//            revolverDrive.setPower( revolverDrivePower );
//        }
//        else if (gamepad2.dpad_right)
//        {
//            revolverDrive.setPower( -revolverDrivePower );
//        }
//        else
//        {
//            revolverDrive.setPower( 0.0 );
//        }

        tm.addData("Ball Feeder (X Button): ", ballFeedServo.getPosition());
        tm.addData("Sorter (D-Pad Left/Right): ", revolverDrive.getPower());
    }
}

