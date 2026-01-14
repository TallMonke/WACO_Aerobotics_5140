package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    final static private int HOME_X = 61;
    final static private int HOME_Y = -10;
    final static private int HOME_ANGLE = -45;

    // Red Team setup
    final static private Pose2d farShootingPos = new Pose2d(61, -10, Math.toRadians(-60));
    final static private Pose2d midShootingPos = new Pose2d(0, 0, Math.toRadians(45));
    final static private Pose2d nearShootingPos = new Pose2d(-15, -13.2, Math.toRadians(45));
    final static private Pose2d loadingPos = new Pose2d( 56, -56, Math.toRadians(45));

    static private final Pose2d firstLinePos = new Pose2d(36, -42, Math.toRadians(-90));
    static private final Pose2d secondLinePos = new Pose2d(11.6, -42, Math.toRadians(-90));
    static private final Pose2d thirdLinePos = new Pose2d(-12, -42, Math.toRadians(-90));

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(90, 75, Math.toRadians(180), Math.toRadians(180), 16)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(HOME_X, HOME_Y, Math.toRadians(HOME_ANGLE)))
                .lineToX(56) // Pull away from wall
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)
                // Turn on sweeper, set revolver to loading position
                // ***FIRST LINE OF BALLS
                .splineTo(new Vector2d(firstLinePos.component1().x, firstLinePos.component1().y), Math.toRadians(-90)) // ingest first ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(firstLinePos.component1().y - 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(firstLinePos.component1().y - 10) // ingest 2nd ball
                .splineTo(midShootingPos.position, midShootingPos.heading.real)
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                // ***SECOND LINE OF BALLS
                .splineTo(new Vector2d(secondLinePos.component1().x, secondLinePos.component1().y), Math.toRadians(-90))
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(secondLinePos.component1().y-5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(secondLinePos.component1().y-10) // ingest 2nd ball
                .splineTo(midShootingPos.position, midShootingPos.heading.real)
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                //***THIRD LINE OF BALLS
                .splineTo(new Vector2d(thirdLinePos.component1().x, thirdLinePos.component1().y), Math.toRadians(-90))
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(thirdLinePos.component1().y - 5) // ingest 2nd ball
                // revolver to next loading position
                .waitSeconds(1)
                .lineToY(thirdLinePos.component1().y - 10) // ingest 2nd ball
                .splineTo(nearShootingPos.position, nearShootingPos.heading.real)
                // Auto search for target QR
                // Fire 3 into target
                .waitSeconds(3)

                // add action to shoot balls in color order
                .splineTo(loadingPos.position, loadingPos.heading.real)
                .build()
        );

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}