package com.swapnil.dhanwal.homework.traverser;

import com.swapnil.dhanwal.homework.graph.Point;

public class TraverserContext {

    private final boolean[][] visited;

    private final Point previous;

    private final Point current;

    private final Point next;

    public TraverserContext(boolean[][] visited, Point previous, Point current, Point next) {
        this.visited = visited;
        this.previous = previous;
        this.current = current;
        this.next = next;
    }

    public boolean[][] getVisited() {
        return visited;
    }

    public Point getPrevious() {
        return previous;
    }

    public Point getCurrent() {
        return current;
    }

    public Point getNext() {
        return next;
    }
}
