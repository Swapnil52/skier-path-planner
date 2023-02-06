package com.swapnil.dhanwal.homework.graph;

import java.util.Objects;
import java.util.Stack;

public class PathNode implements Comparable<PathNode> {

    private final Point point;

    private final PathNode parent;

    private final int cost;

    private final ApproachDirection approachDirection;

    public PathNode(Point point, PathNode parent, int cost) {
        this.point = point;
        this.parent = parent;
        this.cost = cost;
        approachDirection = initialiseApproachDirection();
    }

    public PathNode getParent() {
        return parent;
    }

    public Point getPoint() {
        return point;
    }

    public ApproachDirection getApproachDirection() {
        return approachDirection;
    }

    public int getCost() {
        return cost;
    }

    public static String getPathString(PathNode node) {
        PathNode current = node;
        Stack<Point> stack = new Stack<>();
        StringBuilder builder = new StringBuilder();

        while (Objects.nonNull(current)) {
            stack.push(current.getPoint());
            current = current.getParent();
        }

        if (stack.isEmpty()) {
            return "FAIL";
        }

        while (!stack.isEmpty()) {
            builder.append(String.format("%d,%d ", stack.peek().getJ(), stack.peek().getI()));
            stack.pop();
        }

        return builder.toString().trim();
    }

    @Override
    public int compareTo(PathNode other) {
        if (getCost() < other.getCost()) {
            return -1;
        }
        else if (getCost() > other.getCost()) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathNode pathNode = (PathNode) o;
        return point.equals(pathNode.point);
    }

    @Override
    public String toString() {
        return String.format("((%d,%d)[%d])->", point.getI(), point.getJ(), cost);
    }

    private ApproachDirection initialiseApproachDirection() {
        if (Objects.isNull(parent)) {
            return ApproachDirection.NONE;
        }
        int deltaI = point.getI() - parent.getPoint().getI();
        int deltaJ = point.getJ() - parent.getPoint().getJ();
        return ApproachDirection.fromDelta(deltaI, deltaJ);
    }
}
