package com.swapnil.dhanwal.homework.traverser;

import com.swapnil.dhanwal.homework.graph.Graph;
import com.swapnil.dhanwal.homework.graph.PathNode;
import com.swapnil.dhanwal.homework.graph.Point;
import com.swapnil.dhanwal.homework.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Solver<Q extends Queue<PathNode>> {

    protected final SolverType type;

    protected final Graph graph;

    protected final int stamina;

    /*
        We'll update this whenever we encounter a PathNode for the same point with a smaller cost.
        Note: Any non-null entry in this table suggests that that point has already been visited once.
        However, unlike BFS, if we encounter a node with a shorter path, we will nullify this entry
        and enqueue the new node for processing.
    */
    protected final PathNode[][] processed;

    protected final Q queue;

    public Solver(SolverType type, Graph graph, int stamina) {
        this.type = type;
        this.graph = graph;
        this.stamina = stamina;
        this.processed = new PathNode[graph.getH()][graph.getW()];
        this.queue = initialiseQueue();
    }

    public Graph getGraph() {
        return graph;
    }

    public int getPathCost(List<Point> points) {
        Point destination = points.get(points.size() - 1);
        PathNode current = new PathNode(points.get(0), null, 0);
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < points.size(); i++) {
            if (!isNeighbourSafe(current, points.get(i))) {
                return -1;
            }
            builder.append(String.format("(%d,%d,%d)->", current.getPoint().getJ(), current.getPoint().getI(), current.getCost()));
            current = new PathNode(points.get(i), current, getCost(current, points.get(i), destination));
        }
        builder.append(String.format("(%d,%d,%d)->", current.getPoint().getJ(), current.getPoint().getI(), current.getCost()));
        System.out.println(builder);
        return current.getCost();
    }

    public List<PathNode> solve(Point source, Point destination) {
        reset();
        List<PathNode> solutions = new ArrayList<>();
        queue.add(new PathNode(source, null, 0));
        while (!queue.isEmpty()) {
            PathNode current = queue.remove();
            if (current.getPoint().equals(destination)) {
                solutions.add(current);
            }
            if (isPointProcessed(current.getPoint())) {
                if (current.getCost() < findPointInProcessed(current.getPoint()).getCost()) {
                    addNodeToProcessed(current);
                }
                else {
                    continue;
                }
            }
            else {
                addNodeToProcessed(current);
            }
            List<PathNode> neighbours = getNeighbouringNodes(current, destination);
            queue.addAll(neighbours);
        }
        return solutions;
    }

    protected abstract Q initialiseQueue();

    private List<PathNode> getNeighbouringNodes(PathNode current, Point destination) {
        return Utils.emptyIfNull(getNeighbouringPoints(current)).stream()
                .filter(next -> isNeighbourSafe(current, next))
                .map(next -> new PathNode(next, current, getCost(current, next, destination)))
                .collect(Collectors.toList());
    }

    private List<Point> getNeighbouringPoints(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        return Stream.of(graph.get(i - 1, j - 1), graph.get(i - 1, j), graph.get(i - 1, j + 1),
                        graph.get(i, j - 1), graph.get(i, j + 1), graph.get(i + 1, j - 1),
                        graph.get(i + 1, j), graph.get(i + 1, j + 1))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * For BFS and UCS, a neighbour is deemed safe if the following conditions hold:
     * <p>
     *     1. The generated neighbours must be within the bounds of the graph
     *     2. When moving to a hill, the difference between the height of the neighbour and current point must be less
     *        than or equal to the skier's stamina
     *     3. When moving to a tree, the tree's height must be less than or equal to that of the current point no matter
     *        the skier's stamina
     * </p>
     */
    protected boolean isNeighbourSafe(PathNode current, Point next) {
        if (Objects.isNull(current)) {
            throw new IllegalStateException("Current node cannot be null");
        }
        if (Objects.isNull(next)) {
            return false;
        }
        switch (next.getType()) {
            case HILL:
                return stamina >= next.getHeight() - current.getPoint().getHeight();
            case TREE:
                return current.getPoint().getHeight() >= next.getHeight();
            default:
                throw new IllegalArgumentException(String.format("Invalid point type encountered: %s", next.getType()));
        }
    }

    protected abstract int getCost(PathNode current, Point next, Point destination);

    /**
     * Utility functions for the queue and corresponding tables.
     * Note: MUST BE CALLED BEFORE EVERY CALL TO SOLVE(...)
     */
    private void reset() {
        queue.clear();
        for (int i = 0; i < graph.getH(); i++) {
            for (int j = 0; j < graph.getW(); j++) {
                processed[i][j] = null;
            }
        }
    }

    private void addNodeToProcessed(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        processed[i][j] = node;
    }

    private boolean isPointProcessed(Point point) {
        return Objects.nonNull(processed[point.getI()][point.getJ()]);
    }

    private PathNode findPointInProcessed(Point point) {
        return processed[point.getI()][point.getJ()];
    }
}
