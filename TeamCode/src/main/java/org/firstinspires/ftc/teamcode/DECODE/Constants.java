package org.firstinspires.ftc.teamcode.DECODE;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;

public class Constants {

    static public final int RED_HOME_X = 62;
    static public final int RED_HOME_Y = 10;
    static public final double RED_HOME_ANGLE = 0;
    static public final int BLUE_HOME_X = 62;
    static public final int BLUE_HOME_Y = -10;
    static public final double BLUE_HOME_ANGLE = 0;

    // Set field positions
    static public final Vector2d red_farShootingPos = new Vector2d(51, 7);
    static public final Vector2d red_midShootingPos = new Vector2d(-15, 13.2);
    static public final Vector2d red_nearShootingPos = new Vector2d(-30.0, 21.7);
    static public final Vector2d blue_farShootingPos = new Vector2d(51, -7);
    static public final Vector2d blue_midShootingPos = new Vector2d(-15, -13.2);
    static public final Vector2d blue_nearShootingPos = new Vector2d(-30.0, -21.7);

    // This is defaulted to the red team. Y position should be inverted for blue team
    static public final Pose2d red_firstLinePos = new Pose2d(34, 30, Math.toRadians(-90));
    static public final Pose2d red_secondLinePos = new Pose2d(11, 30, Math.toRadians(-90));
    static public final Pose2d red_thirdLinePos = new Pose2d(-12, 30, Math.toRadians(-90));
    static public final Pose2d blue_firstLinePos = new Pose2d(33, -30, Math.toRadians(-90));
    static public final Pose2d blue_secondLinePos = new Pose2d(11, -30, Math.toRadians(-90));
    static public final Pose2d blue_thirdLinePos = new Pose2d(-12, -30, Math.toRadians(-90));

    // Human player loading zone, changes based on team color
    static public final Pose2d red_loadingPos = new Pose2d( 52, -52, Math.toRadians(45));
    static public final Pose2d blue_loadingPos = new Pose2d( 52, 52, Math.toRadians(45));
}
