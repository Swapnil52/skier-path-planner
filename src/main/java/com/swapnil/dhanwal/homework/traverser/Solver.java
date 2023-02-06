package com.swapnil.dhanwal.homework.traverser;

import com.swapnil.dhanwal.homework.graph.Graph;
import com.swapnil.dhanwal.homework.graph.PathNode;
import com.swapnil.dhanwal.homework.graph.Point;
import com.swapnil.dhanwal.homework.util.Utils;

import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Solver {

    protected final SolverType type;

    protected final Graph graph;

    protected final int stamina;

    public Solver(SolverType type, Graph graph, int stamina) {
        this.type = type;
        this.graph = graph;
        this.stamina = stamina;
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

    public abstract List<PathNode> solve(Point source, Point destination);

    protected abstract Queue<PathNode> initialiseQueue();

    protected List<PathNode> getNeighbouringNodes(PathNode current, Point destination) {
        return Utils.emptyIfNull(getNeighbouringPoints(current)).stream()
                .filter(next -> isNeighbourSafe(current, next))
                .map(next -> new PathNode(next, current, getCost(current, next, destination)))
                .collect(Collectors.toList());
    }

    protected abstract boolean isNeighbourSafe(PathNode current, Point next);

    protected abstract int getCost(PathNode current, Point next, Point destination);

    protected List<Point> getNeighbouringPoints(PathNode node) {
        int i = node.getPoint().getI();
        int j = node.getPoint().getJ();
        return Stream.of(graph.get(i - 1, j - 1), graph.get(i - 1, j), graph.get(i - 1, j + 1),
                        graph.get(i, j - 1), graph.get(i, j + 1), graph.get(i + 1, j - 1),
                        graph.get(i + 1, j), graph.get(i + 1, j + 1))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
