package com.github.jaguarrobotics.jaglibs.net.targetip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.Consumer;
import com.github.jaguarrobotics.jaglibs.util.Pointer;

public class TargetDiscoverer {
    private static final IPossibleTarget[] POSSIBLE_TARGETS = { new MulticastDNSTarget(""), new DNSTarget(),
                    new USBTarget(), new StaticEthernetTarget(), new MulticastDNSTarget(".frc-robot") };
    private final Consumer<String>         info;
    private final Consumer<String>         error;

    public String discover() throws IOException {
        int teamNumber = -1;
        try (Scanner scan = new Scanner(
                        new FileInputStream(new File(System.getProperty("user.home"), "wpilib/wpilib.properties")))) {
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                if (line.startsWith("team-number=")) {
                    teamNumber = Integer.parseInt(line.substring("team-number=".length()));
                }
            }
            if (teamNumber <= 0 || teamNumber >= 10000) {
                throw new IllegalArgumentException("Team number is invalid");
            }
        } catch (Exception ex) {
            error.accept("Team number not set.");
            throw ex;
        }
        info.accept("Finding roboRIO");
        Thread[] threads = new Thread[POSSIBLE_TARGETS.length];
        Thread current = Thread.currentThread();
        Pointer<String> target = new Pointer<String>();
        int _teamNumber = teamNumber;
        for (int _i = 0; _i < POSSIBLE_TARGETS.length; ++_i) {
            final int i = _i;
            threads[i] = new Thread() {
                @Override
                public void run() {
                    try {
                        String hostName = POSSIBLE_TARGETS[i].getHostName(_teamNumber);
                        info.accept(String.format("Trying %s: %s", POSSIBLE_TARGETS[i].getProtocolName(), hostName));
                        String ip = InetAddress.getByName(hostName).getHostAddress();
                        Socket sock = new Socket(ip, 80);
                        sock.close();
                        target.value = ip;
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
        if (target.value != null && target.value.length() > 0) {
            info.accept(String.format("roboRIO found at %s", target));
        } else {
            error.accept("roboRIO not found, please check that the roboRIO is connected, imaged and that the team number is set properly");
            throw new IOException(
                            "roboRIO not found, please check that the roboRIO is connected, imaged and that the team number is set properly");
        }
        return target.value;
    }

    public TargetDiscoverer(Consumer<String> info, Consumer<String> error) {
        this.info = info;
        this.error = error;
    }
}
