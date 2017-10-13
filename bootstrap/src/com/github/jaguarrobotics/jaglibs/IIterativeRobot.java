package com.github.jaguarrobotics.jaglibs;

public interface IIterativeRobot {
    void robotInit();
    
    void disabledInit();
    
    void autonomousInit();
    
    void teleopInit();
    
    void testInit();
    
    void robotPeriodic();
    
    void disabledPeriodic();
    
    void autonomousPeriodic();
    
    void teleopPeriodic();
    
    void testPeriodic();
}
