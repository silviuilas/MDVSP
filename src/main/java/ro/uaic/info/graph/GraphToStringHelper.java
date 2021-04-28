package ro.uaic.info.graph;

import java.util.Arrays;
import java.util.List;

public class GraphToStringHelper {
    String output;

    public GraphToStringHelper() {
        output = "";
    }

    public void add(String string) {
        output = output.concat(string).concat("\t");
    }

    public void add(int integ) {
        output = output.concat(String.valueOf(integ)).concat("\t");
    }

    public void endl() {
        output = output.concat("\n");
    }

    public List<String> toList() {
        return Arrays.asList(output.split("\n"));
    }

    @Override
    public String toString() {
        return output;
    }
}
