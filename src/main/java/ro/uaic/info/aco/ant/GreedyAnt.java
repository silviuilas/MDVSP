package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GreedyAnt extends MdvspAntMasterDepot {

    public GreedyAnt(MdvspAntColonyGraph mdvspAntColonyGraph) {
        super(mdvspAntColonyGraph);
        Random random = new Random();
        currentLocation = random.nextInt(mdvspAntColonyGraph.getM());
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
        edges.sort((t1, t2) -> (int) (mdvspAntColonyGraph.getEdgeWeight(t1) - mdvspAntColonyGraph.getEdgeWeight(t2)));

        for (DefaultWeightedEdge edge :
                edges) {
            Integer target = mdvspAntColonyGraph.getEdgeTarget(edge);
            if (!mdvspAntColonyGraph.isDepot(target)) {
                return edge;
            }
        }
        return null;
    }
}
