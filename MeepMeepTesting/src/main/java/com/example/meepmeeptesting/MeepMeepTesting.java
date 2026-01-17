package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    final static private int RED_HOME_X = 61;
    final static private int RED_HOME_Y = -10;
    final static private int RED_HOME_ANGLE = 0;

    // Red Team setup
    final static private Vector2d red_farShootingPos = new Vector2d(51, -7);
    final static private Vector2d red_midShootingPos = new Vector2d(-15, -13.2);
    final static private Vector2d red_nearShootingPos = new Vector2d(-30, -21.7);
    final static private Pose2d red_loadingPos = new Pose2d( 52, -52, Math.toRadians(45));

    static private final Pose2d red_firstLinePos = new Pose2d(36, -42, Math.toRadians(-90));
    static private final Pose2d red_secondLinePos = new Pose2d(11.6, -42, Math.toRadians(-90));
    static private final Pose2d red_thirdLinePos = new Pose2d(-12, -42, Math.toRadians(-90));

    final static private int BLUE_HOME_X = 61;
    final static private int BLUE_HOME_Y = 10;
    final static private int BLUE_HOME_ANGLE = 180;

    // Red Team setup
    final static private Pose2d blue_farShootingPos = new Pose2d(51, 7, Math.toRadians(-60));
    final static private Pose2d blue_midShootingPos = new Pose2d(0, 0, Math.toRadians(45));
    final static private Pose2d blue_nearShootingPos = new Pose2d(-15, 13.2, Math.toRadians(45));
    final static private Pose2d blue_loadingPos = new Pose2d( 52, 52, Math.toRadians(45));

    static private final Pose2d blue_firstLinePos = new Pose2d(36, 42, Math.toRadians(-90));
    static private final Pose2d blue_secondLinePos = new Pose2d(11.6, 42, Math.toRadians(-90));
    static private final Pose2d blue_thirdLinePos = new Pose2d(-12, 42, Math.toRadians(-90));

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity blueBot = new DefaultBotBuilder(meepMeep)
                // We set this bot to be blue
                .setColorScheme(new ColorSchemeBlueDark())
                .setConstraints(90, 75, Math.toRadians(180), Math.toRadians(180), 16)
                .setDimensions(17, 16)
                .build();

        RoadRunnerBotEntity redBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setColorScheme(new ColorSchemeRedDark())
                .setConstraints(90, 75, Math.toRadians(180), Math.toRadians(180), 16)
                .setDimensions(17, 16)
                .build();

        redBot.runAction(redBot.getDrive().actionBuilder(new Pose2d(RED_HOME_X, RED_HOME_Y, Math.toRadians(RED_HOME_ANGLE)))
                .lineToX(red_farShootingPos.x) // Pull away from wall
                .turn(Math.toRadians(-45.0))
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)
                // Turn on sweeper, set revolver to loading position
                // ***FIRST LINE OF BALLS
                .turn(Math.toRadians(-45))
                .splineToConstantHeading(new Vector2d(red_firstLinePos.component1().x, red_firstLinePos.component1().y), Math.toRadians(-90)) // ingest first ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_firstLinePos.component1().y - 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_firstLinePos.component1().y - 10) // ingest 2nd ball
                .strafeToSplineHeading(red_farShootingPos, Math.toRadians(-35))
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                // ***SECOND LINE OF BALLS
                .splineTo(new Vector2d(red_secondLinePos.component1().x, red_secondLinePos.component1().y), Math.toRadians(-90))
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_secondLinePos.component1().y - 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_secondLinePos.component1().y - 10)
                .waitSeconds(1)
                .lineToY(red_secondLinePos.component1().y - 15) // ingest 2nd ball
                .strafeToLinearHeading(red_midShootingPos, -45)
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                //***THIRD LINE OF BALLS
                .splineTo(new Vector2d(red_thirdLinePos.component1().x, red_thirdLinePos.component1().y), Math.toRadians(-90))
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_thirdLinePos.component1().y - 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_thirdLinePos.component1().y - 10) // ingest 2nd ball
                .strafeToLinearHeading(red_nearShootingPos, -90)
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                // add action to shoot balls in color order
                .splineTo(red_loadingPos.position, -25)
                .build()
        );

        blueBot.runAction(blueBot.getDrive().actionBuilder(new Pose2d(BLUE_HOME_X, BLUE_HOME_Y, Math.toRadians(BLUE_HOME_ANGLE)))
                .lineToX(56) // Pull away from wall
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)
                // Turn on sweeper, set revolver to loading position
                // ***FIRST LINE OF BALLS
                .splineTo(new Vector2d(blue_firstLinePos.component1().x, blue_firstLinePos.component1().y), Math.toRadians(90)) // ingest first ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(blue_firstLinePos.component1().y + 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(blue_firstLinePos.component1().y + 10) // ingest 2nd ball
                .strafeToSplineHeading(blue_farShootingPos.position, -150)
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                // ***SECOND LINE OF BALLS
                .splineTo(new Vector2d(blue_secondLinePos.component1().x, blue_secondLinePos.component1().y), Math.toRadians(90))
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(blue_secondLinePos.component1().y + 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(blue_secondLinePos.component1().y + 10) // ingest 2nd ball
                .strafeToSplineHeading(blue_midShootingPos.position, -135)
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                //***THIRD LINE OF BALLS
                .splineTo(new Vector2d(blue_thirdLinePos.component1().x, blue_thirdLinePos.component1().y), Math.toRadians(90))
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(blue_thirdLinePos.component1().y + 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(blue_thirdLinePos.component1().y + 10) // ingest 2nd ball
                .strafeToSplineHeading(blue_nearShootingPos.position, 60)
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                // add action to shoot balls in color order
                .splineTo(blue_loadingPos.position, blue_loadingPos.heading.real)
                .build()
        );

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(blueBot)
                .addEntity(redBot)
                .start();
    }
}