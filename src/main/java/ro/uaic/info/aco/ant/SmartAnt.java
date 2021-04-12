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
        int pheromoneTableN = antColonyGraph.vertexSet().size();
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
                distance = 1;
            sum = sum + Math.pow(pheromone, antColony.getAlpha()) + Math.pow(((1 / distance) * 6000), antColony.getBeta());
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
