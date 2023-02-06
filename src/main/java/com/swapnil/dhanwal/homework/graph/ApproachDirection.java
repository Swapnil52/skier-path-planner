package com.swapnil.dhanwal.homework.graph;

import com.swapnil.dhanwal.homework.homework.Pair;

import java.util.HashMap;
import java.util.Map;

public enum ApproachDirection {

    NONE(0, 0),
    DOWN(+1, 0),
    RIGHT_DOWN(+1, -1),
    RIGHT(0, -1),
    RIGHT_UP(-1, -1),
    UP(-1, 0),
    LEFT_UP(-1, +1),
    LEFT(0, +1),
    LEFT_DOWN(+1, +1);

    private final int deltaI;
    private final int deltaJ;

    ApproachDirection(int deltaI, int deltaJ) {
        this.deltaI = deltaI;
        this.deltaJ = deltaJ;
    }

    public static ApproachDirection fromDelta(int deltaI, int deltaJ) {
        if (Math.abs(deltaI) > 1 || Math.abs(deltaJ) > 1) {
            throw new IllegalArgumentException("Delta in any direction must be at most 1");
        }
        Map<Pair, ApproachDirection> deltas = new HashMap<>();
        for (ApproachDirection approachDirection : values()) {
            Pair pair = new Pair(approachDirection.getDeltaI(), approachDirection.getDeltaJ());
            deltas.put(pair, approachDirection);
        }
        return deltas.get(new Pair(deltaI, deltaJ));
    }

    public int getDeltaI() {
        return deltaI;
    }

    public int getDeltaJ() {
        return deltaJ;
    }

    public static void main(String[] args) {
        System.out.println(ApproachDirection.fromDelta(1, -1));
        System.out.println(ApproachDirection.fromDelta(1, 0));
        System.out.println(ApproachDirection.fromDelta(1, 1));
        System.out.println(ApproachDirection.fromDelta(0, -1));
        System.out.println(ApproachDirection.fromDelta(0, 1));
        System.out.println(ApproachDirection.fromDelta(-1, -1));
        System.out.println(ApproachDirection.fromDelta(-1, 0));
        System.out.println(ApproachDirection.fromDelta(-1, 1));
    }
}
