package com.swapnil.dhanwal.homework.util;

import com.swapnil.dhanwal.homework.graph.Graph;
import com.swapnil.dhanwal.homework.graph.Point;
import com.swapnil.dhanwal.homework.traverser.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PathCostApplication {

    public static final String INPUT_FILE = "./paths.txt";

    public static void main(String[] args) throws IOException {
        Configuration configuration = loadConfiguration();
        Graph graph = new Graph(configuration.map, configuration.H, configuration.W);
        List<Point> points = configuration.coordinates.stream()
                .map(pair -> graph.get(pair.getI(), pair.getJ()))
                .collect(Collectors.toList());
        Solver solver = getSolver(configuration.type, graph);
        System.out.println(solver.getPathCost(points));
    }

    public static class Configuration {

        private final SolverType type;

        private final int W;

        private final int H;

        private final int[][] map;

        private final List<Pair> coordinates;

        public Configuration(SolverType type, int w, int h, int[][] map, List<Pair> coordinates) {
            this.type = type;
            W = w;
            H = h;
            this.map = map;
            this.coordinates = coordinates;
        }
    }

    public static class Pair {

        private final int i;

        private final int j;

        public Pair(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }
    }

    private static Configuration loadConfiguration() throws IOException {
        File file = new File(INPUT_FILE);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        SolverType type;
        int W;
        int H;
        int[][] map;
        int nPoints;
        List<Pair> coordinates = new ArrayList<>();

        String line = reader.readLine();
        type = SolverType.fromLabel(line);

        line = reader.readLine();
        W = Integer.parseInt(line.split(" ")[0]);
        H = Integer.parseInt(line.split(" ")[1]);

        map = new int[H][W];
        for (int i = 0; i < H; i++) {
            line = reader.readLine();
            String[] numbers = line.split(" ");
            for (int j = 0; j < W; j++) {
                map[i][j] = Integer.parseInt(numbers[j]);
            }
        }

        line = reader.readLine();
        nPoints = Integer.parseInt(line);

        while (nPoints-- > 0) {
            line = reader.readLine();
            String[] _coordinates = line.split(" ");
            for (String coordinate : _coordinates) {
                int j = Integer.parseInt(coordinate.split(",")[0]);
                int i = Integer.parseInt(coordinate.split(",")[1]);
                coordinates.add(new Pair(i, j));
            }
        }

        return new Configuration(type, W, H, map, coordinates);
    }


    private static Solver getSolver(SolverType type, Graph graph) {
        switch (type) {
            case BFS:
                return new BFSSolver(graph, 0);
            case USC:
                return new UCSSolver(graph, 0);
            case A:
                return new AStarSolver(graph, 0);
            default:
                throw new IllegalArgumentException("Invalid type received");
        }
    }
}
