package ro.uaic.info.prb;


import java.util.ArrayList;

public class Tour extends ArrayList<Integer> {
    public Tour() {
        super();
    }

    public boolean checkTour() {
        if (this.size() > 0)
            return this.get(this.size() - 1).equals(this.get(0));
        else
            return false;
    }
}
