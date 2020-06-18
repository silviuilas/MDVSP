package ro.uaic.info.prb;

import java.util.Deque;

public class CalculateSolutionCost {
    ProblemIO problemIO;

    public CalculateSolutionCost(ProblemIO problemIO) {
        this.problemIO = problemIO;
    }

    public Integer calculate(Deque<Tour> solution) {
        int sum = 0;
        for (Tour tour :
                solution) {
            for (int i = 1; i < tour.size(); i++) {
                int a = tour.get(i - 1);
                int b = tour.get(i);
                sum = sum + problemIO.getCost()[a][b];
            }
        }
        return sum;
    }
}
