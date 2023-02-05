package com.swapnil.dhanwal.homework.traverser;

import com.swapnil.dhanwal.homework.graph.Graph;
import com.swapnil.dhanwal.homework.graph.PathNode;
import com.swapnil.dhanwal.homework.graph.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class UCSSolver extends Solver {

    public UCSSolver(Graph graph, int stamina) {
        super(SolverType.USC, graph, stamina);
    }

    @Override
    protected PriorityQueue<PathNode> initialiseQueue() {
        return new PriorityQueue<>();
    }

    /**
     * For UCS, the cost of a move is calculated as follows:
     * <p>
     *     1. If the next vertex is diagonal in any direction, cost += 14
     *     2. Else cost += 10
     * </p>
     */
    protected int getCost(PathNode current, Point next, Point destination) {
        int deltaI = Math.abs(current.getPoint().getI() - next.getI());
        int deltaJ = Math.abs(current.getPoint().getJ() - next.getJ());
        return ((deltaI > 0 && deltaJ > 0) ? 14 : 10) + current.getCost();
    }
}
