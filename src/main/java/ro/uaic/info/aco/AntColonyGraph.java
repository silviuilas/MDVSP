package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.prb.EdgeType;
import ro.uaic.info.prb.ProblemGraph;

import java.util.List;


public class AntColonyGraph extends ProblemGraph {
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

        int vertex = vertexSet().size();
        this.addVertex(vertex);
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
        return i < m && i >= 0;
    }

    public boolean isMaster(int i) {
        return i == this.m + this.n;
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
