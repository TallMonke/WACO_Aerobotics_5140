package org.firstinspires.ftc.teamcode.DECODE;

import static org.firstinspires.ftc.teamcode.mechanisms.RotationalMath.getRPM;
import static org.firstinspires.ftc.teamcode.mechanisms.RotationalMath.x_Distance;

import android.annotation.SuppressLint;

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
import java.util.List;

@TeleOp(name="TeleOP_Decode", group="Linear OpMode")
public class TeleOP_Decode extends LinearOpMode {
    private final AprilTagColors aprilTagColors = new AprilTagColors();
    // Obelisk colors hold the color order of the balls to shoot
    private ArrayList<DetectedColor> currentObeliskColors = null;

    // Select before match to set which team Red\Blue we use. This ID corresponds to the AprilTag ID
    // we should aim for when shooting
    private Integer teamColorID = aprilTagColors.getRedTeamID();
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

        long initTime = System.currentTimeMillis();

        /*/ Only spend 5s looking for the obelisk
        while (currentObeliskColors == null && System.currentTimeMillis() - initTime < 5000) {
            if (detectObelisk()) {
                telemetry.addData("Obelisk", "Detected");
                telemetry.update();
                break;
            }
        }
        */
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

            if(currentObeliskColors == null){
                detectObelisk();
            }

            // Get the launcher spun up
            if (!init) {
                launcher.setWheelVelocity(950.0);
                launcher.run();
            }

            telemetry.addData("Obelisk", printCurrentObelisk() );

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

//            webcam.update();
//            List<AprilTagDetection> detections = webcam.getDetectedTags();
//            for (AprilTagDetection detection: detections ) {
//                if(detection != null && detection.ftcPose != null) {
//                    telemetry.addData("Detection",
//                            String.format("ID: %d, R: %.2f, B: %.2f",
//                                    detection.id, detection.ftcPose.range, detection.ftcPose.bearing));
//                }
//            }

            // Attempt to auto-aim and fire ball at the team tower
            if(gamepad2.left_trigger > 0.5 && webcam != null){ autoFire(); }

            // Just push the ball out the launcher
            if(gamepad2.x) { manualFire(); }

            if(gamepad1.a) {
                driveTrain.rotate(90.0);
            }

            telemetry.update();
        }
    }

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
     * Just push the ball out the launcher using the current velocity
     */
    private void manualFire() {
        // Ensure the launcher is running
        launcher.run();

        launcher.push();
        sleep(500);

        launcher.release();

        // Ensure the launcher is running
        launcher.run();
    }

    /**
     * Attempt to auto-aim (move robot) and fire ball at the team tower
     */
    private void autoFire() {
        if(webcam == null || autoFireInit)
            return;

        autoFireInit = true;

        // detect target AprilTag
        webcam.update();
        AprilTagDetection towerDetection = webcam.getTagByID(teamColorID);

        // Calculate RPM from range to April Tag
        // Set the wheel velocity to achieve distance
        if (towerDetection != null && towerDetection.ftcPose != null) {
            telemetry.addData("Detection", "Found");
            telemetry.update();

            // Steer robot to center AprilTag using the pose bearing which is the angle
            // from the camera to the AprilTag center
            aim(towerDetection);
            telemetry.addData("Detection", "AIM");
            telemetry.update();

            // Calculate the velocity needed to shoot the ball at the correct distance
            double rpm = getRPM(x_Distance(towerDetection.ftcPose.range));
            telemetry.addData("Detection", "SPEED");
            telemetry.update();

            launcher.setWheelVelocity(rpm);

            // Ensure the wheels are spinning at that velocity
            launcher.run();

            sleep(750);

            // Fire the ball and return to ready state
            launcher.push();
            sleep(500);

            launcher.release();
            launcher.run();
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
    private void aim(AprilTagDetection target) {
        final double bearingWeighting = 1.0;

        driveTrain.rotate(target.ftcPose.bearing + bearingWeighting);
    }

    private Boolean detectObelisk() {
        webcam.update();
        ArrayList<Integer> obelisksIDs = aprilTagColors.getObeliskIDs();
        for (AprilTagDetection detection: webcam.getDetectedTags()){
            if(detection != null) {
                if(aprilTagColors.isObeliskID(detection.id)){
                    currentObeliskColors = aprilTagColors.getColor(detection.id);
                    telemetry.addLine(String.format("Obelisk ID: %d", detection.id));
                    telemetry.update();

                    return true;
                }
            }
        }

        return false;
    }
}