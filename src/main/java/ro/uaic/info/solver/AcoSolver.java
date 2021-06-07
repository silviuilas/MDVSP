package ro.uaic.info.solver;

import ro.uaic.info.aco.graph.AntColonyGraph;
import ro.uaic.info.aco.acoVariants.masterDepot.AdaptingMMAS;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.AntColonyGraphMasterDepot;
import ro.uaic.info.aco.graph.AntColonyGraphPeerToPeer;
import ro.uaic.info.prb.ProblemIO;
import ro.uaic.info.prb.Tour;

import java.util.Deque;

public class AcoSolver implements Solver {
    ProblemIO problemIO;
    AntColonyGraph antColonyGraph;
    AntColony antColony;


    @Override
    public void init(ProblemIO problemIO) {
        this.problemIO = problemIO;
        antColonyGraph = new AntColonyGraphMasterDepot(problemIO.getN(), problemIO.getM(), problemIO.getCost(), problemIO.getDepotsCapacity());
        antColony = new AdaptingMMAS(antColonyGraph);
    }

    @Override
    public Deque<Tour> solve() {
        return antColony.run();
    }

    @Override
    public Deque<Tour> solveAnIter() {
        return antColony.runOnce();
    }

    @Override
    public void saveLogs(String name) {
        antColony.getCustomLogs().saveToMemory(name);
    }
}
