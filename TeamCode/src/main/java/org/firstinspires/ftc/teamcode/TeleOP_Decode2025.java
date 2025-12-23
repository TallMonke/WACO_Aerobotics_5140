package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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
        // Initialize the drive base
        driveTrain = new DriveTrain(hardwareMap, telemetry);
        revolver = new Revolver(hardwareMap, telemetry);
        sweeper = new Sweeper(hardwareMap, telemetry);
        launcher = new Launcher(hardwareMap, telemetry);

        // ColorSensor Init
        colorSensor = new BallColorSensor(hardwareMap, telemetry);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());

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

            //revolver run "up arrow" & "Down Arrow"
            if(gamepad2.dpad_up)
            {
                revolver.setSweepDirection(Revolver.RevolverDirection.FORWARD);
            }
            else if (gamepad2.dpad_down)
            {
                revolver.setSweepDirection(Revolver.RevolverDirection.BACKWARD);
            }
            else
            {
                revolver.setSweepDirection(Revolver.RevolverDirection.STOP);
            }
            revolver.run();

            sweeper.enable(gamepad2.right_bumper);
            sweeper.run();

            // A-Button set far Shooter Power
            if(gamepad2.a){
                launcher.setWheelPower( Launcher.WHEEL_POWER.FAR_POWER );
            }

            // B button sets close shooter power
            if(gamepad2.b){
                launcher.setWheelPower( Launcher.WHEEL_POWER.NEAR_POWER );
            }

            //BallFeed servo "x" push ball out
            if(gamepad2.x)
            {
                launcher.push();
            }
            else
            {
                launcher.release();
            }

            launcher.run();

            // Show the elapsed game time and wheel power.
            BallColorSensor.DetectedColor detectedColor = colorSensor.getColor();
            if( detectedColor == BallColorSensor.DetectedColor.PURPLE ){
                telemetry.addData( "Color: ", "Purple" );
            } else if(detectedColor == BallColorSensor.DetectedColor.GREEN) {
                telemetry.addData( "Color: ", "Green" );
            }

            telemetry.update();
        }
    }
}