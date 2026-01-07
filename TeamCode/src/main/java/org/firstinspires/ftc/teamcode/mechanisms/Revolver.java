package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Revolver {
    Telemetry tm;
    private BallColorSensor colorSensor = null;

    // Maps the positions of the Servo to the index of the revolver. Even indexes are loading positions
    // Odd indexes are firing positions
    List<Double> revolverPositions = new ArrayList<>(6);

    // Balls currently loaded into the revolver
    List<BallColorSensor.DetectedColor> currentLoad = new ArrayList<>();

    //revolver
    private ServoImplEx revolverDrive = null;
    private int currentIndex = 1;

    //variables to eliminate double button sences
    boolean buttonWasPressedUp = false;
    int motorModeUp = 0;
    boolean buttonWasPressedDown = false;
    int motorModeDown = 0;


    // Step size for the 6 position revolver to hit each spot
    double revolverStep = 0.036;
    Telemetry tm;

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
        currentLoad.add(BallColorSensor.DetectedColor.UNKNOWN);
        currentLoad.add(BallColorSensor.DetectedColor.UNKNOWN);
        currentLoad.add(BallColorSensor.DetectedColor.UNKNOWN);
        currentLoad.add(BallColorSensor.DetectedColor.UNKNOWN);
        currentLoad.add(BallColorSensor.DetectedColor.UNKNOWN);
        currentLoad.add(BallColorSensor.DetectedColor.UNKNOWN);


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
    public void seekToColor(BallColorSensor.DetectedColor color){
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
        BallColorSensor.DetectedColor detectedColor = colorSensor.getColor();

        int currentIndex = getIndex();

        // Test the object distance to the color sensor and if something is close (<2) and a valid
        // color, store it for the current index
        if( colorSensor.getDistance() < 2.0 && detectedColor == BallColorSensor.DetectedColor.PURPLE ){
            currentLoad.set(currentIndex, BallColorSensor.DetectedColor.PURPLE);
            tm.addLine( String.format("%d) Color: %s", currentIndex, "PURPLE" ) );
        } else if(colorSensor.getDistance() < 2.0 && detectedColor == BallColorSensor.DetectedColor.GREEN) {
            currentLoad.set(currentIndex, BallColorSensor.DetectedColor.GREEN);
            tm.addLine( String.format("%d) Color: %s", currentIndex, "GREEN" ) );
        } else if(colorSensor.getDistance() >= 3.0 ) {
            // Nothing close enough to detect at this current index
            currentLoad.set(currentIndex, BallColorSensor.DetectedColor.UNKNOWN);
            tm.addLine( String.format("%d) Color: %s", currentIndex, "NONE" ) );
        }

        displayTelemetry();
    }
}

