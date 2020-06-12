package ro.uaic.info.prb;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ProblemGraph extends SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> {

    protected Map<DefaultWeightedEdge,EdgeType> edgeTypeMap;


    public ProblemGraph(int n, int m, int[][] cost) {
        super(DefaultWeightedEdge.class);
        this.edgeTypeMap = new HashMap<>();
        createGraph(n, m, cost);
    }

    public void createGraph(int n, int m, int[][] cost) {

        for (int i = 0; i < n + m; i++) {
            this.addVertex(i);
        }

        for (int i = 0; i < n + m; i++) {
            for (int j = 0; j < n + m; j++) {
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
    public EdgeType getEdgeType(DefaultWeightedEdge e){
        return edgeTypeMap.get(e);
    }

    public void duplicateVertexEdges(Integer vertexFrom ,Integer vertexTo){
        Set<DefaultWeightedEdge> edges = this.edgesOf(vertexFrom);
        for (DefaultWeightedEdge edge:
                edges) {
            Integer source = this.getEdgeSource(edge);
            Integer target = this.getEdgeTarget(edge);
            if(source.equals(vertexFrom)){
                DefaultWeightedEdge addedEdge=this.addEdge(vertexTo,target);
                this.setEdgeWeight(addedEdge,this.getEdgeWeight(edge));
            }
            else if(target.equals(vertexFrom)){
                DefaultWeightedEdge addedEdge=this.addEdge(source,vertexTo);
                this.setEdgeWeight(addedEdge,this.getEdgeWeight(edge));
                this.addEdge(source,vertexTo);
            }
        }
    }
}

