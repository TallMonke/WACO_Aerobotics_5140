package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

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
    private final double PUSH_FEED_POSITION = 1.0;

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
        leftShoot = hardwareMap.get(DcMotorEx.class, "leftShoot");
        rightShoot = hardwareMap.get(DcMotorEx.class, "rightShoot");

        // Direction of individual wheels.
        leftShoot.setDirection(DcMotorEx.Direction.FORWARD);
        rightShoot.setDirection(DcMotorEx.Direction.REVERSE);

        leftShoot.setMotorEnable();
        rightShoot.setMotorEnable();

        // Run using encoders to get the velocity
        leftShoot.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        rightShoot.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        // initialize the ball feeder
        ballFeedServo = hardwareMap.get(Servo.class, "ballFeed");
        ballFeedServo.setPosition(INIT_FEED_POSITION);
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
        ballFeedServo.setPosition(PUSH_FEED_POSITION);
    }

    /**
     * Sets the position of the feed servo to the default/rest position
     */
    public void release() { ballFeedServo.setPosition(INIT_FEED_POSITION); }

    public void displayTelemetry(){
        if(tm != null){
            tm.addData("Shooter A=Long / B=Short: ", 2 * wheelVelocity);
            tm.addData("Left Shooter (A=600, B=520): ", 2 * leftShoot.getVelocity());
            tm.addData("Right Shooter (A=600, B=520): ", 2 * rightShoot.getVelocity());
            tm.addData("Ball Feeder (X Button): ", ballFeedServo.getPosition());
        }
    }

    /**
     * Performs the actions for the Launcher mechanism
     */
    public void run(){
        leftShoot.setVelocity(wheelVelocity);
        rightShoot.setVelocity(wheelVelocity);

        displayTelemetry();
    }
}
