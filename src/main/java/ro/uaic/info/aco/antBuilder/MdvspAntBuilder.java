package ro.uaic.info.aco.antBuilder;

import ro.uaic.info.aco.acoVariants.AntColony;
import ro.uaic.info.aco.ant.Ant;
import ro.uaic.info.aco.ant.MdvspAntMasterDepot;
import ro.uaic.info.aco.ant.MdvspAntPeerToPeer;

public class MdvspAntBuilder implements AntBuilder {
    @Override
    public Ant generateAnt(AntColony antColony) {
        return new MdvspAntMasterDepot(antColony);
    }
}
