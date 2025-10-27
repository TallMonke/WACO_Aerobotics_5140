package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Wheel Gun Test", group="Linear OpMode")

public class WheelFireTest extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftWheelDCMotor = null;
    private DcMotor rightWheelDCMotor = null;
    private double wheelPower  = 1.0;
    private final double wheelPowerStep = 0.0001;

    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        // drive motors
        leftWheelDCMotor = hardwareMap.get(DcMotor.class, "driveFL");
        rightWheelDCMotor = hardwareMap.get(DcMotor.class, "driveFR");

        // Direction of individual wheels.
        leftWheelDCMotor.setDirection(DcMotor.Direction.REVERSE);
        rightWheelDCMotor.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // Pressing Left Bumper button turns on wheel motors
            if(gamepad1.left_bumper){
                leftWheelDCMotor.setPower(wheelPower);
                rightWheelDCMotor.setPower(wheelPower);
            }
            else{
                leftWheelDCMotor.setPower(0);
                rightWheelDCMotor.setPower(0);
            }

            // open hand at push of A-button
            if(gamepad1.a && wheelPower <= 1.0){
                wheelPower += wheelPowerStep;
            }

            //close hand at push of A-button
            if(gamepad1.b && wheelPower >= 0.0){
                wheelPower -= wheelPowerStep;
            }

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Wheel Power: ", wheelPower );
            telemetry.update();
        }
    }
}
