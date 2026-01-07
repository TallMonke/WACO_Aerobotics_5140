package org.firstinspires.ftc.teamcode;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagColors;
import org.firstinspires.ftc.teamcode.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.BallColorSensor;
import org.firstinspires.ftc.teamcode.mechanisms.DriveTrain;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.Revolver;
import org.firstinspires.ftc.teamcode.mechanisms.Sweeper;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.ArrayList;

@TeleOp(name="TeleOP_Decode2025", group="Linear OpMode")
public class TeleOP_Decode2025 extends LinearOpMode {
    private AprilTagColors aprilTagColors = new AprilTagColors();
    private ArrayList<BallColorSensor.DetectedColor> currentObeliskColors = null;

    // Select before match to set which team Red\Blue we use. This ID corresponds to the AprilTag ID
    // we should aim for when shooting
    private Integer teamColorID = aprilTagColors.getRedTeamID();
    private AprilTagWebcam  webcam;

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();

    private DriveTrain driveTrain = null;
    private Revolver revolver = null;
    private Sweeper sweeper = null;
    private Launcher launcher = null;

    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() {
        // TODO: Select which team color we are, use the AprilTagColors to get red/blue team ID values
        webcam = new AprilTagWebcam();
        webcam.init(hardwareMap, telemetry);

        // Initialize the drive base
        driveTrain = new DriveTrain(hardwareMap, telemetry);
        revolver = new Revolver(hardwareMap, telemetry);
        sweeper = new Sweeper(hardwareMap, telemetry);

        // TODO: Set the teamColorID into the launcher for search and aim
        launcher = new Launcher(hardwareMap, telemetry);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        webcam.update();
        ArrayList<Integer> obelisksIDs = aprilTagColors.getObeliskIDs();
        for (AprilTagDetection detection: webcam.getDetectedTags()){
            if(detection != null) {
                if(aprilTagColors.isObeliskID(detection.id)){
                    currentObeliskColors = aprilTagColors.getColor(detection.id);
                    telemetry.addLine(String.format("Obelisk ID: %d", detection.id));
                }
            }
        }

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
            revolver.stepUp(gamepad2.dpad_up);
            revolver.stepDown(gamepad2.dpad_down);

            revolver.run();

            //sweep in with right bumper and reverse with both bumpers at same time.
            sweeper.enable(gamepad2.right_bumper);
            sweeper.reverse(gamepad2.left_bumper);
            sweeper.run();

            /*
            set velocity getRPM passing distance from camera into..
            */
            if(gamepad2.left_trigger > 0.5){
                // detect target AprilTag
                if(aprilTagWebcam != null) {
                    aprilTagWebcam.update();
                    for (AprilTagDetection detection : aprilTagWebcam.getDetectedTags()) {
                        if (detection != null) {
                            aprilTagWebcam.displayTelemetry(detection);
                        }
                    }
                }

                // Steer robot to center AprilTag

                // Calculate RPM from range to April Tag

                // Set the wheel velocity to achieve distance
                launcher.setWheelVelocity(getRPM(x_Distance(detection.ftcPose.range)));

                // Launch ball at that velocity
            }


            //BallFeed servo "x" push ball out
            if(gamepad2.x) {
                launcher.push();
            }
            else {
                launcher.release();
            }

            launcher.run();

            telemetry.update();
        }
    }
}