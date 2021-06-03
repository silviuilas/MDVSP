package ro.uaic.info.aco;

import org.jgrapht.graph.DefaultWeightedEdge;

public interface AvailableEdgeRestriction {
    boolean shouldRemoveEdge(DefaultWeightedEdge edge);
}
