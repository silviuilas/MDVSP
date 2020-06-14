package ro.uaic.info.aco.antBuilder;

import ro.uaic.info.aco.Ant;
import ro.uaic.info.aco.AntColony;
import ro.uaic.info.aco.GreedyAnt;

public class GreedyAntBuilder implements AntBuilder {
    @Override
    public Ant generateAnt(AntColony antColony) {
        return new GreedyAnt(antColony);
    }
}
