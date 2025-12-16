package org.firstinspires.ftc.teamcode.mechanisms;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Launcher {
    Telemetry tm;

    //Wheel Gun Varable
    private DcMotor leftWheelDCMotor = null;
    private DcMotor rightWheelDCMotor = null;
    private DcMotorEx leftWheelEncoder = null;
    private DcMotorEx rightWheelEncoder = null;

    public enum WHEEL_POWER {
        NEAR_POWER,
        FAR_POWER,
        NONE
    }

    //power for each distance of shot far and near
    private final double nearWheelPower = 0.26;
    private final double farWheelPower = 0.275;
    private double wheelPower = 0.0;

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
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
    }
    public void setWheelPower(WHEEL_POWER power){
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
    public void run(){
        //Wheel Gun Test
        leftWheelDCMotor.setPower(wheelPower);
        rightWheelDCMotor.setPower(wheelPower);

        tm.addData("Shooter A=Long / B=Short: ", wheelPower);
        tm.addData("Left Shooter (A=600, B=520): ", leftWheelEncoder.getVelocity());
        tm.addData("Right Shooter (A=600, B=520): ", rightWheelEncoder.getVelocity());
    }
}
