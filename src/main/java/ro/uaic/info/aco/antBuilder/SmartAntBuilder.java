package ro.uaic.info.aco.antBuilder;

import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.ant.SmartAnt;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;

public class SmartAntBuilder implements AntBuilder {
    @Override
    public Ant generateAnt(MdvspAntColonyGraph mdvspAntColonyGraph) {
        return new SmartAnt(mdvspAntColonyGraph);
    }
}
