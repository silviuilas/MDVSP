package ro.uaic.info.aco.acoVariants;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.AntColonyGraph;
import ro.uaic.info.aco.EvaluateOnThread;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.antBuilder.AntBuilder;
import ro.uaic.info.aco.antBuilder.SmartAntBuilder;
import ro.uaic.info.aco.antSelection.AntSelectionStrategy;
import ro.uaic.info.aco.antSelection.ElitistSelection;
import ro.uaic.info.aco.antSelection.RandomSelection;
import ro.uaic.info.helpers.CustomLogs;
import ro.uaic.info.prb.Tour;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AntSystem extends AntColony {
    public AntSystem(AntColonyGraph antColonyGraph) {
        super(antColonyGraph);
        this.setAntBuilder(new SmartAntBuilder());
        this.setAntSelectionStrategy(new ElitistSelection());
        this.setAlpha(2);
        this.setBeta(3);
        this.setPheromoneEvaporationPercent(0.2);
    }

    @Override
    public Deque<Tour> runOnce() {
        Deque<Tour> res = super.runOnce();
        removeUselessDepots();
        return res;
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
                antColonyGraph.setPheromone(i, j, 2);
            }
        }
    }

    @Override
    public void updatePheromones() {
        sortAnts();
        int index = 1;
        for (Ant ant : ants) {
            for (Tour path :
                    ant.getPaths()) {
                for (int i = 1; i < path.size(); i++) {
                    int last = path.get(i - 1);
                    int current = path.get(i);
                    double calculated_pheromone = antColonyGraph.getPheromone(last, current) + pheromoneAddition / index;
                    antColonyGraph.setPheromone(last, current, calculated_pheromone);
                }
            }
            index++;
        }
        pheromoneEvaporation();
    }

    @Override
    public void pheromoneEvaporation() {
        int n = antColonyGraph.getPheromoneTable().length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (antColonyGraph.getPheromone(i, j) != 0)
                    antColonyGraph.setPheromone(i, j, Math.max(antColonyGraph.getPheromone(i, j) * (1 - pheromoneEvaporationPercent), 0));
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
