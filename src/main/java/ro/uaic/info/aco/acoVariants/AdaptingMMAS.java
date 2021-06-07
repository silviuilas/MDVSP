package ro.uaic.info.aco.acoVariants;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.antBuilder.GreedyAntBuilder;
import ro.uaic.info.aco.antBuilder.SmartAntBuilder;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.antSelection.ElitistSelection;
import ro.uaic.info.prb.Tour;

import java.util.*;

public class AdaptingMMAS extends AntColony {
    public double maxPher = 1;
    public double minPher;
    public double threshold;
    int index = 0;

    public AdaptingMMAS(MdvspAntColonyGraph mdvspAntColonyGraph) {
        super(mdvspAntColonyGraph);
        this.setAntBuilder(new SmartAntBuilder());
        this.setAntSelectionStrategy(new ElitistSelection());
        this.setPheromoneEvaporationPercent(0.2);
        this.setColonySize(20);
        maxPher = 1;
        threshold = 0.7;
        initPheromones();
    }

    @Override
    public Deque<Tour> runOnce() {
        Deque<Tour> res = super.runOnce();
        updateMinMaxPher();
        index ++;
        if(index%500 ==0) {
            initPheromones();
            minPher = maxPher;
        }
        System.out.println("Avg number of paths " + getBestAntThisIteration().getAvgOfNumberOfAvailablePaths() + " w number of decizions " + getBestAntThisIteration().getNumberOfDecisions());
        // removeUselessDepots();
        return res;
    }

    public void updateMinMaxPher() {
        Ant bestAntThisIteration = getBestAntThisIteration();
        maxPher = (1.0 / (pheromoneEvaporationPercent)) * (1.0 / bestAntThisIteration.getCurrentCost());
        if (minPher == 0)
            minPher = maxPher;
        if (areTheSolutionsAlikeHash(threshold)) {
            minPher *= (1 - pheromoneEvaporationPercent);
        } else {
            minPher /= (1 - pheromoneEvaporationPercent);
        }
    }

    public boolean areTheSolutionsAlike(double threshold) {
        Ant bestAntThisIteration = getBestAntThisIteration();
        int numberOfSimilarAnts = 0;
        for (Ant ant :
                ants) {
            if (bestAntThisIteration.getCurrentCost() == ant.getCurrentCost()) {
                numberOfSimilarAnts++;
            }
        }
        return colonySize * threshold >= numberOfSimilarAnts;
    }

    public boolean areTheSolutionsAlikeHash(double threshold) {
        Ant bestAntThisIteration = getBestAntThisIteration();
        Map<Long, Boolean> booleanMap = new HashMap<>();
        Queue<Integer> toRemove = new LinkedList<>();
        for (Tour tour :
                bestAntThisIteration.getDequeTour()) {
            long hashSum = tour.hashCode();
            booleanMap.put(hashSum, true);
        }
        int sameHashSum = 0;
        int iSum = 0;
        for (Ant ant :
                ants) {
            int sameHash = 0;
            int i = 0;
            for (Tour tour :
                    ant.getDequeTour()) {
                long hashSum = tour.hashCode();
                if (booleanMap.get(hashSum) != null && booleanMap.get(hashSum)) {
                    sameHash++;
                }
                i++;
            }
            sameHashSum += sameHash;
            iSum += (i);
        }
        double avg = ((double) sameHashSum / (double) iSum);
        System.out.println(avg + "  " + minPher);
        return avg <= threshold;
    }


    @Override
    public boolean condition() {
        return this.index < 10000;
    }

    @Override
    public void initPheromones() {
        for (Integer vertex : mdvspAntColonyGraph.vertexSet()) {
            for (DefaultWeightedEdge edge : mdvspAntColonyGraph.edgesOf(vertex)) {
                int i = mdvspAntColonyGraph.getEdgeSource(edge);
                int j = mdvspAntColonyGraph.getEdgeTarget(edge);
                mdvspAntColonyGraph.setPheromone(i, j, maxPher);
            }
        }
    }

    @Override
    public void updatePheromones() {
        sortAnts();

        Ant ant = ants.get(0);
        int size = ant.getAntsVisitedPath().size();
        List<Integer> path = ant.getAntsVisitedPath();
        Ant bestAnt = getBestAntThisIteration();
        for (int i = 1; i < size; i++) {
            int source = path.get(i - 1);
            int target = path.get(i);
            double calculated_pheromone = mdvspAntColonyGraph.getPheromone(source, target) + ((1.0 / bestAnt.getCurrentCost()));
            mdvspAntColonyGraph.setPheromone(source, target, calculated_pheromone);
        }
        checkConstrains();
        pheromoneEvaporation();
    }

    public void checkConstrains() {
        int n = mdvspAntColonyGraph.getPheromoneTable().length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double pher = mdvspAntColonyGraph.getPheromone(i, j);
                if (pher != 0) {
                    if (pher > maxPher) {
                        mdvspAntColonyGraph.setPheromone(i, j, maxPher);
                        // System.out.println("The max has been reached on " + i + " " + j);
                    } else if (pher < minPher) {
                        mdvspAntColonyGraph.setPheromone(i, j, minPher);
                        // System.out.println("The min has been reached on " + i + " " + j);
                    }
                }
            }
        }
    }

    @Override
    public void pheromoneEvaporation() {
        int n = mdvspAntColonyGraph.getPheromoneTable().length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (mdvspAntColonyGraph.getPheromone(i, j) != 0)
                    mdvspAntColonyGraph.setPheromone(i, j, Math.max(mdvspAntColonyGraph.getPheromone(i, j) * (1 - pheromoneEvaporationPercent), minPher));
            }
        }
    }

    @Override
    public Ant getBestAntThisIteration() {
        return this.ants.get(0);
    }

    public void sortAnts() {
        ants.sort((ant1, ant2) -> {
            int unsatisfiedNr1 = ant1.getNumberOfNotVisitedVertexes();
            int unsatisfiedNr2 = ant2.getNumberOfNotVisitedVertexes();
            if (unsatisfiedNr1 == 0 && unsatisfiedNr2 > 0)
                return -1;
            else if (unsatisfiedNr1 > 0 && unsatisfiedNr2 == 0)
                return 1;
            else if (unsatisfiedNr1 == 0 && unsatisfiedNr2 == 0)
                return ant1.getCurrentCost() - ant2.getCurrentCost();
            else
                return ant1.getNumberOfNotVisitedVertexes() - ant2.getNumberOfNotVisitedVertexes();
        });
    }
}
