package com.github.jaguarrobotics.jaglibs.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventLoop extends Thread {
    private static class Task {
        InterruptableRunnable code;
        long                  targetTime;
        long                  period;

        Task(InterruptableRunnable code, long targetTime, long period) {
            this.code = code;
            this.targetTime = targetTime;
            this.period = period;
        }
    }

    public interface InterruptableRunnable {
        void run() throws InterruptedException;
    }

    private static final Logger    log      = LogManager.getLogger();
    private static final EventLoop INSTANCE = new EventLoop();
    private final Set<Task>        tasks;
    private boolean                run;

    @Override
    public void run() {
        try {
            while (run) {
                if (tasks.isEmpty()) {
                    Thread.sleep(10);
                } else {
                    long now = System.currentTimeMillis();
                    for (Task task : tasks.toArray(new Task[0])) {
                        if (task.targetTime <= now) {
                            try {
                                task.code.run();
                            } catch (InterruptedException ex) {
                                throw ex;
                            } catch (Throwable t) {
                                log.catching(t);
                            } finally {
                                if (task.period <= 0) {
                                    tasks.remove(task);
                                } else {
                                    task.targetTime += task.period;
                                }
                            }
                        }
                    }
                }
                Thread.sleep(1);
            }
        } catch (InterruptedException ex) {
        }
    }

    @Override
    public synchronized void start() {
        run = true;
        super.start();
    }

    @Override
    public void interrupt() {
        run = false;
        while (isAlive()) {
            super.interrupt();
            while (isAlive() && isInterrupted()) {
                Thread.yield();
            }
        }
    }

    public static void runInterruptable(InterruptableRunnable code) {
        repeatInterruptable(code, 0, 0);
    }

    public static void delayInterruptable(InterruptableRunnable code, long millis) {
        repeatInterruptable(code, millis, 0);
    }

    public static void repeatInterruptable(InterruptableRunnable code, long delay, long period) {
        INSTANCE.tasks.add(new Task(code, delay, period));
    }

    public static void run(Runnable code) {
        repeatInterruptable((InterruptableRunnable) code::run, 0, 0);
    }

    public static void delay(Runnable code, long millis) {
        repeatInterruptable((InterruptableRunnable) code::run, millis, 0);
    }

    public static void repeat(Runnable code, long delay, long period) {
        repeatInterruptable((InterruptableRunnable) code::run, delay, period);
    }

    private EventLoop() {
        super("Event-Loop");
        tasks = Collections.synchronizedSet(new HashSet<Task>());
        start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::interrupt));
    }
}
