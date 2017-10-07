package com.github.jaguarrobotics.jaglibs.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.bundling.Jar;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class DeployTask extends DefaultTask {
    private static final Logger LOG           = Logging.getLogger(DeployTask.class);
    private static final String ROBOT_COMMAND = "/usr/local/frc/bin/netconsole-host /usr/local/frc/JRE/bin/java -Djava.library.path=/usr/local/frc/lib/ -jar /home/lvuser/FRCUserProgram.jar\n";
    
    private static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        // 1 for error,
        // 2 for fatal error,
        // -1
        if (b == 0) return b;
        if (b == -1) return b;
        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                LOG.error(sb.toString());
            }
            if (b == 2) { // fatal error
                LOG.error(sb.toString());
            }
        }
        return b;
    }
    
    @TaskAction
    public void run() throws Exception {
        LOG.info("[athena-deploy] Copying code over.");
        JSch jsch = new JSch();
        Session session = jsch.getSession("admin", getProject().property("target").toString());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("scp -t /home/lvuser/FRCUserProgram.jar");
        Jar jar = (Jar) getProject().getTasks().getByName("jar");
        File jarFile = jar.getArchivePath();
        try (PrintStream stream = new PrintStream(channel.getOutputStream());
                        InputStream in = channel.getInputStream()) {
            channel.connect();
            if (checkAck(in) != 0) {
                LOG.error("SSH Failure");
                throw new Exception("SSH Failure");
            }
            stream.printf("C0644 %ld %s%n", jarFile.length(), jarFile.getName());
            if (checkAck(in) != 0) {
                LOG.error("SSH Failure");
                throw new Exception("SSH Failure");
            }
            try (FileInputStream file = new FileInputStream(jarFile)) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = file.read(buffer)) > 0) {
                    stream.write(buffer, 0, read);
                }
            }
            if (checkAck(in) != 0) {
                LOG.error("SSH Failure");
                throw new Exception("SSH Failure");
            }
        }
        channel.disconnect();
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("ldconfig");
        channel.connect();
        while (!channel.isClosed()) {
            Thread.sleep(10);
        }
        if (channel.getExitStatus() != 0) {
            LOG.error("Command failed");
            channel.disconnect();
            session.disconnect();
            throw new Exception("Command failed");
        }
        channel.disconnect();
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("killall -q netconsole-host || :");
        channel.connect();
        while (!channel.isClosed()) {
            Thread.sleep(10);
        }
        if (channel.getExitStatus() != 0) {
            LOG.error("Command failed");
            channel.disconnect();
            session.disconnect();
            throw new Exception("Command failed");
        }
        channel.disconnect();
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("scp -t /home/lvuser/robotCommand");
        try (PrintStream stream = new PrintStream(channel.getOutputStream());
                        InputStream in = channel.getInputStream()) {
            channel.connect();
            if (checkAck(in) != 0) {
                LOG.error("SSH Failure");
                throw new Exception("SSH Failure");
            }
            stream.printf("C0644 %ld robotCommand%n", ROBOT_COMMAND.length());
            if (checkAck(in) != 0) {
                LOG.error("SSH Failure");
                throw new Exception("SSH Failure");
            }
            stream.print(ROBOT_COMMAND);
            if (checkAck(in) != 0) {
                LOG.error("SSH Failure");
                throw new Exception("SSH Failure");
            }
        }
        channel.disconnect();
        LOG.info("[athena-deploy] Starting program.");
        session.disconnect();
        session = jsch.getSession("lvuser", getProject().property("target").toString());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(". /etc/profile.d/natinst-path.sh; /usr/local/frc/bin/frcKillRobot.sh -t -r");
        channel.connect();
        while (!channel.isClosed()) {
            Thread.sleep(10);
        }
        if (channel.getExitStatus() != 0) {
            LOG.error("Command failed");
            channel.disconnect();
            session.disconnect();
            throw new Exception("Command failed");
        }
        channel.disconnect();
        channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand("sync");
        channel.connect();
        while (!channel.isClosed()) {
            Thread.sleep(10);
        }
        if (channel.getExitStatus() != 0) {
            LOG.error("Command failed");
            channel.disconnect();
            session.disconnect();
            throw new Exception("Command failed");
        }
        channel.disconnect();
        session.disconnect();
    }
}
