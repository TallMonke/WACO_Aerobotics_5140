package org.firstinspires.ftc.teamcode.mechanisms;

import java.util.ArrayList;
import java.util.HashMap;

/// Object wrapper for FTC DECODE specific AprilTag IDs.
/// Obelisk IDs:
///    21: GREEN, PURPLE, PURPLE
///    22: PURPLE, GREEN, PURPLE
///    23: PURPLE, PURPLE, GREEN
/// Red Team Bucket: 24
/// Blue Team Bucket: 20
public class AprilTagColors {
    /**
     * FTC DECODE AprilTag IDs
     */
    final private int RED_TEAM_ID = 24; // Corresponds to the red team goal
    final private int BLUE_TEAM_ID = 20; // Corresponds to the blue team goal
    final private int OBELISK_ID_1 = 21; // GREEN, PURPLE, PURPLE
    final private int OBELISK_ID_2 = 22; // PURPLE, GREEN, PURPLE
    final private int OBELISK_ID_3 = 23; // PURPLE, PURPLE, GREEN

    /**
     * Map of AprilTag IDs to the colors associated with them
     */
    private final HashMap<Integer, ArrayList<BallColorSensor.DetectedColor>> colorMap = new HashMap<Integer, ArrayList<BallColorSensor.DetectedColor>>(3);

    /**
     * Initializes the color map for each AprilTag ID
     */
    public AprilTagColors()
    {
        // Obelisk colors are SPECIFIC to each ID. Do NOT rearrange the color orders
        ArrayList<BallColorSensor.DetectedColor> id21 = new ArrayList<BallColorSensor.DetectedColor>(3);
        id21.add( BallColorSensor.DetectedColor.GREEN );
        id21.add( BallColorSensor.DetectedColor.PURPLE );
        id21.add( BallColorSensor.DetectedColor.PURPLE );

        colorMap.put(OBELISK_ID_1, id21);

        ArrayList<BallColorSensor.DetectedColor> id22 = new ArrayList<BallColorSensor.DetectedColor>(3);
        id22.add( BallColorSensor.DetectedColor.PURPLE );
        id22.add( BallColorSensor.DetectedColor.GREEN );
        id22.add( BallColorSensor.DetectedColor.PURPLE );

        colorMap.put(OBELISK_ID_2, id22);

        ArrayList<BallColorSensor.DetectedColor> id23 = new ArrayList<BallColorSensor.DetectedColor>(3);
        id23.add( BallColorSensor.DetectedColor.PURPLE );
        id23.add( BallColorSensor.DetectedColor.PURPLE );
        id23.add( BallColorSensor.DetectedColor.GREEN );

        colorMap.put(OBELISK_ID_3, id23);

        // Blue/Red represent the teams colored goals
        ArrayList<BallColorSensor.DetectedColor> blueTeam = new ArrayList<BallColorSensor.DetectedColor>(1);
        blueTeam.add( BallColorSensor.DetectedColor.BLUE );
        colorMap.put(BLUE_TEAM_ID, blueTeam);

        ArrayList<BallColorSensor.DetectedColor> redTeam = new ArrayList<BallColorSensor.DetectedColor>(1);
        redTeam.add( BallColorSensor.DetectedColor.RED );
        colorMap.put(RED_TEAM_ID, redTeam);
    }

    /**
     * Retrieves the AprilTag ID of the red team
     *
     * @return AprilTag ID corresponding to the red team
     */
    public int getRedTeamID()
    {
        return RED_TEAM_ID;
    }

    /**
     * Checks if the given ID corresponds to the red team
     *
     * @param id ID to check
     * @return True if the ID corresponds to the red team goal, false otherwise
     */
    public boolean isRedTeamID(int id) {
        return (id == RED_TEAM_ID);
    }

    /**
     * Checks if the given ID corresponds to the blue team goal
     *
     * @param id ID to check
     * @return True if the ID corresponds to the blue team goal, false otherwise
     */
    public boolean isBlueTeamID(int id) {
        return (id == BLUE_TEAM_ID);
    }

    /**
     * Retrieves the AprilTag ID of the blue team
     *
     * @return AprilTag ID correpsonding to the blue team
     */
    public int getBlueTeamID()
    {
        return BLUE_TEAM_ID;
    }

    /**
     * Retrieves the IDs of the obelisks
     *
     * @return IDs for each of the obelisk sides
     */
    public ArrayList<Integer> getObeliskIDs()
    {
        ArrayList<Integer> obelisks = new ArrayList<Integer>(3);
        obelisks.add(OBELISK_ID_1);
        obelisks.add(OBELISK_ID_2);
        obelisks.add(OBELISK_ID_3);

        return obelisks;
    }

    /**
     * Checks if the given ID corresponds to an obelisks
     *
     * @param id ID to check
     * @return True if the ID corresponds to an obelisks, false otherwise
     */
    public boolean isObeliskID(int id)
    {
        return getObeliskIDs().contains(id);
    }

    /**
     * Searches for the colors associated with the given ID
     *
     * @param id ID of the AprilTag to search
     * @return List of colors associated with the ID or null if not found
     */
    public ArrayList<BallColorSensor.DetectedColor> getColor(int id)
    {
        return colorMap.get(id);
    }
}
