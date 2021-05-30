package ro.uaic.info;

import org.graphstream.algorithm.Algorithm;
import ro.uaic.info.graph.AcoAlgorithm;
import ro.uaic.info.graph.AcoGraphGen;
import ro.uaic.info.graph.RandomGraphGen;
import ro.uaic.info.graph.RenderGraph;
import ro.uaic.info.prb.ProblemIO;
import ro.uaic.info.solver.AcoSolver;
import ro.uaic.info.solver.Solver;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
//        AcoGraphGen randomGraphGen = new AcoGraphGen(5, 50, new int[]{15, 15, 15, 15, 15}, 1000, 1000);
//        RenderGraph renderGraph = new RenderGraph(randomGraphGen.getGraph());
//
//        ProblemIO problemIO = new ProblemIO();
//        problemIO.populate(randomGraphGen.getFile());
//        AcoSolver solver = new AcoSolver();
//        solver.init(problemIO);
//        Problem problem = new Problem(problemIO, solver);
//        Algorithm algorithm = new AcoAlgorithm(problem);
//        algorithm.init(renderGraph.getGraph());
//        algorithm.compute();


        ProblemIO problemIO = new ProblemIO();
        problemIO.read("src/main/java/ro/uaic/info/Dataset/Mdvsp_4dep_500trips/m4n500s0.inp");
        //problemIO.read("src/main/java/ro/uaic/info/generator/MDVSP-data/m4n200s0.inp");
        Solver solver = new AcoSolver();
        Problem problem = new Problem(problemIO, solver);
        problem.run();
    }
}
