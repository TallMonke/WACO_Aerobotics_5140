package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Launcher {
    Telemetry tm;

    //Wheel Gun Variable
    private DcMotor leftWheelDCMotor = null;
    private DcMotor rightWheelDCMotor = null;
    private DcMotorEx leftWheelEncoder = null;
    private DcMotorEx rightWheelEncoder = null;

    public enum WHEEL_POWER {
        NEAR_POWER,
        FAR_POWER,
        NONE
    }

    private double wheelPower = 0.0;

    // Ball Feed servo
    private Servo ballFeedServo = null;
    private final double INIT_FEED_POSITION = 0.6;
    private double feedPosition = INIT_FEED_POSITION;


    public Launcher(HardwareMap hardwareMap, Telemetry telemetry) {
        if(telemetry == null) {
            return;
        }

        if(hardwareMap == null) {
            telemetry.addData("Error", "Hardware map is null");
            return;
        }

        tm = telemetry;

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

        // initialize the ball feeder
        ballFeedServo = hardwareMap.get(Servo.class, "BallFeed");
    }

    /**
     * Sets the power of the shooter wheels to NEAR_POWER or FAR_POWER
     *
     * @param power NEAR_POWER or FAR_POWER or NONE (off)
     */
    public void setWheelPower(WHEEL_POWER power){
        //power for each distance of shot far and near
        final double nearWheelPower = 0.26;
        final double farWheelPower = 0.275;

        if(power==WHEEL_POWER.NEAR_POWER){
            wheelPower = nearWheelPower;
        }
        else if(power == WHEEL_POWER.FAR_POWER)
        {
            wheelPower = farWheelPower;
        }
        else{
            wheelPower = 0.0;
        }
    }

    /**
     * Sets the position of the feed servo to push the ball forward
     */
    public void push()
    {
        feedPosition = 1.0;
    }

    /**
     * Sets the position of the feed servo to the default/rest position
     */
    public void release()
    {
        feedPosition = INIT_FEED_POSITION;
    }

    public void run(){
        leftWheelDCMotor.setPower(wheelPower);
        rightWheelDCMotor.setPower(wheelPower);
        ballFeedServo.setPosition(feedPosition);

        tm.addData("Shooter A=Long / B=Short: ", wheelPower);
        tm.addData("Left Shooter (A=600, B=520): ", leftWheelEncoder.getVelocity());
        tm.addData("Right Shooter (A=600, B=520): ", rightWheelEncoder.getVelocity());
        tm.addData("Ball Feeder (X Button): ", ballFeedServo.getPosition());
    }
}
