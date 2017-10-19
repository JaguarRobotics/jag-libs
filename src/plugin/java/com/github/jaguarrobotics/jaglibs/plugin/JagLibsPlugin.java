package com.github.jaguarrobotics.jaglibs.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.java.archives.Attributes;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.bundling.Jar;

public class JagLibsPlugin implements Plugin<Project> {
    private static final Logger log = Logging.getLogger(JagLibsPlugin.class);

    public void applyAfter(Project project) {
        Jar jar = (Jar) project.getTasks().getByName("jar");
        List<Object> files = new ArrayList<Object>();
        for (File file : project.getConfigurations().getByName("compile")) {
            if (file.isDirectory()) {
                files.add(file);
            } else {
                files.add(project.zipTree(file));
            }
        }
        jar.from(files);
    }
    
    @Override
    public void apply(Project project) {
        Jar jar = (Jar) project.getTasks().getByName("jar");
        Attributes attr = jar.getManifest().getAttributes();
        attr.put("Main-Class", "com.github.jaguarrobotics.jaglibs.Main");
        project.afterEvaluate(this::applyAfter);
    }
}
