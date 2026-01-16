package org.firstinspires.ftc.teamcode.tests;

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

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.Revolver;
import org.firstinspires.ftc.teamcode.mechanisms.Sweeper;

@Autonomous(name = "Sweeper Pickup Test", group = "tests")
public final class Auto_SweeperPickup extends LinearOpMode {

    // Hardware objects
    FtcDashboard dashboard = null;

    MecanumDrive drive = null;
    Revolver revolver = null;
    Sweeper sweeper = null;

    @Override
    public void runOpMode() throws InterruptedException {
        dashboard = FtcDashboard.getInstance();

        // Initialize at SPECIFIC coordinates, touching the wall and scoring zone
        drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, Math.toRadians(-90.0)));

        revolver = new Revolver(hardwareMap, telemetry);
        sweeper = new Sweeper(hardwareMap, telemetry);

        waitForStart();

        intakeBallLine();
    }

    private boolean intakeBallLine() {
        if (sweeper == null || drive == null || revolver == null) {
            sendTelemetryPacket("Invalid Hardware");
            return false;
        }

        Pose2d ballLinePos = drive.localizer.getPose();

        Actions.runBlocking(
                new ParallelAction(
                        sweeper.enableAction(),
                        new SequentialAction(
                                sweeper.enableAction(),
                                revolver.stepToLoadAction(), // Select next ball in loading slot
                                drive.actionBuilder(drive.localizer.getPose()) // Suck up first ball
                                        .lineToY(ballLinePos.component1().y - 5)
                                        .build(),
                                new SleepAction(0.25),
                                revolver.stepToLoadAction(), // Select next ball in loading slot
                                drive.actionBuilder(drive.localizer.getPose()) // Suck up second ball
                                        .lineToY(ballLinePos.component1().y - 10)
                                        .build(),
                                new SleepAction(0.25),
                                revolver.stepToLoadAction(), // Select next ball in loading slot
                                drive.actionBuilder(drive.localizer.getPose()) // Suck up third ball
                                        .lineToY(ballLinePos.component1().y - 15)
                                        .build(),
                                sweeper.disableAction()
                        )
                )
        );

        dashboard.getTelemetry().update();

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