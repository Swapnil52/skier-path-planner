package com.swapnil.dhanwal.homework.util;

import com.swapnil.dhanwal.homework.graph.PathNode;

import java.util.*;

public class Utils {

    public static <E> List<E> emptyIfNull(List<E> collection) {
        if (isEmpty(collection)) {
            return Collections.emptyList();
        }
        return collection;
    }

    public static <E> boolean isEmpty(Collection<E> collection) {
        if (Objects.isNull(collection)) {
            return true;
        }
        return collection.size() == 0;
    }

    public static void printCopy(Queue<PathNode> queue) {
        Queue<PathNode> copy = new PriorityQueue<>();
        copy.addAll(queue);

        StringBuilder builder = new StringBuilder();
        while (!copy.isEmpty()) {
            builder.append(copy.remove());
        }

        System.out.println(builder);
    }

}
