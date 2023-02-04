package com.swapnil.dhanwal.homework.traverser;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SolverType {

    BFS("BFS"),
    USC("UCS"),
    A("A*");

    private static final Map<String, SolverType> valuesMap = Arrays.stream(values())
            .collect(Collectors.toMap(SolverType::getLabel, Function.identity()));

    private final String label;

    SolverType(String label) {
        this.label = label;
    }

    public static SolverType fromLabel(String label) {
        return valuesMap.get(label);
    }

    public String getLabel() {
        return label;
    }
}
