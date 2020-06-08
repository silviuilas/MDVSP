package ro.uaic.info.prb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProblemIO {
    private int n;
    private int m;
    private List<Integer>depotsCapacity = new ArrayList<>();
    private int [][]cost;

    public void read(String filename) throws IOException {
        Path path = Paths.get(filename);
        List<String> lines = Files.readAllLines(path);
        String sep = "\t";
        String []oneLine = lines.get(0).split(sep);
        m = Integer.parseInt(oneLine[0]); //depots
        n = Integer.parseInt(oneLine[1]); //trips
        cost= new int[n+m][n+m];
        for(int i=0;i<m;i++)
            depotsCapacity.add(Integer.parseInt(oneLine[i+2]));
        for(int i=0;i<n+m;i++){
            oneLine=lines.get(i+1).split(sep);
            for(int j=0;j<n+m;j++){
                cost[i][j]=Integer.parseInt(oneLine[j]);
            }
        }

    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }

    public int[][] getCost() {
        return cost;
    }

    public List<Integer> getDepotsCapacity() {
        return depotsCapacity;
    }

    @Override
    public String toString() {
        return "ProblemIO{" +
                "n=" + n +
                ", m=" + m +
                ", depotsCapacity=" + depotsCapacity +
                '}';
    }
}
