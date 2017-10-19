package com.github.jaguarrobotics.jaglibs.net.targetip;

interface IPossibleTarget {
    String getProtocolName();

    String getHostName(int teamNumber);
}
