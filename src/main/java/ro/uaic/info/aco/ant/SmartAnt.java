package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.AntColony;

import java.util.List;
import java.util.Random;

public class SmartAnt extends Ant {

    public SmartAnt(AntColony antColony) {
        super(antColony);
        Random random = new Random();
        currentLocation = random.nextInt(antColonyGraph.getM());
        currentDepot = currentLocation;
    }

    public DefaultWeightedEdge pickAnEdge(List<DefaultWeightedEdge> availableEdges) {
        Random random = new Random();
        double[] select = new double[availableEdges.size()];
        double sum = 0;
        //TODO finish
        int index = 0;
        for (DefaultWeightedEdge edge :
                availableEdges) {
            int source = antColonyGraph.getEdgeSource(edge);
            int target = antColonyGraph.getEdgeTarget(edge);
            double pheromone = antColony.getAntColonyGraph().getPheromone(source, target);
            double distance = antColonyGraph.getEdgeWeight(edge);
            if (distance < 1)
                distance = 50000;
            double intensity = Math.pow(pheromone, antColony.getAlpha());
            double relevance = Math.pow(((1 / distance)), antColony.getBeta());
            double desirability = intensity * relevance;
            sum = sum + (desirability);
            select[index] = sum;
            index++;
        }

        double toFind = random.nextDouble() * sum;
        int selected = -1;
        for (int i = 0; i < availableEdges.size(); i++) {
            if (toFind < select[i]) {
                selected = i;
                break;
            }
        }
        return availableEdges.get(selected);
    }
}
