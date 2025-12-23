package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Launcher {
    Telemetry tm;

    //Wheel Gun Variable
    private DcMotorEx leftWheelDCMotor = null;
    private DcMotorEx rightWheelDCMotor = null;
    private DcMotorEx leftWheelEncoder = null;
    private DcMotorEx rightWheelEncoder = null;

    public enum WHEEL_VELOCITY {
        NEAR_VELOCITY,
        FAR_VELOCITY,
        NONE
    }

    private double wheelVelocity = 0.0;

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
        leftWheelDCMotor = hardwareMap.get(DcMotorEx.class, "par1");
        rightWheelDCMotor = hardwareMap.get(DcMotorEx.class, "perp");

        //Calling upon shooter motor encoder ports
        leftWheelEncoder = hardwareMap.get(DcMotorEx.class,"rightFront");
        rightWheelEncoder = hardwareMap.get(DcMotorEx.class,"sweep");

        //Resetting then enabling shooter encoders
        leftWheelEncoder.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        leftWheelEncoder.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightWheelEncoder.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        rightWheelEncoder.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        //Wheel Gun Motor Direction
        leftWheelDCMotor.setDirection(DcMotorEx.Direction.REVERSE);
        rightWheelDCMotor.setDirection(DcMotorEx.Direction.FORWARD);

        // initialize the ball feeder
        ballFeedServo = hardwareMap.get(Servo.class, "BallFeed");
    }

    /**
     * Sets the VELOCITY of the shooter wheels to NEAR_VELOCITY or FAR_VELOCITY
     *
     * @param velocity NEAR_VELOCITY or FAR_VELOCITY or NONE (off)
     */
    public void setWheelVelocity(WHEEL_VELOCITY velocity){
        //VELOCITY for each distance of shot far and near
        final double nearWheelVelocity = 775.0;
        final double farWheelVelocity = 1000.0;

        if(velocity==WHEEL_VELOCITY.NEAR_VELOCITY){
            wheelVelocity = nearWheelVelocity;
        }
        else if(velocity == WHEEL_VELOCITY.FAR_VELOCITY)
        {
            wheelVelocity = farWheelVelocity;
        }
        else{
            wheelVelocity = 0.0;
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
        leftWheelDCMotor.setVelocity(wheelVelocity);
        rightWheelDCMotor.setVelocity(wheelVelocity);
        ballFeedServo.setPosition(feedPosition);

        tm.addData("Shooter A=Long / B=Short: ", wheelVelocity);
        tm.addData("Left Shooter (A=600, B=520): ", leftWheelEncoder.getVelocity());
        tm.addData("Right Shooter (A=600, B=520): ", rightWheelEncoder.getVelocity());
        tm.addData("Ball Feeder (X Button): ", ballFeedServo.getPosition());
    }
}
