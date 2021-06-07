package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;
import ro.uaic.info.prb.EdgeType;

public abstract class MdvspAnt extends Ant {
    int[] remainingDepotsNr;
    int currentDepot;
    int currentTourSize;


    public MdvspAnt(MdvspAntColonyGraph mdvspAntColonyGraph) {
        super(mdvspAntColonyGraph);
        if (remainingDepotsNr == null) {
            remainingDepotsNr = mdvspAntColonyGraph.getDepotsCapacity().stream().mapToInt(i -> i).toArray();
        }

        this.addAvailableEdgesRestriction((edge) -> {
            EdgeType edgeType = mdvspAntColonyGraph.getEdgeType(edge);
            Integer target = mdvspAntColonyGraph.getEdgeTarget(edge);
            Integer source = mdvspAntColonyGraph.getEdgeSource(edge);
            if (edgeType == EdgeType.PULL_IN) {
                if (currentDepot != target) {
                    return true;
                }
            }
            if (edgeType == EdgeType.PULL_OUT) {
                currentTourSize = 0;
                int actualValue = mdvspAntColonyGraph.getVertexActualValue().get(source);
                int remainingTrips = remainingDepotsNr[actualValue];
                return remainingTrips <= 0;
            }
//            if (edgeType == EdgeType.NORMAL && currentTourSize > 6) {
//                return true;
//            }
            return false;
        });
    }

    @Override
    public Integer moveOnce() {
        int res = super.moveOnce();
        EdgeType lastPickedEdgeType = this.getMdvspAntColonyGraph().getEdgeType(lastPickedEdge);
        if (lastPickedEdgeType == EdgeType.PULL_OUT) {
            currentDepot = getMdvspAntColonyGraph().getEdgeSource(lastPickedEdge);
            currentTourSize = 0;
            int actualValue = mdvspAntColonyGraph.getVertexActualValue().get(currentDepot);
            this.remainingDepotsNr[actualValue]--;
        }
        currentTourSize++;
        return res;
    }
}
