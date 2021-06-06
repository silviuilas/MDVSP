package ro.uaic.info.aco.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.prb.EdgeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AntColonyGraphPeerToPeer extends AntColonyGraph {
    double[][] pheromoneTable;
    private Map<Integer, Integer> vertexActualValue;


    public AntColonyGraphPeerToPeer(int n, int m, int[][] cost, List<Integer> depotsCapacity) {
        super(n, m, cost, depotsCapacity);
        pheromoneTable = new double[vertexSet().size()][vertexSet().size()];
    }

    public void init() {
        vertexActualValue = new HashMap<>();
        //connect each depot
        for (int i = 0; i < m; i++) {
            vertexActualValue.put(i, i);
            isVertexRepeatable.put(i, true);
            for (int j = 0; j < m; j++) {
                if (i != j) {
                    DefaultWeightedEdge e = this.addEdge(i, j);
                    this.setEdgeWeight(e, 0);
                }
            }
        }

        //duplicate each depot with the number of vehicles
        int depotNr = 0;
        for (Integer capacity :
                depotsCapacity) {
            for (int j = 0; j < capacity; j++) {
                int vertex = vertexSet().size();
                this.addVertex(vertex);
                duplicateVertexEdges(depotNr, vertex);
                isVertexRepeatable.put(vertex, true);
                vertexActualValue.put(vertex, depotNr);
                System.out.println(edgeSet().size());
            }
            depotNr++;
        }
        initEdgeTypeMap();
    }

    public void initEdgeTypeMap() {
        for (DefaultWeightedEdge edge :
                this.edgeSet()) {
            Integer source = this.getEdgeSource(edge);
            Integer target = this.getEdgeTarget(edge);
            if (isDepot(source) && isDepot(target))
                edgeTypeMap.put(edge, EdgeType.DEPOT_DEPOT);
            else if (isDepot(source) && !isDepot(target))
                edgeTypeMap.put(edge, EdgeType.PULL_OUT);
            else if (!isDepot(source) && isDepot(target))
                edgeTypeMap.put(edge, EdgeType.PULL_IN);
            else if (!isDepot(source) && !isDepot(target))
                edgeTypeMap.put(edge, EdgeType.NORMAL);
        }
    }

    public boolean isDepot(int i) {
        if (vertexActualValue.get(i) == null)
            return i < m;
        else
            return vertexActualValue.get(i) < m;
    }

    public Map<Integer, Integer> getVertexActualValue() {
        return vertexActualValue;
    }

    public boolean isMaster(int i) {
        return false;
    }
}
