package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.AvailableEdgeRestriction;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;
import ro.uaic.info.prb.EdgeType;
import ro.uaic.info.prb.Tour;

import java.util.*;

public abstract class Ant {
    protected final Map<Integer, Integer> timesNodesWhereVisited = new HashMap<>();
    private final List<AvailableEdgeRestriction> availableEdgeRestrictions = new ArrayList<>();
    private final List<Integer> antsVisitedPath = new ArrayList<>();
    private final List<DefaultWeightedEdge> antsVisitedPathEdges = new ArrayList<>();
    protected AntColony antColony;
    protected int currentLocation = 0;
    protected DefaultWeightedEdge lastPickedEdge;
    MdvspAntColonyGraph mdvspAntColonyGraph;
    int numberOfDecisions = 0;
    int sumOfNumberOfAvailablePaths = 0;
    private List<DefaultWeightedEdge> availableEdgesCache;
    private boolean isAvailableEdgesCacheValid = false;
    private int currentCost = 0;

    public Ant(MdvspAntColonyGraph mdvspAntColonyGraph) {
        this.mdvspAntColonyGraph = mdvspAntColonyGraph;
    }

    public void run() {
        antsVisitedPath.add(currentLocation);
        while (!isFinished()) {
            moveOnce();
        }
    }

    public boolean isFinished() {
        return getAvailableEdges(currentLocation).size() == 0 || isValid();
    }

    public Integer moveOnce() {
        List<DefaultWeightedEdge> availableEdges = getAvailableEdges(currentLocation);
        DefaultWeightedEdge pickedEdge = pickAnEdge(availableEdges);
        isAvailableEdgesCacheValid = false;
        lastPickedEdge = pickedEdge;
        numberOfDecisions++;
        sumOfNumberOfAvailablePaths += availableEdges.size();
        return goToNextPosition(pickedEdge);
    }

    public List<DefaultWeightedEdge> getAvailableEdges(int position) {
        if (isAvailableEdgesCacheValid) {
            return availableEdgesCache;
        }
        Set<DefaultWeightedEdge> edges = mdvspAntColonyGraph.outgoingEdgesOf(position);
        List<DefaultWeightedEdge> availableEdges = new ArrayList<>(mdvspAntColonyGraph.outgoingEdgesOf(position));
        for (DefaultWeightedEdge edge :
                edges) {
            Integer target = mdvspAntColonyGraph.getEdgeTarget(edge);
            if ((timesNodesWhereVisited.get(target) != null) && mdvspAntColonyGraph.getIsVertexRepeatable().get(target) == null) {
                availableEdges.remove(edge);
            } else {
                for (AvailableEdgeRestriction availableEdgeRestriction :
                        availableEdgeRestrictions) {
                    if (availableEdgeRestriction.shouldRemoveEdge(edge)) {
                        availableEdges.remove(edge);
                    }
                }
            }
        }
        // System.out.println(position);
        if (availableEdges.size() <= 1) {
            System.out.print("");
        }
        isAvailableEdgesCacheValid = true;
        availableEdgesCache = availableEdges;
        return availableEdges;
    }

    public Integer goToNextPosition(DefaultWeightedEdge e) {
        Integer source = mdvspAntColonyGraph.getEdgeSource(e);
        Integer target = mdvspAntColonyGraph.getEdgeTarget(e);
        antsVisitedPath.add(target);
        antsVisitedPathEdges.add(e);
        timesNodesWhereVisited.putIfAbsent(source, 0);
        int nr = timesNodesWhereVisited.get(source);
        timesNodesWhereVisited.put(source, nr + 1);
        currentLocation = target;
        currentCost += mdvspAntColonyGraph.getEdgeWeight(e);
        // System.out.println("remaining to visit " + getNumberOfNotVisitedVertexes() + " from " + source + " visited " + target);

        return currentLocation;
    }

    public abstract DefaultWeightedEdge pickAnEdge(List<DefaultWeightedEdge> availableEdges);

    public int getNumberOfNotVisitedVertexes() {
        return ((mdvspAntColonyGraph.vertexSet().size()) - timesNodesWhereVisited.size());
    }

    public boolean isValid() {
        try {
            return (getNumberOfNotVisitedVertexes() == 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Deque<Tour> getDequeTour() {
        Deque<Tour> deque = new ArrayDeque<>();
        Tour tour = null;
        for (DefaultWeightedEdge edge :
                antsVisitedPathEdges) {
            EdgeType edgeType = mdvspAntColonyGraph.getEdgeType(edge);
            // TODO make it work for master depot
            Integer source = mdvspAntColonyGraph.getEdgeSource(edge);
            Integer target = mdvspAntColonyGraph.getEdgeTarget(edge);
//            Integer source1 = ((PeerToPeerACG) mdvspAntColonyGraph).getVertexActualValue().get(source);
//            if(source1 != null)
//                source = source1;
//            Integer target1 = ((PeerToPeerACG) mdvspAntColonyGraph).getVertexActualValue().get(target);
//            if(target1 != null)
//                target = target1;
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


    public int getCurrentLocation() {
        return currentLocation;
    }

    public int getNumberOfDecisions() {
        return numberOfDecisions;
    }

    public double getAvgOfNumberOfAvailablePaths() {
        return (double) sumOfNumberOfAvailablePaths / (double) numberOfDecisions;
    }

    public void addAvailableEdgesRestriction(AvailableEdgeRestriction availableEdgeRestriction) {
        availableEdgeRestrictions.add(availableEdgeRestriction);
    }

    public Map<Integer, Integer> getTimesNodesWhereVisited() {
        return timesNodesWhereVisited;
    }

    public DefaultWeightedEdge getLastPickedEdge() {
        return lastPickedEdge;
    }

    public MdvspAntColonyGraph getMdvspAntColonyGraph() {
        return mdvspAntColonyGraph;
    }

    public List<Integer> getAntsVisitedPath() {
        return antsVisitedPath;
    }

    public int getCurrentCost() {
        return currentCost;
    }
}
