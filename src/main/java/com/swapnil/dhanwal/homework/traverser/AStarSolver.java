package com.swapnil.dhanwal.homework.traverser;

import com.swapnil.dhanwal.homework.graph.ApproachDirection;
import com.swapnil.dhanwal.homework.graph.Graph;
import com.swapnil.dhanwal.homework.graph.PathNode;
import com.swapnil.dhanwal.homework.graph.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

public class AStarSolver extends Solver {

    private final PathNode[][][] processed;

    private final PriorityQueue<PathNode> queue;

    public AStarSolver(Graph graph, int stamina) {
        super(SolverType.A, graph, stamina);
        processed = new PathNode[graph.getH()][graph.getW()][ApproachDirection.values().length];
        queue = initialiseQueue();
    }

    @Override
    protected PriorityQueue<PathNode> initialiseQueue() {
        return new PriorityQueue<>();
    }

    @Override
    public List<PathNode> solve(Point source, Point destination) {
        reset();
        List<PathNode> solutions = new ArrayList<>();
        queue.add(new PathNode(source, null, 0));
        while (!queue.isEmpty()) {
            PathNode current = queue.remove();
            if (current.getPoint().equals(destination)) {
                solutions.add(current);
            }
            PathNode existing = findNodeInProcessed(current.getPoint(), current.getApproachDirection());
            if (Objects.isNull(existing) || current.getCost() < existing.getCost()) {
                addNodeToProcessed(current);
            }
            else {
                continue;
            }
            List<PathNode> neighbours = getNeighbouringNodes(current, destination);
            queue.addAll(neighbours);
        }
        return solutions;
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

    /**
     * Utility functions for the queue and corresponding tables.
     * Note: MUST BE CALLED BEFORE EVERY CALL TO SOLVE(...)
     */
    private void reset() {
        queue.clear();
        for (int i = 0; i < graph.getH(); i++) {
            for (int j = 0; j < graph.getW(); j++) {
                for (int k = 0; k < ApproachDirection.values().length; k++) {
                    processed[i][j][k] = null;
                }
            }
        }
    }

    private void addNodeToProcessed(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        ApproachDirection approachDirection = node.getApproachDirection();
        processed[i][j][approachDirection.ordinal()] = node;
    }

    private PathNode findNodeInProcessed(Point point, ApproachDirection approachDirection) {
        return processed[point.getI()][point.getJ()][approachDirection.ordinal()];
    }
}
