package com.github.jaguarrobotics.jaglibs.plugin.gettargetip;

class USBTarget implements IPossibleTarget {
    @Override
    public String getProtocolName() {
        return "USB";
    }

    @Override
    public String getHostName(int teamNumber) {
        return "172.22.11.2";
    }
}
