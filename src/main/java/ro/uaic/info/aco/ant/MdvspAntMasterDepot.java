package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;
import ro.uaic.info.prb.EdgeType;

public abstract class MdvspAntMasterDepot extends MdvspAnt {

    public MdvspAntMasterDepot(MdvspAntColonyGraph mdvspAntColonyGraph) {
        super(mdvspAntColonyGraph);
        // This is for Master Depot Design
        this.addAvailableEdgesRestriction((edge) -> {
            EdgeType edgeType = mdvspAntColonyGraph.getEdgeType(edge);
            Integer target = mdvspAntColonyGraph.getEdgeTarget(edge);
            Integer source = mdvspAntColonyGraph.getEdgeSource(edge);
            DefaultWeightedEdge lastEdge = this.getLastPickedEdge();
            EdgeType lastPickedEdgeType = this.getMdvspAntColonyGraph().getEdgeType(lastEdge);
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
}
