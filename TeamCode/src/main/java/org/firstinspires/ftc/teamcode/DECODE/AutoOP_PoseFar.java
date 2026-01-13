package org.firstinspires.ftc.teamcode.DECODE;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.AprilTagColors;
import org.firstinspires.ftc.teamcode.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.DetectedColor;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.Revolver;
import org.firstinspires.ftc.teamcode.mechanisms.Sweeper;

import java.util.ArrayList;

@Autonomous(name = "Auto Pose Far", group = "autonomous")
public final class AutoOP_PoseFar extends LinearOpMode {
    static private int HOME_X = 56;
    static private int HOME_Y = 10;
    static private double HOME_ANGLE = 45;

    // Set field positions
    static private Vector2d farShootingPos = new Vector2d(HOME_X, HOME_Y);
    static private Vector2d midShootingPos = new Vector2d(0, 0);
    static private Vector2d nearShootingPos = new Vector2d(-25, 20);

    // This is defaulted to the red team. Y position should be inverted for blue team
    static private Pose2d firstLinePos = new Pose2d(36, 40, Math.toRadians(90));
    static private Pose2d secondLinePos = new Pose2d(10, 40, Math.toRadians(90));
    static private Pose2d thirdLinePos = new Pose2d(-15, 40, Math.toRadians(90));

    // Human player loading zone, changes based on team color
    static private Pose2d loadingPos = null; // position based on team color

    // Hardware objects
    FtcDashboard dashboard = null;

    MecanumDrive drive = null;
    final AprilTagColors aprilTagColors = new AprilTagColors();
    // Obelisk colors hold the color order of the balls to shoot
    ArrayList<DetectedColor> currentObeliskColors = null;

    // Select before match to set which team Red\Blue we use. This ID corresponds to the AprilTag ID
    // we should aim for when shooting
    Integer teamColorID = -1;
    AprilTagWebcam webcam = null;
    Revolver revolver = null;
    Sweeper sweeper = null;
    Launcher launcher = null;

    @Override
    public void runOpMode() throws InterruptedException {
        dashboard = FtcDashboard.getInstance();

        // Initialize at SPECIFIC coordinates, touching the wall and scoring zone
        drive = new MecanumDrive(hardwareMap, new Pose2d(HOME_X, HOME_Y, Math.toRadians(HOME_ANGLE)));

        // Select before match to set which team Red\Blue we use. This ID corresponds to the AprilTag ID
        // we should aim for when shooting
        teamColorID = aprilTagColors.getRedTeamID();
        webcam = new AprilTagWebcam(hardwareMap, telemetry);
        revolver = new Revolver(hardwareMap, telemetry);
        sweeper = new Sweeper(hardwareMap, telemetry);
        launcher = new Launcher(hardwareMap, telemetry);

        if (teamColorID == aprilTagColors.getRedTeamID()) {
            loadingPos = new Pose2d( -56, 55, Math.toRadians(45));

            sendTelemetryPacket("RED Team Ready!");
        } else if (teamColorID == aprilTagColors.getBlueTeamID()) {
            loadingPos = new Pose2d( 56, -55, Math.toRadians(45));

            sendTelemetryPacket("BLUE Team Ready!");
        }

        waitForStart();

        // Drive "backward" away from wall, but touching far shooting zone
        Actions.runBlocking( new SequentialAction(
                drive.actionBuilder(drive.localizer.getPose())
                        .lineToX(42)
                        .turn(Math.toRadians(45)) // Turn towards tower
                        .build())
        );

        // TODO
        //  Detect tower, auto-aim, calculate firing velocity
//        webcam.update();
//        AprilTagDetection towerDetection = webcam.getTagByID(teamColorID);
//
//        // Turn 1 degree attempting to find target AprilTag
//        while (towerDetection == null) {
//            Actions.runBlocking( new SequentialAction(
//                    drive.actionBuilder(drive.localizer.getPose())
//                            .turn(Math.toRadians(20)) // Turn towards tower
//                            .build())
//            );
//            webcam.update();
//            towerDetection = webcam.getTagByID(teamColorID);
//        }
//
//        // Aim robot towards team tower
//        Actions.runBlocking( new SequentialAction(
//                drive.actionBuilder(drive.localizer.getPose())
//                        .turn(Math.toRadians(Math.toRadians(towerDetection.ftcPose.bearing)))
//                        .build())
//        );

        // Calculate the velocity needed to shoot the ball at the correct distance
//        webcam.update();
//        towerDetection = webcam.getTagByID(teamColorID);
//
//        double rpm = getRPM(x_Distance(towerDetection.ftcPose.range));
        double rpm = 900;

        // Fire the ball
        if(!firingSequence(rpm)) {
            sendTelemetryPacket("Firing sequence failed");
            stop();
        }

        // Drive to first line of field balls and intake
        if(!intakeFirstLine()) {
            sendTelemetryPacket("Error running intake sequence");
            stop();
        }

        Actions.runBlocking(
                new SequentialAction(
                        drive.actionBuilder(drive.localizer.getPose()) // Drive to far shooting position
                                .splineTo(farShootingPos, Math.toRadians(90))
                                .build()
                )
        );

        // TODO
        //  Detect tower, auto-aim, calculate firing velocity

        if(!firingSequence(rpm)) {
            sendTelemetryPacket("Error running firing sequence");
            stop();
        }
    }

    private boolean firingSequence(double rpm){
        if(launcher == null || revolver == null){
            sendTelemetryPacket("Invalid Hardware");
            return false;
        }

        Actions.runBlocking(
                new ParallelAction(
                        launcher.spinUp(rpm), // Ensure the launcher runs for the entire action
                        new SequentialAction(
                                launcher.fireAction(), // Fire loaded ball
                                launcher.releaseAction(),
                                revolver.stepToFireAction(), // Select next ball in firing slot
                                launcher.fireAction(), // Fire second ball
                                launcher.releaseAction(),
                                revolver.stepToFireAction(), // Select next ball in the firing slot
                                launcher.fireAction(), // Fire third ball
                                launcher.releaseAction()
                        )
                )
        );

        return true;
    }

    private boolean intakeFirstLine() {
        if (sweeper == null || drive == null || revolver == null) {
            sendTelemetryPacket("Invalid Hardware");
            return false;
        }

        Actions.runBlocking(
                new ParallelAction(
                        sweeper.enableAction(),
                        new SequentialAction(
                                revolver.stepToLoadAction(), // Select next ball in loading slot
                                drive.actionBuilder(drive.localizer.getPose()) // Drive to line of balls
                                        .splineTo(new Vector2d(firstLinePos.component1().x, firstLinePos.component1().y), Math.toRadians(90))
                                        .build(),
                                revolver.stepToLoadAction(), // Select next ball in loading slot
                                drive.actionBuilder(drive.localizer.getPose()) // Suck up first ball
                                        .splineTo(new Vector2d(firstLinePos.component1().x, firstLinePos.component1().y + 4), 52.0)
                                        .build(),
                                revolver.stepToLoadAction(), // Select next ball in loading slot
                                drive.actionBuilder(drive.localizer.getPose()) // Suck up second ball
                                        .splineTo(new Vector2d(firstLinePos.component1().x, firstLinePos.component1().y + 8), 52.0)
                                        .build(),
                                revolver.stepToLoadAction(), // Select next ball in loading slot
                                drive.actionBuilder(drive.localizer.getPose()) // Suck up last ball
                                        .splineTo(new Vector2d(firstLinePos.component1().x, firstLinePos.component1().y + 12), 52.0)
                                        .build(),
                                sweeper.disableAction()
                        )
                )
        );

        return true;
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
