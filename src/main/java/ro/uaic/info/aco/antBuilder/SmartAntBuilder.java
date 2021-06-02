package ro.uaic.info.aco.antBuilder;

import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.ant.SmartAnt;

public class SmartAntBuilder implements AntBuilder {
    @Override
    public Ant generateAnt(AntColony antColony) {
        return new SmartAnt(antColony);
    }
}
