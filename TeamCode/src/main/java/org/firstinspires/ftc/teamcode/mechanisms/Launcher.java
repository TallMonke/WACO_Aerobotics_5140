package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {
    // Telemetry object for logging in the driver station
    Telemetry tm;

    // Motors for the launcher wheels
    private DcMotorEx leftShoot = null;
    private DcMotorEx rightShoot = null;

    // Current velocity of the launcher motors
    private double wheelVelocity = 0.0;

    // Servo controlling the push mechanism
    private Servo ballFeedServo = null;
    private final double INIT_FEED_POSITION = 0.6;
    private double feedPosition = INIT_FEED_POSITION;

    /**
     * Initializes the Launcher mechanism for the given hardware
     *
     * @param hardwareMap Hardware object for the Op Mode
     * @param telemetry Logging object to display on Drivers Station
     */
    public Launcher(HardwareMap hardwareMap, Telemetry telemetry) {
        if(telemetry == null) {
            return;
        }

        if(hardwareMap == null) {
            telemetry.addData("Error", "Hardware map is null");
            return;
        }

        tm = telemetry;

        // drive motors
        leftShoot = hardwareMap.get(DcMotorEx.class, "perp");
        rightShoot = hardwareMap.get(DcMotorEx.class, "rightShoot");

        // Direction of individual wheels.
        leftShoot.setDirection(DcMotorEx.Direction.REVERSE);
        rightShoot.setDirection(DcMotorEx.Direction.FORWARD);

        leftShoot.setMotorEnable();
        rightShoot.setMotorEnable();

        // Run using encoders to get the velocity
        leftShoot.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightShoot.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);


        // initialize the ball feeder
        ballFeedServo = hardwareMap.get(Servo.class, "BallFeed");
    }

    /**
     * Sets the VELOCITY of the shooter wheels to the given value
     *
     * @param velocity The RPM value to set the wheel velocity
     */
    public void setWheelVelocity(double velocity){
        wheelVelocity = velocity;
    }

    /**
     * Sets the position of the feed servo to push the ball forward
     */
    public void push()
    {
        feedPosition = 1.0;
        ballFeedServo.setPosition(feedPosition);
    }

    /**
     * Sets the position of the feed servo to the default/rest position
     */
    public void release()
    {
        feedPosition = INIT_FEED_POSITION;
        ballFeedServo.setPosition(feedPosition);
    }

    /**
     * Performs the actions for the Launcher mechanism
     */
    public void run(){
        leftShoot.setVelocity(wheelVelocity);
        rightShoot.setVelocity(wheelVelocity);

        tm.addData("Shooter A=Long / B=Short: ", wheelVelocity);
        tm.addData("Left Shooter (A=600, B=520): ", leftShoot.getVelocity());
        tm.addData("Right Shooter (A=600, B=520): ", rightShoot.getVelocity());
        tm.addData("Ball Feeder (X Button): ", ballFeedServo.getPosition());
    }
}
