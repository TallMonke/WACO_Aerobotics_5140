package com.example.meepmeeptesting;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

public class AimFireAction implements Action {
    @Override
    public boolean run(TelemetryPacket telemetry) {
        System.out.println("Shoot ball");
        return true;
    }
}
