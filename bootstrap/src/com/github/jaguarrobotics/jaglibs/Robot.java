package com.github.jaguarrobotics.jaglibs;

import java.util.Scanner;

public class Robot implements IIterativeRobot {
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
    public void disabledInit() {
    }

    @Override
    public void autonomousInit() {
    }

    @Override
    public void teleopInit() {
    }

    @Override
    public void testInit() {
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
            System.out.println("quit");
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
        server = new MQTTServer();
    }
}
