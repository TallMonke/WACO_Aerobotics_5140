package org.firstinspires.ftc.teamcode.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;

@Autonomous(name = "Auto_BoxTest", group = "tests")
public class Auto_BoxTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        FtcDashboard dashboard = FtcDashboard.getInstance();

        // We are starting the robot at the bottom left corner of the square
        Pose2d beginPose = new Pose2d(0, 0, 0);
        MecanumDrive drive = new MecanumDrive(hardwareMap, beginPose);

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(
                drive.actionBuilder(beginPose)
                        .lineToX(20)
                        .turn(Math.toRadians(90))
                        .lineToY(20)
                        .turn(Math.toRadians(90))
                        .lineToX(-20)
                        .turn(Math.toRadians(90))
                        .lineToY(-20)
                        .turn(Math.toRadians(90))
                        .lineToX(20)
                        .turn(Math.toRadians(90))
                        .lineToY(0)
                        .build()
        );
    }
}
