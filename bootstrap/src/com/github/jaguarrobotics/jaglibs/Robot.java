package com.github.jaguarrobotics.jaglibs;

import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.jaguarrobotics.jaglibs.logging.NetworkEgressAppender;
import com.github.jaguarrobotics.jaglibs.logging.NetworkLogSource;
import com.github.jaguarrobotics.jaglibs.net.NetServer;
import com.github.jaguarrobotics.jaglibs.net.WpiBasedFactories;

public class Robot implements IIterativeRobot {
    private static final Logger log = LogManager.getLogger();
    private NetServer           server;
    private Runnable            stop;

    @Override
    public void robotInit() {
        try {
            log.info("Starting server...");
            server.start();
            new NetworkLogSource(server);
            NetworkEgressAppender.setClient(server);
            new WpiBasedFactories(server);
        } catch (Exception ex) {
            log.fatal("Unable to start server");
            log.catching(ex);
        } finally {
            stop = () -> {};            
        }
    }

    @Override
    public void disabledInit() {
        stop.run();
        server.getLifecycle().disabledStart();
        stop = server.getLifecycle()::disabledEnd;
    }

    @Override
    public void autonomousInit() {
        stop.run();
        server.getLifecycle().autonomousStart();
        stop = server.getLifecycle()::autonomousEnd;
    }

    @Override
    public void teleopInit() {
        stop.run();
        server.getLifecycle().teleopStart();
        stop = server.getLifecycle()::teleopEnd;
    }

    @Override
    public void testInit() {
        stop.run();
        server.getLifecycle().testStart();
        stop = server.getLifecycle()::testEnd;
    }

    @Override
    public void robotPeriodic() {
    }

    @Override
    public void disabledPeriodic() {
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

    public static void main(String[] args) throws Exception {
        try (Scanner scan = new Scanner(System.in)) {
            Robot robot = new Robot();
            robot.robotInit();
            robot.disabledInit();
            Runnable method = robot::disabledPeriodic;
            System.out.println();
            System.out.println();
            System.out.println("The robot is now running in a testing environment. All I/O are disabled, and you");
            System.out.println("can control which period the code is in via commands. Below is a list of");
            System.out.println("commands that you can use:");
            System.out.println();
            System.out.println("disabled            auto                autonomous          tele");
            System.out.println("teleop              test                exit                kill");
            System.out.println("quit                stop");
            System.out.println();
            System.out.println();
            while (method != null) {
                robot.robotPeriodic();
                method.run();
                if (System.in.available() > 0) {
                    switch (scan.next()) {
                        case "disabled":
                            robot.disabledInit();
                            method = robot::disabledPeriodic;
                            break;
                        case "auto":
                        case "autonomous":
                            robot.autonomousInit();
                            method = robot::autonomousPeriodic;
                            break;
                        case "tele":
                        case "teleop":
                            robot.teleopInit();
                            method = robot::teleopPeriodic;
                            break;
                        case "test":
                            robot.testInit();
                            method = robot::testPeriodic;
                            break;
                        case "exit":
                        case "kill":
                        case "stop":
                        case "quit":
                            method = null;
                            break;
                    }
                }
            }
        }
        System.exit(0);
    }

    public Robot() {
        try {
            server = new NetServer();
        } catch (Exception ex) {
            log.catching(ex);
        }
    }
}
