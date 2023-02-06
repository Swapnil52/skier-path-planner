package com.swapnil.dhanwal.homework;

import com.swapnil.dhanwal.homework.homework;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PathCostApplication {

    public static final String INPUT_FILE = "./paths.txt";

    public static void main(String[] args) throws IOException {
        Configuration configuration = loadConfiguration();
        homework.Graph graph = new homework.Graph(configuration.map, configuration.H, configuration.W);

        List<List<homework.Point>> paths = configuration.paths.stream()
                .map(path -> path.stream()
                        .map(coordinate -> graph.get(coordinate.getI(), coordinate.getJ()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        homework.Solver solver = getSolver(configuration.type, graph, configuration.stamina);
        for (List<homework.Point> path : paths) {
            System.out.println(solver.getPathCost(path));
        }
    }

    public static class Configuration {

        private final homework.SolverType type;

        private final int W;

        private final int H;

        private final int[][] map;

        private final int stamina;

        private final List<List<Pair>> paths;

        public Configuration(homework.SolverType type, int w, int h, int[][] map, int stamina, List<List<Pair>> paths) {
            this.type = type;
            W = w;
            H = h;
            this.map = map;
            this.stamina = stamina;
            this.paths = paths;
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

        homework.SolverType type;
        int W;
        int H;
        int[][] map;
        int stamina;
        int nPaths;
        List<List<Pair>> paths = new ArrayList<>();

        String line = reader.readLine();
        type = homework.SolverType.fromLabel(line);

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
        stamina = Integer.parseInt(line);

        line = reader.readLine();
        nPaths = Integer.parseInt(line);

        while (nPaths-- > 0) {
            line = reader.readLine();
            String[] _coordinates = line.split(" ");
            List<Pair> path = new ArrayList<>();
            for (String coordinate : _coordinates) {
                int j = Integer.parseInt(coordinate.split(",")[0]);
                int i = Integer.parseInt(coordinate.split(",")[1]);
                path.add(new Pair(i, j));
            }
            paths.add(path);
        }

        return new Configuration(type, W, H, map, stamina, paths);
    }


    private static homework.Solver getSolver(homework.SolverType type, homework.Graph graph, int stamina) {
        switch (type) {
            case BFS:
                return new homework.BFSSolver(graph, stamina);
            case USC:
                return new homework.UCSSolver(graph, stamina);
            case A:
                return new homework.AStarSolver(graph, stamina);
            default:
                throw new IllegalArgumentException("Invalid type received");
        }
    }
}
