package ro.uaic.info.aco;

import ro.uaic.info.aco.ant.Ant;

import java.util.List;

public class EvaluateOnThread implements Runnable {
    List<Ant> ants;
    int from;
    int to;

    public EvaluateOnThread(List<Ant> ants, int from, int to) {
        this.ants = ants;
        this.from = from;
        this.to = to;
    }

    @Override
    public void run() {
        for (int i = from; i < to; i++) {
            Ant ant = ants.get(i);
            ant.run();
            // System.out.println(ant.wrapGetUnsatisfiedClientsNr() + " " + ant.getCurrentCost());
        }
    }
}
