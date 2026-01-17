package org.firstinspires.ftc.teamcode.DECODE;

import static org.firstinspires.ftc.teamcode.mechanisms.RotationalMath.getRPM;
import static org.firstinspires.ftc.teamcode.mechanisms.RotationalMath.x_Distance;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.AprilTagColors;
import org.firstinspires.ftc.teamcode.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.DetectedColor;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.Revolver;
import org.firstinspires.ftc.teamcode.mechanisms.Sweeper;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.ArrayList;

@Autonomous(name = "Blue Auto 1", group = "auto", preselectTeleOp = "DECODE_2025_BLUE")
public final class Blue_PoseFar extends LinearOpMode {
    ElapsedTime timer = null;

    static private int HOME_X = 62;
    static private int HOME_Y = 11;
    static private double HOME_ANGLE = 0;

    // Set field positions
    static private final Vector2d farShootingPos = new Vector2d(51, 11);
    static private final Vector2d midShootingPos = new Vector2d(-15, 13.2);
    static private final Vector2d nearShootingPos = new Vector2d(-30.0, 21.7);

    // This is defaulted to the red team. Y position should be inverted for blue team
    static private final Pose2d firstLinePos = new Pose2d(36 - 5, 30, Math.toRadians(-90));
    static private final Pose2d secondLinePos = new Pose2d(12 - 5, 30, Math.toRadians(-90));
    static private final Pose2d thirdLinePos = new Pose2d(-12 + 5, 30, Math.toRadians(-90));

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
    final Integer teamColorID = aprilTagColors.getBlueTeamID();
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
        webcam = new AprilTagWebcam(hardwareMap, telemetry);
        revolver = new Revolver(hardwareMap, telemetry);
        sweeper = new Sweeper(hardwareMap, telemetry);
        launcher = new Launcher(hardwareMap, telemetry);

        if (teamColorID == aprilTagColors.getRedTeamID()) {
            loadingPos = new Pose2d( 52, -52, Math.toRadians(45));

            sendTelemetryPacket("RED Team Ready!");
        } else if (teamColorID == aprilTagColors.getBlueTeamID()) {
            loadingPos = new Pose2d( 52, 52, Math.toRadians(45));

            sendTelemetryPacket("BLUE Team Ready!");
        }

        waitForStart();

        timer = new ElapsedTime();

        // Drive "backward" away from wall, but touching far shooting zone
        Actions.runBlocking( new SequentialAction(
                drive.actionBuilder(drive.localizer.getPose())
                        .lineToX(farShootingPos.x)
                        .turn(Math.toRadians(-45)) // Turn camera towards tower
                        .build())
        );
        dashboard.getTelemetry().update();

        double rpm = aimBot();

        if (rpm > 0.0) {
            // Fire the ball
            if (!tripleFireSequence(rpm)) {
                sendTelemetryPacket("Firing sequence failed");
                stop();
            }
        }
        dashboard.getTelemetry().update();

        // ***First Line of Balls***
        if(!intakeBallLine(1)) {
            sendTelemetryPacket("Error running intake sequence");
            stop();
        }
        dashboard.getTelemetry().update();

        Actions.runBlocking(
                new SequentialAction(
                        drive.actionBuilder(drive.localizer.getPose()) // Drive to far shooting position
                                .strafeToSplineHeading(farShootingPos, Math.toRadians(-35))
                                .turn(Math.toRadians(-60))
                                .build()
                )
        );
        dashboard.getTelemetry().update();

        rpm = aimBot();

        if (rpm > 0.0) {
            // Fire the ball
            if (!tripleFireSequence(rpm)) {
                sendTelemetryPacket("Firing sequence failed");
                stop();
            }
        }
        dashboard.getTelemetry().update();

        // ***Second Line of Balls***
        // Ensure we have plenty of time to get back to human player
        if(timer.seconds() <= 20) {
            if (!intakeBallLine(2)) {
                sendTelemetryPacket("Error running intake sequence");
                stop();
            }

            Actions.runBlocking(
                    new SequentialAction(
                            drive.actionBuilder(drive.localizer.getPose()) // Drive to far shooting position
                                    .strafeToLinearHeading(midShootingPos, Math.toRadians(-45))
                                    .turn(Math.toRadians(-30.0))
                                    .build()
                    )
            );
            dashboard.getTelemetry().update();

            rpm = aimBot();

            if (rpm > 0.0) {
                // Fire the ball
                if (!tripleFireSequence(rpm)) {
                    sendTelemetryPacket("Firing sequence failed");
                    stop();
                }
            }

            dashboard.getTelemetry().update();
        }

        // ***Third Line of Balls***
        // Ensure we have plenty of time to get back to human player
        if(timer.seconds() <= 20) {
            if (!intakeBallLine(3)) {
                sendTelemetryPacket("Error running intake sequence");
                stop();
            }

            Actions.runBlocking(
                    new SequentialAction(
                            drive.actionBuilder(drive.localizer.getPose()) // Drive to far shooting position
                                    .strafeToLinearHeading(nearShootingPos, Math.toRadians(-90))
                                    .turn(Math.toRadians(-20.0))
                                    .build()
                    )
            );
            dashboard.getTelemetry().update();

            rpm = aimBot();

            if (rpm > 0.0) {
                // Fire the ball
                if (!tripleFireSequence(rpm)) {
                    sendTelemetryPacket("Firing sequence failed");
                    stop();
                }
            }
        }

        // Return to loading zone to start TeleOp
        Actions.runBlocking(
                new SequentialAction(
                        drive.actionBuilder(drive.localizer.getPose()) // Drive to far shooting position
                                .splineTo(loadingPos.position, loadingPos.heading.real)
                                .build()
                )
        );

        dashboard.getTelemetry().update();
    }

    /**
     * Utilizes the webcam to detect the teams tower and rotate the robot towards it
     *
     * @return Calculated velocity in RPM for shooting
     */
    private double aimBot() {
        webcam.update();
        AprilTagDetection towerDetection = webcam.getTagByID(teamColorID);

        int totalDegrees = 0;
        final int degreeChange = 45;

        // Turn attempting to find target AprilTag
        while (towerDetection == null && totalDegrees < 360) {
            Actions.runBlocking(new SequentialAction(
                    drive.actionBuilder(drive.localizer.getPose())
                            .turn(Math.toRadians(-degreeChange)) // Turn towards tower
                            .build())
            );
            totalDegrees += degreeChange;
            webcam.update();
            towerDetection = webcam.getTagByID(teamColorID);
        }

        // Only fire if we detected something
        if (towerDetection != null && towerDetection.ftcPose != null) {
            // Aim robot towards team tower
            Actions.runBlocking(new SequentialAction(
                    drive.actionBuilder(drive.localizer.getPose())
                            .turn(Math.toRadians(towerDetection.ftcPose.bearing))
                            .build())
            );

            // Calculate the velocity needed to shoot the ball at the correct distance
            double rpm = getRPM(x_Distance(towerDetection.ftcPose.range));

            sendTelemetryPacket("calc_rpm", rpm);

            return rpm;
        }

        return -1.0;
    }

    private boolean singleFireSequence(double rpm) {
        if(launcher == null || revolver == null){
            sendTelemetryPacket("Invalid Hardware");
            return false;
        }

        sendTelemetryPacket("fire_rpm", rpm);

        Actions.runBlocking(
                new SequentialAction(
                        launcher.spinUp(rpm),
                        launcher.fireAction(), // Fire loaded ball
                        launcher.releaseAction()
                )
        );

        return true;
    }

    private boolean tripleFireSequence(double rpm){
        if(launcher == null || revolver == null){
            sendTelemetryPacket("Invalid Hardware");
            return false;
        }

        sendTelemetryPacket("fire_rpm", rpm);

        Actions.runBlocking(
                new SequentialAction(
                        launcher.spinUp(rpm),
                        new SleepAction(0.5),
                        launcher.fireAction(), // Fire loaded ball
                        launcher.releaseAction(),
                        revolver.stepToFireAction(), // Select next ball in firing slot
                        launcher.fireAction(), // Fire second ball
                        launcher.releaseAction(),
                        revolver.stepToFireAction(), // Select next ball in the firing slot
                        launcher.fireAction(), // Fire third ball
                        launcher.releaseAction()
                )
        );

        return true;
    }

    private boolean intakeBallLine(int row) {
        if (sweeper == null || drive == null || revolver == null) {
            sendTelemetryPacket("Invalid Hardware");
            return false;
        }

        Pose2d ballLinePos = null;

        switch (row) {
            case 1:
                ballLinePos = firstLinePos;
                break;
            case 2:
                ballLinePos = secondLinePos;
                break;
            case 3:
                ballLinePos = thirdLinePos;
                break;
            default:
                sendTelemetryPacket("Invalid ball line position");
                return false;
        }

        double posOffset = 5.0;

        if(teamColorID == aprilTagColors.getBlueTeamID()) {
            posOffset = -5.0;
        }


        Actions.runBlocking(
                new ParallelAction(
                        drive.actionBuilder(drive.localizer.getPose()) // Drive to far shooting position
                                .splineTo(new Vector2d(ballLinePos.component1().x, ballLinePos.component1().y), Math.toRadians(-90))
                                .build(),

                        revolver.stepToLoadAction() // Select next ball in loading slot
                )
        );

        ballLinePos = drive.localizer.getPose();

        Actions.runBlocking(
                new ParallelAction(
                        sweeper.enableAction(),
                        new SequentialAction(
                                drive.actionBuilder(drive.localizer.getPose()) // Suck up first ball
                                        .lineToY(ballLinePos.component1().y + posOffset)
                                        .build(),
                                new SleepAction(0.25),
                                revolver.stepToLoadAction(), // Select next ball in loading slot
                                drive.actionBuilder(drive.localizer.getPose()) // Suck up second ball
                                        .lineToY(ballLinePos.component1().y + (posOffset * 2.0))
                                        .build(),
                                new SleepAction(0.25),
                                revolver.stepToLoadAction(), // Select next ball in loading slot
                                drive.actionBuilder(drive.localizer.getPose()) // Suck up third ball
                                        .lineToY(ballLinePos.component1().y + (posOffset * 3.0))
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
