package ro.uaic.info.aco.antBuilder;

import ro.uaic.info.aco.Ant;
import ro.uaic.info.aco.AntColony;
import ro.uaic.info.aco.SmartAnt;

public class SmartAntBuilder implements AntBuilder {
    @Override
    public Ant generateAnt(AntColony antColony) {
        return new SmartAnt(antColony);
    }
}
