package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.prb.ProblemIO;
import ro.uaic.info.prb.EdgeType;
import ro.uaic.info.prb.ProblemGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AntColonyGraph extends ProblemGraph {
    private Map<Integer,Integer> depotType;
    double[][] pheromoneTable;

    public AntColonyGraph(ProblemIO problemIO) {
        super(problemIO);
        depotType = new HashMap<>();
        List<Integer> depotsCapacity = problemIO.getDepotsCapacity();

        //connect each depot
        for(int i=0;i<problemIO.getM();i++) {
            depotType.put(i,i);
            for (int j = 0; j < problemIO.getM(); j++) {
                if (i != j) {
                    DefaultWeightedEdge e = this.addEdge(i, j);
                    this.setEdgeWeight(e, 1);
                }
            }
        }

        //duplicate each depot with the number of vehicles
        int depotNr = 0;
        for (Integer capacity :
                depotsCapacity) {
            for (int j = 0; j < capacity; j++) {
                int vertex=vertexSet().size();
                this.addVertex(vertex);
                duplicateVertexEdges(depotNr,vertex);
                depotType.put(vertex, depotNr);
                System.out.println(edgeSet().size());
            }
            depotNr++;
        }
        initEdgeTypeMap();
        pheromoneTable = new double[vertexSet().size()][vertexSet().size()];
    }

    public void initEdgeTypeMap() {
        for (DefaultWeightedEdge edge :
                this.edgeSet()) {
            Integer source = this.getEdgeSource(edge);
            Integer target = this.getEdgeTarget(edge);
            if(isDepot(source) && isDepot(target))
                edgeTypeMap.put(edge,EdgeType.DEPOT_DEPOT);
            else if(isDepot(source) && !isDepot(target))
                edgeTypeMap.put(edge,EdgeType.PULL_OUT);
            else if(!isDepot(source) && isDepot(target))
                edgeTypeMap.put(edge,EdgeType.PULL_IN);
            else if(!isDepot(source) && !isDepot(target))
                edgeTypeMap.put(edge,EdgeType.NORMAL);
        }
    }

    public boolean isDepot(int i){
        if(depotType.get(i)==null)
            return i<problemIO.getM();
        else
            return depotType.get(i)<problemIO.getM();
    }

    public Map<Integer, Integer> getDepotType() {
        return depotType;
    }

    public double getPheromone(int i,int j){
        return pheromoneTable[i][j];
    }
    public void setPheromone(int i,int j,double nr){
        pheromoneTable[i][j]=nr;
    }
}
