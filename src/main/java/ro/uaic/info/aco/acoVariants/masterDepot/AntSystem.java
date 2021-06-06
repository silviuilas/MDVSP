package ro.uaic.info.aco.acoVariants.masterDepot;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.AntColonyGraph;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.antBuilder.MdvspAntBuilder;
import ro.uaic.info.aco.antSelection.ElitistSelection;
import ro.uaic.info.prb.Tour;

import java.util.Deque;
import java.util.List;

public class AntSystem extends AntColony {
    public AntSystem(AntColonyGraph antColonyGraph) {
        super(antColonyGraph);
        this.setAntBuilder(new MdvspAntBuilder());
        this.setAntSelectionStrategy(new ElitistSelection());
        this.setAlpha(1);
        this.setBeta(2);
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
        Ant ant = ants.get(0);
        int size = ant.getAntsVisitedPath().size();
        List<Integer> path = ant.getAntsVisitedPath();
        Ant bestAnt = getBestAntThisIteration();
        for (int i = 1; i < size; i++) {
            int source = path.get(i - 1);
            int target = path.get(i);
            double calculated_pheromone = antColonyGraph.getPheromone(source, target) + ((1.0 / bestAnt.getCurrentCost()));
            antColonyGraph.setPheromone(source, target, calculated_pheromone);
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
