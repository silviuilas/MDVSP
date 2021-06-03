package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.AntColonyGraph;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.prb.EdgeType;

public class MdvspAnt extends SmartAnt {
    int[] remainingDepotsNr;
    int currentDepot;


    public MdvspAnt(AntColony antColony) {
        super(antColony);
        if (remainingDepotsNr == null) {
            remainingDepotsNr = antColony.getAntColonyGraph().getDepotsCapacity().stream().mapToInt(i -> i).toArray();
        }

        this.addAvailableEdgesRestriction((edge) -> {
            AntColonyGraph antColonyGraph = this.getAntColonyGraph();
            EdgeType edgeType = antColonyGraph.getEdgeType(edge);
            Integer target = antColonyGraph.getEdgeTarget(edge);
            Integer source = antColonyGraph.getEdgeSource(edge);
            if (edgeType == EdgeType.PULL_IN) {
                if (currentDepot != target) {
                    return true;
                }
            }
            if (edgeType == EdgeType.PULL_OUT) {
                int remainingTrips = remainingDepotsNr[source];
                return remainingTrips <= 0;
            }
            return false;
        });
        // This is for Master Depot Design
        this.addAvailableEdgesRestriction((edge) -> {
            AntColonyGraph antColonyGraph = this.getAntColonyGraph();
            EdgeType edgeType = antColonyGraph.getEdgeType(edge);
            Integer target = antColonyGraph.getEdgeTarget(edge);
            Integer source = antColonyGraph.getEdgeSource(edge);
            DefaultWeightedEdge lastEdge = this.getLastPickedEdge();
            EdgeType lastPickedEdgeType = this.getAntColonyGraph().getEdgeType(lastEdge);
            // Check if we can
            if (edgeType == EdgeType.MASTER_PULL_OUT) {
                int remainingTrips = remainingDepotsNr[target];
                if (remainingTrips <= 0)
                    return true;
            }
            if (edgeType == EdgeType.MASTER_PULL_IN) {
                if (lastPickedEdgeType == EdgeType.MASTER_PULL_OUT)
                    return true;
            }
            if (edgeType != EdgeType.MASTER_PULL_IN) {
                return lastPickedEdgeType == EdgeType.PULL_IN;
            }
            return false;
        });
    }

    @Override
    public Integer moveOnce() {
        int res = super.moveOnce();
        EdgeType lastPickedEdgeType = this.getAntColonyGraph().getEdgeType(lastPickedEdge);
        if (lastPickedEdgeType == EdgeType.PULL_OUT) {
            currentDepot = getAntColonyGraph().getEdgeSource(lastPickedEdge);
            this.remainingDepotsNr[currentDepot]--;
        }
        return res;
    }
}
