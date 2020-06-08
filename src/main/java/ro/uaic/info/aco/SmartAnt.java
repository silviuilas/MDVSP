package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.prb.Tour;
import ro.uaic.info.prb.EdgeType;

import java.util.*;

public class SmartAnt extends Ant {

    public SmartAnt(AntColony antColony) {
        super(antColony);
        Random random = new Random();
        currentLocation = random.nextInt(antColonyGraph.getProblemIO().getM());
        currentDepot = currentLocation;
    }

    public DefaultWeightedEdge pickAnEdge(List<DefaultWeightedEdge> availableEdges) {
        Random random = new Random();
        int pheromoneTableN=antColonyGraph.vertexSet().size();
        double []select = new double[availableEdges.size()];
        double sum=0;
        //TODO finish
        int index=0;
        for (DefaultWeightedEdge edge:
             availableEdges) {
            int source = antColonyGraph.getEdgeSource(edge);
            int target = antColonyGraph.getEdgeTarget(edge);
            double pheromone=antColony.getAntColonyGraph().getPheromone(source,target);
            double distance = antColonyGraph.getEdgeWeight(edge);
            sum = sum+Math.pow(pheromone,antColony.getAlpha()) + Math.pow(1/distance,antColony.getBeta());
            select[index]=sum;
            index++;
        }

        double toFind = random.nextDouble()*sum;
        int selected=-1;
        for(int i=0;i<availableEdges.size();i++){
            if(toFind<select[i]){
                selected=i;
                break;
            }
        }
        return availableEdges.get(selected);
    }
}
