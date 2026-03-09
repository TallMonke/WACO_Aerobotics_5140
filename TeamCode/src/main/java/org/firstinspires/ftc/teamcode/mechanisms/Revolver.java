package org.firstinspires.ftc.teamcode.mechanisms;

import static java.lang.Thread.activeCount;
import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

public class Revolver {
    Telemetry tm;
    private BallColorSensor colorSensor = null;
    List<DetectedColor> currentLoad = new ArrayList<>();

    //revolver
    private DcMotorEx genevaDrive = null;       //initialization of the motor we are moving
    private ColorSensor genevaColor = null;
    private int ticksPerRev = 448;              //how many ticks of the motor it takes to go one revolution *motor ticks multiplied by gearbox*
    private double maxMotorPower = 1.0;         //The power the motor runs at.
    private double proportionalGain = 0.0013;    //rate motor will accelerate and decelerate to reach a more accurate revolution. * Overshoot: decrease / Undershoot: increase *
    private int tolerance = 1;                  //how far from exact target pos is acceptable to say "we made it"
    private int startDecel = 165;               //when to start the deceleration *value is a percent of the way though the total distance: start pos - target pos (smaller = closer to start : bigger = closer to target)
    private ElapsedTime timer;
    private int targetPosition;
    private int startPosition;
    private int revolutions = 1;                                  //How many revolution you want the motor to do. *can be fraction like 1.25 or 4.5* (needs to be float because converting double to int is to hard for the computer. float to int is much easier.
    private boolean isPressed = false;

    private boolean moving = false;

    private boolean buttonWasPressedUp = false;
    private boolean buttonWasPressedDown = false;

    /**
     * Initializes the Revolver sorting mechanism
     *
     * @param hardwareMap Initialized hardware map from the Op Mode
     * @param telemetry Telemetry object from the Op Mode
     */
    public Revolver(HardwareMap hardwareMap, Telemetry telemetry){
        if(telemetry == null) {
            return;
        }

        if(hardwareMap == null) {
            telemetry.addData("Error", "Hardware map is null");
            return;
        }
        tm = telemetry;

        // ColorSensor Init
        colorSensor = new BallColorSensor(hardwareMap, telemetry);

        /* Odd indexes are "firing" positions, even are load
        revolverPositions.add(0.43);    //Index 0
        revolverPositions.add(0.4525);  //Index 1
        revolverPositions.add(0.4725);  //Index 2
        revolverPositions.add(0.494);   //Index 3
        revolverPositions.add(0.515);   //Index 4
        revolverPositions.add(0.54);    //Index 5        
         */

        // Init each revolver position with an UNKNOWN color
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);


        //revolver servo
        genevaDrive = hardwareMap.get(DcMotorEx.class, "genevaDrive");
        genevaDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);            //reset the encoder values upon initialization
        genevaDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);                 //runs the motor using the encoder wire
        genevaDrive.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);    //When the motor stops it will use a break so is does not drift

        genevaColor = hardwareMap.get(ColorSensor.class,"genevaColor");
    }

    /**
     * Spins the revolver one segment of the drive. Passing true starts the motion and only returns false
     * one the operation is complete.
     */
    public void spin() {
        if (moving) {                                                                       //if a trigger was pushed
            int currentPosition = genevaDrive.getCurrentPosition();                         //the current motor pos
            int targetDistance = targetPosition - currentPosition;                          //how far is left to go till target pos

            int totalDistance = Math.abs(targetPosition - startPosition);
            int distanceTraveled = Math.abs(currentPosition - startPosition);
            int slowZone = totalDistance - startDecel;                                      //when to start the deceleration based of the target position *value is a set tick amount back from total distance

            double power;                                                                   //set the power to max when current position is less than the slowZone


            if (distanceTraveled < slowZone) {                                              // DECELERATION ZONE           *if we are not at Max Power Zone we are in deceleration zone
                power = maxMotorPower * Math.signum(targetDistance);                        //apply gain to how far we have to go
            }
            else {
                double powerMag = Math.abs(targetDistance) * proportionalGain;
                powerMag = Range.clip(powerMag, 0, maxMotorPower);                     //make sure power does not exceed max set power
                power = powerMag * Math.signum(targetDistance);
            }

            if (timer.seconds() > 2.0){
                genevaDrive.setPower(power * -1.0);
                try {
                    sleep(150);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                timer.reset();
            }

            tm.addData("Revolver Power", power);
            tm.addData("Geneva Color", genevaColor);
            tm.update();

            genevaDrive.setPower(power);                                                    //set power each part calculates to motor

            if (Math.abs(targetDistance) <= tolerance && power < 0.01) {                                    //how far from exact target pos is acceptable to say "we made it"
                genevaDrive.setPower(0);
                moving = false;                                                             //set moving back to false so we can do it again
            }
        }
    }

    public double getMotorRevs() {                                                         //how many revolution the motor is away from when it was initialized
        return genevaDrive.getCurrentPosition() / ticksPerRev;
    }


    /**
     * Increases the revolver to the next position. Sleeps for 500ms to allow time for the revolver
     * to actually complete the operation
     */
    public void stepUp(boolean buttonIsPressed) {
        if (buttonIsPressed && !moving) {
            timer = new ElapsedTime();
            moving = true;
            targetPosition = calculateTargetPos(1);

            tm.addData("Revolver Step Up", "Pressed");
            tm.update();
        }
    }

    /**
     * Turns the ball revolver to the next position.
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public Action stepUpAction(){
        return new Action() {
            ElapsedTime elapsedTime = null;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (elapsedTime == null) {
                    elapsedTime = new ElapsedTime();
                    stepUp(true);
                }
                return (elapsedTime.seconds() < 2.5);
            }
        };
    }

    /**
     * Turns the ball revolver to the next loading position. Even indexes are "loading" positions.
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public void stepToLoad(boolean buttonIsPressed){
        if (buttonIsPressed && !moving){
            if (getMotorRevs() % 2 == 0){
                timer = new ElapsedTime();
                moving = true;
                targetPosition = calculateTargetPos(-1);
            }
            else if (getMotorRevs() % 2 != 0){
                timer = new ElapsedTime();
                moving = true;
                targetPosition = calculateTargetPos(-2);
            }
        }
    }

    /**
     * Turns the ball revolver to the next loading position. Even indexes are "loading" positions.
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public Action stepToLoadAction(){
        return new Action() {
            ElapsedTime elapsedTime = null;
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (elapsedTime == null) {
                    elapsedTime = new ElapsedTime();
                    stepToLoad(true);
                }
                return (elapsedTime.seconds() < 2.5);
            }
        };
    }

    /**
     * Reverses the revolver to the previous position. This will sleep for 500ms to allow time
     * for the revolver to actually complete the operation
     */
    public void stepDown(boolean buttonIsPressed) {
        if (buttonIsPressed && !moving) {
            timer = new ElapsedTime();
            moving = true;
            targetPosition = calculateTargetPos(-1);

            tm.addData("Revolver Step Down", "Pressed");
            tm.update();
        }
    }

    /**
     * Turns the ball revolver to the next position.
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public Action stepDownAction(){
        return new Action() {
            ElapsedTime elapsedTime = null;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (elapsedTime == null) {
                    elapsedTime = new ElapsedTime();
                    stepDown(true);
                }
                return (elapsedTime.seconds() < 2.5);
            }
        };
    }

    public void stepToFire(boolean buttonIsPressed){
        if (buttonIsPressed && !moving){
            if (getMotorRevs() % 2 == 0){
                timer = new ElapsedTime();
                moving = true;
                targetPosition = calculateTargetPos(-2);
            }
            else if (getMotorRevs() % 2 != 0){
                timer = new ElapsedTime();
                moving = true;
                targetPosition = calculateTargetPos(-1);
            }
        }
    }
     /**
     * Turns the ball revolver to the next firing position. Odd indexes are "firing" positions.
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public Action stepToFireAction(){
        return new Action() {
            boolean init = false;
            int targetPosition;
            int startPosition;
            double power;
            ElapsedTime timer = new ElapsedTime();

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {

                /// --- INITIALIZATION ---
                if (!init) {
                    startPosition = genevaDrive.getCurrentPosition();
                    if (getMotorRevs() % 2 == 0) {
                        targetPosition = calculateTargetPos(-2);
                    } else if (getMotorRevs() % 2 != 0) {
                        targetPosition = calculateTargetPos(-1);
                    }
                    timer.reset();
                    init = true;
                }

                /// --- CONTROL LOOP ---
                int currentPosition = genevaDrive.getCurrentPosition();                         //the current motor pos
                int targetDistance = targetPosition - currentPosition;                          //how far is left to go till target pos

                int totalDistance = Math.abs(targetPosition - startPosition);
                int distanceTraveled = Math.abs(currentPosition - startPosition);
                int slowZone = totalDistance - startDecel;                                      //when to start the deceleration based of the target position *value is a set tick amount back from total distance

                /// Compute power
                if (distanceTraveled < slowZone) {                                              // DECELERATION ZONE           *if we are not at Max Power Zone we are in deceleration zone
                    power = maxMotorPower * Math.signum(targetDistance);                        //apply gain to how far we have to go
                } else {
                    double powerMag = Math.abs(targetDistance) * proportionalGain;
                    powerMag = Range.clip(powerMag, 0, maxMotorPower);                     //make sure power does not exceed max set power
                    power = powerMag * Math.signum(targetDistance);
                }

                /// Safety pulse every 2 seconds
                if (timer.seconds() > 2.0) {
                    genevaDrive.setPower(power * -1.0);
                    try {
                        sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    timer.reset();
                }

                genevaDrive.setPower(power);                                                    //set power each part calculates to motor

                // Telemetry
                tm.addData("Target Position", targetPosition);
                tm.addData("Revolver Power", genevaDrive.getPower());
                tm.addData("Target Distance", Math.abs(targetDistance));
                tm.addData("Tolerence", tolerance);
                tm.update();

            /// --- Exit Condition
                boolean done = Math.abs(targetDistance) <= tolerance && power < 0.01;    //how far from exact target pos is acceptable to say "we made it"
                if (done) {
                    genevaDrive.setPower(0);
                }
                    return !done;
            }
        };
    }

    public void displayTelemetry(){
        if(tm != null){
            tm.addData("Sorter (D-pad): ", "Up / Down");
            tm.addData("Sorter (A / B)","Fire / Load");
            tm.addData("Indexer Revolution", getMotorRevs());
        }
    }
//
//    /**
//     * Searches the revolver for the given color and move the current position of the revolver to
//     * that index if found. If the color is not found, the current position is not changed.
//     *
//     * @param color Color to search for (PURPLE, GREEN)
//     */
//    public void seekToColor(DetectedColor color){
//        /*
//        for(int i = 0; i < currentLoad.size(); i++) {
//            if (currentLoad.get(i) == color) {
//  //              genevaDrive.setPosition(revolverPositions.get(currentIndex));
//            }
//        }
//         */
//    }
//
//    /**
//     * Selects the next odd index in the revolver.
//     * @return Next index that is odd
//     */
//    private int selectNextOddIndex() {
//        /*
//        int nextIndex = currentIndex;
//
//        do {
//            nextIndex++;
//            if (nextIndex > 5) {
//                nextIndex = 0;
//            }
//        } while (nextIndex % 2 != 0);
//
//        return nextIndex;
//         */
//    }
//
//    /**
//     * Selects the next even index in the revolver.
//     * @return Next index that is even
//     */
//    private int selectNextEvenIndex() {
//        /*
//        int nextIndex = currentIndex;
//        do {
//            nextIndex++;
//            if (nextIndex > 5) {
//                nextIndex = 0;
//            }
//        } while (nextIndex % 2 == 0);
//
//        return nextIndex;
//         */
//    }
//
//    /**
//     * Performs the actions for Revolver sorting mechanism
//     */
//    public void run(){
//        /*
//        int currentIndex = 1;
//        int setIndex = -1;
//
//        // colorSensor only reports a color based on the distance to the object
//        DetectedColor detectedColor = colorSensor.getColor();
//
//        // The "currentIndex" is the position at the top of the revolver
//        // while color detection is at the bottom, need opposite index to place the color into
//        //  \ 3 /
//        //  1 | 5
//        // Even values are loading positions, odd are firing positions
//        if( currentIndex == 2) {
//            setIndex = 5;
//        }
//        else if( currentIndex == 4) {
//            setIndex = 1;
//        }
//        else if( currentIndex == 6) {
//            setIndex = 3;
//        }
//
//        if( setIndex != -1 ) {
//            currentLoad.set(setIndex, detectedColor);
//            tm.addLine(String.format("%d) Color: %s", setIndex, detectedColor.toString()));
//        }
//
//        displayTelemetry();
//         */
//    }
    private int calculateTargetPos(int revolutions){
        int position = genevaDrive.getCurrentPosition() + (ticksPerRev * revolutions);
        return position;
    }
}

