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
            if(gamepad2.left_trigger > 0.5 && webcam != null){
                // detect target AprilTag
                webcam.update();
                AprilTagDetection towerDetection = webcam.getTagByID(teamColorID);

                // TODO: remove this for loop, just displaying detections
                for (AprilTagDetection detection : webcam.getDetectedTags()) {
                    if (detection != null) {
                        webcam.displayTelemetry(detection);
                    }
                }

                // Steer robot to center AprilTag

                // Calculate RPM from range to April Tag
                // Set the wheel velocity to achieve distance
                if (towerDetection != null && towerDetection.ftcPose != null) {
                    double rpm = getRPM(x_Distance(towerDetection.ftcPose.range));

                    launcher.setWheelVelocity(rpm);

                    // Launch ball at that velocity
                    launcher.run();

                    launcher.push();
                    launcher.release();
                }
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

    /**
     * @param distance of shooter to apirl tag into x-component distance robot to basket.
     */
    public double x_Distance(double distance){
        double launchHeight = 13.75; //distance of point of launch of the robot from the ground, y-component (pla=11.25 + centerOfBall=2.5 = 13.75)
        double x_DistanceCamera = 7.0; //distance (x-component) the camera is away from launch point.
        double y_DistanceCamera = 1.68; //distance (y-component) the camera is away from launch point.
        double aprilFromTop = 9.25; //distance from center of april tag to top of the basket front wall.
        double aprilTagWallHeight = 38.75; //total height of the basket front wall with the location april tags on it.
        double Y = aprilTagWallHeight-y_DistanceCamera-launchHeight-aprilFromTop;
        double X = (Math.sqrt(Math.pow(distance,2)-(Math.pow(Y,2)))-x_DistanceCamera);
        return X;
    }

    /**
     * Calculate the RPM needed based on the @param x_distance of launcher to basket. (x component)
     */
    public double getRPM(double distance) {
        double valueInDegrees = 44.3; //launch angle in degrees
        double valueInRadians = Math.toRadians(valueInDegrees);
        double height = 27.75;  //height the ball needs to be off the ground to make basket based from the launcher of robot. 41-13.75
        double term1 = (60 / (4 * Math.PI));
        double numerator = (386.09 * Math.pow(distance, 2)); // 386.09(D^2)
        double cosineSquared = Math.pow((Math.cos(valueInRadians)), 2);
        double RPM = (term1 * (Math.sqrt(numerator / (2 * cosineSquared * (distance * (Math.tan(valueInRadians)) - height))))); //total formula to get RPM from x-component distance.
        return RPM;
    }
}