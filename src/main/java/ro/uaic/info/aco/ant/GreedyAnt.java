package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.acoVariants.AntColony;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GreedyAnt extends Ant {

    public GreedyAnt(AntColony antColony) {
        super(antColony);
        Random random = new Random();
        currentLocation = random.nextInt(antColonyGraph.getM());
    }

    @Override
    public DefaultWeightedEdge pickAnEdge(List<DefaultWeightedEdge> availableEdges) {
        Collections.shuffle(availableEdges);
        DefaultWeightedEdge nextTrip = getShortestPath(availableEdges);
        if (nextTrip != null) {
            return nextTrip;
        }
        return availableEdges.get(0);
    }


    public DefaultWeightedEdge getShortestPath(List<DefaultWeightedEdge> edges) {
        edges.sort((t1, t2) -> (int) (antColonyGraph.getEdgeWeight(t1) - antColonyGraph.getEdgeWeight(t2)));

        for (DefaultWeightedEdge edge :
                edges) {
            Integer target = antColonyGraph.getEdgeTarget(edge);
            if (!antColonyGraph.isDepot(target) && !antColonyGraph.isMaster(target)) {
                return edge;
            }
        }
        return null;
    }
}
