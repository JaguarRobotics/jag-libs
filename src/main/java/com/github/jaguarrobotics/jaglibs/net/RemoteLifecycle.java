package com.github.jaguarrobotics.jaglibs.net;

import java.io.IOException;
import com.github.jaguarrobotics.jaglibs.math.RealCoordinate;

class RemoteLifecycle extends Lifecycle {
    private void shutdown(RealCoordinate c) {
        shutdown();
    }

    private void disabledStart(RealCoordinate c) {
        disabledStart();
    }

    private void disabledEnd(RealCoordinate c) {
        disabledEnd();
    }

    private void autonomousStart(RealCoordinate c) {
        autonomousStart();
    }

    private void autonomousEnd(RealCoordinate c) {
        autonomousEnd();
    }

    private void teleopStart(RealCoordinate c) {
        teleopStart();
    }

    private void teleopEnd(RealCoordinate c) {
        teleopEnd();
    }

    private void testStart(RealCoordinate c) {
        testStart();
    }

    private void testEnd(RealCoordinate c) {
        testEnd();
    }

    RemoteLifecycle(NetClient client) throws IOException {
        client.subscribe("/lifecycle/shutdown").on(this::shutdown);
        client.subscribe("/lifecycle/start/disabled").on(this::disabledStart);
        client.subscribe("/lifecycle/end/disabled").on(this::disabledEnd);
        client.subscribe("/lifecycle/start/autonomous").on(this::autonomousStart);
        client.subscribe("/lifecycle/end/autonomous").on(this::autonomousEnd);
        client.subscribe("/lifecycle/start/teleop").on(this::teleopStart);
        client.subscribe("/lifecycle/end/teleop").on(this::teleopEnd);
        client.subscribe("/lifecycle/start/test").on(this::testStart);
        client.subscribe("/lifecycle/end/test").on(this::testEnd);
    }
}
