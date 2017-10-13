package com.github.jaguarrobotics.jaglibs.math;

public final class Real extends RealCoordinate {
    double get() {
        return getDimension(0);
    }
    
    void set(double val) {
        setDimension(0, val);
    }
    
    public Real add(Real coord) {
        if (coord == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        return new Real(get() + coord.get());
    }

    @Override
    public Real add(RealCoordinate coord) {
        if (coord == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        if (coord instanceof Real) {
            return add((Real) coord);
        } else {
            throw new IllegalArgumentException("Coordinates are in different spaces");
        }
    }
    
    public Real subtract(Real coord) {
        if (coord == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        return new Real(get() - coord.get());
    }

    @Override
    public Real subtract(RealCoordinate coord) {
        if (coord == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        if (coord instanceof Real) {
            return subtract((Real) coord);
        } else {
            throw new IllegalArgumentException("Coordinates are in different spaces");
        }
    }

    @Override
    public double dot(RealCoordinate coord) {
        if (coord == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        if (coord instanceof Real) {
            return get() * coord.getDimension(0);
        } else {
            throw new IllegalArgumentException("Coordinates are in different spaces");
        }
    }

    @Override
    public Real scale(double factor) {
        return new Real(get() * factor);
    }
    
    public Real multiply(Real coord) {
        if (coord == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        return scale(coord.get());
    }
    
    public Real divide(Real coord) {
        if (coord == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        return new Real(get() / coord.get());
    }

    @Override
    public double magnitude() {
        return get();
    }

    @Override
    public Real normalize() {
        return new Real(1);
    }

    public Real(double val) {
        super(new double[] { val });
    }
}
