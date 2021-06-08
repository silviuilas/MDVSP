package ro.uaic.info.aco.acoVariants;

import ro.uaic.info.aco.EvaluateOnThread;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;
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
    protected int colonySize = 20;
    protected double pheromoneAddition = 20;
    protected double pheromoneEvaporationPercent = 0.02;

    protected int index = 0;

    protected List<Ant> ants;
    protected MdvspAntColonyGraph mdvspAntColonyGraph;
    AntSelectionStrategy antSelectionStrategy = new ElitistSelection();
    AntBuilder antBuilder = new SmartAntBuilder();
    CustomLogs customLogs;

    public AntColony(MdvspAntColonyGraph mdvspAntColonyGraph) {
        this.mdvspAntColonyGraph = mdvspAntColonyGraph;
        customLogs = new CustomLogs("");
        ants = new ArrayList<>();
        initPheromones();
    }

    public Deque<Tour> run() {
        int bestAntTourCost = Integer.MAX_VALUE;
        while (condition()) {
            runOnce();
            Ant bestAntThisIteration = getBestAntThisIteration();
            if (bestAntThisIteration.getNumberOfNotVisitedVertexes() == 0 && bestAntThisIteration.getCurrentCost() < bestAntTourCost) {
                bestAntTourCost = bestAntThisIteration.getCurrentCost();
            }
        }
        return getBestAntThisIteration().getDequeTour();
    }


    public Deque<Tour> runOnce() {
        generateAnts();
        evaluateAnts();
        updatePheromones();
        Ant bestAntInThisIteration = getBestAntThisIteration();
        showResults(bestAntInThisIteration);
        customLogs.addToValue(String.valueOf(index), String.valueOf(bestAntInThisIteration.getNumberOfNotVisitedVertexes()), String.valueOf(getBestAntThisIteration().getCurrentCost()), calculateAverageUnsatisfied(), calculateAverageCost());
        System.out.println(index + " The best had " + getBestAntThisIteration().getNumberOfNotVisitedVertexes() + " unsatisfied customers (average " + calculateAverageUnsatisfied() + ") with the total cost of " + getBestAntThisIteration().getCurrentCost() + "( average " + calculateAverageCost() + ")");
        index++;
        return getBestAntThisIteration().getDequeTour();
    }

    public abstract boolean condition();

    public void generateAnts() {
        ants = antSelectionStrategy.generateAnts(ants, this, antBuilder);
    }

    public void showResults(Ant bestAntInThisIteration) {
    }

    public void evaluateAntsSync() {
        for (int i = 0; i < ants.size(); i++) {
            Ant ant = ants.get(i);
            ant.run();
        }
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

    public void removeUselessDepots() {
        Ant ant = getBestAntThisIteration();
        if (ant.getNumberOfNotVisitedVertexes() != 0)
            return;
        Deque<Tour> tours = ant.getDequeTour();
        int size = mdvspAntColonyGraph.getM();
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
        // System.out.println(mdvspAntColonyGraph.getDepotsCapacity().toString());
        for (int i = 0; i < size; i++) {
            mdvspAntColonyGraph.getDepotsCapacity().set(i, visited[i]);
        }
    }


    public abstract void initPheromones();

    public abstract void updatePheromones();

    public abstract void pheromoneEvaporation();

    public abstract Ant getBestAntThisIteration();


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

    public String calculateAverageUnsatisfied() {
        double sum = 0.0;
        for (Ant ant :
                ants) {
            sum += ant.getNumberOfNotVisitedVertexes();
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

    public void setColonySize(int colonySize) {
        this.colonySize = colonySize;
    }

    public MdvspAntColonyGraph getMdvspAntColonyGraph() {
        return mdvspAntColonyGraph;
    }

    public void setPheromoneAddition(double pheromoneAddition) {
        this.pheromoneAddition = pheromoneAddition;
    }

    public void setPheromoneEvaporationPercent(double pheromoneEvaporationPercent) {
        this.pheromoneEvaporationPercent = pheromoneEvaporationPercent;
    }
}
