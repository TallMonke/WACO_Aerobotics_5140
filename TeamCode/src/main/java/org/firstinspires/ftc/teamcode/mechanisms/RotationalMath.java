package org.firstinspires.ftc.teamcode.mechanisms;

public class RotationalMath {
    /**
     * @param distance of shooter to april tag into x-component distance robot to basket.
     */
    public static double x_Distance(double distance){
        double launchHeight = 14.0; //distance of point of launch of the robot from the ground, y-component
        double x_DistanceCamera = 7.0; //distance (x-component) the camera is away from launch point.
        double y_DistanceCamera = 1.5; //distance (y-component) the camera is away from launch point.
        double aprilFromTop = 9.25; //distance from center of april tag to top of the basket front wall.
        double aprilTagWallHeight = 38.75; //total height of the basket front wall with the location april tags on it.
        double Y = aprilTagWallHeight-y_DistanceCamera-launchHeight-aprilFromTop; //Y-component distance from the camera to the center of the april tag. ( = 14)
        double X = (Math.sqrt(Math.pow(distance,2)-(Math.pow(Y,2)))-x_DistanceCamera);
        return X;
    }

    /**
     * Calculate the RPM needed based on the @param x_distance of launcher to basket. (x component)
     */
    public static double getRPM(double distance) {
       //robot
        double angleInDegrees = 45.0; //launch angle in degrees
        double angleInRadians = Math.toRadians(angleInDegrees);
        double height = 29.0;  //height the ball needs to be off the ground to make basket based from the launcher of robot. 41-13.75
        double tuneCorrection = 220; //Value you set to eliminate discreptincies.
        double minimalLaunchDistance = 58; //Derivative of equation set to 0. Gives us the closest we can get to the basket without the need of changing the Launch angle.
        double RPM_at_minimalDiatance = 810.45; //plug the minimalLaunchDistance into the formula and this is that RPM
        //parts of equation.
        double term1 = (60.0 / (4 * Math.PI));
        double numerator = (386.09 * Math.pow(distance, 2)); // 386.09(D^2)
        double cosineSquared = Math.pow((Math.cos(angleInRadians)), 2);
        double denominator = 2 * cosineSquared * ((distance * (Math.tan(angleInRadians))) - height);
        //Total formula
        double RPM = (term1 * (Math.sqrt(numerator / denominator)))- tuneCorrection; //total formula to get RPM from x-component distance - a little correction due to wheels changing velocity when they apply pressure on the ball.

        if (distance <= minimalLaunchDistance){
            return RPM_at_minimalDiatance;
        }
        else {
            return RPM;
        }
    }
}
