package ro.uaic.info;

import ro.uaic.info.prb.CalculateSolutionCost;
import ro.uaic.info.prb.ProblemIO;
import ro.uaic.info.prb.SolutionChecker;
import ro.uaic.info.prb.Tour;
import ro.uaic.info.solver.Solver;

import java.util.Deque;

public class Problem {
    private final ProblemIO problemIO;
    private final Solver solver;

    public Problem(ProblemIO problemIO, Solver solver) {
        this.problemIO = problemIO;
        this.solver = solver;
    }

    public void run() {
        SolutionChecker solutionChecker = new SolutionChecker(problemIO);
        Deque<Tour> solution = solver.solve(problemIO);
        if (!solutionChecker.isValidSol(solution)) {
            System.out.println("The solution is not valid");
            return;
        }
        CalculateSolutionCost calculateSolutionCost = new CalculateSolutionCost(problemIO);
        System.out.println("The solution is valid with the total cost of " + calculateSolutionCost.calculate(solution));
    }
}
