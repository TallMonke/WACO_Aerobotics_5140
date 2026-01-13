package org.firstinspires.ftc.teamcode.DECODE;

import static org.firstinspires.ftc.teamcode.mechanisms.RotationalMath.getRPM;
import static org.firstinspires.ftc.teamcode.mechanisms.RotationalMath.x_Distance;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.AprilTagColors;
import org.firstinspires.ftc.teamcode.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.DetectedColor;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.Revolver;
import org.firstinspires.ftc.teamcode.mechanisms.Sweeper;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.ArrayList;

@Autonomous(name = "Auto Pose Far", group = "autonomous")
public final class AutoOP_PoseFar extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize at SPECIFIC coordinates, touching the wall and scoring zone
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(47, 56, Math.toRadians(-180)));
        final AprilTagColors aprilTagColors = new AprilTagColors();
        // Obelisk colors hold the color order of the balls to shoot
        ArrayList<DetectedColor> currentObeliskColors = null;

        // Select before match to set which team Red\Blue we use. This ID corresponds to the AprilTag ID
        // we should aim for when shooting
        Integer teamColorID = aprilTagColors.getRedTeamID();
        AprilTagWebcam webcam = new AprilTagWebcam(hardwareMap, telemetry);
        Revolver revolver = new Revolver(hardwareMap, telemetry);
        Sweeper sweeper = new Sweeper(hardwareMap, telemetry);
        Launcher launcher = new Launcher(hardwareMap, telemetry);

        if (teamColorID == aprilTagColors.getRedTeamID()) {
            telemetry.addData("Status", "RED Team Ready!");
        } else if (teamColorID == aprilTagColors.getBlueTeamID()) {
            telemetry.addData("Status", "BLUE Team Ready!");
        }

        waitForStart();

        // Drive "backward" away from wall, but touching far shooting zone
        Actions.runBlocking( new SequentialAction(
                drive.actionBuilder(drive.localizer.getPose())
                        .lineToX(42)
                        .turn(Math.toRadians(45)) // Turn towards tower
                        .build())
        );

        // Detect the team tower AprilTag
//        webcam.update();
//        AprilTagDetection towerDetection = webcam.getTagByID(teamColorID);
//
//        // Turn 1 degree attempting to find target AprilTag
//        while (towerDetection == null) {
//            Actions.runBlocking( new SequentialAction(
//                    drive.actionBuilder(drive.localizer.getPose())
//                            .turn(Math.toRadians(1.0)) // Turn towards tower
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
        //launcher.setWheelVelocity(900);
        //launcher.run();

        // Fire the ball
        Actions.runBlocking(
                new ParallelAction(
                        launcher.spinUp(900), // Ensure the launcher runs for the entire action
                        new SequentialAction(
                                launcher.pushAction(), // Fire loaded ball
                                launcher.releaseAction(),
                                revolver.stepUpAction(), // Select next ball
                                revolver.stepUpAction(), // Select next ball
                                launcher.pushAction(), // Fire second ball
                                launcher.releaseAction(),
                                revolver.stepUpAction(), // Select next ball
                                revolver.stepUpAction(), // Select next ball
                                launcher.pushAction(), // Fire third ball
                                launcher.releaseAction()
                        )
                )
        );

        // Drive to first line of field balls and intake
        Actions.runBlocking( new SequentialAction(
                drive.actionBuilder(drive.localizer.getPose()) // Drive to line of balls
                        .splineTo(new Vector2d(42, 51), 52.0)
                        .turn(Math.toRadians(45))
                        .build(),
                sweeper.enableAction(),
                // TODO: Revolver action to skip to empty loading position
                drive.actionBuilder(drive.localizer.getPose()) // Suck up first ball
                        .splineTo(new Vector2d(42 + 4, 51), 52.0)
                        .build(),
                // TODO: Revolver action to skip to next empty loading position
                drive.actionBuilder(drive.localizer.getPose()) // Suck up second ball
                        .splineTo(new Vector2d(42 + 8, 51), 52.0)
                        .build(),
                // TODO: Revolver action to skip to last empty loading position
                drive.actionBuilder(drive.localizer.getPose()) // Suck up last ball
                        .splineTo(new Vector2d(42 + 12, 51), 52.0)
                        .build(),
                // TODO: Drive to scoring zone
                sweeper.disableAction()
                )
        );
    }
}
