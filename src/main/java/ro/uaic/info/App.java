package ro.uaic.info;

import ro.uaic.info.aco.AntColony;
import ro.uaic.info.aco.AntColonyGraph;
import ro.uaic.info.prb.ProblemIO;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        ProblemIO problemIO = new ProblemIO();
        problemIO.read("src/main/java/ro/uaic/info/Dataset/Mdvsp_4dep_500trips/m4n500s0.inp");
        AntColonyGraph antColonyGraph = new AntColonyGraph(problemIO.getN(), problemIO.getM(), problemIO.getCost(), problemIO.getDepotsCapacity());
        AntColony antColony = new AntColony(antColonyGraph);
        antColony.run();
    }
}
