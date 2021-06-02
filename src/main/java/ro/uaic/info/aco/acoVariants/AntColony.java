package ro.uaic.info.aco.acoVariants;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.AntColonyGraph;
import ro.uaic.info.aco.EvaluateOnThread;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.antBuilder.AntBuilder;
import ro.uaic.info.aco.antBuilder.SmartAntBuilder;
import ro.uaic.info.aco.antSelection.AntSelectionStrategy;
import ro.uaic.info.aco.antSelection.ElitistSelection;
import ro.uaic.info.helpers.CustomLogs;
import ro.uaic.info.prb.Tour;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AntColony {
    int colonySize = 20;
    double alpha = 1;
    double beta = 2;
    double pheromoneAddition = 20;
    double pheromoneEvaporationPercent = 0.02;

    int index = 0;

    AntColonyGraph antColonyGraph;
    AntSelectionStrategy antSelectionStrategy;
    AntBuilder antBuilder;
    List<Ant> ants;
    CustomLogs customLogs;

    public AntColony(AntColonyGraph antColonyGraph) {
        this.antColonyGraph = antColonyGraph;
        customLogs = new CustomLogs("");
        ants = new ArrayList<>();
        index = 0;
        initPheromones();
    }

    public Deque<Tour> run() {
        Deque<Tour> bestAntTour = null;
        int bestAntTourCost = Integer.MAX_VALUE;
        while (condition()) {
            runOnce();
            Ant bestAntThisIteration = getBestAntThisIteration();
            if (bestAntThisIteration.wrapGetUnsatisfiedClientsNr() == 0 && bestAntThisIteration.getCurrentCost() < bestAntTourCost) {
                bestAntTour = bestAntThisIteration.getPaths();
                bestAntTourCost = bestAntThisIteration.getCurrentCost();
            }
        }
        return transformTourList(bestAntTour);
    }


    public Deque<Tour> runOnce() {
        generateAnts();
        evaluateAnts();
        updatePheromones();
        Ant bestAntInThisIteration = getBestAntThisIteration();
        showResults(bestAntInThisIteration);
        return bestAntInThisIteration.getPaths();
    }

    public abstract boolean condition();

    public void generateAnts() {
        ants = antSelectionStrategy.generateAnts(ants, this, antBuilder);
    }

    public void showResults(Ant bestAntInThisIteration){
        if (customLogs != null) {
            String outputToFile = index + "," + bestAntInThisIteration.wrapGetUnsatisfiedClientsNr() + "," + bestAntInThisIteration.getCurrentCost() + "," + calculateAverageUnsatisfied() + "," + calculateAverageCost() + "\n";
            customLogs.addToValue(outputToFile);
        }
        System.out.println(index + " The best had " + bestAntInThisIteration.wrapGetUnsatisfiedClientsNr() + " unsatisfied customers (average " + calculateAverageUnsatisfied() + ") with the total cost of " + bestAntInThisIteration.getCurrentCost() + "( average " + calculateAverageCost() + ")");
        index++;
    }

    public void evaluateAnts() {
        ExecutorService es = Executors.newCachedThreadPool();
//        new EvaluateOnThread(ants, 0, colonySize).run();
        int batch = 1;
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

    public abstract void initPheromones();

    public abstract void updatePheromones();

    public abstract void pheromoneEvaporation();

    public abstract Ant getBestAntThisIteration();

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


    public void removeUselessDepots() {
        Ant ant = getBestAntThisIteration();
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
        // System.out.println(antColonyGraph.getDepotsCapacity().toString());
        for (int i = 0; i < size; i++) {
            antColonyGraph.getDepotsCapacity().set(i, visited[i]);
        }
    }

    public Deque<Tour> transformTourList(Deque<Tour> antTour) {
        if (antTour == null) {
            return null;
        }
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

    public void setAntSelectionStrategy(AntSelectionStrategy antSelectionStrategy) {
        this.antSelectionStrategy = antSelectionStrategy;
    }

    public void setAntBuilder(AntBuilder antBuilder) {
        this.antBuilder = antBuilder;
    }

    public CustomLogs getCustomLogs() {
        return customLogs;
    }

    public int getColonySize() {
        return colonySize;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }

    public AntColonyGraph getAntColonyGraph() {
        return antColonyGraph;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public void setPheromoneAddition(double pheromoneAddition) {
        this.pheromoneAddition = pheromoneAddition;
    }

    public void setPheromoneEvaporationPercent(double pheromoneEvaporationPercent) {
        this.pheromoneEvaporationPercent = pheromoneEvaporationPercent;
    }

    public void setColonySize(int colonySize) {
        this.colonySize = colonySize;
    }
}
