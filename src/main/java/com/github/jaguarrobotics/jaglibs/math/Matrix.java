package com.github.jaguarrobotics.jaglibs.math;

public class Matrix {
    private RealCoordinate[][] vals;

    public int getRows() {
        return vals.length;
    }

    public int getColumns() {
        return vals.length > 0 ? vals[0].length : 0;
    }

    public RealCoordinate get(int row, int column) {
        if (row < 0 || row >= getRows()) {
            throw new IllegalArgumentException("Row out of bounds");
        }
        if (column < 0 || column >= getColumns()) {
            throw new IllegalArgumentException("Column out of bounds");
        }
        return vals[row][column];
    }

    public void set(int row, int column, RealCoordinate val) {
        if (row < 0 || row >= getRows()) {
            throw new IllegalArgumentException("Row out of bounds");
        }
        if (column < 0 || column >= getColumns()) {
            throw new IllegalArgumentException("Column out of bounds");
        }
        vals[row][column] = val;
    }

    public Matrix cofactor(int row, int column) {
        int rows = getRows();
        int cols = getColumns();
        RealCoordinate[][] cofactor = new RealCoordinate[rows - 1][cols - 1];
        for (int i = 0, j = 0; i < rows; ++i) {
            if (i != row) {
                for (int k = 0, l = 0; k < cols; ++k) {
                    if (k != column) {
                        cofactor[j][l] = get(i, k);
                        ++l;
                    }
                }
                ++j;
            }
        }
        return new Matrix(cofactor);
    }
    
    private RealCoordinate multiply(RealCoordinate a, RealCoordinate b) {
        if (a instanceof Real) {
            return b.scale(a.getDimension(0));
        } else if (b instanceof Real) {
            return a.scale(b.getDimension(0));
        } else {
            throw new IllegalArgumentException("Cannot solve multiplication");
        }
    }

    public RealCoordinate determinant() {
        int size = getRows();
        if (size != getColumns()) {
            throw new IllegalArgumentException("Matrix must be square to get the determinant");
        }
        switch (size) {
            case 0:
                return new Null();
            case 1:
                return get(0, 0);
            case 2:
                return multiply(get(0, 0), get(1, 1)).subtract(multiply(get(0, 1), get(1, 0)));
            default:
                RealCoordinate det = RealCoordinate.create(new double[size]);
                double sign = 1;
                for (int i = 0; i < size; ++i) {
                    det = det.add(cofactor(i, 0).determinant().scale(sign));
                    sign = -sign;
                }
                return det;
        }
    }

    public Matrix(RealCoordinate[][] vals) {
        if (vals == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }
        this.vals = new RealCoordinate[vals.length][];
        int cols = vals.length > 0 ? vals[0].length : 0;
        for (int i = 0; i < vals.length; ++i) {
            if (vals[i].length != cols) {
                throw new IllegalArgumentException("Values cannot be a jagged array");
            }
            this.vals[i] = new RealCoordinate[cols];
            for (int j = 0; j < cols; ++j) {
                this.vals[i][j] = vals[i][j];
            }
        }
    }

    public Matrix(int rows, int cols) {
        if (rows < 0 || cols < 0) {
            throw new IllegalArgumentException("Matrix cannot have a negative size");
        }
        vals = new RealCoordinate[rows][cols];
    }
}
