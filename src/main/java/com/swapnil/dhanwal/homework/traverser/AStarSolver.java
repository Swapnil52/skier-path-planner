package com.swapnil.dhanwal.homework.traverser;

import com.swapnil.dhanwal.homework.graph.Graph;
import com.swapnil.dhanwal.homework.graph.PathNode;
import com.swapnil.dhanwal.homework.graph.Point;

import java.util.Objects;
import java.util.PriorityQueue;

public class AStarSolver extends Solver<PriorityQueue<PathNode>> {

    public AStarSolver(Graph graph, int stamina) {
        super(SolverType.A, graph, stamina);
    }

    @Override
    protected PriorityQueue<PathNode> initialiseQueue() {
        return new PriorityQueue<>();
    }

    @Override
    protected boolean isNeighbourSafe(PathNode current, Point next) {
        if (Objects.isNull(current)) {
            throw new IllegalStateException("Current node cannot be null");
        }
        if (Objects.isNull(next)) {
            return false;
        }
        int momentum = getMomentum(current, next);
        switch (next.getType()) {
            case HILL:
                return stamina + momentum >= next.getHeight() - current.getPoint().getHeight();
            case TREE:
                return current.getPoint().getHeight() >= next.getHeight();
            default:
                throw new IllegalArgumentException(String.format("Invalid point type encountered: %s", next.getType()));
        }
    }

    /**
     * Cost = Horizontal move distance + Elevation change cost + Heuristic cost (Euclidean distance)
     */
    @Override
    protected int getCost(PathNode current, Point next, Point destination) {
        int horizontalMoveDistance = getHorizontalMoveDistance(current.getPoint(), next);
        int euclideanDistance = getEuclideanDistance(next, destination);
        int elevationChangeCost = getElevationChangeCost(current, next);
        return (horizontalMoveDistance + elevationChangeCost + euclideanDistance) + current.getCost();
    }

    private static int getMomentum(PathNode current, Point next) {
        if (Objects.isNull(current.getParent())) {
            return 0;
        }
        Point previous = current.getParent().getPoint();
        if (next.getHeight() <= current.getPoint().getHeight()) {
            return 0;
        }
        return Math.max(0, previous.getHeight() - current.getPoint().getHeight());
    }

    private int getHorizontalMoveDistance(Point current, Point next) {
        int deltaI = Math.abs(current.getI() - next.getI());
        int deltaJ = Math.abs(current.getJ() - next.getJ());
        return (deltaI > 0 && deltaJ > 0) ? 14 : 10;
    }

    private int getElevationChangeCost(PathNode current, Point next) {
        int momentum = getMomentum(current, next);
        if (next.getHeight() - current.getPoint().getHeight() <= momentum) {
            return 0;
        }
        return Math.max(0, next.getHeight() - current.getPoint().getHeight() - momentum);
    }

    private int getEuclideanDistance(Point next, Point destination) {
        int multiplier = 10;
        return (int) Math.round(Math.sqrt(Math.pow((multiplier * next.getI() - multiplier * destination.getI()), 2) + Math.pow((multiplier * next.getJ() - multiplier * destination.getJ()), 2)));
    }
}
