package com.github.jaguarrobotics.jaglibs.net.targetip;

class MulticastDNSTarget implements IPossibleTarget {
    private final String extension;
    
    @Override
    public String getProtocolName() {
        return "mDNS";
    }

    @Override
    public String getHostName(int teamNumber) {
        return String.format("roboRIO-%d-FRC%s.local", teamNumber, extension);
    }
    
    public MulticastDNSTarget(String extension) {
        this.extension = extension;
    }
}
