package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.AntColonyGraph;
import ro.uaic.info.prb.EdgeType;
import ro.uaic.info.prb.Tour;

import java.util.*;

public abstract class Ant {
    AntColonyGraph antColonyGraph;
    protected AntColony antColony;

    protected Deque<Tour> paths;
    protected int currentLocation = 0;
    protected int currentCost = 0;
    protected int currentDepot;
    protected boolean hasTheLastMoveBeenConnectedToMaster;
    protected DefaultWeightedEdge lastPickedEdge;
    protected int unsatisfiedClients;
    private final int[] remainingDepotsNr;
    private List<DefaultWeightedEdge> availableEdgesCache;
    private boolean isAvailableEdgesCacheValid = false;
    Map<Integer, Boolean> visitedNodes;

    int numberOfDecisions;
    int sumOfNumberOfAvailablePaths;

    public Ant(AntColony antColony) {
        this.antColony = antColony;
        antColonyGraph = antColony.getAntColonyGraph();
        visitedNodes = new HashMap<>();
        paths = new ArrayDeque<>();
        numberOfDecisions = 0;
        sumOfNumberOfAvailablePaths = 0;
        int m = antColony.getAntColonyGraph().getM();
        remainingDepotsNr = new int[m];
        int depotNr = 0;
        for (Integer capacity :
                antColony.getAntColonyGraph().getDepotsCapacity()) {
            remainingDepotsNr[depotNr] = capacity;
            depotNr++;
        }
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
        EdgeType edgeType = antColonyGraph.getEdgeType(pickedEdge);
        isAvailableEdgesCacheValid = false;
        lastPickedEdge = pickedEdge;
        if (edgeType != EdgeType.MASTER_PULL_OUT && edgeType != EdgeType.MASTER_PULL_IN) {
            numberOfDecisions++;
            sumOfNumberOfAvailablePaths += availableEdges.size();
            return goToNextPosition(pickedEdge);
        } else {
            currentLocation = antColonyGraph.getEdgeTarget(pickedEdge);
            currentDepot = currentLocation;
            return currentLocation;
        }
    }

    public List<DefaultWeightedEdge> getAvailableEdges(int position) {
        if (isAvailableEdgesCacheValid) {
            return availableEdgesCache;
        }
        Set<DefaultWeightedEdge> edges = antColonyGraph.outgoingEdgesOf(position);
        List<DefaultWeightedEdge> availableEdges = new ArrayList<>();
        EdgeType lastPickedEdgeType = antColonyGraph.getEdgeType(lastPickedEdge);
        for (DefaultWeightedEdge edge :
                edges) {
            Integer target = antColonyGraph.getEdgeTarget(edge);
            EdgeType edgeType = antColonyGraph.getEdgeType(edge);
            //only go to unvisited nodes
            if ((visitedNodes.get(target) == null || !visitedNodes.get(target)) || (edgeType == EdgeType.MASTER_PULL_OUT || edgeType == EdgeType.MASTER_PULL_IN || edgeType == EdgeType.PULL_IN)) {
                //don't let pull in happen if the depot is different then the starting point
                if (edgeType == EdgeType.PULL_IN) {
                    // uncomment this if you want better results at TSP else ignore
//                    if (visitedNodes.size() + 1  < (antColonyGraph.getM() + antColonyGraph.getN()))
//                        continue;
                    if (currentDepot != target) {
                        continue;
                    }
                }
                //don't let pull out happen if there are no depots of the same type to return to
                else if (edgeType == EdgeType.PULL_OUT) {
                    int remainingTrips = getRemainingDepotsNr()[position];
                    if (remainingTrips <= 0)
                        continue;
                }
                //don't go to the depot if you can't make a pull out trip
                else if (edgeType == EdgeType.MASTER_PULL_OUT) {
                    int remainingTrips = getRemainingDepotsNr()[target];
                    if (remainingTrips <= 0)
                        continue;
                } else if (edgeType == EdgeType.MASTER_PULL_IN) {
                    if (lastPickedEdgeType == EdgeType.MASTER_PULL_OUT) {
                        continue;
                    }
                }
                if (edgeType != edgeType.MASTER_PULL_IN){
                    if(lastPickedEdgeType == EdgeType.PULL_IN){
                        continue;
                    }
                }
                // uncomment this if you want to the ants to have a max capacity before returning
//                if (this.getPaths().size() > 0 && this.getPaths().getLast().size() > 6)
//                    if(edgeType == EdgeType.NORMAL)
//                        continue;
                availableEdges.add(edge);
            }
        }
        availableEdgesCache = availableEdges;
        isAvailableEdgesCacheValid = true;
        return availableEdges;
    }

    public Integer goToNextPosition(DefaultWeightedEdge e) {
        Integer source = antColonyGraph.getEdgeSource(e);
        Integer target = antColonyGraph.getEdgeTarget(e);
        EdgeType edgeType = antColonyGraph.getEdgeType(e);
        if (edgeType == EdgeType.PULL_OUT) {
            getRemainingDepotsNr()[source]--;
            paths.add(new Tour());
        }
        paths.getLast().add(source);
        if (edgeType == EdgeType.PULL_IN) {
            paths.getLast().add(target);
        }
        visitedNodes.put(source, true);
        currentLocation = target;
        currentCost = (int) (currentCost + antColonyGraph.getEdgeWeight(e));
        return currentLocation;
    }

    public abstract DefaultWeightedEdge pickAnEdge(List<DefaultWeightedEdge> availableEdges);

    public int getUnsatisfiedClientsNr() throws Exception {
        int n = antColonyGraph.getN();
        int m = antColonyGraph.getM();
        int[] happyCustomers = new int[n];
        for (int i = 0; i < n; i++)
            happyCustomers[i] = 1;
        for (Tour path :
                paths) {
            for (int i = 1; i < path.size() - 1; i++) {
                int id = path.get(i);
                if (happyCustomers[id - m] != 1) {
                    // throw new Exception("The same client has been served twice");
                }
                DefaultWeightedEdge edge = antColonyGraph.getEdge(path.get(i - 1), id);
                if (!antColonyGraph.containsEdge(edge) || antColonyGraph.getEdgeWeight(edge) < 0) {
                    throw new Exception("The path should not exist");
                }
                happyCustomers[id - m] = 0;
            }
        }
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (happyCustomers[i] != 0)
                count++;
        }
        return count;
    }

    public boolean isValid() {
        try {
            return ((antColonyGraph.getN() + antColonyGraph.getM()) - visitedNodes.size()) == 0;
            // return getUnsatisfiedClientsNr() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int wrapGetUnsatisfiedClientsNr() {
        try {
            unsatisfiedClients = getUnsatisfiedClientsNr();
            return unsatisfiedClients;
        } catch (Exception e) {
            e.printStackTrace();
            return 99999999;
        }
    }

    public int getCurrentCost() {
        return currentCost;
    }

    public int getCurrentLocation() {
        return currentLocation;
    }

    public Deque<Tour> getPaths() {
        return paths;
    }

    public int[] getRemainingDepotsNr() {
        return remainingDepotsNr;
    }

    public int getNumberOfDecisions() {
        return numberOfDecisions;
    }

    public double getAvgOfNumberOfAvailablePaths() {
        return (double) sumOfNumberOfAvailablePaths / (double) numberOfDecisions;
    }
}
