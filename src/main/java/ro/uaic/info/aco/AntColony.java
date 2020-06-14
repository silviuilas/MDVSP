package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.antBuilder.AntBuilder;
import ro.uaic.info.aco.antBuilder.GreedyAntBuilder;
import ro.uaic.info.aco.antBuilder.SmartAntBuilder;
import ro.uaic.info.aco.antSelection.AntSelectionStrategy;
import ro.uaic.info.aco.antSelection.ElitistSelection;
import ro.uaic.info.prb.Tour;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AntColony {
    final int colonySize = 100;
    final double alpha = 4;
    final double beta = 2;
    final double pheromoneAddition = 1;
    final double pheromoneEvaporationPercent = 0.1;
    final double minPheromone = 1;
    final double maxPheromone = 50;

    AntColonyGraph antColonyGraph;
    AntSelectionStrategy antSelectionStrategy;
    List<Ant> ants;

    public AntColony(AntColonyGraph antColonyGraph) {
        this.antColonyGraph = antColonyGraph;
        ants = new ArrayList<>();
        antSelectionStrategy = new ElitistSelection();
        initPheromones();
    }

    public void run() {
        int index = 0;
        AntBuilder antBuilder = new GreedyAntBuilder();
        while (true) {
            if (index == 1)
                antBuilder = new SmartAntBuilder();
            ants = antSelectionStrategy.generateAnts(ants, this, antBuilder);
            evaluateAnts();
            updatePheromones();
            System.out.println(index + " The best had " + ants.get(0).wrapGetUnsatisfiedClientsNr() + " unsatisfied customers (average " + calculateAverageUnsatisfied() + ") with the total cost of " + ants.get(0).getCurrentCost() + "( average " + calculateAverageCost() + ")");
            index++;
        }
    }

    public String calculateAverageUnsatisfied() {
        double sum = 0.0;
        for (Ant ant :
                ants) {
            sum += ant.wrapGetUnsatisfiedClientsNr();
        }
        return Double.toString(sum / ants.size());
    }

    public String calculateAverageCost() {
        double sum = 0.0;
        for (Ant ant :
                ants) {
            sum += ant.getCurrentCost();
        }
        return Double.toString(sum / ants.size());
    }

    public void evaluateAnts() {
        List<Ant> antsToEvaluate = new ArrayList<>();
        ExecutorService es = Executors.newCachedThreadPool();
        for (int i = 0; i < colonySize; i++) {
            antsToEvaluate.add(ants.get(i));
            if (i % 10 == 0 && i != 0) {
                es.execute(new EvaluateOnThread(ants, i - 10, i));
            }
        }
        es.execute(new EvaluateOnThread(ants, colonySize - 10, colonySize));
        es.shutdown();
        try {
            while (!es.awaitTermination(1, TimeUnit.MINUTES)) ;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void initPheromones() {
        for (Integer vertex : antColonyGraph.vertexSet()) {
            for (DefaultWeightedEdge edge : antColonyGraph.edgesOf(vertex)) {
                int i = antColonyGraph.getEdgeSource(edge);
                int j = antColonyGraph.getEdgeTarget(edge);
                antColonyGraph.setPheromone(i, j, 2);
            }
        }
    }

    public void updatePheromones() {
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
        int index = 1;
        for (Ant ant : ants) {
            for (Tour path :
                    ant.getPaths()) {
                for (int i = 1; i < path.size(); i++) {
                    int last = path.get(i - 1);
                    int current = path.get(i);
                    if (antColonyGraph.getPheromone(last, current) + pheromoneAddition / index < maxPheromone)
                        antColonyGraph.setPheromone(last, current, antColonyGraph.pheromoneTable[last][current] + pheromoneAddition / index);
                }
            }
            index++;
        }
        pheromoneEvaporation();
    }

    public void pheromoneEvaporation() {
        for (Integer vertex : antColonyGraph.vertexSet()) {
            for (DefaultWeightedEdge edge : antColonyGraph.edgesOf(vertex)) {
                int i = antColonyGraph.getEdgeSource(edge);
                int j = antColonyGraph.getEdgeTarget(edge);
                if (antColonyGraph.getPheromone(i, j) >= minPheromone) {
                    antColonyGraph.setPheromone(i, j, antColonyGraph.getPheromone(i, j) * (1 - pheromoneEvaporationPercent));
                }
            }
        }
    }

    public AntColonyGraph getAntColonyGraph() {
        return antColonyGraph;
    }

    public List<Ant> getAnts() {
        return ants;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }

    public int getColonySize() {
        return colonySize;
    }
}
