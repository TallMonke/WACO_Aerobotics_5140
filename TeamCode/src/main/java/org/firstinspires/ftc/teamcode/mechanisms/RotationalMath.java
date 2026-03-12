package org.firstinspires.ftc.teamcode.mechanisms;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class RotationalMath {

    /**
     * normalize a degree between -2pi / +2pi
     */
    private static final double TWO_PI = 2 * Math.PI;
    private static double normalize(double theta) {
        double normalized = theta % TWO_PI;
        normalized = (normalized + TWO_PI) % TWO_PI;
        return normalized <= Math.PI ? normalized : normalized - TWO_PI;
    }

    /**
     * Takes in the position of robot and position of target(AprilTag)
     *
     * returns the x-component of the shooter to the position as well as the heading the robot needs to move to point towards the target position
     */
    public static Pose2d get_XYH_FromPose(Pose2d robotPose, Vector2d pose) {
        double shooterOffsetX = 5.5;    // distance from the center of robot to launcher

        double rx = robotPose.position.x;           //robot x_position
        double ry = robotPose.position.y;           //robot y_position
        double rh = robotPose.heading.toDouble();   //robot heading - h (Angle of robot)

        double tx = pose.x;                  // target X_position
        double ty = pose.y;                  // target y_position

        double targetHeading = Math.atan2(ty - ry,tx - rx);
        double headingError = normalize(targetHeading - rh);                              // angle away from meeting target
        double distance = Math.hypot(tx - rx, ty - ry);                                         // distance from the launcher of the robot to the april tag X- component
        return new Pose2d(new Vector2d(distance-shooterOffsetX,0.0), headingError);         // return distance of robot from april tag { (x,y), (Heading) }

            //call function passing in the robot and blue tower position
        //get_XYH_FromAprilTag(new Pose2d(odo.getPosition().getX(DistanceUnit.INCH), odo.getPosition().getY(DistanceUnit.INCH), odo.getPosition().getHeading(AngleUnit.RADIANS)), new Vector2d(Constants.blue_tower_pos.x,Constants.blue_tower_pos.y));

    }


    /**
     * @param distance of shooter to April tag into x-component distance robot to basket.
     */
    public static double x_DistanceCamera(double distance){                                 //Measurements in inches
        /// --- CAMERA BASED TRIANGULATION ---
        double launchHeight = 14.0;                                                       //distance of point of launch of the robot from the ground, y-component
        double x_DistanceCamera = 6.0;                                                    //distance (x-component) the camera is away from launch point.
        double y_DistanceCamera = 1.5;                                                    //distance (y-component) the camera is away from launch point.
        double aprilFromTop = 9.25;                                                       //distance from center of april tag to top of the basket front wall.
        double aprilTagWallHeight = 38.75;                                                //total height of the basket front wall with the location april tags on it.
        double Y = aprilTagWallHeight-y_DistanceCamera-launchHeight-aprilFromTop;         //Y-component distance from the camera to the center of the april tag. ( = 14)
        double X = (Math.sqrt(Math.pow(distance,2)-(Math.pow(Y,2)))-x_DistanceCamera);    // Put it all together
        return X;                                                                         // return x-component
    }

    /**
     * Calculate the RPM needed based on the @param x_distance of launcher to basket. (x component)
     */
    public static double getRPM(double distance) {
        //robot
        double angleInDegrees = 45.0;                               //launch angle in degrees
        double angleInRadians = Math.toRadians(angleInDegrees);
        double height = 29.0;                                       //height the ball needs to be off the ground to make basket based from the launcher of robot. 41-13.75
        double tuneCorrection = 215;                                //Value you set to eliminate discrepancies.
        double minimalLaunchDistance = 58;                          //Derivative of equation set to 0. Gives us the closest we can get to the basket without the need of changing the Launch angle.
        double RPM_at_minimalDistance = 810.45;                     //plug the minimalLaunchDistance into the formula and this is that RPM

        //parts of equation.
        double term1 = (60.0 / (4 * Math.PI));
        double numerator = (386.09 * Math.pow(distance, 2)); // 386.09(D^2)
        double cosineSquared = Math.pow((Math.cos(angleInRadians)), 2);
        double denominator = 2 * cosineSquared * ((distance * (Math.tan(angleInRadians))) - height);

        //Total formula
        double RPM = (term1 * (Math.sqrt(numerator / denominator)))- tuneCorrection; //total formula to get RPM from x-component distance - a little correction due to wheels changing velocity when they apply pressure on the ball.

        if (distance <= minimalLaunchDistance){
            return RPM_at_minimalDistance;
        }
        else {
            return RPM;
        }
    }

    public static Pose2D pose2dToPose2D(Pose2d pos) {
        return new Pose2D(DistanceUnit.INCH, pos.position.x, pos.position.y, AngleUnit.RADIANS, pos.heading.toDouble());
    }

    public static Pose2d pose2DToPose2d(Pose2D pos) {
        return new Pose2d(pos.getX(DistanceUnit.INCH), pos.getY(DistanceUnit.INCH), pos.getHeading(AngleUnit.RADIANS));
    }
}
