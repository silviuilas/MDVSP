package ro.uaic.info.aco.antBuilder;

import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.ant.Ant;

public interface AntBuilder {
    Ant generateAnt(AntColony antColony);
}
