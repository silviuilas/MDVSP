package ro.uaic.info.aco.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.prb.ProblemGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class AntColonyGraph extends ProblemGraph {
    protected final int n;
    protected final int m;
    protected final int[][] cost;
    protected final int maxNr;
    protected final Map<Integer, Boolean> isVertexRepeatable = new HashMap<>();
    double[][] pheromoneTable;
    Map<Integer, Integer> connectivityValues;
    List<Integer> depotsCapacity;

    public AntColonyGraph(int n, int m, int[][] cost, List<Integer> depotsCapacity) {
        super(n, m, cost);
        this.n = n;
        this.m = m;
        this.cost = cost;
        this.depotsCapacity = new ArrayList<>(depotsCapacity);
        maxNr = vertexSet().size();
        init();
        initEdgeTypeMap();
        pheromoneTable = new double[vertexSet().size()][vertexSet().size()];
        connectivityValues = new HashMap<>();
        initConnectivityValues();
    }

    private void initConnectivityValues() {
        for (DefaultWeightedEdge edge :
                this.edgeSet()) {
            int source = this.getEdgeSource(edge);
            connectivityValues.putIfAbsent(source, 1);
            if (!isDepot(source)) {
                int count = connectivityValues.get(source);
                connectivityValues.put(source, count + 1);
            }
        }
    }

    public abstract void init();

    public abstract void initEdgeTypeMap();

    public abstract boolean isDepot(int i);

    public abstract boolean isMaster(int i);

    public boolean isNormal(int i) {
        return !isDepot(i) && !isMaster(i);
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

    public int getMaxNr() {
        return maxNr;
    }

    public double[][] getPheromoneTable() {
        return pheromoneTable;
    }

    public Map<Integer, Boolean> getIsVertexRepeatable() {
        return isVertexRepeatable;
    }

    public List<Integer> getDepotsCapacity() {
        return depotsCapacity;
    }

    public Map<Integer, Integer> getConnectivityValues() {
        return connectivityValues;
    }
}
