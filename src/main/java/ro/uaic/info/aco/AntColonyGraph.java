package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.prb.EdgeType;
import ro.uaic.info.prb.ProblemGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AntColonyGraph extends ProblemGraph {
    private final Map<Integer, Integer> depotType;
    double[][] pheromoneTable;
    private final int n;
    private final int m;
    private final int[][] cost;
    private final List<Integer> depotsCapacity;
    private final int maxNr;

    public AntColonyGraph(int n, int m, int[][] cost, List<Integer> depotsCapacity) {
        super(n, m, cost);
        this.n = n;
        this.m = m;
        this.cost = cost;
        this.depotsCapacity = depotsCapacity;
        depotType = new HashMap<>();

        //duplicate each depot with the number of vehicles
        int depotNr = 0;
        for (Integer capacity :
                depotsCapacity) {
            for (int j = 0; j < capacity; j++) {
                int vertex = vertexSet().size();
                this.addVertex(vertex);
                duplicateVertexEdges(depotNr, vertex);
                depotType.put(vertex, depotNr);
                System.out.println(edgeSet().size());
            }
            depotNr++;
        }
        for (int i = 0; i < m; i++)
            depotType.put(i, i);
        int vertex = vertexSet().size();
        this.addVertex(vertex);
        depotType.put(vertex, m);
        for (Integer val :
                this.vertexSet()) {
            if (isDepot(val)) {
                this.addEdge(vertex, val);
                this.addEdge(val, vertex);
                this.setEdgeWeight(vertex, val, 0);
                this.setEdgeWeight(val, vertex, 0);
            }
        }
        initEdgeTypeMap();
        pheromoneTable = new double[vertexSet().size()][vertexSet().size()];
        maxNr = vertexSet().size();
    }

    public void initEdgeTypeMap() {
        for (DefaultWeightedEdge edge :
                this.edgeSet()) {
            Integer source = this.getEdgeSource(edge);
            Integer target = this.getEdgeTarget(edge);
            if (isMaster(source) && isDepot(target))
                edgeTypeMap.put(edge, EdgeType.MASTER_PULL_OUT);
            else if (isDepot(source) && isMaster(target))
                edgeTypeMap.put(edge, EdgeType.MASTER_PULL_IN);
            else if (isDepot(source) && !isDepot(target))
                edgeTypeMap.put(edge, EdgeType.PULL_OUT);
            else if (!isDepot(source) && isDepot(target))
                edgeTypeMap.put(edge, EdgeType.PULL_IN);
            else if (!isDepot(source) && !isDepot(target))
                edgeTypeMap.put(edge, EdgeType.NORMAL);
        }
    }

    public boolean isDepot(int i) {
        if (depotType.get(i) == null)
            return i < m;
        else
            return depotType.get(i) < m;
    }

    public boolean isMaster(int i) {
        if (depotType.get(i) == null)
            return false;
        else
            return depotType.get(i) == this.m;
    }

    public Map<Integer, Integer> getDepotType() {
        return depotType;
    }

    public double getPheromone(int i, int j) {
        return pheromoneTable[i][j];
    }

    public void setPheromone(int i, int j, double nr) {
        pheromoneTable[i][j] = nr;
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int[][] getCost() {
        return cost;
    }

    public List<Integer> getDepotsCapacity() {
        return depotsCapacity;
    }

    public int getMaxNr() {
        return maxNr;
    }
}
