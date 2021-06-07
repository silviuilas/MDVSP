package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;
import ro.uaic.info.prb.EdgeType;

public abstract class MdvspAntPeerToPeer extends MdvspAnt {
    public MdvspAntPeerToPeer(MdvspAntColonyGraph mdvspAntColonyGraph) {
        super(mdvspAntColonyGraph);
        if (remainingDepotsNr == null) {
            remainingDepotsNr = antColony.getMdvspAntColonyGraph().getDepotsCapacity().stream().mapToInt(i -> i).toArray();
        }

        this.addAvailableEdgesRestriction((edge) -> {
            EdgeType edgeType = mdvspAntColonyGraph.getEdgeType(edge);
            Integer target = mdvspAntColonyGraph.getEdgeTarget(edge);
            Integer source = mdvspAntColonyGraph.getEdgeSource(edge);
            DefaultWeightedEdge lastEdge = this.getLastPickedEdge();
            EdgeType lastPickedEdgeType = this.getMdvspAntColonyGraph().getEdgeType(lastEdge);
            // once we arrive in depot we must pull out
            if(lastPickedEdgeType == EdgeType.DEPOT_DEPOT){
                if(edgeType != EdgeType.PULL_OUT){
                    return true;
                }
            }
            // don't return to a different depot than the one you started at
            if (edgeType == EdgeType.PULL_IN) {
                if (currentDepot != target) {
                    return true;
                }
            }
            if(lastPickedEdgeType == EdgeType.PULL_IN){
                if(edgeType != EdgeType.DEPOT_DEPOT){
                    return true;
                }
                if (this.getTimesNodesWhereVisited().get(target) != null && this.getTimesNodesWhereVisited().get(target) >= 2) {
                    return true;
                }
            }
            return false;
        });

    }

    @Override
    public int getNumberOfNotVisitedVertexes() {
        int start = this.mdvspAntColonyGraph.getM();
        int size = this.mdvspAntColonyGraph.getN();
        int nr = 0;
        for (int i = start; i < start + size; i++) {
            if (this.timesNodesWhereVisited.get(i) == null) {
                nr++;
                continue;
            }
            if (this.timesNodesWhereVisited.get(i) == 0) {
                nr++;
            }
        }
        return nr;
    }

//    @Override
//    public Integer moveOnce() {
//        int res = super.moveOnce();
//        EdgeType lastPickedEdgeType = this.getMdvspAntColonyGraph().getEdgeType(lastPickedEdge);
//        if (lastPickedEdgeType == EdgeType.PULL_OUT) {
//            currentDepot = getMdvspAntColonyGraph().getEdgeSource(lastPickedEdge);
//        } else if (lastPickedEdgeType == EdgeType.DEPOT_DEPOT) {
//            currentDepot = getMdvspAntColonyGraph().getEdgeTarget(lastPickedEdge);
//        }
//        return res;
//    }
}
