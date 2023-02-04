package com.swapnil.dhanwal.homework.traverser;

import com.swapnil.dhanwal.homework.graph.Graph;
import com.swapnil.dhanwal.homework.graph.PathNode;
import com.swapnil.dhanwal.homework.graph.Point;
import com.swapnil.dhanwal.homework.graph.PointType;

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
        return horizontalMoveDistance + elevationChangeCost + euclideanDistance;
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

    private static int getHorizontalMoveDistance(Point current, Point next) {
        int deltaI = Math.abs(current.getI() - next.getI());
        int deltaJ = Math.abs(current.getJ() - next.getJ());
        return (deltaI > 0 && deltaJ > 0) ? 14 : 10;
    }

    private static int getElevationChangeCost(PathNode current, Point next) {
        int momentum = getMomentum(current, next);
        if (next.getHeight() - current.getPoint().getHeight() <= momentum) {
            return 0;
        }
        return Math.max(0, next.getHeight() - current.getPoint().getHeight() - momentum);
    }

    private int getEuclideanDistance(Point next, Point destination) {
        return (int) Math.round(Math.sqrt(Math.pow((10 * next.getI() - 10 * destination.getI()), 2) + Math.pow((10 * next.getJ() - 10 * destination.getJ()), 2)));
    }

    public static void main(String[] args) {
        Point previous = new Point(PointType.HILL, 0, 0, 20);
        Point current = new Point(PointType.HILL, 0, 1, 8);
        Point next = new Point(PointType.HILL, 0, 2, 25);

        PathNode pathNode = new PathNode(current, new PathNode(previous, null, 10), 20);
        System.out.println(getElevationChangeCost(pathNode, next));
    }
}
