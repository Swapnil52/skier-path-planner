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

    /*
        We'll update this table whenever we enqueue / dequeue a node. Help's with constant time access
        to a node in the queue.
    */
    protected final PathNode[][] enqueued;

    /*
        We'll use this queue to store nodes for processing. Initially, it will only contain the source
        node.
    */
    protected final Q queue;

    public Solver(SolverType type, Graph graph, int stamina) {
        this.type = type;
        this.graph = graph;
        this.stamina = stamina;
        this.processed = new PathNode[graph.getH()][graph.getW()];
        this.enqueued = new PathNode[graph.getH()][graph.getW()];
        this.queue = initialiseQueue();
    }

    public Graph getGraph() {
        return graph;
    }

    public int getPathCost(List<Point> points) {
        Point destination = points.get(points.size() - 1);
        PathNode current = new PathNode(points.get(0), null, 0);
        for (int i = 1; i < points.size(); i++) {
            current = new PathNode(points.get(i), current, current.getCost() + getCost(current, points.get(i), destination));
        }
        return current.getCost();
    }

    public List<PathNode> solve(Point source, Point destination) {
        reset();
        List<PathNode> solutions = new ArrayList<>();
        enqueue(new PathNode(source, null, 0));
        while (!queue.isEmpty()) {
            PathNode current = dequeue();
            if (current.getPoint().equals(destination)) {
                solutions.add(current);
                break;
            }
            addToProcessed(current);
            List<PathNode> neighbours = getNeighbouringNodes(current, destination);
            for (PathNode neighbour : neighbours) {
                if (!isEnqueued(neighbour) && !isProcessed(neighbour)) {
                    enqueue(neighbour);
                }
                else if (isEnqueued(neighbour)) {
                    PathNode existing = findInQueue(neighbour);
                    if (neighbour.compareTo(existing) < 0) {
                        removeFromQueue(existing);
                        enqueue(neighbour);
                    }
                }
                else if (isProcessed(neighbour)) {
                    PathNode existing = findInProcessed(neighbour);
                    if (neighbour.compareTo(existing) < 0) {
                        removeFromProcessed(existing);
                        enqueue(neighbour);
                    }
                }
            }
        }
        return solutions;
    }

    protected abstract Q initialiseQueue();

    private List<PathNode> getNeighbouringNodes(PathNode current, Point destination) {
        return Utils.emptyIfNull(getNeighbouringPoints(current)).stream()
                .filter(next -> isNeighbourSafe(current, next))
                .map(next -> new PathNode(next, current, getCost(current, next, destination) + current.getCost()))
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
                enqueued[i][j] = null;
            }
        }
    }

    private void enqueue(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        enqueued[i][j] = node;
        queue.add(node);
    }

    private PathNode dequeue() {
        PathNode node = queue.remove();
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        enqueued[i][j] = null;
        return node;
    }

    private boolean isEnqueued(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        return Objects.nonNull(enqueued[i][j]);
    }

    private PathNode findInQueue(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        return enqueued[i][j];
    }

    private void removeFromQueue(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        PathNode toBeRemoved = enqueued[i][j];
        queue.remove(toBeRemoved);
    }

    private void addToProcessed(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        processed[i][j] = node;
    }

    private boolean isProcessed(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        return Objects.nonNull(processed[i][j]);
    }

    private PathNode findInProcessed(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        return processed[i][j];
    }

    private void removeFromProcessed(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        processed[i][j] = null;
    }
}
