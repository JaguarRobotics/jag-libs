package com.github.jaguarrobotics.jaglibs;

import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.github.jaguarrobotics.jaglibs.config.DataSourceConfiguration;
import com.github.jaguarrobotics.jaglibs.config.RobotConfiguration;
import com.github.jaguarrobotics.jaglibs.logging.NetworkEgressAppender;
import com.github.jaguarrobotics.jaglibs.logging.NetworkLogSource;
import com.github.jaguarrobotics.jaglibs.net.CommandFactory;
import com.github.jaguarrobotics.jaglibs.net.IOFactory;
import com.github.jaguarrobotics.jaglibs.net.NetClient;
import com.github.jaguarrobotics.jaglibs.net.targetip.TargetDiscoverer;

public class Main {
    private static final Logger log = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        log.info("Robot code is starting up.");
        RobotConfiguration config;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (InputStream stream = Main.class.getResourceAsStream("/robot.xml")) {
            if (stream == null) {
                log.fatal("Unable to find 'robot.xml'!");
                System.exit(1);
            }
            config = new RobotConfiguration(builder.parse(stream));
        }
        log.info("Discovering RoboRIO...");
        TargetDiscoverer discoverer = new TargetDiscoverer(log::info, log::error);
        String target;
        if (args.length == 1) {
            target = args[0];
        } else {
            try {
                target = discoverer.discover();
            } catch (FileNotFoundException ex) {
                log.info("WPILib not installed; assuming this is running on the robot.");
                target = "localhost";
            }
        }
        log.info("Found RoboRIO at {}.", target);
        log.info("Connecting to robot...");
        NetClient client = new NetClient(target);
        log.info("Socket connected; setting up logging...");
        new NetworkLogSource(client);
        NetworkEgressAppender.setClient(client);
        log.info("Robot code connected to robot.");
        new IOFactory(client);
        new CommandFactory(client);
        for (DataSourceConfiguration io : config.io.sources.values()) {
            io.create(client);
        }
    }
}
