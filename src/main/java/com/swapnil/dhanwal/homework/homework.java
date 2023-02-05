package com.swapnil.dhanwal.homework;

import com.swapnil.dhanwal.homework.graph.Graph;
import com.swapnil.dhanwal.homework.graph.PathNode;
import com.swapnil.dhanwal.homework.graph.Point;
import com.swapnil.dhanwal.homework.traverser.*;
import com.swapnil.dhanwal.homework.util.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"rawtypes", "unchecked"})
public class homework {

    private static final String INPUT_FILE = "./input.txt";
    private static final String OUTPUT_FILE = "./output.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        Configuration configuration = loadConfiguration();
        Solver solver = getTraverser(configuration);

        BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE, false));
        for (Pair lodgeCoordinate : configuration.lodgeCoordinates) {
            Point source = solver.getGraph().get(configuration.startI, configuration.startJ);
            Point destination = solver.getGraph().get(lodgeCoordinate.getI(), lodgeCoordinate.getJ());

            List<PathNode> solutions = solver.solve(source, destination);

            if (Utils.isEmpty(solutions)) {
                writer.append("FAIL\n");
            }
            else {
                solutions.sort(PathNode::compareTo);
                writer.append(PathNode.getPathString(solutions.get(0)));
                writer.append("\n");
            }
        }
        writer.close();
        System.out.println((String.format("Time taken: %d", (System.currentTimeMillis() - start) / 1000)));
    }

    private static Configuration loadConfiguration() throws IOException {
        SolverType type;
        int W, H;
        int startJ, startI;
        int stamina;
        int lodges;
        List<Pair> lodgeCoordinates = new ArrayList<>();
        int[][] map;

        File file = new File(INPUT_FILE);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();
        type = SolverType.fromLabel(line);

        line = reader.readLine();
        W = Integer.parseInt(line.split(" ")[0]);
        H = Integer.parseInt(line.split(" ")[1]);

        line = reader.readLine();
        startJ = Integer.parseInt(line.split(" ")[0]);
        startI = Integer.parseInt(line.split(" ")[1]);

        line = reader.readLine();
        stamina = Integer.parseInt(line);

        line = reader.readLine();
        lodges = Integer.parseInt(line);

        int n = lodges;
        while (n-- > 0) {
            line = reader.readLine();
            int j = Integer.parseInt(line.split(" ")[0]);
            int i = Integer.parseInt(line.split(" ")[1]);
            lodgeCoordinates.add(new Pair(i, j));
        }

        map = new int[H][W];
        for (int i = 0; i < H; i++) {
            line = reader.readLine();
            String[] numbers = line.split(" ");
            for (int j = 0; j < W; j++) {
                map[i][j] = Integer.parseInt(numbers[j]);
            }
        }

        return new Configuration(type, W, H, startJ, startI, stamina, lodges, lodgeCoordinates, map);
    }

    private static Solver getTraverser(Configuration configuration) {
        switch (configuration.type) {
            case BFS:
                return new BFSSolver(new Graph(configuration.map, configuration.H, configuration.W), configuration.stamina);
            case USC:
                return new UCSSolver(new Graph(configuration.map, configuration.H, configuration.W), configuration.stamina);
            case A:
                return new AStarSolver(new Graph(configuration.map, configuration.H, configuration.W), configuration.stamina);
            default:
                throw new IllegalArgumentException("Invalid type received");
        }
    }

    public static class Configuration {

        private final SolverType type;

        private final int W;

        private final int H;

        private final int startJ;

        private final int startI;

        private final int stamina;

        private final int lodges;

        private final List<Pair> lodgeCoordinates;

        private final int[][] map;

        public Configuration(SolverType type, int w, int h, int startJ, int startI, int stamina, int lodges, List<Pair> lodgeCoordinates, int[][] map) {
            this.type = type;
            W = w;
            H = h;
            this.startJ = startJ;
            this.startI = startI;
            this.stamina = stamina;
            this.lodges = lodges;
            this.lodgeCoordinates = lodgeCoordinates;
            this.map = map;
        }

        @Override
        public String toString() {
            return "Configuration{" +
                    "type=" + type +
                    ", W=" + W +
                    ", H=" + H +
                    ", startJ=" + startJ +
                    ", startI=" + startI +
                    ", stamina=" + stamina +
                    ", lodges=" + lodges +
                    ", lodgeCoordinates=" + lodgeCoordinates +
                    ", map=" + Arrays.toString(map) +
                    '}';
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
}
