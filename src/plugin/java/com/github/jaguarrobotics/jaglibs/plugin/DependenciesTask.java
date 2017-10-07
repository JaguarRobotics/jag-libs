package com.github.jaguarrobotics.jaglibs.plugin;

import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class DependenciesTask extends DefaultTask {
    private static final Logger       LOG            = Logging.getLogger(DependenciesTask.class);
    private static final int          ALLOWED_YEAR   = 2017;
    private static final Set<Integer> ALLOWED_IMAGES = new HashSet<>(Arrays.asList(8));
    private static final String       JRE_DIR        = "/usr/local/frc/JRE";

    @TaskAction
    public void run() throws Exception {
        URL url = new URL(String.format("http://%s/nisysapi/server", getProject().property("target")));
        URLConnection connection = url.openConnection();
        try (PrintStream stream = new PrintStream(connection.getOutputStream())) {
            stream.println("Function=GetPropertiesOfItem&Plugins=nisyscfg&Items=system");
        }
        String platform;
        try (Scanner scan = new Scanner(connection.getInputStream())) {
            platform = scan.nextLine();
            LOG.info(String.format("Detected roboRIO version '%s'", platform));
        }
        int image = Integer.parseInt(platform.replaceAll("FRC_roboRIO_[0-9]+_v([0-9]+)", "$1"));
        int year = Integer.parseInt(platform.replaceAll("FRC_roboRIO_([0-9]+)_v[0-9]+", "$1"));
        if (!ALLOWED_IMAGES.contains(image) || year != ALLOWED_YEAR) {
            LOG.error("Image of roboRIO does not match plugin.");
            LOG.error(String.format("Allowed image year: %s version: %s.", ALLOWED_YEAR, ALLOWED_IMAGES));
            LOG.error(String.format("Actual image year: %s version %s.", year, image));
            LOG.error("RoboRIO needs to be re-imaged or plugins updated.");
            throw new Exception("RoboRIO needs to be re-imaged or plugins updated.");
        }
        LOG.info("roboRIO image version validated");
        LOG.info("Checking for JRE. If this fails install the JRE using these instructions: https://wpilib.screenstepslive.com/s/4485/m/13503/l/288822-installing-java-8-on-the-roborio-using-the-frc-roborio-java-installer-java-only");
        JSch jsch = new JSch();
        Session session = jsch.getSession("lvuser", getProject().property("target").toString());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("test -d ".concat(JRE_DIR));
        channel.connect();
        while (!channel.isClosed()) {
            Thread.sleep(10);
        }
        if (channel.getExitStatus() != 0) {
            LOG.error("The JRE is not installed");
            channel.disconnect();
            session.disconnect();
            throw new Exception("The JRE is not installed");
        }
        channel.disconnect();
        session.disconnect();
    }
}
