package com.github.jaguarrobotics.jaglibs.net.targetip;

class StaticEthernetTarget implements IPossibleTarget {
    @Override
    public String getProtocolName() {
        return "Static Ethernet";
    }

    @Override
    public String getHostName(int teamNumber) {
        return String.format("10.%d.%d.2", teamNumber / 100, teamNumber % 100);
    }
}
