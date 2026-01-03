package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

public class Revolver {

    List<Double> revolverPositions = new ArrayList<>(6);


    //revolver
    private ServoImplEx revolverDrive = null;
    private int currentIndex = 1;

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

        revolverPositions.add(0.43);  //Index 0  X
        revolverPositions.add(0.4515);//Index 1  X
        revolverPositions.add(0.4725);//Index 2  X
        revolverPositions.add(0.494); //Index 3  X
        revolverPositions.add(0.515);  //Index 4
        revolverPositions.add(0.538);  //Index 5

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
    public void stepUp(){
        if (currentIndex == 5 ){
            currentIndex = 0;
        }
        else {
            currentIndex++;
        }
        revolverDrive.setPosition(revolverPositions.get(currentIndex));
    }
    
    /**
     * Reverses the revolver to the previous position
     */
    public void stepDown(){
        if (currentIndex == 0 ){
            currentIndex = 5;
        }
        else {
            currentIndex--;
        }
        revolverDrive.setPosition(revolverPositions.get(currentIndex));
    }

    /**
     * Performs the actions for Revolver sorting mechanism
     */
    public void run(){
        tm.addData("Sorter (D-Up/D-Down): ", String.format("%d(%.6f)", getIndex(), revolverDrive.getPosition()));
    }
}

