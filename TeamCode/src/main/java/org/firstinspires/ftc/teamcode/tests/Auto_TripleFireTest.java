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

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.Revolver;

@Autonomous(name = "Triple Fire Test", group = "tests", preselectTeleOp = "DECODE_2025")
public final class Auto_TripleFireTest extends LinearOpMode {
    static private int HOME_X = 61;
    static private int HOME_Y = -10;
    static private double HOME_ANGLE = 0;

    // Hardware objects
    FtcDashboard dashboard = null;

    MecanumDrive drive = null;
    Revolver revolver = null;
    Launcher launcher = null;

    @Override
    public void runOpMode() throws InterruptedException {
        dashboard = FtcDashboard.getInstance();

        // Initialize at SPECIFIC coordinates, touching the wall and scoring zone
        drive = new MecanumDrive(hardwareMap, new Pose2d(HOME_X, HOME_Y, Math.toRadians(HOME_ANGLE)));

        revolver = new Revolver(hardwareMap, telemetry);
        launcher = new Launcher(hardwareMap, telemetry);

        waitForStart();

        tripleFireSequence(900.0);
    }

    private boolean singleFireSequence(double rpm) {
        if (launcher == null || revolver == null) {
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

    private boolean tripleFireSequence(double rpm) {
        if (launcher == null || revolver == null) {
            sendTelemetryPacket("Invalid Hardware");
            return false;
        }

        sendTelemetryPacket("fire_rpm", rpm);

        Actions.runBlocking(
                new SequentialAction(
                        launcher.spinUp(rpm),
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
