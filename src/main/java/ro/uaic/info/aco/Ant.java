package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;
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
    protected int unsatisfiedClients;
    private final int[] remainingDepotsNr;
    Map<Integer, Boolean> visitedNodes;

    public Ant(AntColony antColony) {
        this.antColony = antColony;
        antColonyGraph = antColony.getAntColonyGraph();
        visitedNodes = new HashMap<>();
        paths = new ArrayDeque<>();
        int m = antColony.getAntColonyGraph().getM();
        remainingDepotsNr = new int[m];
        int depotNr = 0;
        for (Integer capacity :
                antColony.getAntColonyGraph().getDepotsCapacity()) {
            remainingDepotsNr[depotNr] = capacity - 1;
            depotNr++;
        }
    }

    public void run() {
        while (!isFinished()) {
            moveOnce();
        }
    }

    public List<DefaultWeightedEdge> getAvailableEdges(int position) {
        Set<DefaultWeightedEdge> edges = antColonyGraph.outgoingEdgesOf(position);
        List<DefaultWeightedEdge> availableEdges = new ArrayList<>();
        for (DefaultWeightedEdge edge :
                edges) {
            Integer target = antColonyGraph.getEdgeTarget(edge);
            //only go to unvisited nodes
            if (visitedNodes.get(target) == null || !visitedNodes.get(target)) {
                //don't let pull in happen if the depot is different then the starting point
                if (antColonyGraph.getEdgeType(edge) == EdgeType.PULL_IN) {
                    if (currentDepot != antColonyGraph.getDepotType().get(target)) {
                        continue;
                    }
                }
                //don't let pull out happen if there are no depots of the same type to return to
                else if (antColonyGraph.getEdgeType(edge) == EdgeType.PULL_OUT) {
                    int remainingTrips = getRemainingDepotsNr()[antColonyGraph.getDepotType().get(position)];
                    if (remainingTrips <= 0)
                        continue;
                }
                //don't go to the depot if you can't make a pull out trip
                else if (antColonyGraph.getEdgeType(edge) == EdgeType.DEPOT_DEPOT) {
                    int remainingTrips = getRemainingDepotsNr()[antColonyGraph.getDepotType().get(target)];
                    if (remainingTrips <= 0)
                        continue;
                }
                availableEdges.add(edge);
            }
        }
        return availableEdges;
    }

    public abstract DefaultWeightedEdge  pickAnEdge(List<DefaultWeightedEdge> availableEdges);

    public Integer moveOnce() {
        List<DefaultWeightedEdge> availableEdges = getAvailableEdges(currentLocation);
        DefaultWeightedEdge pickedEdge = pickAnEdge(availableEdges);
        if (antColonyGraph.getEdgeType(pickedEdge) != EdgeType.DEPOT_DEPOT)
            return goToNextPosition(pickedEdge);
        else {
            currentLocation = antColonyGraph.getEdgeTarget(pickedEdge);
            currentDepot = antColonyGraph.getDepotType().get(currentLocation);
            return currentLocation;
        }
    }

    public boolean isFinished() {
        return getAvailableEdges(currentLocation).size() == 0 || isValid();
    }

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
                    throw new Exception("The same client has been served twice");
                }
                DefaultWeightedEdge edge = antColonyGraph.getEdge(path.get(i - 1), id);
                if (antColonyGraph.getEdgeWeight(edge) < 0) {
                    throw new Exception("The path should not exist");
                }
                happyCustomers[id - m]--;
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
            return getUnsatisfiedClientsNr() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

    public Integer goToNextPosition(DefaultWeightedEdge e) {
        Integer source = antColonyGraph.getEdgeSource(e);
        Integer target = antColonyGraph.getEdgeTarget(e);
        //FIXME maybe make me shorter
        if (antColonyGraph.getEdgeType(e) == EdgeType.PULL_OUT) {
            Integer depot = antColonyGraph.getDepotType().get(source);
            getRemainingDepotsNr()[depot]--;
            paths.add(new Tour());
        }
        paths.getLast().add(source);
        if (antColonyGraph.getEdgeType(e) == EdgeType.PULL_IN) {
            paths.getLast().add(target);
        }
        visitedNodes.put(source, true);
        currentLocation = target;
        currentCost = (int) (currentCost + antColonyGraph.getEdgeWeight(e));
        return currentLocation;
    }

    public int wrapGetUnsatisfiedClientsNr() {
        try {
            return getUnsatisfiedClientsNr();
        } catch (Exception e) {
            e.printStackTrace();
            return 99999999;
        }
    }
}
