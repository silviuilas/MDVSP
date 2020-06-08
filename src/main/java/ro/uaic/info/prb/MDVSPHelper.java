package ro.uaic.info.prb;

public abstract class MDVSPHelper {
    ProblemIO problemIO = null;

    boolean isDepotToDepot(int i, int j) {
        return i < problemIO.getM() && j < problemIO.getM();
    }

    boolean isPullOut(int i, int j) {
        return i < problemIO.getM() && j >= problemIO.getM();
    }

    boolean isPullIn(int i, int j) {
        return i >= problemIO.getM() && j < problemIO.getM();
    }

    boolean isNormalTrip(int i, int j) {
        return i >= problemIO.getM() && j >= problemIO.getM();
    }

    boolean isDepot(int i) {
        return i < problemIO.getM();
    }

    boolean isTrip(int i) {
        return i >= problemIO.getM();
    }
}
