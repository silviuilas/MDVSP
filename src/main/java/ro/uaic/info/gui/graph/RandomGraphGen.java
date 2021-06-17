package ro.uaic.info.gui.graph;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import ro.uaic.info.helpers.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomGraphGen {
    protected Graph graph;
    private final int number_of_points;
    private final int maxX;
    private final int maxY;

    public RandomGraphGen(int number_of_points, int maxX, int maxY) {
        this.number_of_points = number_of_points;
        this.maxX = maxX;
        this.maxY = maxY;
        graph = new SingleGraph("RandomGraph");
    }

    public void init() {
        List<Pair<Integer, Integer>> coords = getRandomPoints(number_of_points, maxX, maxY);
        addVertexes(graph, coords, 0);
        //connectVertexes(graph);
    }

    protected List<Pair<Integer, Integer>> getRandomPoints(int number_of_points, int maxX, int maxY) {
        List<Pair<Integer, Integer>> coords = new ArrayList<>();
        for (int i = 0; i < number_of_points; i++) {
            int randomX = ThreadLocalRandom.current().nextInt(0, maxX + 1);
            int randomY = ThreadLocalRandom.current().nextInt(0, maxY + 1);
            Pair<Integer, Integer> pair = new Pair<>(randomX, randomY);
            coords.add(pair);
        }
        return coords;
    }

    protected List<Pair<Integer, Integer>> getOutsidePoints(int number_of_points, int maxX, int maxY) {
        List<Pair<Integer, Integer>> coords = new ArrayList<>();
        Random r = new Random();
        NormalDistribution normal = new NormalDistributionImpl(0.5, 0.2);

        for (int i = 0; i < number_of_points; i++) {
            try {
                int randomX = (int) (normal.cumulativeProbability(r.nextDouble()) * maxX);
                int randomY = (int) (normal.cumulativeProbability(r.nextDouble()) * maxY);
                Pair<Integer, Integer> pair = new Pair<>(randomX, randomY);
                coords.add(pair);
            } catch (MathException e) {
                e.printStackTrace();
            }
        }
        return coords;
    }

    protected List<Pair<Integer, Integer>> getPointsNearCircle(int number_of_points, int maxX, int maxY) {
        List<Pair<Integer, Integer>> coords = new ArrayList<>();
        Random r = new Random();
        NormalDistribution normal = new NormalDistributionImpl(0.5, 0.2);

        Map<Double, Double> distribution = new HashMap<>();
        for (int i = 0; i < number_of_points; i++) {
            double x = ThreadLocalRandom.current().nextDouble(0, 1) * 2 - 1;
            double y = Math.sqrt((1.0 - (Math.pow(x, 2))));
            distribution.putIfAbsent(x, y);
            if (r.nextDouble() > 0.5) {
                y = -y;
            }
            int randomX = (int) (x * (maxX / 2));
            int randomY = (int) (y * (maxX / 2));
            randomX += maxX / 2;
            randomY += maxY / 2;
            Pair<Integer, Integer> pair = new Pair<>(randomX, randomY);
            coords.add(pair);
        }
        System.out.println(distribution);
        return coords;
    }

    protected List<Pair<Integer, Integer>> getPointsNearCircleSmart(int number_of_points, int maxX, int maxY, double standardDev) {
        List<Pair<Integer, Integer>> coords = new ArrayList<>();
        Random r = new Random();
        double pi = 3.14159265359;
        Map<Double, Double> distribution = new HashMap<>();
        for (int i = 0; i < number_of_points; i++) {
            double nr = r.nextDouble();
            double x = Math.cos(nr * 2 * pi);
            double y = Math.sin(nr * 2 * pi);
            int addX = (int) (r.nextGaussian() * standardDev + 0);
            int addY = (int) (r.nextGaussian() * standardDev + 0);
            int randomX = (int) (x * maxX / 2) + maxX / 2 + addX;
            int randomY = (int) (y * maxY / 2) + maxY / 2 + addY;
            Pair<Integer, Integer> pair = new Pair<>(randomX, randomY);
            coords.add(pair);
        }
        System.out.println(distribution);
        return coords;
    }

    protected List<Pair<Integer, Integer>> getInsidePoints(int number_of_points, int maxX, int maxY, double desiredStandardDeviation) {
        List<Pair<Integer, Integer>> coords = new ArrayList<>();
        Random r = new Random();
        double desiredMean = maxX / 2.0;

        for (int i = 0; i < number_of_points; i++) {
            int randomX = (int) (r.nextGaussian() * desiredStandardDeviation + desiredMean);
            int randomY = (int) (r.nextGaussian() * desiredStandardDeviation + desiredMean);
            Pair<Integer, Integer> pair = new Pair<>(randomX, randomY);
            coords.add(pair);
        }
        return coords;
    }


    protected Graph addVertexes(Graph graph, List<Pair<Integer, Integer>> coords, int offset) {
        int i = offset;
        for (Pair<Integer, Integer> cord :
                coords) {
            Node node = graph.addNode(String.valueOf(i));
            node.setAttribute("x", cord.getFirst());
            node.setAttribute("y", cord.getSecond());
            i++;
        }
        return graph;
    }

    private Graph connectVertexes(Graph graph) {
        graph.nodes().forEach(a -> {
            graph.nodes().forEach(b -> {
                if (!a.getId().equals(b.getId())) {
                    String id = a.getId().concat("+").concat(b.getId());
                    graph.addEdge(id, a, b, true);
                }
            });
        });
        return graph;
    }

    public Graph getGraph() {
        return graph;
    }
}

//class RandomCircle {
//    double RAD, XC, YC;
//
//    public RandomCircle(double radius, double x_center, double y_center) {
//        RAD = radius;
//        XC = x_center;
//        YC = y_center;
//    }
//
//    public double[] randPoint() {
//        double ang = Math.random() * 2 * Math.PI,
//                hyp = Math.sqrt(Math.random()) * RAD,
//                adj = Math.cos(ang) * hyp,
//                opp = Math.sin(ang) * hyp;
//        return new double[]{XC + adj, YC + opp};
//    }
//}
