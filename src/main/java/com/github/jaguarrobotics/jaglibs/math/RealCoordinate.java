package com.github.jaguarrobotics.jaglibs.math;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;

public class RealCoordinate implements Iterable<Double> {
    private double[] vals;

    public int getDimensions() {
        return vals.length;
    }

    public double getDimension(int dim) {
        if (dim < 0 || dim >= getDimensions()) {
            throw new IllegalArgumentException("Dimension is out of bounds");
        }
        return vals[dim];
    }

    public void setDimension(int dim, double val) {
        if (dim < 0 || dim >= getDimensions()) {
            throw new IllegalArgumentException("Dimension is out of bounds");
        }
        vals[dim] = val;
    }

    @Override
    public Iterator<Double> iterator() {
        return new DoubleIterator(vals);
    }

    public RealCoordinate add(RealCoordinate coord) {
        if (coord == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        int dim = getDimensions();
        if (dim != coord.getDimensions()) {
            throw new IllegalArgumentException("Coordinates are in different spaces");
        }
        double[] res = new double[dim];
        for (int i = 0; i < dim; ++i) {
            res[i] = getDimension(i) + coord.getDimension(i);
        }
        return create(res);
    }

    public RealCoordinate subtract(RealCoordinate coord) {
        if (coord == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        int dim = getDimensions();
        if (dim != coord.getDimensions()) {
            throw new IllegalArgumentException("Coordinates are in different spaces");
        }
        double[] res = new double[dim];
        for (int i = 0; i < dim; ++i) {
            res[i] = getDimension(i) - coord.getDimension(i);
        }
        return create(res);
    }

    public double dot(RealCoordinate coord) {
        if (coord == null) {
            throw new IllegalArgumentException("Coordinate cannot be null");
        }
        int dim = getDimensions();
        if (dim != coord.getDimensions()) {
            throw new IllegalArgumentException("Coordinates are in different spaces");
        }
        double res = 0;
        for (int i = 0; i < dim; ++i) {
            res += getDimension(i) * coord.getDimension(i);
        }
        return res;
    }

    public RealCoordinate scale(double factor) {
        int dim = getDimensions();
        double[] res = new double[dim];
        for (int i = 0; i < dim; ++i) {
            res[i] = getDimension(i) * factor;
        }
        return create(res);
    }

    public double magnitude() {
        double magSquared = 0;
        for (double dim : this) {
            magSquared += dim * dim;
        }
        return Math.sqrt(magSquared);
    }

    public RealCoordinate normalize() {
        return scale(1.0 / magnitude());
    }

    public ByteBuffer serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(4 + 8 * vals.length);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(vals.length);
        for (double val : vals) {
            buffer.putDouble(val);
        }
        return buffer;
    }

    public static RealCoordinate create(double[] vals) {
        if (vals == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }
        switch (vals.length) {
            case 0:
                return new Null();
            case 1:
                return new Real(vals[0]);
            case 2:
                return new Vector2(vals[0], vals[1]);
            case 3:
                return new Vector3(vals[0], vals[1], vals[2]);
            default:
                return new RealCoordinate(vals);
        }
    }

    public static RealCoordinate deserialize(ByteBuffer buffer) {
        buffer.position(0);
        buffer.order(ByteOrder.BIG_ENDIAN);
        double[] vals = new double[buffer.getInt()];
        for (int i = 0; i < vals.length; ++i) {
            vals[i] = buffer.getDouble();
        }
        return create(vals);
    }

    protected RealCoordinate(double[] vals) {
        if (vals == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }
        this.vals = vals;
    }
}
