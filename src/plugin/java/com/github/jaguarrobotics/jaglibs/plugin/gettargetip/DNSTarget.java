package com.github.jaguarrobotics.jaglibs.plugin.gettargetip;

class DNSTarget implements IPossibleTarget {
    @Override
    public String getProtocolName() {
        return "DNS";
    }

    @Override
    public String getHostName(int teamNumber) {
        return String.format("roboRIO-%d-FRC.lan", teamNumber);
    }
}
