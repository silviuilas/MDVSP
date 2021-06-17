package ro.uaic.info.gui.graph;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import ro.uaic.info.Problem;
import ro.uaic.info.prb.ProblemIO;
import ro.uaic.info.prb.Tour;
import ro.uaic.info.solver.Solver;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class AcoAlgorithm implements Algorithm {
    Graph graph;
    Solver solver;
    ProblemIO problemIO;

    public AcoAlgorithm(Problem problem) {
        this.problemIO = problem.getProblemIO();
        this.solver = problem.getSolver();
    }


    @Override
    public void init(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void compute() {
        List<String> colors = generateRandomColors(problemIO.getM());

        for (int kk = 0; kk < 1000000; kk++) {
            System.out.print(kk + " ");
            Deque<Tour> solution = solver.solveAnIter();
            if (kk % 1 == 0) {
                while (graph.getEdgeCount() > 0)
                    graph.removeEdge(0);
                for (Tour tour :
                        solution) {
                    for (int i = 1; i < tour.size(); i++) {
                        String a = String.valueOf(tour.get(i - 1));
                        String b = String.valueOf(tour.get(i));
                        graph.addEdge(a + '+' + b, a, b, true);
                        Node source = graph.getNode(a);
                        source.setAttribute("ui.style", "fill-color: " + colors.get(tour.get(0)) + ";");
                    }
                }
            }
        }
    }

    private List<String> generateRandomColors(int number) {
        List<String> colors = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            int r = ThreadLocalRandom.current().nextInt(0, 255);
            int g = ThreadLocalRandom.current().nextInt(0, 255);
            int b = ThreadLocalRandom.current().nextInt(0, 255);
            String color = "rgb(" + r + "," + g + "," + b + ")";
            colors.add(color);
        }
        return colors;
    }
}
