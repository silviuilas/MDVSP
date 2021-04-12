package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.antBuilder.AntBuilder;
import ro.uaic.info.aco.antBuilder.GreedyAntBuilder;
import ro.uaic.info.aco.antBuilder.SmartAntBuilder;
import ro.uaic.info.aco.antSelection.AntSelectionStrategy;
import ro.uaic.info.aco.antSelection.ElitistSelection;
import ro.uaic.info.prb.Tour;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AntColony {
    final int colonySize = 30;
    final double alpha = 5;
    final double beta = 1;
    final double pheromoneAddition = 0.3;
    final double pheromoneEvaporationPercent = 0.05;
    final double minPheromone = 1;
    final double maxPheromone = 20;

    int index = 0;
    AntColonyGraph antColonyGraph;
    AntSelectionStrategy antSelectionStrategy;
    List<Ant> ants;

    public AntColony(AntColonyGraph antColonyGraph) {
        this.antColonyGraph = antColonyGraph;
        ants = new ArrayList<>();
        antSelectionStrategy = new ElitistSelection();
        initPheromones();
    }

    public Deque<Tour> run() {
        AntBuilder antBuilder = new GreedyAntBuilder();
        while (condition()) {
            if (index == 1)
                antBuilder = new SmartAntBuilder();
            ants = antSelectionStrategy.generateAnts(ants, this, antBuilder);
            evaluateAnts();
            updatePheromones();
            System.out.println(index + " The best had " + ants.get(0).wrapGetUnsatisfiedClientsNr() + " unsatisfied customers (average " + calculateAverageUnsatisfied() + ") with the total cost of " + ants.get(0).getCurrentCost() + "( average " + calculateAverageCost() + ")");
            removeUselessDepots();
            index++;
        }
        Deque<Tour> bestAntTour = ants.get(0).getPaths();
        return transformTourList(bestAntTour);
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
        //System.out.println(antColonyGraph.getDepotsCapacity().toString());
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
        for (Integer vertex : antColonyGraph.vertexSet()) {
            for (DefaultWeightedEdge edge : antColonyGraph.edgesOf(vertex)) {
                int i = antColonyGraph.getEdgeSource(edge);
                int j = antColonyGraph.getEdgeTarget(edge);
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
