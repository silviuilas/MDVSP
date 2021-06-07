package ro.uaic.info.aco.antBuilder;

import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.ant.GreedyAnt;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;

public class GreedyAntBuilder implements AntBuilder {
    @Override
    public Ant generateAnt(MdvspAntColonyGraph mdvspAntColonyGraph) {
        return new GreedyAnt(mdvspAntColonyGraph);
    }
}
