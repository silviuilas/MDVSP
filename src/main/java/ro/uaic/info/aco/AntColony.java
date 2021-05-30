package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.antBuilder.AntBuilder;
import ro.uaic.info.aco.antBuilder.GreedyAntBuilder;
import ro.uaic.info.aco.antBuilder.SmartAntBuilder;
import ro.uaic.info.aco.antSelection.AntSelectionStrategy;
import ro.uaic.info.aco.antSelection.ElitistSelection;
import ro.uaic.info.aco.antSelection.RandomSelection;
import ro.uaic.info.prb.Tour;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.min;

public class AntColony {
    final int colonySize = 30;
    double alpha = 2;
    double beta = 4;
    double pheromoneAddition = 10;
    double pheromoneEvaporationPercent = 0.3;
    final double minPheromone = 1;
    final double maxPheromone = 10000;

    int index = 0;
    AntColonyGraph antColonyGraph;
    AntSelectionStrategy antSelectionStrategy;
    AntBuilder antBuilder;
    List<Ant> ants;

    public AntColony(AntColonyGraph antColonyGraph) {
        this.antColonyGraph = antColonyGraph;
        ants = new ArrayList<>();
        antSelectionStrategy = new ElitistSelection();
        antBuilder = new SmartAntBuilder();
        index = 0;
        initPheromones();
    }

    public Deque<Tour> run() {
        Deque<Tour> bestAntTour = null;
        int bestAntTourCost = Integer.MAX_VALUE;
        while (condition()) {
            runOnce();
            if (ants.get(0).wrapGetUnsatisfiedClientsNr() == 0 && ants.get(0).getCurrentCost() < bestAntTourCost) {
                bestAntTour = ants.get(0).getPaths();
                bestAntTourCost = ants.get(0).getCurrentCost();
            }
        }
        assert bestAntTour != null;
        return transformTourList(bestAntTour);
    }

    public Deque<Tour> runOnce() {
        ants = antSelectionStrategy.generateAnts(ants, this, antBuilder);
        evaluateAnts();
        updatePheromones();
        System.out.println(index + " The best had " + ants.get(0).wrapGetUnsatisfiedClientsNr() + " unsatisfied customers (average " + calculateAverageUnsatisfied() + ") with the total cost of " + ants.get(0).getCurrentCost() + "( average " + calculateAverageCost() + ")");
        //removeUselessDepots();
        index++;
        return ants.get(0).getPaths();
    }

    public boolean condition() {
        return index <= 1000;
    }

    public Deque<Tour> transformTourList(Deque<Tour> antTour) {
        Deque<Tour> newList = new ArrayDeque<>();
        for (Tour tour :
                antTour) {
            Tour tour1 = (Tour) tour.clone();
            int first = 0;
            tour1.set(first, tour1.get(first));
            int last = tour1.size() - 1;
            tour1.set(last, tour1.get(last));
            newList.add(tour1);
        }
        return newList;
    }

    public void removeUselessDepots() {
        Ant ant = ants.get(0);
        if (ant.wrapGetUnsatisfiedClientsNr() != 0)
            return;
        Deque<Tour> tours = ant.getPaths();
        int size = antColonyGraph.getM();
        int[] visited = new int[size];
        for (int i = 0; i < size; i++) {
            visited[i] = 0;
        }

        for (Tour tour :
                tours) {
            int start = tour.get(0);
            int end = tour.get(tour.size() - 1);
            if (start == end)
                visited[start]++;
            else {
                System.out.println("Tour is not valid");
            }
        }
        //System.out.println(antColonyGraph.getDepotsCapacity().toString());
        for (int i = 0; i < size; i++) {
            antColonyGraph.getDepotsCapacity().set(i, visited[i]);
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
        ExecutorService es = Executors.newCachedThreadPool();
//        new EvaluateOnThread(ants, 0, colonySize).run();
        int batch = 5;
        for (int i = 0; i < colonySize; i++) {
            if (i % batch == 0 && i != 0) {
                es.execute(new EvaluateOnThread(ants, i - batch, i));
            }
        }
        es.execute(new EvaluateOnThread(ants, colonySize - batch, colonySize));
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
                    double calculated_pheromone = antColonyGraph.getPheromone(last, current) + pheromoneAddition / index;
                    if (calculated_pheromone < maxPheromone)
                        antColonyGraph.setPheromone(last, current, calculated_pheromone);
                }
            }
            index++;
        }
        pheromoneEvaporation();
    }

    public void pheromoneEvaporation() {
        int n = antColonyGraph.pheromoneTable.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (antColonyGraph.getPheromone(i, j) != 0)
                    antColonyGraph.setPheromone(i, j, Math.max(antColonyGraph.getPheromone(i, j) * (1 - pheromoneEvaporationPercent), minPheromone));
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
