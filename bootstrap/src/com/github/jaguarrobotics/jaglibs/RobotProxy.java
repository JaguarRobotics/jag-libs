package com.github.jaguarrobotics.jaglibs;

import edu.wpi.first.wpilibj.IterativeRobot;

public class RobotProxy extends IterativeRobot implements IIterativeRobot {
    private final IIterativeRobot instance = new Robot();
    
    @Override
    public void robotInit() {
        instance.robotInit();
    }

    @Override
    public void disabledInit() {
        instance.disabledInit();
    }

    @Override
    public void autonomousInit() {
        instance.autonomousInit();
    }

    @Override
    public void teleopInit() {
        instance.teleopInit();
    }

    @Override
    public void testInit() {
        instance.testInit();
    }

    @Override
    public void robotPeriodic() {
        instance.robotPeriodic();
    }

    @Override
    public void disabledPeriodic() {
        instance.disabledPeriodic();
    }

    @Override
    public void autonomousPeriodic() {
        instance.autonomousPeriodic();
    }

    @Override
    public void teleopPeriodic() {
        instance.teleopPeriodic();
    }

    @Override
    public void testPeriodic() {
        instance.testPeriodic();
    }
}
