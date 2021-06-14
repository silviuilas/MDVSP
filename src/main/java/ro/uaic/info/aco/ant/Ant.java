package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.AvailableEdgeRestriction;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.AntColonyGraph;
import ro.uaic.info.prb.Tour;

import java.util.*;

public abstract class Ant {
    protected final Map<Integer, Integer> timesNodesWhereVisited = new HashMap<>();
    protected final List<Integer> antsVisitedPath = new ArrayList<>();
    protected final List<DefaultWeightedEdge> antsVisitedPathEdges = new ArrayList<>();
    private final List<AvailableEdgeRestriction> availableEdgeRestrictions = new ArrayList<>();
    protected AntColony antColony;
    protected int currentLocation = 0;
    protected DefaultWeightedEdge lastPickedEdge;
    AntColonyGraph antColonyGraph;
    int numberOfDecisions = 0;
    int sumOfNumberOfAvailablePaths = 0;
    private List<DefaultWeightedEdge> availableEdgesCache;
    private boolean isAvailableEdgesCacheValid = false;
    private int currentCost = 0;

    public Ant(AntColonyGraph antColonyGraph) {
        this.antColonyGraph = antColonyGraph;
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
        Set<DefaultWeightedEdge> edges = antColonyGraph.outgoingEdgesOf(position);
        List<DefaultWeightedEdge> availableEdges = new ArrayList<>(edges);
        for (DefaultWeightedEdge edge :
                edges) {
            Integer target = antColonyGraph.getEdgeTarget(edge);
            if ((timesNodesWhereVisited.get(target) != null) && antColonyGraph.getIsVertexRepeatable().get(target) == null) {
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
        Integer source = antColonyGraph.getEdgeSource(e);
        Integer target = antColonyGraph.getEdgeTarget(e);
        antsVisitedPath.add(target);
        antsVisitedPathEdges.add(e);
        timesNodesWhereVisited.putIfAbsent(source, 0);
        int nr = timesNodesWhereVisited.get(source);
        timesNodesWhereVisited.put(source, nr + 1);
        currentLocation = target;
        currentCost += antColonyGraph.getEdgeWeight(e);
        // System.out.println("remaining to visit " + getNumberOfNotVisitedVertexes() + " from " + source + " visited " + target);

        return currentLocation;
    }

    public abstract DefaultWeightedEdge pickAnEdge(List<DefaultWeightedEdge> availableEdges);

    public int getNumberOfNotVisitedVertexes() {
        return ((antColonyGraph.vertexSet().size()) - timesNodesWhereVisited.size());
    }

    public abstract Deque<Tour> getDequeTour();

    public boolean isValid() {
        try {
            return (getNumberOfNotVisitedVertexes() == 0);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
