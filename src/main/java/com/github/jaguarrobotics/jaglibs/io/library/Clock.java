package com.github.jaguarrobotics.jaglibs.io.library;

import java.util.Timer;
import java.util.TimerTask;
import com.github.jaguarrobotics.jaglibs.io.AbstractIODevice;
import com.github.jaguarrobotics.jaglibs.io.IOParameters;
import com.github.jaguarrobotics.jaglibs.math.Real;

public class Clock extends AbstractIODevice {
    private Timer timer;
    
    @Override
    protected void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                setValue(new Real(System.currentTimeMillis()));
            }
        }, 0, 1000);
    }

    @Override
    protected void stop() {
        timer.cancel();
        timer = null;
    }

    protected Clock(IOParameters params) {
        super(params);
    }
}
