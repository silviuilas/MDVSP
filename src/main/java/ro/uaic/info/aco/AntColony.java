package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.prb.Tour;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AntColony {
    final int colonySize = 50;
    final double alpha = 5;
    final double beta = 2;
    final double pheromoneAddition = 1;
    final double pheromoneEvaporationPercent = 0.1;
    final double minPheromone = 1;
    final double maxPheromone = 50;

    AntColonyGraph antColonyGraph;
    List<Ant> ants;

    public AntColony(AntColonyGraph antColonyGraph) {
        this.antColonyGraph = antColonyGraph;
        ants = new ArrayList<>();
        initPheromones();
    }

    public void run() {
        int index = 0;
        String typeOfAnt = "GreedyAnt";
        while (true) {
            if (index == 3)
                typeOfAnt = "SmartAnt";
            generateNewAnts(typeOfAnt);
            evaluateAnts();
            updatePheromones();
            System.out.println(index + " The best ant was " + ants.get(0).wrapGetUnsatisfiedClientsNr() + " with the total cost of " + ants.get(0).getCurrentCost());
            index++;
        }
    }

    public void generateNewAnts(String typeOfAnt) {
        try {
            String pathToType = "ro.uaic.info.aco." + typeOfAnt;
            Class clazz = Class.forName(pathToType);
            Constructor constructor = clazz.getConstructor(AntColony.class);
            ants.clear();
            for (int i = 0; i < colonySize; i++)
                ants.add((Ant) constructor.newInstance(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                antColonyGraph.setPheromone(i,j,2);
            }
        }
    }

    public void updatePheromones() {
        Collections.sort(ants, new Comparator<Ant>() {
            @Override
            public int compare(Ant ant1, Ant ant2) {
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
            }
        });
        //TODO MAKE BETTER
        int index = 1;
        for (Ant ant : ants) {
            for (Tour path :
                    ant.getPaths()) {
                for (int i = 1; i < path.size(); i++) {
                    int last = path.get(i - 1);
                    int current = path.get(i);
                    if (antColonyGraph.getPheromone(last,current) + pheromoneAddition / index < maxPheromone)
                        antColonyGraph.setPheromone(last,current, antColonyGraph.pheromoneTable[last][current] + pheromoneAddition / index);
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
                if (antColonyGraph.getPheromone(i,j) >= minPheromone) {
                    antColonyGraph.setPheromone(i,j,antColonyGraph.getPheromone(i,j) * (1 - pheromoneEvaporationPercent));
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
}
