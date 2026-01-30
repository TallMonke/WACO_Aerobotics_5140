package org.firstinspires.ftc.teamcode.DECODE;

import static org.firstinspires.ftc.teamcode.mechanisms.RotationalMath.getRPM;
import static org.firstinspires.ftc.teamcode.mechanisms.RotationalMath.x_Distance;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagColors;
import org.firstinspires.ftc.teamcode.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.DetectedColor;
import org.firstinspires.ftc.teamcode.mechanisms.DriveTrain;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.Revolver;
import org.firstinspires.ftc.teamcode.mechanisms.Sweeper;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.ArrayList;

@TeleOp(name = "DECODE_2025_BLUE", group = "teleop")
@Disabled // Do not need this class if the Constants.TEAM_COLOR_ID works
public class TeleOP_Decode_Blue extends LinearOpMode {
    FtcDashboard dashboard = FtcDashboard.getInstance();

    private final AprilTagColors aprilTagColors = new AprilTagColors();
    // Obelisk colors hold the color order of the balls to shoot
    private ArrayList<DetectedColor> currentObeliskColors = null;

    // Select before match to set which team Red\Blue we use. This ID corresponds to the AprilTag ID
    // we should aim for when shooting
    private Integer teamColorID = aprilTagColors.getBlueTeamID();
    private AprilTagWebcam  webcam = null;
    private boolean autoFireInit = false;

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();

    private DriveTrain driveTrain = null;
    private Revolver revolver = null;
    private Sweeper sweeper = null;
    private Launcher launcher = null;

    @SuppressLint("DefaultLocale")
    @Override
    public void runOpMode() {
        boolean init = false;

        // Initialize the hardware mechanisms
        // TODO: Select which team color we are, use the AprilTagColors to get red/blue team ID values
        webcam = new AprilTagWebcam(hardwareMap, telemetry);
        driveTrain = new DriveTrain(hardwareMap, telemetry);
        revolver = new Revolver(hardwareMap, telemetry);
        sweeper = new Sweeper(hardwareMap, telemetry);
        launcher = new Launcher(hardwareMap, telemetry);

        // Bases the team color set from autonomous mode
        if (Constants.TEAM_COLOR_ID != -1) {
            teamColorID = Constants.TEAM_COLOR_ID;
        }

        // Attempt pulling obelisk motif from the Autonomous
        if (Constants.OBELISK_ID != -1) {
            if (aprilTagColors.isObeliskID(Constants.OBELISK_ID)) {
                currentObeliskColors = aprilTagColors.getColor(Constants.OBELISK_ID);

                sendTelemetryPacket("obelisk_id", Constants.OBELISK_ID);
                printCurrentObelisk();
            }
        }

        // Wait for the game to start (driver presses START)
        if (teamColorID == aprilTagColors.getRedTeamID()) {
            telemetry.addData("Status", "RED Team Ready!");
        } else if (teamColorID == aprilTagColors.getBlueTeamID()) {
            telemetry.addData("Status", "BLUE Team Ready!");
        }

        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            if( teamColorID == aprilTagColors.getRedTeamID() ) {
                telemetry.addData("Red Team", runtime.toString());
            }
            else if( teamColorID == aprilTagColors.getBlueTeamID() ) {
                telemetry.addData("Blue Team", runtime.toString());
            }

            // Attempt to detect the obelisk
            if (currentObeliskColors == null) {
                currentObeliskColors = webcam.detectObelisk();
            }

            if (currentObeliskColors != null) {
                printCurrentObelisk();
            }

            // Get the launcher spun up
            if (!init) {
                launcher.setWheelVelocity(950.0);
                launcher.run();
            }

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
            if(gamepad1.y) {
                aimBot();
            }

            // Revolver Controls
            // D-Pad Up - step to next position
            // D-Pad Down - Step to previous position
            // A-Button - jump to next load position
            // B-Button - Jump to next firing position
            revolver.stepUp(gamepad2.dpad_up);
            revolver.stepDown(gamepad2.dpad_down);
            revolver.stepToLoad(gamepad2.a);
            revolver.stepToFire(gamepad2.b);

            revolver.run();

            //sweep in with right bumper and reverse with both bumpers at same time.
            sweeper.enable(gamepad2.right_bumper);
            sweeper.reverse(gamepad2.left_bumper);
            sweeper.run();

            // Attempt to auto-aim and fire ball at the team tower
            if(gamepad2.left_trigger > 0.5){ autoFire(); }

            // Just push the ball out the launcher
            if(gamepad2.x) { manualFire(); }

            telemetry.update();
        }
    }

    @NonNull
    private String printCurrentObelisk() {
        String currentColors = "Unknown";

        if( currentObeliskColors == null){
            return "Not Set";
        }

        for (DetectedColor color : currentObeliskColors) {
            if (color == DetectedColor.PURPLE) {
                currentColors.concat("P");
            } else if (color == DetectedColor.GREEN) {
                currentColors.concat("G");
            } else {
                currentColors.concat("U");
            }
        }

        return currentColors;
    }

    /**
     * Attempts to detect the tower distance and fire the ball
     * using the calculated velocity. If the tower cannot be found, shoot at 900RPM
     */
    private void manualFire() {
        if(launcher == null){
            telemetry.addData("Error", "Launcher is null");
            return;
        }

        double rpm = 900.0;

        if(webcam != null) {
            webcam.update();
            AprilTagDetection towerDetection = webcam.getTagByID(teamColorID);

            // Calculate RPM from range to April Tag
            // Set the wheel velocity to achieve distance
            if (towerDetection != null && towerDetection.ftcPose != null) {
                // Calculate the velocity needed to shoot the ball at the correct distance
                rpm = getRPM(x_Distance(towerDetection.ftcPose.range));
            }
        }

        // Ensure the launcher is running
        Actions.runBlocking(new ParallelAction(
                launcher.spinUp(rpm),
                new SequentialAction(
                        launcher.fireAction(),
                        launcher.releaseAction()
                )
            )
        );

        sendTelemetryPacket("launcher_rpm", rpm);
    }

    private AprilTagDetection aimBot(){
        if(webcam == null) {
            telemetry.addData("AimBot", "Invalid Camera");
            return null;
        }

        // detect target AprilTag
        webcam.update();
        AprilTagDetection towerDetection = webcam.getTagByID(teamColorID);

        // Calculate RPM from range to April Tag
        // Set the wheel velocity to achieve distance
        if (towerDetection != null && towerDetection.ftcPose != null) {
            telemetry.addData("AimBot", "Detection Found");
            telemetry.update();

            // Steer robot to center AprilTag using the pose bearing which is the angle
            // from the camera to the AprilTag center
            aim(towerDetection);
        }

        return towerDetection;
    }

    /**
     * Attempt to auto-aim (move robot) and fire ball at the team tower
     */
    private void autoFire() {
        if(webcam == null || autoFireInit) {
            return;
        }

        autoFireInit = true;

        // detect target AprilTag
        AprilTagDetection towerDetection = aimBot();

        // Calculate RPM from range to April Tag
        // Set the wheel velocity to achieve distance
        if (towerDetection != null && towerDetection.ftcPose != null) {
            // Calculate the velocity needed to shoot the ball at the correct distance
            double rpm = getRPM(x_Distance(towerDetection.ftcPose.range));
            telemetry.addData("Detection", "SPEED");
            telemetry.update();

            Actions.runBlocking(new ParallelAction(
                            launcher.spinUp(rpm),
                            new SequentialAction(
                                    launcher.fireAction(),
                                    launcher.releaseAction()
                            )
                    )
            );

            sendTelemetryPacket("launcher_rpm", rpm);
        }
        else{
            telemetry.addData("Detection", "Not Found");
        }

        autoFireInit = false;
    }

    /**
     * Steer robot to center AprilTag using the pose bearing which is the angle
     * from the camera to the AprilTag center
     *
     * @param target Target to steer the robot to center the camera
     */
    private void aim(@NonNull AprilTagDetection target) {
        final double bearingWeighting = 1.0;

        driveTrain.rotate(target.ftcPose.bearing + bearingWeighting);
    }

    private void sendTelemetryPacket(String key, Object value) {
        TelemetryPacket packet = new TelemetryPacket();
        packet.put(key, value);
        dashboard.sendTelemetryPacket(packet);
    }

    private void sendTelemetryPacket(String message) {
        TelemetryPacket packet = new TelemetryPacket();
        packet.addLine(message);
        dashboard.sendTelemetryPacket(packet);
    }
}