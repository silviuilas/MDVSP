package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.AntColony;
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
        if (antColonyGraph.getEdgeType(pickedEdge) != EdgeType.MASTER_PULL_OUT && antColonyGraph.getEdgeType(pickedEdge) != EdgeType.MASTER_PULL_IN)
            return goToNextPosition(pickedEdge);
        else {
            currentLocation = antColonyGraph.getEdgeTarget(pickedEdge);
            currentDepot = currentLocation;
            return currentLocation;
        }
    }

    public List<DefaultWeightedEdge> getAvailableEdges(int position) {
        Set<DefaultWeightedEdge> edges = antColonyGraph.outgoingEdgesOf(position);
        List<DefaultWeightedEdge> availableEdges = new ArrayList<>();
        for (DefaultWeightedEdge edge :
                edges) {
            Integer target = antColonyGraph.getEdgeTarget(edge);
            //only go to unvisited nodes
            if ((visitedNodes.get(target) == null || !visitedNodes.get(target)) || (antColonyGraph.getEdgeType(edge) == EdgeType.MASTER_PULL_OUT || antColonyGraph.getEdgeType(edge) == EdgeType.MASTER_PULL_OUT || antColonyGraph.getEdgeType(edge) == EdgeType.PULL_IN)) {
                //don't let pull in happen if the depot is different then the starting point
                if (antColonyGraph.getEdgeType(edge) == EdgeType.PULL_IN) {
                    // uncomment this if you want better results at TSP else ignore
//                    if (visitedNodes.size() + 1  < (antColonyGraph.getM() + antColonyGraph.getN()))
//                        continue;
                    if (currentDepot != target) {
                        continue;
                    }
                }
                //don't let pull out happen if there are no depots of the same type to return to
                else if (antColonyGraph.getEdgeType(edge) == EdgeType.PULL_OUT) {
                    int remainingTrips = getRemainingDepotsNr()[position];
                    if (remainingTrips <= 0)
                        continue;
                }
                //don't go to the depot if you can't make a pull out trip
                else if (antColonyGraph.getEdgeType(edge) == EdgeType.MASTER_PULL_OUT) {
                    int remainingTrips = getRemainingDepotsNr()[target];
                    if (remainingTrips <= 0)
                        continue;
                }
                // uncomment this if you want to the ants to have a max capacity before returning
                else if (this.getPaths().size() > 0 && this.getPaths().getLast().size() > 6)
                    continue;
                availableEdges.add(edge);
            }
        }
        return availableEdges;
    }

    public Integer goToNextPosition(DefaultWeightedEdge e) {
        Integer source = antColonyGraph.getEdgeSource(e);
        Integer target = antColonyGraph.getEdgeTarget(e);
        if (antColonyGraph.getEdgeType(e) == EdgeType.PULL_OUT) {
            getRemainingDepotsNr()[source]--;
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
            return getUnsatisfiedClientsNr() == 0;
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


}
