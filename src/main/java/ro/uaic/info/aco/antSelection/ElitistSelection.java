package ro.uaic.info.aco.antSelection;

import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.antBuilder.AntBuilder;

import java.util.ArrayList;
import java.util.List;

public class ElitistSelection implements AntSelectionStrategy {
    int nrEliteAnts;

    @Override
    public List<Ant> generateAnts(List<Ant> oldAnts, AntColony antColony, AntBuilder antBuilder) {
        nrEliteAnts = 1;
        List<Ant> newAnts = new ArrayList<>();
        int colonySize = antColony.getColonySize();
        if (oldAnts.size() <= 0)
            nrEliteAnts = 0;
        if (nrEliteAnts > colonySize)
            nrEliteAnts = colonySize;
        for (int i = 0; i < nrEliteAnts; i++)
            newAnts.add(oldAnts.get(i));
        for (int i = nrEliteAnts; i < colonySize; i++)
            newAnts.add(antBuilder.generateAnt(antColony.getMdvspAntColonyGraph()));
        return newAnts;
    }
}
