package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    final static private int HOME_X = 56;
    final static private int HOME_Y = 10;
    final static private int HOME_ANGLE = 45;

    final static private Pose2d farShootingPos = new Pose2d(HOME_X, HOME_Y, Math.toRadians(-60));
    final static private Pose2d midShootingPos = new Pose2d(0, 0, Math.toRadians(45));
    final static private Pose2d nearShootingPos = new Pose2d(-25, 20, Math.toRadians(45));
    final static private Pose2d loadingPos = new Pose2d( 56, 55, Math.toRadians(45));

    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 16)
                .build();

        // The X/Y is the coordinate on the mapped field for DECODE. linToX/Y drives a straight line to
        // that coordinate
        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(HOME_X, HOME_Y, Math.toRadians(HOME_ANGLE)))
                // add action to shoot initially loaded balls from far position
                .splineTo(new Vector2d(36, 40), Math.toRadians(90))
                .splineTo(midShootingPos.position, midShootingPos.heading.real)
                // add action to shoot balls in color order
                .splineTo(new Vector2d(10, 40), Math.toRadians(90))
                .splineTo(midShootingPos.position, midShootingPos.heading.real)
                // add action to shoot balls in color order
                .splineTo(new Vector2d(-15, 40), Math.toRadians(90))
                .splineTo(nearShootingPos.position, nearShootingPos.heading.real)
                // add action to shoot balls in color order
                .splineTo(loadingPos.position, loadingPos.heading.real)
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}