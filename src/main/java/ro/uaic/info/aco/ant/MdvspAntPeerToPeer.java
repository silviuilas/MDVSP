package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.AntColonyGraph;
import ro.uaic.info.prb.EdgeType;

public class MdvspAntPeerToPeer extends SmartAnt {
    int[] remainingDepotsNr;
    int currentDepot;


    public MdvspAntPeerToPeer(AntColony antColony) {
        super(antColony);
        if (remainingDepotsNr == null) {
            remainingDepotsNr = antColony.getAntColonyGraph().getDepotsCapacity().stream().mapToInt(i -> i).toArray();
        }

        this.addAvailableEdgesRestriction((edge) -> {
            AntColonyGraph antColonyGraph = this.getAntColonyGraph();
            EdgeType edgeType = antColonyGraph.getEdgeType(edge);
            Integer target = antColonyGraph.getEdgeTarget(edge);
            Integer source = antColonyGraph.getEdgeSource(edge);
            // don't return to a different depot than the one you started at
            if (edgeType == EdgeType.PULL_IN) {
                // int value = ((AntColonyGraphPeerToPeer) antColonyGraph).getVertexActualValue().get(target);
                if (currentDepot != target) {
                    return true;
                }
            }
            // don't pull in a depot that has been visited more then once, except when it's a pull in
            if (edgeType != EdgeType.PULL_IN && (this.getTimesNodesWhereVisited().get(target) != null && this.getTimesNodesWhereVisited().get(target) >= 1)) {
                return true;
            }

            if (edgeType == EdgeType.DEPOT_DEPOT) {
                DefaultWeightedEdge lastEdge = this.getLastPickedEdge();
                EdgeType lastPickedEdgeType = this.getAntColonyGraph().getEdgeType(lastEdge);
                if (lastPickedEdgeType != EdgeType.PULL_IN) {
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
        int start = this.antColonyGraph.getM();
        int size = this.antColonyGraph.getN();
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

    @Override
    public Integer moveOnce() {
        int res = super.moveOnce();
        EdgeType lastPickedEdgeType = this.getAntColonyGraph().getEdgeType(lastPickedEdge);
        if (lastPickedEdgeType == EdgeType.PULL_OUT) {
            currentDepot = getAntColonyGraph().getEdgeSource(lastPickedEdge);
        } else if (lastPickedEdgeType == EdgeType.DEPOT_DEPOT) {
            currentDepot = getAntColonyGraph().getEdgeTarget(lastPickedEdge);
        }
        return res;
    }
}
