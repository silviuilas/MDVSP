package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.AntColonyGraph;
import ro.uaic.info.aco.AvailableEdgeRestriction;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.prb.EdgeType;
import ro.uaic.info.prb.Tour;

import java.util.*;

public abstract class Ant {
    private final Map<Integer, Boolean> visitedNodes = new HashMap<>();
    private final List<AvailableEdgeRestriction> availableEdgeRestrictions = new ArrayList<>();
    private final List<Integer> antsVisitedPath = new ArrayList<>();
    private final List<DefaultWeightedEdge> antsVisitedPathEdges = new ArrayList<>();
    protected AntColony antColony;
    protected int currentLocation = 0;
    protected DefaultWeightedEdge lastPickedEdge;
    AntColonyGraph antColonyGraph;
    int numberOfDecisions = 0;
    int sumOfNumberOfAvailablePaths = 0;
    private List<DefaultWeightedEdge> availableEdgesCache;
    private boolean isAvailableEdgesCacheValid = false;
    private int currentCost = 0;

    public Ant(AntColony antColony) {
        this.antColony = antColony;
        antColonyGraph = antColony.getAntColonyGraph();
    }

    public void run() {
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
        Set<DefaultWeightedEdge> edges = antColonyGraph.outgoingEdgesOf(position);
        List<DefaultWeightedEdge> availableEdges = new ArrayList<>(antColonyGraph.outgoingEdgesOf(position));
        for (DefaultWeightedEdge edge :
                edges) {
            Integer target = antColonyGraph.getEdgeTarget(edge);
            if ((visitedNodes.get(target) != null) && antColonyGraph.getIsVertexRepeatable().get(target) == null) {
                availableEdges.remove(edge);
            }
            for (AvailableEdgeRestriction availableEdgeRestriction :
                    availableEdgeRestrictions) {
                if (availableEdgeRestriction.shouldRemoveEdge(edge)) {
                    availableEdges.remove(edge);
                }
            }
        }
        if (availableEdges.size() <= 1) {
            System.out.print("");
        }
        isAvailableEdgesCacheValid = true;
        availableEdgesCache = availableEdges;
        return availableEdges;
    }

    public Integer goToNextPosition(DefaultWeightedEdge e) {
        Integer source = antColonyGraph.getEdgeSource(e);
        Integer target = antColonyGraph.getEdgeTarget(e);
        antsVisitedPath.add(target);
        antsVisitedPathEdges.add(e);
        visitedNodes.put(source, true);
        currentLocation = target;
        currentCost += antColonyGraph.getEdgeWeight(e);
        // System.out.println("remaining to visit " + getNumberOfNotVisitedVertexes() + " from " + source + " visited " + target);

        return currentLocation;
    }

    public abstract DefaultWeightedEdge pickAnEdge(List<DefaultWeightedEdge> availableEdges);

    public int getNumberOfNotVisitedVertexes() {
        return ((antColonyGraph.getN() + antColonyGraph.getM()) - visitedNodes.size());
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
            EdgeType edgeType = antColonyGraph.getEdgeType(edge);
            if (edgeType == EdgeType.PULL_OUT) {
                tour = new Tour();
                tour.add(antColonyGraph.getEdgeSource(edge));
                tour.add(antColonyGraph.getEdgeTarget(edge));
            } else if (edgeType == EdgeType.PULL_IN) {
                assert (tour != null);
                tour.add(antColonyGraph.getEdgeTarget(edge));
                deque.add(tour);
            } else if (edgeType == EdgeType.NORMAL) {
                assert (tour != null);
                tour.add(antColonyGraph.getEdgeTarget(edge));
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

    public Map<Integer, Boolean> getVisitedNodes() {
        return visitedNodes;
    }


    public DefaultWeightedEdge getLastPickedEdge() {
        return lastPickedEdge;
    }

    public AntColonyGraph getAntColonyGraph() {
        return antColonyGraph;
    }

    public List<Integer> getAntsVisitedPath() {
        return antsVisitedPath;
    }

    public int getCurrentCost() {
        return currentCost;
    }
}
