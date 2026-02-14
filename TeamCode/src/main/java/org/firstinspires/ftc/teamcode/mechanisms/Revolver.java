package org.firstinspires.ftc.teamcode.mechanisms;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

public class Revolver {
    Telemetry tm;
    private BallColorSensor colorSensor = null;

    // Maps the positions of the Servo to the index of the revolver. Even indexes are loading positions
    // Odd indexes are firing positions
    List<Double> revolverPositions = new ArrayList<>();

    // Balls currently loaded into the revolver
    List<DetectedColor> currentLoad = new ArrayList<>();

    //revolver
    private DcMotorEx revolverDrive = null;
    private int currentIndex = 1;
    private final int oneRevolutionTicks = 448;
    private final double MAX_POWER = 1.0;


    //variables to eliminate double button presses
    boolean buttonWasPressedUp = false;
    int motorModeUp = 0;
    boolean buttonWasPressedDown = false;
    int motorModeDown = 0;

    //Initialized position
    int currentPosition = 1;

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

        // Odd indexes are "firing" positions, even are load
        revolverPositions.add(0.43);    //Index 0
        revolverPositions.add(0.4525);  //Index 1
        revolverPositions.add(0.4725);  //Index 2
        revolverPositions.add(0.494);   //Index 3
        revolverPositions.add(0.515);   //Index 4
        revolverPositions.add(0.54);    //Index 5

        // Init each revolver position with an UNKNOWN color
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);


        //revolver servo
        revolverDrive = hardwareMap.get(DcMotorEx.class, "revolverDrive");
        revolverDrive.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE); // Resists motion when no power applied
        revolverDrive.setDirection(DcMotorSimple.Direction.FORWARD);

    }

    /**
     * Spins the revolver the given number of revolutions. One rotation = one section of the
     * revolver
     *
     * @param rotations Number of times the revolver should spin
     */
    private void spin(int rotations){
        currentPosition = revolverDrive.getCurrentPosition();
        revolverDrive.setTargetPosition(currentPosition + (oneRevolutionTicks * rotations));
        revolverDrive.setPower(MAX_POWER);
        revolverDrive.setPower(0.0);
    }

    /**
     * Increases the revolver to the next position. Sleeps for 500ms to allow time for the revolver
     * to actually complete the operation
     */
    public void stepUp(boolean buttonIsPressed){
        if (buttonIsPressed && !buttonWasPressedUp) {

            // Cycle through 0 → 1 → 2 → 0
            if (motorModeUp == 0) {
                motorModeUp = 1;
            } else if (motorModeUp == 1) {
                motorModeUp = 2;
            } else {
                motorModeUp = 0;
            }

            // cycle revolver index up
            if (currentIndex == 5) {
                currentIndex = 0;
            } else {
                currentIndex++;
            }

            spin(1);
        }

        // Update debounce tracker
        buttonWasPressedUp = buttonIsPressed;
    }

    /**
     * Turns the ball revolver to the next position.
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */

    /**
     * Turns the ball revolver to the next loading position. Even indexes are "loading" positions.
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public void stepToLoad(boolean buttonIsPressed){
        if (buttonIsPressed && !buttonWasPressedUp) {

            // Cycle through 0 → 1 → 2 → 0
            if (motorModeUp == 0) {
                motorModeUp = 1;
            } else if (motorModeUp == 1) {
                motorModeUp = 2;
            } else {
                motorModeUp = 0;
            }

            // Move the servo to the new position
            currentIndex = selectNextOddIndex();
        }

        // Update debounce tracker
        buttonWasPressedUp = buttonIsPressed;
    }

    /**
     * Turns the ball revolver to the next loading position. Even indexes are "loading" positions.
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public Action stepToLoadAction(){

        return new Action() {
            //ElapsedTime timer = null;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                //if (timer == null) {
                //    timer = new ElapsedTime();

                    currentIndex = selectNextOddIndex();

                    packet.put("revolver_position", revolverPositions.get(currentIndex));
                //}

                //return (timer.seconds() < 2.5);
                return false;
            }
        };
    }

    /**
     * Reverses the revolver to the previous position. This will sleep for 500ms to allow time
     * for the revolver to actually complete the operation
     */
    public void stepDown(boolean buttonIsPressed){
        if (buttonIsPressed && !buttonWasPressedDown) {

            // Cycle through 0 → 1 → 2 → 0
            if (motorModeDown == 0) {
                motorModeDown = 1;
            } else if (motorModeDown == 1) {
                motorModeDown = 2;
            } else {
                motorModeDown = 0;
            }

            // cycle revolver index down
            if (currentIndex == 0) {
                currentIndex = 5;
            } else {
                currentIndex--;
            }

            revolverDrive.setDirection(DcMotorSimple.Direction.REVERSE);

            spin(1);

            revolverDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        }

        // Update debounce tracker
        buttonWasPressedDown = buttonIsPressed;
    }

    /**
     * Turns the ball revolver to the next position.
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public Action stepDownAction(){
        return new Action() {
            //ElapsedTime timer = null;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                //if (timer == null) {
                //    timer = new ElapsedTime();

                    // cycle revolver index down
                    if (currentIndex == 0) {
                        currentIndex = 5;
                    } else {
                        currentIndex--;
                    }

                    stepDown(true);
                    packet.put("revolver_position", revolverPositions.get(currentIndex));
                //}

                //return (timer.seconds() < 2.5);
                return false;
            }
        };
    }
    public void stepToFire(boolean buttonIsPressed){
        if (buttonIsPressed && !buttonWasPressedDown) {

            // Cycle through 0 → 1 → 2 → 0
            if (motorModeDown == 0) {
                motorModeDown = 1;
            } else if (motorModeDown == 1) {
                motorModeDown = 2;
            } else {
                motorModeDown = 0;
            }

            currentIndex = selectNextEvenIndex();
        }

        // Update debounce tracker
        buttonWasPressedDown = buttonIsPressed;
    }
    /**
     * Turns the ball revolver to the next firing position. Odd indexes are "firing" positions.
     * @return RoadRunner Action to be used in the Autonomous OpModes
     */
    public Action stepToFireAction(){
        return new Action() {
            //ElapsedTime timer = null;

            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                //if (timer == null) {
                //   timer = new ElapsedTime();

                    currentIndex = selectNextEvenIndex();

                    // Move the servo to the new position
      //              revolverDrive.setPosition(revolverPositions.get(currentIndex));
                    packet.put("revolver_position", revolverPositions.get(currentIndex));
                //}

                //return (timer.seconds() < 2.5);
                return false;
            }
        };
    }

    public void displayTelemetry(){
        if(tm != null){
            tm.addData("Sorter (D-Up/D-Down): ", String.format("%.6f", revolverDrive.getCurrentPosition()));
        }
    }

    /**
     * Searches the revolver for the given color and move the current position of the revolver to
     * that index if found. If the color is not found, the current position is not changed.
     *
     * @param color Color to search for (PURPLE, GREEN)
     */
    public void seekToColor(DetectedColor color){
        for(int i = 0; i < currentLoad.size(); i++) {
            if (currentLoad.get(i) == color) {
  //              revolverDrive.setPosition(revolverPositions.get(currentIndex));
            }
        }
    }

    /**
     * Selects the next odd index in the revolver.
     * @return Next index that is odd
     */
    private int selectNextOddIndex() {
        int nextIndex = currentIndex;

        do {
            nextIndex++;
            if (nextIndex > 5) {
                nextIndex = 0;
            }
        } while (nextIndex % 2 != 0);

        return nextIndex;
    }

    /**
     * Selects the next even index in the revolver.
     * @return Next index that is even
     */
    private int selectNextEvenIndex() {
        int nextIndex = currentIndex;
        do {
            nextIndex++;
            if (nextIndex > 5) {
                nextIndex = 0;
            }
        } while (nextIndex % 2 == 0);

        return nextIndex;
    }

    /**
     * Performs the actions for Revolver sorting mechanism
     */
    public void run(){
        int currentIndex = 1;
        int setIndex = -1;

        // colorSensor only reports a color based on the distance to the object
        DetectedColor detectedColor = colorSensor.getColor();

        // The "currentIndex" is the position at the top of the revolver
        // while color detection is at the bottom, need opposite index to place the color into
        //  \ 3 /
        //  1 | 5
        // Even values are loading positions, odd are firing positions
        if( currentIndex == 2) {
            setIndex = 5;
        }
        else if( currentIndex == 4) {
            setIndex = 1;
        }
        else if( currentIndex == 6) {
            setIndex = 3;
        }

        if( setIndex != -1 ) {
            currentLoad.set(setIndex, detectedColor);
            tm.addLine(String.format("%d) Color: %s", setIndex, detectedColor.toString()));
        }

        displayTelemetry();
    }
}

