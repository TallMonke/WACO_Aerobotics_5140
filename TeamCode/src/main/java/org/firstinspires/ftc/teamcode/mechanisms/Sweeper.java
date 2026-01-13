package org.firstinspires.ftc.teamcode.mechanisms;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Object to define and run the sweeper intake mechanism. Direction can be reversed to
 * eject the balls.
 */
public class Sweeper {
    // Sweeper Variables
    private DcMotor sweeperMotor = null;
    private double sweeperSpeed = 1.0;

    Telemetry tm;

    /**
     * Initializes the Sweeper mechanisms for the given hardware
     *
     * @param hardwareMap Hardware object for the Op Mode
     * @param telemetry Telemetry object for logging in the driver station
     */
    public Sweeper(HardwareMap hardwareMap, Telemetry telemetry) {
        if(telemetry == null) {
            return;
        }

        if(hardwareMap == null) {
            telemetry.addData("Error", "Hardware map is null");
            return;
        }

        tm = telemetry;

        // Sweeper component initialization
        sweeperMotor = hardwareMap.get(DcMotor.class, "sweeper");
        sweeperMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    /**
     * Enables the sweeper mechanism
     *
     * @param enable True, sets the power of the sweeper to SWEEP_SPEED_MAX, false 0.0
     */
    public void enable(Boolean enable){
        final double SWEEP_SPEED_MAX = 1.0;

        if(enable){
            sweeperSpeed = SWEEP_SPEED_MAX;
        }
        else {
            sweeperSpeed = 0.0;
        }
    }

    public Action enableAction(){
        return new Action() {
            @Override
            public boolean run(TelemetryPacket packet) {
                enable(true);
                sweeperMotor.setPower(sweeperSpeed);

                return true;
            }
        };
    }

    public Action disableAction(){
        return new Action() {
            @Override
            public boolean run(TelemetryPacket packet) {
                enable(false);
                sweeperMotor.setPower(sweeperSpeed);

                return false;
            }
        };
    }

    /**
     * Sets the direction of the sweeper intake. Forward (true) will eject the balls, reverse (false)
     * will intake the balls
     *
     * @param direction True for forward, false for reverse
     */
    public void reverse(Boolean direction){
        if(direction){
            sweeperMotor.setDirection(DcMotor.Direction.FORWARD);
        }
        else {
            sweeperMotor.setDirection(DcMotor.Direction.REVERSE);
        }
    }

    /**
     * Performs the sweeper movement using the given hardware
     */
    public void run(){
        sweeperMotor.setPower(sweeperSpeed);

        tm.addData("Sweeper (Right_Bumper=In Both Bumper=Reverse): ", sweeperMotor.getPower());
    }
}
