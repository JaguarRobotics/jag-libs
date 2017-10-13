package com.github.jaguarrobotics.jaglibs;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {
    private MQTTServer server;

    @Override
    public void robotInit() {
        try {
            server.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void autonomousInit() {
    }

    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void teleopPeriodic() {
    }

    @Override
    public void testPeriodic() {
    }

    public Robot() {
        server = new MQTTServer();
    }
}
