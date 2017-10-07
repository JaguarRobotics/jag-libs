package com.github.jaguarrobotics.jaglibs.plugin.gettargetip;

import java.net.InetAddress;
import java.net.Socket;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import com.github.jaguarrobotics.jaglibs.plugin.JagLibsPluginExtension;

public class GetTargetIPTask extends DefaultTask {
    private static final Logger            LOG              = Logging.getLogger(GetTargetIPTask.class);
    private static final IPossibleTarget[] POSSIBLE_TARGETS = { new MulticastDNSTarget(""), new DNSTarget(),
                    new USBTarget(), new StaticEthernetTarget(), new MulticastDNSTarget(".frc-robot") };

    @TaskAction
    public void run() throws Exception {
        int teamNumber;
        try {
            teamNumber = getProject().getExtensions().getByType(JagLibsPluginExtension.class).teamNumber;
            if (teamNumber <= 0 || teamNumber >= 10000) {
                throw new IllegalArgumentException("Team number is invalid");
            }
        } catch (Exception ex) {
            LOG.error("Team number not set.");
            throw ex;
        }
        LOG.info("Finding roboRIO");
        Thread[] threads = new Thread[POSSIBLE_TARGETS.length];
        Thread current = Thread.currentThread();
        for (int _i = 0; _i < POSSIBLE_TARGETS.length; ++_i) {
            final int i = _i;
            threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        String hostName = POSSIBLE_TARGETS[i].getHostName(teamNumber);
                        LOG.info(String.format("Trying %s: %s", POSSIBLE_TARGETS[i].getProtocolName(), hostName));
                        String ip = InetAddress.getByName(hostName).getHostAddress();
                        Socket sock = new Socket(ip, 80);
                        sock.close();
                        getProject().setProperty("target", ip);
                        current.interrupt();
                    } catch (Exception ex) {
                    }
                }
            };
            threads[i].start();
        }
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
        }
        for (int i = 0; i < POSSIBLE_TARGETS.length; ++i) {
            threads[i].interrupt();
        }
        if (current.isInterrupted()) {
            try {
                Thread.sleep(0);
            } catch (InterruptedException ex) {
            }
        }
        String target;
        try {
            target = (String) getProject().property("target");
        } catch (Exception ex) {
            target = null;
        }
        if (target != null && target.length() > 0) {
            LOG.info(String.format("roboRIO found at %s", target));
        } else {
            LOG.error("roboRIO not found, please check that the roboRIO is connected, imaged and that the team number is set properly");
            throw new Exception(
                            "roboRIO not found, please check that the roboRIO is connected, imaged and that the team number is set properly");
        }
    }
}
