package ro.uaic.info.aco.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.*;


public abstract class AntColonyGraph  extends SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> {
    protected final int size;
    protected final int[][] cost;
    protected final int maxNr;
    protected final Map<Integer, Boolean> isVertexRepeatable = new HashMap<>();
    double[][] pheromoneTable;

    public AntColonyGraph(int size, int[][] cost) {
        super(DefaultWeightedEdge.class);
        this.size = size;
        this.cost = cost;
        maxNr = vertexSet().size();
        createGraph();
    }

    public void createGraph() {

        for (int i = 0; i < size; i++) {
            this.addVertex(i);
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cost[i][j] >= 0) {
                    DefaultWeightedEdge e = this.addEdge(i, j);
                    if (cost[i][j] == 0) {
                        cost[i][j] = 1;
                    }
                    this.setEdgeWeight(e, cost[i][j]);
                }
            }
        }
    }

    public void init(){
        initEdgeTypeMap();
        pheromoneTable = new double[vertexSet().size()][vertexSet().size()];
    }

    public abstract void initEdgeTypeMap();


    public double getPheromone(int i, int j) {
        return pheromoneTable[i][j];
    }

    public void setPheromone(int i, int j, double nr) {
        pheromoneTable[i][j] = nr;
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

    protected void duplicateVertexEdges(Integer vertexFrom, Integer vertexTo) {
        Set<DefaultWeightedEdge> edges = this.edgesOf(vertexFrom);
        for (DefaultWeightedEdge edge :
                edges) {
            Integer source = this.getEdgeSource(edge);
            Integer target = this.getEdgeTarget(edge);
            if (source.equals(vertexFrom)) {
                DefaultWeightedEdge addedEdge = this.addEdge(vertexTo, target);
                this.setEdgeWeight(addedEdge, this.getEdgeWeight(edge));
            } else if (target.equals(vertexFrom)) {
                DefaultWeightedEdge addedEdge = this.addEdge(source, vertexTo);
                this.setEdgeWeight(addedEdge, this.getEdgeWeight(edge));
                this.addEdge(source, vertexTo);
            }
        }
    }
}
