package ro.uaic.info.solver;

import ro.uaic.info.prb.ProblemIO;
import ro.uaic.info.prb.Tour;

import java.util.Deque;

public interface Solver {
    void init(ProblemIO problemIO);

    Deque<Tour> solve();

    Deque<Tour> solveAnIter();
}
