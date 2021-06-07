package ro.uaic.info.aco.ant;

import org.jgrapht.graph.DefaultWeightedEdge;
import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;

import java.util.List;
import java.util.Random;

public class SmartAnt extends MdvspAntPeerToPeer {
    double alpha = 1;
    double beta = 2;

    public SmartAnt(MdvspAntColonyGraph mdvspAntColonyGraph) {
        super(mdvspAntColonyGraph);
        currentLocation = 0;
    }

    public DefaultWeightedEdge pickAnEdge(List<DefaultWeightedEdge> availableEdges) {
        Random random = new Random();
        double[] select = new double[availableEdges.size()];
        double sum = 0;
        int index = 0;
        for (DefaultWeightedEdge edge :
                availableEdges) {
            int source = getMdvspAntColonyGraph().getEdgeSource(edge);
            int target = getMdvspAntColonyGraph().getEdgeTarget(edge);
            double pheromone = getMdvspAntColonyGraph().getPheromone(source, target);
            double distance = getMdvspAntColonyGraph().getEdgeWeight(edge);
            if (distance < 1)
                distance = 1;
            double intensity = Math.pow(pheromone, alpha);
            int connectivity = this.getMdvspAntColonyGraph().getConnectivityValues().get(target);
            double relevance = Math.pow(((1 / distance)) * connectivity, beta);
            double desirability = intensity * relevance;
            sum = sum + (desirability);
            select[index] = sum;
            index++;
        }

        double toFind = random.nextDouble() * sum;

        // assert(binary_search(select, 0, availableEdges.size(), toFind) == normal_search(select, 0, availableEdges.size(), toFind));
        int selected = binary_search(select, 0, availableEdges.size(), toFind);
//        DefaultWeightedEdge edge = availableEdges.get(selected);
//        int target = getMdvspAntColonyGraph().getEdgeTarget(edge);
//        int connectivity = this.getMdvspAntColonyGraph().getConnectivityValues().get(target);
//        System.out.println(edge);
//        System.out.println(getMdvspAntColonyGraph().getEdgeType(edge));
//        System.out.println(connectivity);
        return availableEdges.get(selected);
    }

    private int binary_search(double[] vec, int left, int right, double val) {
        int middle;
        while (left < right) {
            middle = (left + right) / 2;
            if (val < vec[middle])
                right = middle;
            else
                left = middle + 1;
        }
        return left;
    }

    private int normal_search(double[] vec, int start, int end, double val) {
        int selected = -1;
        for (int i = start; i < end; i++) {
            if (val < vec[i]) {
                selected = i;
                break;
            }
        }
        return selected;
    }
}
