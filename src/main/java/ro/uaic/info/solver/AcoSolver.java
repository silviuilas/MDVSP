package ro.uaic.info.solver;

import ro.uaic.info.aco.acoVariants.AntSystem;
import ro.uaic.info.aco.acoVariants.MMAS;
import ro.uaic.info.aco.graph.MasterDepotACG;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;
import ro.uaic.info.aco.acoVariants.AdaptingMMAS;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.PeerToPeerACG;
import ro.uaic.info.prb.ProblemIO;
import ro.uaic.info.prb.Tour;

import java.util.Deque;

public class AcoSolver implements Solver {
    ProblemIO problemIO;
    MdvspAntColonyGraph mdvspAntColonyGraph;
    AntColony antColony;


    @Override
    public void init(ProblemIO problemIO) {
        this.problemIO = problemIO;
        mdvspAntColonyGraph = new PeerToPeerACG(problemIO.getN(), problemIO.getM(), problemIO.getCost(), problemIO.getDepotsCapacity());
        mdvspAntColonyGraph.init();
        antColony = new AdaptingMMAS(mdvspAntColonyGraph);
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
