package ro.uaic.info.aco.antBuilder;

import ro.uaic.info.aco.Ant;
import ro.uaic.info.aco.AntColony;

public interface AntBuilder {
    Ant generateAnt(AntColony antColony);
}
