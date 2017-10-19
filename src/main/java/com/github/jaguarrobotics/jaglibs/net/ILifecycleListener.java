package com.github.jaguarrobotics.jaglibs.net;

public interface ILifecycleListener {
    void shutdown();
    
    void disabledStart();
    
    void disabledEnd();
    
    void autonomousStart();
    
    void autonomousEnd();
    
    void teleopStart();
    
    void teleopEnd();
    
    void testStart();
    
    void testEnd();
}
