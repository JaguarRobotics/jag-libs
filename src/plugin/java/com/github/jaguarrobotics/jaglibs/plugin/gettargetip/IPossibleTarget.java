package com.github.jaguarrobotics.jaglibs.plugin.gettargetip;

interface IPossibleTarget {
    String getProtocolName();

    String getHostName(int teamNumber);
}
