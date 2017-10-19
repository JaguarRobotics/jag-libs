package com.github.jaguarrobotics.jaglibs.logging;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.core.LogEvent;

class EgressQueue extends Thread {
    private final Queue<LogEvent> queue;

    @Override
    public void run() {
        try {
            while (true) {
                LogEvent event = queue.poll();
                if (event == null) {
                    Thread.sleep(10);
                } else {
                    NetworkLogSource.log(new NetworkProducedLogEvent(event));
                }
                Thread.sleep(1);
            }
        } catch (InterruptedException ex) {
            LogEvent event;
            while ((event = queue.poll()) != null) {
                NetworkLogSource.log(new NetworkProducedLogEvent(event));
            }
        }
    }
    
    void queue(LogEvent event) {
        queue.add(event);
    }

    EgressQueue() {
        super("Log-Egress-Queue");
        queue = new LinkedBlockingQueue<LogEvent>();
    }
}
