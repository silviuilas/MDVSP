package ro.uaic.info.prb;

import java.util.Deque;

public class SolutionChecker {
    ProblemIO problemIO;

    public SolutionChecker(ProblemIO problemIO) {
        this.problemIO = problemIO;
    }

    public boolean isValidSol(Deque<Tour> solution) {
        for (Tour tour :
                solution) {
            //check if the the trip is actually possible
            for (int i = 1; i < tour.size(); i++) {
                int a = tour.get(i - 1);
                int b = tour.get(i);
                if (problemIO.getCost()[a][b] < 0) {
                    return false;
                }
            }
            if (!tour.checkTour())
                return false;
        }
        return areClientsAreSatisfied(solution);
    }

    private boolean areClientsAreSatisfied(Deque<Tour> solution) {
        int n = problemIO.getN();
        int m = problemIO.getM();
        int[] visitedNodes = new int[m + n];
        for (int i = 0; i < m; i++)
            visitedNodes[i] = problemIO.getDepotsCapacity().get(i);
        for (int i = m; i < n; i++)
            visitedNodes[i] = 1;
        for (Tour tour :
                solution) {
            for (int i = 0; i < tour.size() - 1; i++) {
                visitedNodes[tour.get(i)]--;
            }
        }
        //check if we used more vehicles than possible
        for (int i = 0; i < m; i++)
            if (visitedNodes[i] < 0)
                return false;
        //check if all the clients have been served
        for (int i = m; i < n; i++)
            if (visitedNodes[i] != 0)
                return false;
        return true;
    }
}
