import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class homework {

    private static final String INPUT_FILE = "input.txt";
    private static final String OUTPUT_FILE = "output.txt";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        Configuration configuration = loadConfiguration();
        Solver solver = getSolver(configuration);
        BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_FILE, false));

        for (Pair lodgeCoordinate : configuration.lodgeCoordinates) {
            Graph.Point destination = solver.getGraph().get(lodgeCoordinate.getI(), lodgeCoordinate.getJ());
            List<Graph.PathNode> solutions = solver.solve(destination);
            if (Utils.isEmpty(solutions)) {
                writer.append("FAIL\n");
            }
            else {
                solutions.sort(Graph.PathNode::compareTo);
                writer.append(Graph.PathNode.getPathString(solutions.get(0)));
                writer.append("\n");
            }
        }

        writer.close();
        System.out.println((String.format("Time taken: %d", (System.currentTimeMillis() - start) / 1000)));
    }

    private static Configuration loadConfiguration() throws IOException {
        Solver.SolverType type;
        int W, H;
        int startJ, startI;
        int stamina;
        int lodges;
        List<Pair> lodgeCoordinates = new ArrayList<>();
        int[][] map;

        File file = new File(INPUT_FILE);
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();
        type = Solver.SolverType.fromLabel(line);

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

    private static Solver getSolver(Configuration configuration) {
        Graph graph = new Graph(configuration.map, configuration.H, configuration.W);
        Graph.Point source = graph.get(configuration.startI, configuration.startJ);
        List<Graph.Point> destinations = new ArrayList<>();
        for (Pair lodgeCoordinate : configuration.lodgeCoordinates) {
            destinations.add(graph.get(lodgeCoordinate.getI(), lodgeCoordinate.getJ()));
        }
        switch (configuration.type) {
            case BFS:
                return new BFSSolver(graph, source, destinations, configuration.stamina);
            case UCS:
                return new UCSSolver(graph, source, destinations, configuration.stamina);
            case A:
                return new AStarSolver(graph, source, destinations, configuration.stamina);
            default:
                throw new IllegalArgumentException("Invalid type received");
        }
    }

    public static class Configuration {

        private final Solver.SolverType type;

        private final int W;

        private final int H;

        private final int startJ;

        private final int startI;

        private final int stamina;

        private final int lodges;

        private final List<Pair> lodgeCoordinates;

        private final int[][] map;

        public Configuration(Solver.SolverType type, int w, int h, int startJ, int startI, int stamina, int lodges, List<Pair> lodgeCoordinates, int[][] map) {
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return i == pair.i && j == pair.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j);
        }
    }

    public static class Graph {

        private final int H;

        private final int W;

        private final Point[][] points;

        public Graph(int[][] map, int H, int W) {
            this.H = H;
            this.W = W;
            this.points = new Point[H][W];
            for (int i = 0; i < H; i++) {
                for (int j = 0; j < W; j++) {
                    Point.PointType type = map[i][j] < 0 ? Point.PointType.TREE : Point.PointType.HILL;
                    this.points[i][j] = new Point(type, i, j, Math.abs(map[i][j]));
                }
            }
        }

        public Point get(int i, int j) {
            if (i < 0 || j < 0 || i >= H || j >= W) {
                return null;
            }
            return this.points[i][j];
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < H; i++) {
                StringBuilder rowBuilder = new StringBuilder();
                for (int j = 0; j < W; j++) {
                    rowBuilder.append(points[i][j].getHeight() * (points[i][j].getType() == Point.PointType.HILL ? 1 : -1));
                    rowBuilder.append(" ");
                }
                rowBuilder.append("\n");
                builder.append(rowBuilder);
            }
            return builder.toString();
        }

        public static class Point {

            private final PointType type;

            private final int i;

            private final int j;

            private final int height;

            public Point(PointType type, int i, int j, int height) {
                this.type = type;
                this.i = i;
                this.j = j;
                this.height = height;
            }

            public PointType getType() {
                return type;
            }

            public int getI() {
                return i;
            }

            public int getJ() {
                return j;
            }

            public int getHeight() {
                return height;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Point point = (Point) o;
                return i == point.i && j == point.j;
            }

            @Override
            public int hashCode() {
                return Objects.hash(i, j);
            }

            @Override
            public String toString() {
                return "Point{" +
                        "i=" + i +
                        ", j=" + j +
                        '}';
            }

            public enum PointType {
                TREE,
                HILL
            }
        }

        public static class PathNode implements Comparable<PathNode> {

            private final Point point;

            private final PathNode parent;

            private final Point destination;

            private final double cost;

            private final ApproachDirection approachDirection;

            public PathNode(Point point, PathNode parent, Point destination, double cost) {
                this.point = point;
                this.parent = parent;
                this.destination = destination;
                this.cost = cost;
                this.approachDirection = initialiseApproachDirection();
            }

            public boolean isDestination() {
                return getPoint().equals(destination);
            }

            public PathNode getParent() {
                return parent;
            }

            public Point getPoint() {
                return point;
            }

            public Point getDestination() {
                return destination;
            }

            public ApproachDirection getApproachDirection() {
                return approachDirection;
            }

            public double getCost() {
                return cost;
            }

            public static String getPathString(PathNode node) {
                PathNode current = node;
                Stack<Point> stack = new Stack<>();
                StringBuilder builder = new StringBuilder();

                while (Objects.nonNull(current)) {
                    stack.push(current.getPoint());
                    current = current.getParent();
                }

                if (stack.isEmpty()) {
                    return "FAIL";
                }

                while (!stack.isEmpty()) {
                    builder.append(String.format("%d,%d ", stack.peek().getJ(), stack.peek().getI()));
                    stack.pop();
                }

                return builder.toString().trim();
            }

            @Override
            public int compareTo(PathNode other) {
                if (getCost() < other.getCost()) {
                    return -1;
                }
                else if (getCost() > other.getCost()) {
                    return 1;
                }
                return 0;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                PathNode pathNode = (PathNode) o;
                return point.equals(pathNode.point);
            }

            @Override
            public String toString() {
                return String.format("((%d,%d)[%f])->", point.getI(), point.getJ(), cost);
            }

            private ApproachDirection initialiseApproachDirection() {
                if (Objects.isNull(parent)) {
                    return ApproachDirection.NONE;
                }
                int deltaI = point.getI() - parent.getPoint().getI();
                int deltaJ = point.getJ() - parent.getPoint().getJ();
                return ApproachDirection.fromDelta(deltaI, deltaJ);
            }

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

                private static final Map<Pair, ApproachDirection> deltas = new HashMap<>();

                static {
                    for (ApproachDirection approachDirection : values()) {
                        Pair pair = new Pair(approachDirection.getDeltaI(), approachDirection.getDeltaJ());
                        deltas.put(pair, approachDirection);
                    }
                }

                ApproachDirection(int deltaI, int deltaJ) {
                    this.deltaI = deltaI;
                    this.deltaJ = deltaJ;
                }

                public static ApproachDirection fromDelta(int deltaI, int deltaJ) {
                    return deltas.get(new Pair(deltaI, deltaJ));
                }

                public int getDeltaI() {
                    return deltaI;
                }

                public int getDeltaJ() {
                    return deltaJ;
                }
            }
        }
    }

    public abstract static class Solver {

        protected final SolverType type;

        protected final Graph graph;

        protected final Graph.Point source;

        protected final List<Graph.Point> destinations;

        protected final int stamina;

        public Solver(SolverType type, Graph graph, Graph.Point source, List<Graph.Point> destinations, int stamina) {
            this.type = type;
            this.graph = graph;
            this.source = source;
            this.destinations = destinations;
            this.stamina = stamina;
        }

        public Graph getGraph() {
            return graph;
        }

        public double getPathCost(List<Graph.Point> points) {
            Graph.Point destination = points.get(points.size() - 1);
            Graph.PathNode current = new Graph.PathNode(points.get(0), null, destination, 0);
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < points.size(); i++) {
                if (!isNeighbourSafe(current, points.get(i))) {
                    return -1;
                }
                builder.append(String.format("(%d,%d,%f)->", current.getPoint().getJ(), current.getPoint().getI(), current.getCost()));
                current = new Graph.PathNode(points.get(i), current, destination, getCost(current, points.get(i)));
            }
            builder.append(String.format("(%d,%d,%f)->", current.getPoint().getJ(), current.getPoint().getI(), current.getCost()));
            System.out.println(builder);
            return current.getCost();
        }

        public abstract List<Graph.PathNode> solve(Graph.Point destination);

        protected abstract Queue<Graph.PathNode> initialiseQueue();

        protected List<Graph.PathNode> getNeighbouringNodes(Graph.PathNode current) {
            return Utils.emptyIfNull(getNeighbouringPoints(current)).stream()
                    .filter(next -> isNeighbourSafe(current, next))
                    .map(next -> new Graph.PathNode(next, current, current.getDestination(), getCost(current, next)))
                    .collect(Collectors.toList());
        }

        protected abstract boolean isNeighbourSafe(Graph.PathNode current, Graph.Point next);

        protected abstract double getCost(Graph.PathNode current, Graph.Point next);

        protected List<Graph.Point> getNeighbouringPoints(Graph.PathNode node) {
            int i = node.getPoint().getI();
            int j = node.getPoint().getJ();
            return Stream.of(graph.get(i - 1, j - 1), graph.get(i - 1, j), graph.get(i - 1, j + 1),
                            graph.get(i, j - 1), graph.get(i, j + 1), graph.get(i + 1, j - 1),
                            graph.get(i + 1, j), graph.get(i + 1, j + 1))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        public enum SolverType {

            BFS("BFS"),
            UCS("UCS"),
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
    }

    public abstract static class UninformedSolver extends Solver {

        public UninformedSolver(SolverType type, Graph graph, Graph.Point source, List<Graph.Point> destinations, int stamina) {
            super(type, graph, source, destinations, stamina);
        }

        public List<Graph.PathNode> solve(Graph.Point destination) {
            Map<Key, Graph.PathNode> processed = new HashMap<>();
            Queue<Graph.PathNode> queue = initialiseQueue();
            List<Graph.PathNode> solutions = new ArrayList<>();
            queue.add(new Graph.PathNode(this.source, null, destination, 0));
            while (!queue.isEmpty()) {
                Graph.PathNode current = queue.remove();
                if (current.isDestination()) {
                    solutions.add(current);
                    break;
                }
                Key key = Key.of(current);
                Graph.PathNode existing = processed.get(key);
                if (Objects.isNull(existing)) {
                    processed.put(key, current);
                }
                else if (current.getCost() < existing.getCost()) {
                    processed.put(key, current);
                }
                else {
                    continue;
                }
                List<Graph.PathNode> neighbours = getNeighbouringNodes(current);
                queue.addAll(neighbours);
            }
            return solutions;
        }

        /**
         * For BFS and UCS, a neighbour is deemed safe if the following conditions hold:
         * <p>
         *     1. The generated neighbours must be within the bounds of the graph
         *     2. When moving to a hill, the difference between the height of the neighbour and current point must be less
         *        than or equal to the skier's stamina
         *     3. When moving to a tree, the tree's height must be less than or equal to that of the current point no matter
         *        the skier's stamina
         * </p>
         */
        protected boolean isNeighbourSafe(Graph.PathNode current, Graph.Point next) {
            if (Objects.isNull(current)) {
                throw new IllegalStateException("Current node cannot be null");
            }
            if (Objects.isNull(next)) {
                return false;
            }
            switch (next.getType()) {
                case HILL:
                    return stamina >= next.getHeight() - current.getPoint().getHeight();
                case TREE:
                    return current.getPoint().getHeight() >= next.getHeight();
                default:
                    throw new IllegalArgumentException(String.format("Invalid point type encountered: %s", next.getType()));
            }
        }

        private static class Key {

            private final Graph.Point point;
            private final Graph.Point destination;

            public static Key of(Graph.PathNode node) {
                return new Key(node.getPoint(), node.getDestination());
            }

            private Key(Graph.Point point, Graph.Point destination) {
                this.point = point;
                this.destination = destination;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Key key = (Key) o;
                return Objects.equals(point, key.point) && Objects.equals(destination, key.destination);
            }

            @Override
            public int hashCode() {
                return Objects.hash(point, destination);
            }
        }
    }

    public static class BFSSolver extends UninformedSolver {

        public BFSSolver(Graph graph, Graph.Point source, List<Graph.Point> destinations, int stamina) {
            super(SolverType.BFS, graph, source, destinations, stamina);
        }

        @Override
        protected LinkedList<Graph.PathNode> initialiseQueue() {
            return new LinkedList<>();
        }

        @Override
        protected double getCost(Graph.PathNode current, Graph.Point next) {
            return 10 + current.getCost();
        }
    }

    public static class UCSSolver extends UninformedSolver {

        public UCSSolver(Graph graph, Graph.Point source, List<Graph.Point> destinations, int stamina) {
            super(SolverType.UCS, graph, source, destinations, stamina);
        }

        @Override
        protected PriorityQueue<Graph.PathNode> initialiseQueue() {
            return new PriorityQueue<>();
        }

        @Override
        protected double getCost(Graph.PathNode current, Graph.Point next) {
            int deltaI = Math.abs(current.getPoint().getI() - next.getI());
            int deltaJ = Math.abs(current.getPoint().getJ() - next.getJ());
            return ((deltaI > 0 && deltaJ > 0) ? 14 : 10) + current.getCost();
        }
    }

    public static class AStarSolver extends Solver {

        public AStarSolver(Graph graph, Graph.Point source, List<Graph.Point> destinations, int stamina) {
            super(SolverType.A, graph, source, destinations, stamina);
        }

        @Override
        protected PriorityQueue<Graph.PathNode> initialiseQueue() {
            return new PriorityQueue<>();
        }

        @Override
        public List<Graph.PathNode> solve(Graph.Point destination) {
            Map<Key, Graph.PathNode> processed = new HashMap<>();
            Queue<Graph.PathNode> queue = initialiseQueue();
            List<Graph.PathNode> solutions = new ArrayList<>();
            queue.add(new Graph.PathNode(this.source, null, destination, 0));
            while (!queue.isEmpty()) {
                Graph.PathNode current = queue.remove();
                if (current.isDestination()) {
                    solutions.add(current);
                    break;
                }
                Key key = Key.of(current);
                Graph.PathNode existing = processed.get(key);
                if (Objects.isNull(existing)) {
                    processed.put(key, current);
                }
                else if (current.getCost() < existing.getCost()) {
                    processed.put(key, current);
                }
                else {
                    continue;
                }
                List<Graph.PathNode> neighbours = getNeighbouringNodes(current);
                queue.addAll(neighbours);
            }
            return solutions;
        }

        @Override
        protected boolean isNeighbourSafe(Graph.PathNode current, Graph.Point next) {
            if (Objects.isNull(current)) {
                throw new IllegalStateException("Current node cannot be null");
            }
            if (Objects.isNull(next)) {
                return false;
            }
            int momentum = getMomentum(current, next);
            switch (next.getType()) {
                case HILL:
                    return stamina + momentum >= next.getHeight() - current.getPoint().getHeight();
                case TREE:
                    return current.getPoint().getHeight() >= next.getHeight();
                default:
                    throw new IllegalArgumentException(String.format("Invalid point type encountered: %s", next.getType()));
            }
        }

        @Override
        protected double getCost(Graph.PathNode current, Graph.Point next) {
            Graph.Point destination = current.getDestination();
            int horizontalMoveDistance = getHorizontalMoveDistance(current.getPoint(), next);
            double euclideanDistance = getEuclideanDistance(next, destination);
            int elevationChangeCost = getElevationChangeCost(current, next);
            return (horizontalMoveDistance + elevationChangeCost + euclideanDistance) + current.getCost();
        }

        private static int getMomentum(Graph.PathNode current, Graph.Point next) {
            if (Objects.isNull(current.getParent())) {
                return 0;
            }
            Graph.Point previous = current.getParent().getPoint();
            if (next.getHeight() <= current.getPoint().getHeight()) {
                return 0;
            }
            return Math.max(0, previous.getHeight() - current.getPoint().getHeight());
        }

        private int getHorizontalMoveDistance(Graph.Point current, Graph.Point next) {
            int deltaI = Math.abs(current.getI() - next.getI());
            int deltaJ = Math.abs(current.getJ() - next.getJ());
            return (deltaI > 0 && deltaJ > 0) ? 14 : 10;
        }

        private int getElevationChangeCost(Graph.PathNode current, Graph.Point next) {
            int momentum = getMomentum(current, next);
            if (next.getHeight() - current.getPoint().getHeight() <= momentum) {
                return 0;
            }
            return Math.max(0, next.getHeight() - current.getPoint().getHeight() - momentum);
        }

        private double getEuclideanDistance(Graph.Point next, Graph.Point destination) {
            double multiplier = 9.85;
            return Math.sqrt(Math.pow((multiplier * next.getI() - multiplier * destination.getI()), 2) + Math.pow((multiplier * next.getJ() - multiplier * destination.getJ()), 2));
        }

        private static class Key {

            private final Graph.Point point;

            private final Graph.PathNode.ApproachDirection direction;

            private final Graph.Point destination;

            public static Key of(Graph.PathNode node) {
                return new Key(node.getPoint(), node.getApproachDirection(), node.getDestination());
            }

            private Key(Graph.Point point, Graph.PathNode.ApproachDirection direction, Graph.Point destination) {
                this.point = point;
                this.direction = direction;
                this.destination = destination;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Key key = (Key) o;
                return Objects.equals(point, key.point) && direction == key.direction && Objects.equals(destination, key.destination);
            }

            @Override
            public int hashCode() {
                return Objects.hash(point, direction, destination);
            }
        }
    }

    public static class Utils {

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
    }
}
