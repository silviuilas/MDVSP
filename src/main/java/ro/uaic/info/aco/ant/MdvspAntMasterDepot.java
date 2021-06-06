package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.AntColonyGraph;
import ro.uaic.info.prb.EdgeType;

public class MdvspAntMasterDepot extends SmartAnt {
    int[] remainingDepotsNr;
    int currentDepot;
    int currentTourSize;


    public MdvspAntMasterDepot(AntColony antColony) {
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
                currentTourSize = 0;
                int remainingTrips = remainingDepotsNr[source];
                return remainingTrips <= 0;
            }
            if (edgeType == EdgeType.NORMAL && currentTourSize > 6) {
                return true;
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
            // we should not go to a depot that has no more remainingDepots left
            if (edgeType == EdgeType.MASTER_PULL_OUT) {
                int remainingTrips = remainingDepotsNr[target];
                if (remainingTrips <= 0)
                    return true;
            }
            // we can't pull in the master right after we pulled out
            if (edgeType == EdgeType.MASTER_PULL_IN) {
                if (lastPickedEdgeType == EdgeType.MASTER_PULL_OUT)
                    return true;
            }
            // we just pulled in we can't go back until we go to the master depot
            if (lastPickedEdgeType == EdgeType.PULL_IN) {
                if (edgeType != EdgeType.MASTER_PULL_IN)
                    return true;
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
            currentTourSize = 0;
            this.remainingDepotsNr[currentDepot]--;
        }
        currentTourSize++;
        return res;
    }
}
