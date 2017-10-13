package com.github.jaguarrobotics.jaglibs.math;

public final class Vector3 extends RealCoordinate {
    public double getI() {
        return getDimension(0);
    }

    public double getJ() {
        return getDimension(1);
    }
    
    public double getK() {
        return getDimension(2);
    }

    public void setI(double val) {
        setDimension(0, val);
    }

    public void setJ(double val) {
        setDimension(1, val);
    }
    
    public void setK(double val) {
        setDimension(2, val);
    }
    
    @Override
    public Vector3 add(RealCoordinate coord) {
        return (Vector3) super.add(coord);
    }

    @Override
    public Vector3 subtract(RealCoordinate coord) {
        return (Vector3) super.subtract(coord);
    }

    @Override
    public Vector3 scale(double factor) {
        return (Vector3) super.scale(factor);
    }

    @Override
    public Vector3 normalize() {
        return (Vector3) super.normalize();
    }
    
    public Vector3 cross(Vector3 vec) {
        if (vec == null) {
            throw new IllegalArgumentException("Vector cannot be null");
        }
        return (Vector3) new Matrix(new RealCoordinate[][] {
            { new Vector3(1, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1) },
            { new Real(getI()), new Real(getJ()), new Real(getK()) },
            { new Real(vec.getI()), new Real(vec.getJ()), new Real(vec.getK()) }
        }).determinant();
    }

    public Vector3(double i, double j, double k) {
        super(new double[] { i, j, k });
    }
}
