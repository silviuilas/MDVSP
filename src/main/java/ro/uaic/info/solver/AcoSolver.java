package ro.uaic.info.solver;

import ro.uaic.info.aco.AntColony;
import ro.uaic.info.aco.AntColonyGraph;
import ro.uaic.info.prb.ProblemIO;
import ro.uaic.info.prb.Tour;

import java.util.Deque;

public class AcoSolver implements Solver {
    @Override
    public Deque<Tour> solve(ProblemIO problemIO) {
        AntColonyGraph antColonyGraph = new AntColonyGraph(problemIO.getN(), problemIO.getM(), problemIO.getCost(), problemIO.getDepotsCapacity());
        AntColony antColony = new AntColony(antColonyGraph);
        return antColony.run();
    }
}
