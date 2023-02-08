import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PathCostApplication {

    public static final String INPUT_FILE = "./paths.txt";

    public static void main(String[] args) throws IOException {
        Configuration configuration = loadConfiguration();
        homework.Graph graph = new homework.Graph(configuration.map, configuration.H, configuration.W);
        List<List<homework.Graph.Point>> paths = configuration.paths.stream()
                .map(pairs -> pairs.stream()
                        .map(pair -> graph.get(pair.getI(), pair.getJ()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        homework.Solver solver = getSolver(configuration);
        for (List<homework.Graph.Point> path : paths) {
            System.out.println(solver.getPathCost(path));
        }
    }

    public static class Configuration {

        private final homework.Solver.SolverType type;

        private final int W;

        private final int H;

        private final int startJ;

        private final int startI;

        private final int stamina;

        private final int lodges;

        private final List<homework.Pair> lodgeCoordinates;

        private final int[][] map;

        private final List<List<homework.Pair>> paths;

        public Configuration(homework.Solver.SolverType type, int w, int h, int startJ, int startI, int stamina, int lodges, List<homework.Pair> lodgeCoordinates, int[][] map, List<List<homework.Pair>> paths) {
            this.type = type;
            W = w;
            H = h;
            this.startJ = startJ;
            this.startI = startI;
            this.stamina = stamina;
            this.lodges = lodges;
            this.lodgeCoordinates = lodgeCoordinates;
            this.map = map;
            this.paths = paths;
        }

        public int getW() {
            return W;
        }

        public int getH() {
            return H;
        }

        public int getLodges() {
            return lodges;
        }
    }

    private static Configuration loadConfiguration() throws IOException {
        homework.Solver.SolverType type;
        int W, H;
        int startJ, startI;
        int stamina;
        int lodges;
        List<homework.Pair> lodgeCoordinates = new ArrayList<>();
        int[][] map;
        int nPaths;
        List<List<homework.Pair>> paths = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(INPUT_FILE));

        String line = reader.readLine();
        type = homework.Solver.SolverType.fromLabel(line);

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
            lodgeCoordinates.add(new homework.Pair(i, j));
        }

        map = new int[H][W];
        for (int i = 0; i < H; i++) {
            line = reader.readLine();
            String[] numbers = line.split(" ");
            for (int j = 0; j < W; j++) {
                map[i][j] = Integer.parseInt(numbers[j]);
            }
        }

        line = reader.readLine();
        nPaths = Integer.parseInt(line);
        while (nPaths-- > 0) {
            line = reader.readLine();
            String[] _coordinates = line.split(" ");
            List<homework.Pair> path = new ArrayList<>();
            for (String coordinate : _coordinates) {
                int j = Integer.parseInt(coordinate.split(",")[0]);
                int i = Integer.parseInt(coordinate.split(",")[1]);
                path.add(new homework.Pair(i, j));
            }
            paths.add(path);
        }
        return new Configuration(type, W, H, startJ, startI, stamina, lodges, lodgeCoordinates, map, paths);
    }

    private static homework.Solver getSolver(Configuration configuration) {
        homework.Solver.SolverType type = configuration.type;
        homework.Graph graph = new homework.Graph(configuration.map, configuration.H, configuration.W);
        homework.Graph.Point source = graph.get(configuration.startI, configuration.startJ);
        List<homework.Graph.Point> destinations = configuration.lodgeCoordinates.stream()
                .map(pair -> graph.get(pair.getI(), pair.getJ()))
                .collect(Collectors.toList());
        int stamina = configuration.stamina;
        switch (type) {
            case BFS:
                return new homework.BFSSolver(graph, source, destinations, stamina);
            case UCS:
                return new homework.UCSSolver(graph, source, destinations, stamina);
            case A:
                return new homework.AStarSolver(graph, source, destinations, stamina);
            default:
                throw new IllegalArgumentException("Invalid type received");
        }
    }
}