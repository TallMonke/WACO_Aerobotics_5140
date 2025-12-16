package org.firstinspires.ftc.teamcode;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad2;

import android.provider.CalendarContract;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.BallColorSensor;
import org.firstinspires.ftc.teamcode.mechanisms.DriveTrain;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.Revolver;
import org.firstinspires.ftc.teamcode.mechanisms.Sweeper;

@TeleOp(name="TeleOP_Decode2025", group="Linear OpMode")


public class TeleOP_Decode2025 extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();

    private DriveTrain driveTrain = null;
    private Revolver revolver = null;
    private Sweeper sweeper = null;
    private Launcher launcher = null;
    private BallColorSensor colorSensor = null;

    @Override
    public void runOpMode() {

        driveTrain = new DriveTrain();
        driveTrain.init(hardwareMap, telemetry);
        revolver = new Revolver();
        revolver.init(hardwareMap, telemetry);
        sweeper = new Sweeper();
        sweeper.init(hardwareMap, telemetry);
        launcher = new Launcher();
        launcher.init(hardwareMap, telemetry);

        // ColorSensor Init
        colorSensor = new BallColorSensor();
        colorSensor.init(hardwareMap);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            if(gamepad1.left_trigger > 0) {
                driveTrain.setSpeedReduction(0.25);
            }
            else if(gamepad1.right_trigger > 0) {
                driveTrain.setSpeedReduction(1.0);
            }
            else {
                driveTrain.setSpeedReduction(0.5);
            }

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            driveTrain.run(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

            //BallFeed servo "x" push ball out
            if(gamepad2.x)
            {
                revolver.push();
            }
            else
            {
                revolver.release();
            }
            revolver.run();

            sweeper.enable(gamepad2.right_bumper);
            sweeper.run();

            // A-Button set far Shooter Power
            if(gamepad2.a){
                launcher.setWheelPower(Launcher.WHEEL_POWER.FAR_POWER);
            }

            // B button sets close shooter power
            if(gamepad2.b){
                launcher.setWheelPower( Launcher.WHEEL_POWER.NEAR_POWER );
            }

            launcher.run();

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            BallColorSensor.DetectedColor color = colorSensor.getColor(telemetry);
            telemetry.update();
        }
    }
}