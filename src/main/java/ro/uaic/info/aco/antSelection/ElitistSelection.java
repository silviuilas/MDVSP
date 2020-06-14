package ro.uaic.info.aco.antSelection;

import ro.uaic.info.aco.Ant;
import ro.uaic.info.aco.AntColony;
import ro.uaic.info.aco.antBuilder.AntBuilder;

import java.util.ArrayList;
import java.util.List;

public class ElitistSelection implements AntSelectionStrategy {
    int nrEliteAnts;

    @Override
    public List<Ant> generateAnts(List<Ant> oldAnts, AntColony antColony, AntBuilder antBuilder) {
        nrEliteAnts = 2;
        List<Ant> newAnts = new ArrayList<>();
        int colonySize = antColony.getColonySize();
        if (oldAnts.size() <= 0)
            nrEliteAnts = 0;
        if (nrEliteAnts > colonySize)
            nrEliteAnts = colonySize;
        oldAnts.sort((ant1, ant2) -> {
            int unsatisfiedNr1 = ant1.wrapGetUnsatisfiedClientsNr();
            int unsatisfiedNr2 = ant2.wrapGetUnsatisfiedClientsNr();
            if (unsatisfiedNr1 == 0 && unsatisfiedNr2 > 0)
                return -1;
            else if (unsatisfiedNr1 > 0 && unsatisfiedNr2 == 0)
                return 1;
            else if (unsatisfiedNr1 == 0 && unsatisfiedNr2 == 0)
                return ant1.getCurrentCost() - ant2.getCurrentCost();
            else
                return ant1.wrapGetUnsatisfiedClientsNr() - ant2.wrapGetUnsatisfiedClientsNr();
        });
        for (int i = 0; i < nrEliteAnts; i++)
            newAnts.add(oldAnts.get(i));
        for (int i = nrEliteAnts; i < colonySize; i++)
            newAnts.add(antBuilder.generateAnt(antColony));
        return newAnts;
    }
}
