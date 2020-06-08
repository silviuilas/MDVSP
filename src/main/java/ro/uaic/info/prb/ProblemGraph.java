package ro.uaic.info.prb;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProblemGraph extends SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> {
    protected ProblemIO problemIO;
    protected Map<DefaultWeightedEdge,EdgeType> edgeTypeMap;


    public ProblemGraph(ProblemIO problemIO) {
        super(DefaultWeightedEdge.class);
        this.problemIO = problemIO;
        this.edgeTypeMap = new HashMap<>();
        createGraph();
    }

    public void createGraph() {
        int n = problemIO.getN();
        int m = problemIO.getM();
        List<Integer> depotsCapacity = problemIO.getDepotsCapacity();
        int[][] cost = problemIO.getCost();

        for (int i = 0; i < n + m; i++) {
            this.addVertex(i);
        }

        for (int i = 0; i < n + m; i++) {
            for (int j = 0; j < n + m; j++) {
                if (cost[i][j] >= 0) {
                    DefaultWeightedEdge e = this.addEdge(i, j);
                    if(cost[i][j]==0){
                        cost[i][j]=1;
                    }
                    this.setEdgeWeight(e, cost[i][j]);
                }
            }
        }
    }
    public EdgeType getEdgeType(DefaultWeightedEdge e){
        return edgeTypeMap.get(e);
    }

    public ProblemIO getProblemIO() {
        return problemIO;
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

