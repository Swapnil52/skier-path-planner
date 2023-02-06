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
                    PointType type = map[i][j] < 0 ? PointType.TREE : PointType.HILL;
                    this.points[i][j] = new Point(type, i, j, Math.abs(map[i][j]));
                }
            }
        }

        public int getH() {
            return H;
        }

        public int getW() {
            return W;
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
                    rowBuilder.append(points[i][j].getHeight() * (points[i][j].getType() == PointType.HILL ? 1 : -1));
                    rowBuilder.append(" ");
                }
                rowBuilder.append("\n");
                builder.append(rowBuilder);
            }
            return builder.toString();
        }
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

    public static class PathNode implements Comparable<PathNode> {

        private final Point point;

        private final PathNode parent;

        private final int cost;

        private final ApproachDirection approachDirection;

        public PathNode(Point point, PathNode parent, int cost) {
            this.point = point;
            this.parent = parent;
            this.cost = cost;
            approachDirection = initialiseApproachDirection();
        }

        public PathNode getParent() {
            return parent;
        }

        public Point getPoint() {
            return point;
        }

        public ApproachDirection getApproachDirection() {
            return approachDirection;
        }

        public int getCost() {
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
            return String.format("((%d,%d)[%d])->", point.getI(), point.getJ(), cost);
        }

        private ApproachDirection initialiseApproachDirection() {
            if (Objects.isNull(parent)) {
                return ApproachDirection.NONE;
            }
            int deltaI = point.getI() - parent.getPoint().getI();
            int deltaJ = point.getJ() - parent.getPoint().getJ();
            return ApproachDirection.fromDelta(deltaI, deltaJ);
        }
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
        public boolean equals(Object obj) {
            Point otherPoint = (Point) obj;
            if (otherPoint == null) {
                return false;
            }
            return this.i == otherPoint.getI() && this.j == otherPoint.getJ();
        }

        @Override
        public String toString() {
            return "Point{" +
                    "i=" + i +
                    ", j=" + j +
                    '}';
        }
    }

    public enum PointType {
        TREE,
        HILL
    }

    public static class AStarSolver extends Solver {

        private final PathNode[][][] processed;

        private final PriorityQueue<PathNode> queue;

        public AStarSolver(Graph graph, int stamina) {
            super(SolverType.A, graph, stamina);
            processed = new PathNode[graph.getH()][graph.getW()][ApproachDirection.values().length];
            queue = initialiseQueue();
        }

        @Override
        protected PriorityQueue<PathNode> initialiseQueue() {
            return new PriorityQueue<>();
        }

        @Override
        public List<PathNode> solve(Point source, Point destination) {
            reset();
            List<PathNode> solutions = new ArrayList<>();
            queue.add(new PathNode(source, null, 0));
            while (!queue.isEmpty()) {
                PathNode current = queue.remove();
                if (current.getPoint().equals(destination)) {
                    solutions.add(current);
                }
                PathNode existing = findNodeInProcessed(current.getPoint(), current.getApproachDirection());
                if (Objects.isNull(existing) || current.getCost() < existing.getCost()) {
                    addNodeToProcessed(current);
                }
                else {
                    continue;
                }
                List<PathNode> neighbours = getNeighbouringNodes(current, destination);
                queue.addAll(neighbours);
            }
            return solutions;
        }

        @Override
        protected boolean isNeighbourSafe(PathNode current, Point next) {
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

        /**
         * Cost = Horizontal move distance + Elevation change cost + Heuristic cost (Euclidean distance)
         */
        @Override
        protected int getCost(PathNode current, Point next, Point destination) {
            int horizontalMoveDistance = getHorizontalMoveDistance(current.getPoint(), next);
            int euclideanDistance = getEuclideanDistance(next, destination);
            int elevationChangeCost = getElevationChangeCost(current, next);
            return (horizontalMoveDistance + elevationChangeCost + euclideanDistance) + current.getCost();
        }

        private static int getMomentum(PathNode current, Point next) {
            if (Objects.isNull(current.getParent())) {
                return 0;
            }
            Point previous = current.getParent().getPoint();
            if (next.getHeight() <= current.getPoint().getHeight()) {
                return 0;
            }
            return Math.max(0, previous.getHeight() - current.getPoint().getHeight());
        }

        private int getHorizontalMoveDistance(Point current, Point next) {
            int deltaI = Math.abs(current.getI() - next.getI());
            int deltaJ = Math.abs(current.getJ() - next.getJ());
            return (deltaI > 0 && deltaJ > 0) ? 14 : 10;
        }

        private int getElevationChangeCost(PathNode current, Point next) {
            int momentum = getMomentum(current, next);
            if (next.getHeight() - current.getPoint().getHeight() <= momentum) {
                return 0;
            }
            return Math.max(0, next.getHeight() - current.getPoint().getHeight() - momentum);
        }

        private int getEuclideanDistance(Point next, Point destination) {
            int multiplier = 10;
            return (int) Math.round(Math.sqrt(Math.pow((multiplier * next.getI() - multiplier * destination.getI()), 2) + Math.pow((multiplier * next.getJ() - multiplier * destination.getJ()), 2)));
        }

        /**
         * Utility functions for the queue and corresponding tables.
         * Note: MUST BE CALLED BEFORE EVERY CALL TO SOLVE(...)
         */
        private void reset() {
            queue.clear();
            for (int i = 0; i < graph.getH(); i++) {
                for (int j = 0; j < graph.getW(); j++) {
                    for (int k = 0; k < ApproachDirection.values().length; k++) {
                        processed[i][j][k] = null;
                    }
                }
            }
        }

        private void addNodeToProcessed(PathNode node) {
            int i = node.getPoint().getI();
            int j = node.getPoint().getJ();
            ApproachDirection approachDirection = node.getApproachDirection();
            processed[i][j][approachDirection.ordinal()] = node;
        }

        private PathNode findNodeInProcessed(Point point, ApproachDirection approachDirection) {
            return processed[point.getI()][point.getJ()][approachDirection.ordinal()];
        }
    }

    public static class BFSSolver extends UninformedSolver {

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

    public abstract static class Solver {

        protected final SolverType type;

        protected final Graph graph;

        protected final int stamina;

        public Solver(SolverType type, Graph graph, int stamina) {
            this.type = type;
            this.graph = graph;
            this.stamina = stamina;
        }

        public Graph getGraph() {
            return graph;
        }

        public int getPathCost(List<Point> points) {
            Point destination = points.get(points.size() - 1);
            PathNode current = new PathNode(points.get(0), null, 0);
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < points.size(); i++) {
                if (!isNeighbourSafe(current, points.get(i))) {
                    return -1;
                }
                builder.append(String.format("(%d,%d,%d)->", current.getPoint().getJ(), current.getPoint().getI(), current.getCost()));
                current = new PathNode(points.get(i), current, getCost(current, points.get(i), destination));
            }
            builder.append(String.format("(%d,%d,%d)->", current.getPoint().getJ(), current.getPoint().getI(), current.getCost()));
            System.out.println(builder);
            return current.getCost();
        }

        public abstract List<PathNode> solve(Point source, Point destination);

        protected abstract Queue<PathNode> initialiseQueue();

        protected List<PathNode> getNeighbouringNodes(PathNode current, Point destination) {
            return Utils.emptyIfNull(getNeighbouringPoints(current)).stream()
                    .filter(next -> isNeighbourSafe(current, next))
                    .map(next -> new PathNode(next, current, getCost(current, next, destination)))
                    .collect(Collectors.toList());
        }

        protected abstract boolean isNeighbourSafe(PathNode current, Point next);

        protected abstract int getCost(PathNode current, Point next, Point destination);

        protected List<Point> getNeighbouringPoints(PathNode node) {
            int i = node.getPoint().getI();
            int j = node.getPoint().getJ();
            return Stream.of(graph.get(i - 1, j - 1), graph.get(i - 1, j), graph.get(i - 1, j + 1),
                            graph.get(i, j - 1), graph.get(i, j + 1), graph.get(i + 1, j - 1),
                            graph.get(i + 1, j), graph.get(i + 1, j + 1))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

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

    public static class UCSSolver extends UninformedSolver {

        public UCSSolver(Graph graph, int stamina) {
            super(SolverType.USC, graph, stamina);
        }

        @Override
        protected PriorityQueue<PathNode> initialiseQueue() {
            return new PriorityQueue<>();
        }

        /**
         * For UCS, the cost of a move is calculated as follows:
         * <p>
         *     1. If the next vertex is diagonal in any direction, cost += 14
         *     2. Else cost += 10
         * </p>
         */
        protected int getCost(PathNode current, Point next, Point destination) {
            int deltaI = Math.abs(current.getPoint().getI() - next.getI());
            int deltaJ = Math.abs(current.getPoint().getJ() - next.getJ());
            return ((deltaI > 0 && deltaJ > 0) ? 14 : 10) + current.getCost();
        }
    }

    public abstract static class UninformedSolver extends Solver {

        /*
            We'll update this whenever we encounter a PathNode for the same point with a smaller cost.
            Note: Any non-null entry in this table suggests that that point has already been visited once.
            However, unlike BFS, if we encounter a node with a shorter path, we will nullify this entry
            and enqueue the new node for processing.
        */
        protected final PathNode[][] processed;

        protected final Queue<PathNode> queue;

        public UninformedSolver(SolverType type, Graph graph, int stamina) {
            super(type, graph, stamina);
            this.processed = new PathNode[graph.getH()][graph.getW()];
            this.queue = initialiseQueue();
        }

        public List<PathNode> solve(Point source, Point destination) {
            reset();
            List<PathNode> solutions = new ArrayList<>();
            queue.add(new PathNode(source, null, 0));
            while (!queue.isEmpty()) {
                PathNode current = queue.remove();
                if (current.getPoint().equals(destination)) {
                    solutions.add(current);
                    break;
                }
                PathNode existing = findPointInProcessed(current.getPoint());
                if (Objects.isNull(existing) || current.getCost() < existing.getCost()) {
                    addNodeToProcessed(current);
                }
                else {
                    continue;
                }
                List<PathNode> neighbours = getNeighbouringNodes(current, destination);
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
        protected boolean isNeighbourSafe(PathNode current, Point next) {
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

        /**
         * Utility functions for the queue and corresponding tables.
         * Note: MUST BE CALLED BEFORE EVERY CALL TO SOLVE(...)
         */
        private void reset() {
            queue.clear();
            for (int i = 0; i < graph.getH(); i++) {
                for (int j = 0; j < graph.getW(); j++) {
                    processed[i][j] = null;
                }
            }
        }

        private void addNodeToProcessed(PathNode node) {
            int i = node.getPoint().getI();
            int j = node.getPoint().getJ();
            processed[i][j] = node;
        }

        private PathNode findPointInProcessed(Point point) {
            return processed[point.getI()][point.getJ()];
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
