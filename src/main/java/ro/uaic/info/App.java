package ro.uaic.info;

import ro.uaic.info.prb.ProblemIO;
import ro.uaic.info.solver.AcoSolver;
import ro.uaic.info.solver.Solver;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        ProblemIO problemIO = new ProblemIO();
        problemIO.read("src/main/java/ro/uaic/info/Dataset/Mdvsp_4dep_500trips/m4n500s0.inp");
        Solver solver = new AcoSolver();
        Problem problem = new Problem(problemIO, solver);
        problem.run();
    }
}
