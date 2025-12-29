package org.firstinspires.ftc.teamcode.mechanisms;

import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * AprilTag detection object using a webcam. Retrieves the detected tag IDs or searches for
 * a specific tag ID.
 */
public class AprilTagWebcam {
    /**
     * AprilTag recognition and pose estimation using FTC generated code
     */
    private AprilTagProcessor aprilTagProcessor;

    /**
     * OpenCV based camera processing using FTC generated code
     */
    private VisionPortal visionPortal;

    /**
     * List of AprilTag detections
     */
    private List<AprilTagDetection> detectedTags = new ArrayList<>();

    /**
     * Telemetry object for logging in the driver station
     */
    private Telemetry tm;

    /**
     * ControlHub hardware configuration name for the camera.
     */
    final private String WEBCAM_NAME = "Webcam 1";

    /**
        Initializes the hardware and starts the camera for AprilTag processing. DriverHub
        displays the camera feed marking the detections. Reports units in INCHes and DEGREES
     */
    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        tm = telemetry;

        // Initialize AprilTag processor that "highlights" the detection on the DriverHub screen.
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawTagID(true)
                .setDrawTagOutline(true)
                .setDrawAxes(false)
                .setDrawCubeProjection(false)
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.DEGREES)
                .build();

        // Initialize the VisionPortal
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(WebcamName.class, WEBCAM_NAME));
        builder.setCameraResolution(new Size(640, 480));
        builder.addProcessor(aprilTagProcessor);

        visionPortal = builder.build();
    }

    /**
     * Updates the list of detected tags.
     */
    public void update() {
        detectedTags = aprilTagProcessor.getDetections();
    }

    /**
     * Retrieves the all the last detections
     *
     * @return List of AprilTagDetections from the processor
     */
    public List<AprilTagDetection> getDetectedTags() {
        return detectedTags;
    }

    /**
     * Displays the detection information on the DriverHub screen.
     *
     * @param detection Detection to display
     */
    public void displayDetectionTelemetry(AprilTagDetection detection) {
        if(detection != null) {
            tm.addData("Detection ID", detection.id);
            tm.addLine(String.format("   Range: %6.1f", detection.ftcPose.range ));
            tm.addLine(String.format("   Bearing: %6.1f (inch)", detection.ftcPose.bearing ));
            tm.addLine(String.format("   Elevation: %6.1f (inch)", detection.ftcPose.elevation ));
        }
    }

    /**
     * Searches and retrieves the detections for the specific ID
     *
     * @param id Integer ID of the AprilTag to search for
     * @return AprilTagDetection if found, null if not found
     */
    public AprilTagDetection getTagByID( int id ) {
        for( AprilTagDetection tag : detectedTags ) {
            if (tag.id == id) {
                return tag;
            }
        }

        return null;
    }

    /**
     * Stops the camera and releases all resources.
     */
    public void stop() {
        if(visionPortal != null) {
            visionPortal.close();
        }
    }
}
