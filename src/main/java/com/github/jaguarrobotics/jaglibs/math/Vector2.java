package com.github.jaguarrobotics.jaglibs.math;

public final class Vector2 extends RealCoordinate {
    public double getI() {
        return getDimension(0);
    }

    public double getJ() {
        return getDimension(1);
    }

    public void setI(double val) {
        setDimension(0, val);
    }

    public void setJ(double val) {
        setDimension(1, val);
    }

    @Override
    public Vector2 add(RealCoordinate coord) {
        return (Vector2) super.add(coord);
    }

    @Override
    public Vector2 subtract(RealCoordinate coord) {
        return (Vector2) super.subtract(coord);
    }

    @Override
    public Vector2 scale(double factor) {
        return (Vector2) super.scale(factor);
    }

    @Override
    public Vector2 normalize() {
        return (Vector2) super.normalize();
    }

    public Vector2(double i, double j) {
        super(new double[] { i, j });
    }
}
