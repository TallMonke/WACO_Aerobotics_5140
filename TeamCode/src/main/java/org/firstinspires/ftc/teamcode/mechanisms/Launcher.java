package org.firstinspires.ftc.teamcode.mechanisms;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

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
     * RoadRunner Action to start spinning the shooter wheels at setWheelVelocity
     *
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public Action spinUpAction() {
        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if(leftShoot == null || rightShoot == null) {
                    return false;
                }

                leftShoot.setVelocity(wheelVelocity);
                rightShoot.setVelocity(wheelVelocity);

                return true;
            }
        };
    }

    /**
     * Sets the position of the feed servo to push the ball forward
     */
    public void fire()
    {
        ballFeedServo.setPosition(PUSH_FEED_POSITION);
    }

    /**
     * RoadRunner Action to move the feeder arm to the "fire" position
     *
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public Action fireAction() {
        return new Action() {
            ElapsedTime timer = null;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (timer == null) {
                    timer = new ElapsedTime();

                    fire();
                }

                telemetryPacket.addLine("Feed Pushed");

                return timer.seconds() < 1;
            }
        };
    }

    /**
     * Sets the position of the feed servo to the default/rest position
     */
    public void release() { ballFeedServo.setPosition(INIT_FEED_POSITION); }

    /**
     * RoadRunner Action to move the feeder arm to default position
     *
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public Action releaseAction() {
        return new Action() {
            ElapsedTime timer = null;

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                if (timer == null) {
                    timer = new ElapsedTime();

                    release();
                }

                telemetryPacket.addLine("Feed Released");

                return timer.seconds() < 1;
            }
        };
    }

    public void displayTelemetry(){
        if(tm != null){
            tm.addData("Shooter A=Long / B=Short: ", wheelVelocity);
            tm.addData("Left Shooter (A=600, B=520): ", leftShoot.getVelocity());
            tm.addData("Right Shooter (A=600, B=520): ", rightShoot.getVelocity());
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

    public Action spinUp(double velocity) {
        wheelVelocity = velocity;

        return new Action() {
            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                leftShoot.setVelocity(wheelVelocity);
                rightShoot.setVelocity(wheelVelocity);

                telemetryPacket.put("launcher_velocity", wheelVelocity);
                displayTelemetry();

                return false;
            }
        };
    }
}
