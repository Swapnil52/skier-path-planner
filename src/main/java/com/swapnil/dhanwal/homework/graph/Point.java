package com.swapnil.dhanwal.homework.graph;

public class Point {

    private final PointType type;

    private final int i;

    private final int j;

    private final int height;

    public Point(PointType type, int i, int j, int height) {
        this.type = type;
        this.i = i;
        this.j = j;
        this.height = height;
    }

    public PointType getType() {
        return type;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object obj) {
        Point otherPoint = (Point) obj;
        if (otherPoint == null) {
            return false;
        }
        return this.i == otherPoint.getI() && this.j == otherPoint.getJ();
    }

    @Override
    public String toString() {
        return "Point{" +
                "i=" + i +
                ", j=" + j +
                '}';
    }
}
