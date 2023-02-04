package com.swapnil.dhanwal.homework.graph;

public class Graph {

    private final int H;

    private final int W;

    private final Point[][] points;

    public Graph(int[][] map, int H, int W) {
        this.H = H;
        this.W = W;
        this.points = new Point[H][W];
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < W; j++) {
                PointType type = map[i][j] < 0 ? PointType.TREE : PointType.HILL;
                this.points[i][j] = new Point(type, i, j, Math.abs(map[i][j]));
            }
        }
    }

    public int getH() {
        return H;
    }

    public int getW() {
        return W;
    }

    public Point get(int i, int j) {
        if (i < 0 || j < 0 || i >= H || j >= W) {
            return null;
        }
        return this.points[i][j];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < H; i++) {
            StringBuilder rowBuilder = new StringBuilder();
            for (int j = 0; j < W; j++) {
                rowBuilder.append(points[i][j].getHeight() * (points[i][j].getType() == PointType.HILL ? 1 : -1));
                rowBuilder.append(" ");
            }
            rowBuilder.append("\n");
            builder.append(rowBuilder);
        }
        return builder.toString();
    }
}
