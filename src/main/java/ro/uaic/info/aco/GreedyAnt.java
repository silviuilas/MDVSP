package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GreedyAnt extends Ant {

    public GreedyAnt(AntColony antColony) {
        super(antColony);
        Random random = new Random();
        currentLocation = random.nextInt(antColonyGraph.getM());
        currentDepot = currentLocation;
    }

    @Override
    public DefaultWeightedEdge pickAnEdge(List<DefaultWeightedEdge> availableEdges) {
        Collections.shuffle(availableEdges);
        DefaultWeightedEdge nextTrip = getNextTrip(currentLocation);
        if (nextTrip != null) {
            return nextTrip;
        }
        return availableEdges.get(0);
    }



    public DefaultWeightedEdge getNextTrip(Integer position) {
        List<DefaultWeightedEdge> edges = getAvailableEdges(position);
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
