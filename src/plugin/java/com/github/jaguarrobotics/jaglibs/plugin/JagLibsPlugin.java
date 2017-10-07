package com.github.jaguarrobotics.jaglibs.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.java.archives.Attributes;
import org.gradle.api.tasks.bundling.Jar;
import com.github.jaguarrobotics.jaglibs.plugin.gettargetip.GetTargetIPTask;

public class JagLibsPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getExtensions().create("jaglibs", JagLibsPluginExtension.class);
        GetTargetIPTask getTargetIP = project.getTasks().create("get-target-ip", GetTargetIPTask.class);
        DependenciesTask dependencies = project.getTasks().create("dependencies", DependenciesTask.class);
        dependencies.dependsOn(getTargetIP);
        DeployTask deploy = project.getTasks().create("deploy", DeployTask.class);
        Jar jar = (Jar) project.getTasks().getByName("jar");
        deploy.dependsOn(jar, getTargetIP, dependencies);
        Attributes attr = jar.getManifest().getAttributes();
        attr.put("Main-Class", "edu.wpi.first.wpilibj.RobotBase");
        attr.put("Robot-Class", "com.github.jaguarrobotics.jaglibs.Robot");
        attr.put("Class-Path", ".");
    }
}
