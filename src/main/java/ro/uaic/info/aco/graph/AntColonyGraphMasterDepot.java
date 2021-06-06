package ro.uaic.info.aco.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.prb.EdgeType;

import java.util.List;


public class AntColonyGraphMasterDepot extends AntColonyGraph {
    double[][] pheromoneTable;
    List<Integer> depotsCapacity;

    public AntColonyGraphMasterDepot(int n, int m, int[][] cost, List<Integer> depotsCapacity) {
        super(n, m, cost, depotsCapacity);
        pheromoneTable = new double[vertexSet().size()][vertexSet().size()];
    }

    public void init() {
        int vertex = vertexSet().size();
        this.addVertex(vertex);
        this.isVertexRepeatable.put(vertex, true);
        for (Integer val :
                this.vertexSet()) {
            if (isDepot(val)) {
                this.addEdge(vertex, val);
                this.addEdge(val, vertex);
                this.setEdgeWeight(vertex, val, 0);
                this.setEdgeWeight(val, vertex, 0);
                this.isVertexRepeatable.put(val, true);
            }
        }
        initEdgeTypeMap();
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
}
