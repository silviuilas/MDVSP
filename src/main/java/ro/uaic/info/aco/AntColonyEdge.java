package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;

public class AntColonyEdge extends DefaultWeightedEdge {
    double pheromone;

    public void setPheromone(double pheromone) {
        this.pheromone = pheromone;
    }

    public double getPheromone() {
        return pheromone;
    }
}
