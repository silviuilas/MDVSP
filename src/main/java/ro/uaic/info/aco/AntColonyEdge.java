package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;

public class AntColonyEdge extends DefaultWeightedEdge {
    double pheromone;

    public double getPheromone() {
        return pheromone;
    }

    public void setPheromone(double pheromone) {
        this.pheromone = pheromone;
    }
}
