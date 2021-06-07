package ro.uaic.info.aco.antBuilder;

import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.graph.MdvspAntColonyGraph;

public interface AntBuilder {
    Ant generateAnt(MdvspAntColonyGraph mdvspAntColonyGraph);
}
