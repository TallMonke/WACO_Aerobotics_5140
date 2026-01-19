package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeBlueDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedDark;
import com.noahbres.meepmeep.core.colorscheme.scheme.ColorSchemeRedLight;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    final static private int RED_HOME_X = 61;
    final static private int RED_HOME_Y = 10;
    final static private int RED_HOME_ANGLE = 0;

    // Red Team setup
    final static private Vector2d red_farShootingPos = new Vector2d(51, 7);
    final static private Vector2d red_midShootingPos = new Vector2d(-15, 13.2);
    final static private Vector2d red_nearShootingPos = new Vector2d(-30, 21.7);
    final static private Pose2d red_loadingPos = new Pose2d( 52, -52, Math.toRadians(45));

    static private final Pose2d red_firstLinePos = new Pose2d(36, 30, Math.toRadians(-90));
    static private final Pose2d red_secondLinePos = new Pose2d(11.6, 30, Math.toRadians(-90));
    static private final Pose2d red_thirdLinePos = new Pose2d(-12, 30, Math.toRadians(-90));

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

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
                .strafeToLinearHeading(new Vector2d(red_firstLinePos.component1().x, red_firstLinePos.component1().y), Math.toRadians(90)) // ingest first ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_firstLinePos.component1().y + 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_firstLinePos.component1().y + 10) // ingest 2nd ball
                .waitSeconds(1)
                .lineToY(red_firstLinePos.component1().y + 15) // ingest 2nd ball
                .strafeToLinearHeading(red_farShootingPos, Math.toRadians(-35))
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                // ***SECOND LINE OF BALLS
                .strafeToLinearHeading(new Vector2d(red_secondLinePos.component1().x, red_secondLinePos.component1().y), Math.toRadians(90))
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_secondLinePos.component1().y + 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_secondLinePos.component1().y + 10)
                .waitSeconds(1)
                .lineToY(red_secondLinePos.component1().y + 15) // ingest 2nd ball
                .strafeToLinearHeading(red_midShootingPos, -45)
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                //***THIRD LINE OF BALLS
                .strafeToLinearHeading(new Vector2d(red_thirdLinePos.component1().x, red_thirdLinePos.component1().y), Math.toRadians(90))
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_thirdLinePos.component1().y + 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_thirdLinePos.component1().y + 10) // ingest 2nd ball
                .waitSeconds(1)
                .lineToY(red_thirdLinePos.component1().y + 15) // ingest 2nd ball
                .strafeToLinearHeading(red_nearShootingPos, -95)
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                // add action to shoot balls in color order
                .strafeToLinearHeading(new Vector2d(0, 23 ), Math.toRadians(-45))
                .build()
        );


        RoadRunnerBotEntity midRunBot = new DefaultBotBuilder(meepMeep)
                // We set this bot to be blue
                .setColorScheme(new ColorSchemeRedLight())
                .setConstraints(90, 75, Math.toRadians(180), Math.toRadians(180), 16)
                .setDimensions(17, 16)
                .build();

        midRunBot.runAction(midRunBot.getDrive().actionBuilder(new Pose2d(-49, 49, Math.toRadians(50)))
                .strafeToLinearHeading(new Vector2d(-25, 25 ), Math.toRadians(-50) )// Pull away from wall
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)
                //***THIRD LINE OF BALLS
                .strafeToLinearHeading(new Vector2d(red_thirdLinePos.component1().x, red_thirdLinePos.component1().y), Math.toRadians(90))
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_thirdLinePos.component1().y + 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(red_thirdLinePos.component1().y + 10) // ingest 2nd ball
                .waitSeconds(1)
                .lineToY(red_thirdLinePos.component1().y + 15) // ingest 2nd ball
                .strafeToLinearHeading(red_nearShootingPos, -95)
                .strafeToSplineHeading(new Vector2d(-24, 47 ), Math.toRadians(45))
                .build()
        );


        RoadRunnerBotEntity shortRunBot = new DefaultBotBuilder(meepMeep)
                // We set this bot to be blue
                .setColorScheme(new ColorSchemeBlueDark())
                .setConstraints(90, 75, Math.toRadians(180), Math.toRadians(180), 16)
                .setDimensions(17, 16)
                .build();

        shortRunBot.runAction(shortRunBot.getDrive().actionBuilder(new Pose2d(-49, -49, Math.toRadians(50)))
                .strafeToLinearHeading(new Vector2d(-25, -25 ), Math.toRadians(50) )// Pull away from wall
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)
                .strafeToSplineHeading(new Vector2d(0, -23 ), Math.toRadians(45))
                .build()
        );

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(redBot)
                .addEntity(shortRunBot)
                .addEntity(midRunBot)
                .start();
    }
}