package ro.uaic.info.aco.acoVariants;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.AntColonyGraph;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.antBuilder.SmartAntBuilder;
import ro.uaic.info.aco.antSelection.ElitistSelection;
import ro.uaic.info.prb.Tour;

import java.util.Deque;

public class AdaptingMMAS extends AntColony {
    private final double minPheromone = 0.000001;
    private final double maxPheromone = 1;
    public double maxPher = 1;
    public double minPher;
    public double p_best;

    public AdaptingMMAS(AntColonyGraph antColonyGraph) {
        super(antColonyGraph);
        this.setAntBuilder(new SmartAntBuilder());
        this.setAntSelectionStrategy(new ElitistSelection());
        this.setAlpha(1);
        this.setBeta(2);
        this.setPheromoneEvaporationPercent(0.2);
        this.setColonySize(100);
        maxPher = 1;
        p_best = 0.02;
        initPheromones();
    }

    @Override
    public Deque<Tour> runOnce() {
        Deque<Tour> res = super.runOnce();
        updateMinMaxPher();

        removeUselessDepots();
        return res;
    }

    public void updateMinMaxPher() {
        Ant bestAntThisIteration = getBestAntThisIteration();
        maxPher = (1.0 / (pheromoneEvaporationPercent)) * (1.0 / bestAntThisIteration.getCurrentCost());
        if (minPher == 0)
            minPher = maxPher;
        int numberOfSimilarAnts = 0;
        for (Ant ant:
             ants) {
            if(bestAntThisIteration.getCurrentCost() == ant.getCurrentCost()){
                numberOfSimilarAnts++;
            }
        }
        if(colonySize * p_best < numberOfSimilarAnts){
            minPher /= (1 - pheromoneEvaporationPercent);
        }else{
            minPher *= (1 - pheromoneEvaporationPercent);
        }
        System.out.println(minPher);
        System.out.println(maxPher);
    }

    @Override
    public boolean condition() {
        return this.index < 10000;
    }

    @Override
    public void initPheromones() {
        for (Integer vertex : antColonyGraph.vertexSet()) {
            for (DefaultWeightedEdge edge : antColonyGraph.edgesOf(vertex)) {
                int i = antColonyGraph.getEdgeSource(edge);
                int j = antColonyGraph.getEdgeTarget(edge);
                antColonyGraph.setPheromone(i, j, maxPher);
            }
        }
    }

    @Override
    public void updatePheromones() {
        sortAnts();
        for (Ant ant : ants) {
            for (Tour path :
                    ant.getPaths()) {
                int current_cost = ant.getCurrentCost();
                for (int i = 1; i < path.size(); i++) {
                    int last = path.get(i - 1);
                    int current = path.get(i);
                    double calculated_pheromone = antColonyGraph.getPheromone(last, current) + ((1.0 / current_cost));
                    antColonyGraph.setPheromone(last, current, calculated_pheromone);
                }
            }
            index++;
            break;
        }
        int n = antColonyGraph.getPheromoneTable().length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double pher = antColonyGraph.getPheromone(i, j);
                if (pher != 0) {
                    if (pher > maxPher) {
                        antColonyGraph.setPheromone(i, j, maxPher);
                        // System.out.println("The max has been reached on " + i + " " + j);
                    } else if (pher < minPher) {
                        antColonyGraph.setPheromone(i, j, minPher);
                        // System.out.println("The min has been reached on " + i + " " + j);
                    }
                }
            }
        }
        pheromoneEvaporation();
    }

    @Override
    public void pheromoneEvaporation() {
        int n = antColonyGraph.getPheromoneTable().length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (antColonyGraph.getPheromone(i, j) != 0)
                    antColonyGraph.setPheromone(i, j, Math.max(antColonyGraph.getPheromone(i, j) * (1 - pheromoneEvaporationPercent), minPher));
            }
        }
    }

    @Override
    public Ant getBestAntThisIteration() {
        return this.ants.get(0);
    }

    public void sortAnts() {
        ants.sort((ant1, ant2) -> {
            int unsatisfiedNr1 = ant1.wrapGetUnsatisfiedClientsNr();
            int unsatisfiedNr2 = ant2.wrapGetUnsatisfiedClientsNr();
            if (unsatisfiedNr1 == 0 && unsatisfiedNr2 > 0)
                return -1;
            else if (unsatisfiedNr1 > 0 && unsatisfiedNr2 == 0)
                return 1;
            else if (unsatisfiedNr1 == 0 && unsatisfiedNr2 == 0)
                return ant1.getCurrentCost() - ant2.getCurrentCost();
            else
                return ant1.wrapGetUnsatisfiedClientsNr() - ant2.wrapGetUnsatisfiedClientsNr();
        });
    }
}
