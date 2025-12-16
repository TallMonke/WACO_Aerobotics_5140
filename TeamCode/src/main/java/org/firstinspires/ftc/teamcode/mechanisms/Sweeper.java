package org.firstinspires.ftc.teamcode.mechanisms;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Sweeper {
    // Sweeper Variables
    private DcMotor sweeperMotor = null;
    private double sweeperPower = 0.0;
    private double sweeperSpeed = 1.0;

    Telemetry tm;

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        tm = telemetry;
        // Sweeper component initialization
        sweeperMotor = hardwareMap.get(DcMotor.class, "sweep");
        sweeperMotor.setDirection(DcMotor.Direction.REVERSE);
    }

    public void enable(Boolean enable){
        if(enable){
            sweeperSpeed = 1.0;
        }
        else {
            sweeperSpeed = 0.0;
        }
    }
    public void run(){
        sweeperMotor.setPower(sweeperSpeed);

        tm.addData("Sweeper (Right Bumper): ", sweeperMotor.getPower());
    }
}
