package org.firstinspires.ftc.teamcode;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@TeleOp(name="TeleOP_Decode2025", group="Linear OpMode")
public class TeleOP_Decode2025 extends LinearOpMode {
    private final AprilTagColors aprilTagColors = new AprilTagColors();
    // Obelisk colors hold the color order of the balls to shoot
    private ArrayList<DetectedColor> currentObeliskColors = null;

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
        // Initialize the hardware mechanisms
        // TODO: Select which team color we are, use the AprilTagColors to get red/blue team ID values
        webcam = new AprilTagWebcam(hardwareMap, telemetry);
        driveTrain = new DriveTrain(hardwareMap, telemetry);
        revolver = new Revolver(hardwareMap, telemetry);
        sweeper = new Sweeper(hardwareMap, telemetry);
        launcher = new Launcher(hardwareMap, telemetry);

        // Wait for the game to start (driver presses START)
        if( teamColorID == aprilTagColors.getRedTeamID() ) {
            telemetry.addData("Status", "RED Team Ready!");
        }
        else if( teamColorID == aprilTagColors.getBlueTeamID() ) {
            telemetry.addData("Status", "BLUE Team Ready!");
        }

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            if( teamColorID == aprilTagColors.getRedTeamID() ) {
                telemetry.addData("Team", "RED");
            }
            else if( teamColorID == aprilTagColors.getBlueTeamID() ) {
                telemetry.addData("Team", "BLUE");
            }
            if(currentObeliskColors == null){
                detectObelisk();
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

            webcam.update();
            List<AprilTagDetection> detections = webcam.getDetectedTags();
            for (AprilTagDetection detection: detections ) {
                if(detection != null && detection.ftcPose != null) {
                    telemetry.addData("Detection",
                            String.format("ID: %d, R: %.2f, B: %.2f", detection.id, detection.ftcPose.range, detection.ftcPose.bearing));
                }
            }

            // Attempt to auto-aim and fire ball at the team tower
            if(gamepad2.left_trigger > 0.5 && webcam != null){ autoFire(); }

            // Just push the ball out the launcher
            if(gamepad2.x) { manualFire(); }

            if(gamepad1.a) {
                driveTrain.rotate(10.0);
            }
            if(gamepad1.b) {
                driveTrain.rotate(-20.0);
            }

            // This ensures the launcher is always spun up at the set velocity
            launcher.run();

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
        // detect target AprilTag
        webcam.update();
        AprilTagDetection towerDetection = webcam.getTagByID(teamColorID);

        // Calculate RPM from range to April Tag
        // Set the wheel velocity to achieve distance
        if (towerDetection != null && towerDetection.ftcPose != null) {
            // Steer robot to center AprilTag using the pose bearing which is the angle
            // from the camera to the AprilTag center
            aim( towerDetection );

            // Calculate the velocity needed to shoot the ball at the correct distance
            double rpm = getRPM(x_Distance(towerDetection.ftcPose.range));

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
    }

    /**
     * Steer robot to center AprilTag using the pose bearing which is the angle
     * from the camera to the AprilTag center
     *
     * @param target Target to steer the robot to center the camera
     */
    private void aim(AprilTagDetection target) {
        final double bearingWeighting = -180.0;

        driveTrain.rotate(-target.ftcPose.bearing + bearingWeighting);
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

    /**
     * @param distance of shooter to april tag into x-component distance robot to basket.
     */
    private double x_Distance(double distance){
        double launchHeight = 14.0; //distance of point of launch of the robot from the ground, y-component
        double x_DistanceCamera = 7.0; //distance (x-component) the camera is away from launch point.
        double y_DistanceCamera = 1.5; //distance (y-component) the camera is away from launch point.
        double aprilFromTop = 9.25; //distance from center of april tag to top of the basket front wall.
        double aprilTagWallHeight = 38.75; //total height of the basket front wall with the location april tags on it.
        double Y = 14;    //aprilTagWallHeight-y_DistanceCamera-launchHeight-aprilFromTop
        double X = (Math.sqrt(Math.pow(distance,2)-(Math.pow(Y,2)))-x_DistanceCamera);
        return X;
    }

    /**
     * Calculate the RPM needed based on the @param x_distance of launcher to basket. (x component)
     */
    private double getRPM(double distance) {
        double valueInDegrees = 45.0; //launch angle in degrees
        double valueInRadians = Math.toRadians(valueInDegrees);
        double height = 29.0;  //height the ball needs to be off the ground to make basket based from the launcher of robot. 41-13.75
        double tuneCorrection = 200; //Value you set to eliminate discreptincies.
        double term1 = (60.0 / (4 * Math.PI));
        double numerator = (386.09 * Math.pow(distance, 2)); // 386.09(D^2)
        double cosineSquared = Math.pow((Math.cos(valueInRadians)), 2);
        double denominator = 2 * cosineSquared * ((distance * (Math.tan(valueInRadians))) - height);
        double RPM = (term1 * (Math.sqrt(numerator / denominator))); //total formula to get RPM from x-component distance.
        telemetry.addData("RPM =", RPM);
        return RPM - tuneCorrection;
    }
}