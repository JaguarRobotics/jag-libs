package com.github.jaguarrobotics.jaglibs.net;

import java.io.IOException;

public class WpiBasedFactories {
    private String transformClassName(String orig) {
        if (orig.startsWith("com.github.jaguarrobotics.jaglibs.") && orig.contains(".library.")) {
            return orig.replace(".library.", ".library.wpi.");
        } else {
            return orig;
        }
    }
    
    public WpiBasedFactories(NetClient client) throws IOException {
        new IOFactory(client).classNameTransformer = this::transformClassName;
        new CommandFactory(client).classNameTransformer = this::transformClassName;
    }
}
