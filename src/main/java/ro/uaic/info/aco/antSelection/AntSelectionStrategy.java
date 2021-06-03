package ro.uaic.info.aco.antSelection;

import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.antBuilder.AntBuilder;

import java.util.List;

public interface AntSelectionStrategy {
    List<Ant> generateAnts(List<Ant> oldAnts, AntColony antColony, AntBuilder PureantBuilder);
}
