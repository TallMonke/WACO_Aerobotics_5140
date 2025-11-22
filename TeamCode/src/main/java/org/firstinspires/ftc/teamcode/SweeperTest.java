package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="Sweep Test", group="Linear OpMode")

public class SweeperTest extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private CRServo sweeperServo = null;
    private double sweeperPower = 0.0;
    private final double sweeperPowerStep = 0.0001;

    @Override
    public void runOpMode() {
        sweeperServo = hardwareMap.get(CRServo.class, "sweeper");

        // Wait for the game to start (driver presses START)
        telemetry.addData("Sweeper Test", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // Pressing Left Bumper button turns on wheel motors
            if(gamepad1.left_bumper){
                sweeperServo.setPower(1.0);
            }

            if(gamepad1.right_bumper){
                sweeperServo.setPower(0.0);
            }

            // Show the elapsed game time and wheel power.
            telemetry.addData("Sweeper Status", "Run Time: " + runtime.toString());
            telemetry.addData("Left Bumper", "Spin Forward");
            telemetry.addData("Right Bumper", "Spin Reverse");
            telemetry.addData("A Button", "Increase Power");
            telemetry.addData("B Button", "Decrease Power");
            telemetry.addData("Sweeper Port: ", sweeperServo.getPortNumber());
            telemetry.addData("Sweeper Power: ", sweeperServo.getPower());
            telemetry.update();
        }
    }
}
