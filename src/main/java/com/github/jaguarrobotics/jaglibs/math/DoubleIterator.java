package com.github.jaguarrobotics.jaglibs.math;

import java.util.Iterator;

public class DoubleIterator implements Iterator<Double> {
    private final double[] arr;
    private int i;
    
    @Override
    public boolean hasNext() {
        return i + 1 < arr.length;
    }

    @Override
    public Double next() {
        return arr[++i];
    }

    public DoubleIterator(double[] arr) {
        this.arr = arr;
        i = -1;
    }
}
