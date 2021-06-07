package ro.uaic.info.aco.graph;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.prb.EdgeType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MdvspAntColonyGraph extends AntColonyGraph {
    final int n;
    final int m;
    protected Map<Integer, Integer> vertexActualValue;
    protected Map<DefaultWeightedEdge, EdgeType> edgeTypeMap;
    List<Integer> depotsCapacity;
    Map<Integer, Integer> connectivityValues;


    public MdvspAntColonyGraph(int n, int m, int[][] cost, List<Integer> depotsCapacity) {
        super(n + m, cost);
        this.n = n;
        this.m = m;
        this.depotsCapacity = new ArrayList<>(depotsCapacity);
        connectivityValues = new HashMap<>();
        vertexActualValue = new HashMap<>();
        edgeTypeMap = new HashMap<>();
    }

    @Override
    public void init() {
        initConnectivityValues();
        super.init();
    }

    private void initConnectivityValues() {
        for (DefaultWeightedEdge edge :
                this.edgeSet()) {
            int source = this.getEdgeSource(edge);
            int target = this.getEdgeTarget(edge);
            connectivityValues.putIfAbsent(source, 1);
            if (!isDepot(source) && !isDepot(target)) {
                int count = connectivityValues.get(source);
                connectivityValues.put(source, count + 1);
            }
        }
    }

    public abstract boolean isDepot(int i);

    public boolean isNormal(int i) {
        return !isDepot(i);
    }

    public List<Integer> getDepotsCapacity() {
        return depotsCapacity;
    }

    public Map<Integer, Integer> getConnectivityValues() {
        return connectivityValues;
    }

    public Map<Integer, Integer> getVertexActualValue() {
        return vertexActualValue;
    }

    public EdgeType getEdgeType(DefaultWeightedEdge edge) {
        return edgeTypeMap.get(edge);
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }
}
