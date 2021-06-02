package ro.uaic.info.aco.antSelection;

import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.antBuilder.AntBuilder;

import java.util.ArrayList;
import java.util.List;

public class RandomSelection implements AntSelectionStrategy {
    @Override
    public List<Ant> generateAnts(List<Ant> oldAnts, AntColony antColony, AntBuilder antBuilder) {
        List<Ant> ants = new ArrayList<>();
        for (int i = 0; i < antColony.getColonySize(); i++)
            ants.add(antBuilder.generateAnt(antColony));
        return ants;
    }
}
