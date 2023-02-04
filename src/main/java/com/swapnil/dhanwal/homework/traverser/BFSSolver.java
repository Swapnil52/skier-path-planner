package com.swapnil.dhanwal.homework.traverser;

import com.swapnil.dhanwal.homework.graph.Graph;
import com.swapnil.dhanwal.homework.graph.PathNode;
import com.swapnil.dhanwal.homework.graph.Point;

import java.util.LinkedList;

public class BFSSolver extends Solver<LinkedList<PathNode>> {

    public BFSSolver(Graph graph, int stamina) {
        super(SolverType.BFS, graph, stamina);
    }

    @Override
    protected LinkedList<PathNode> initialiseQueue() {
        return new LinkedList<>();
    }

    /**
     * For BFS, the cost of moving to a neighbour is constant, no matter the direction
     */
    @Override
    protected int getCost(PathNode current, Point next, Point destination) {
        return 10 + current.getCost();
    }
}
