package ro.uaic.info.graph;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import ro.uaic.info.helpers.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomGraphGen {
    Graph graph;

    public RandomGraphGen(int number_of_points, int maxX, int maxY) {

        List<Pair<Integer, Integer>> coords = getRandomPoints(number_of_points, maxX, maxY);
        graph = new SingleGraph("RandomGraph");
        addVertexes(graph, coords);
        //connectVertexes(graph);

    }

    private List<Pair<Integer, Integer>> getRandomPoints(int number_of_points, int maxX, int maxY) {
        List<Pair<Integer, Integer>> coords = new ArrayList<>();

        for (int i = 0; i < number_of_points; i++) {
            int randomX = ThreadLocalRandom.current().nextInt(0, maxX + 1);
            int randomY = ThreadLocalRandom.current().nextInt(0, maxY + 1);
            Pair<Integer, Integer> pair = new Pair<>(randomX, randomY);
            coords.add(pair);
        }
        return coords;
    }

    private Graph addVertexes(Graph graph, List<Pair<Integer, Integer>> coords) {
        int i = 0;
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
