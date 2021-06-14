package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;
import ro.uaic.info.prb.EdgeType;
import ro.uaic.info.prb.Tour;

import java.util.ArrayDeque;
import java.util.Deque;

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
            // can't pull in a different depot then the one you started at
            if (edgeType == EdgeType.PULL_IN) {
                if (currentDepot != target) {
                    return true;
                }
            }
            // don't pull pull out a depot if you don't have enough vehicles
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
            int actualValue = getMdvspAntColonyGraph().getVertexActualValue().get(currentDepot);
            this.remainingDepotsNr[actualValue]--;
        }
        currentTourSize++;
        return res;
    }

    public MdvspAntColonyGraph getMdvspAntColonyGraph(){
        return (MdvspAntColonyGraph) getAntColonyGraph();
    }

    public Deque<Tour> getDequeTour() {
        Deque<Tour> deque = new ArrayDeque<>();
        Tour tour = null;
        for (DefaultWeightedEdge edge :
                antsVisitedPathEdges) {
            EdgeType edgeType = getMdvspAntColonyGraph().getEdgeType(edge);
            Integer source = getMdvspAntColonyGraph().getEdgeSource(edge);
            Integer target = getMdvspAntColonyGraph().getEdgeTarget(edge);
            Integer source1 = (getMdvspAntColonyGraph()).getVertexActualValue().get(source);
            if (source1 != null)
                source = source1;
            Integer target1 = (getMdvspAntColonyGraph()).getVertexActualValue().get(target);
            if (target1 != null)
                target = target1;
            if (edgeType == EdgeType.PULL_OUT) {
                tour = new Tour();
                tour.add(source);
                tour.add(target);
            } else if (edgeType == EdgeType.PULL_IN) {
                assert (tour != null);
                tour.add(target);
                deque.add(tour);
            } else if (edgeType == EdgeType.NORMAL) {
                assert (tour != null);
                tour.add(target);
            }
        }
        return deque;
    }

    @Override
    public int getNumberOfNotVisitedVertexes() {
        int start = this.getMdvspAntColonyGraph().getM();
        int size = this.getMdvspAntColonyGraph().getN();
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
}
