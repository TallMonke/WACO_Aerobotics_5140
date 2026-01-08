package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.mechanisms.DetectedColor;

import java.util.ArrayList;
import java.util.List;

public class Revolver {
    Telemetry tm;
    private BallColorSensor colorSensor = null;

    // Maps the positions of the Servo to the index of the revolver. Even indexes are loading positions
    // Odd indexes are firing positions
    List<Double> revolverPositions = new ArrayList<>(6);

    // Balls currently loaded into the revolver
    List<DetectedColor> currentLoad = new ArrayList<>();

    //revolver
    private ServoImplEx revolverDrive = null;
    private int currentIndex = 1;

    //variables to eliminate double button presses
    boolean buttonWasPressedUp = false;
    int motorModeUp = 0;
    boolean buttonWasPressedDown = false;
    int motorModeDown = 0;

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
        revolverPositions.add(0.43);  //Index 0  X
        revolverPositions.add(0.4515);//Index 1  X
        revolverPositions.add(0.4725);//Index 2  X
        revolverPositions.add(0.494); //Index 3  X
        revolverPositions.add(0.515);  //Index 4
        revolverPositions.add(0.538);  //Index 5

        // Init each revolver position with an UNKNOWN color
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);
        currentLoad.add(DetectedColor.UNKNOWN);


        //revolver servo
        revolverDrive = hardwareMap.get(ServoImplEx.class, "revolverServo");
        revolverDrive.setDirection(Servo.Direction.FORWARD);
        setIndex(3);
    }

    public int getIndex(){
        return currentIndex;
    }

    /**
     * Moves the revolver to position
     *
     * @param index Position of the revolver
     */
    public void setIndex(int index ) {
        if (index >= 0 && index <= 5){
            currentIndex = index;
            revolverDrive.setPosition(revolverPositions.get(currentIndex));
        }
    }

    /**
     * Increases the revolver to the next position
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

            // Move the servo to the new position
            revolverDrive.setPosition(revolverPositions.get(currentIndex));
        }

        // Update debounce tracker
        buttonWasPressedUp = buttonIsPressed;
    }
    
    /**
     * Reverses the revolver to the previous position
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

            // Move the servo to the new position
            revolverDrive.setPosition(revolverPositions.get(currentIndex));
        }

        // Update debounce tracker
        buttonWasPressedDown = buttonIsPressed;
    }

    public void displayTelemetry(){
        if(tm != null){
            tm.addData("Sorter (D-Up/D-Down): ", String.format("%d(%.6f)", getIndex(), revolverDrive.getPosition()));
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
                revolverDrive.setPosition(revolverPositions.get(currentIndex));
            }
        }
    }
    /**
     * Performs the actions for Revolver sorting mechanism
     */
    public void run(){
        int currentIndex = getIndex();

        // colorSensor only reports a color based on the distance to the object
        DetectedColor detectedColor = colorSensor.getColor();

        currentLoad.set(currentIndex, detectedColor);
        tm.addLine( String.format("%d) Color: %s", currentIndex, detectedColor.toString() ) );

        displayTelemetry();
    }
}

