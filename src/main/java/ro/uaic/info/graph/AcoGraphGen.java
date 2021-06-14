package ro.uaic.info.graph;

import org.graphstream.graph.Node;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class AcoGraphGen extends RandomGraphGen {
    int number_of_depots;
    int number_of_customers;
    int total;
    int[] availableVehicles;
    List<String> file;

    public AcoGraphGen(int number_of_depots, int number_of_customers, int[] availableVehicles, int maxX, int maxY) {
        super(number_of_depots + number_of_customers, maxX, maxY);
        this.number_of_depots = number_of_depots;
        this.number_of_customers = number_of_customers;
        if (availableVehicles == null) {
            this.availableVehicles = generateAvailableVehicles();
        } else {
            this.availableVehicles = availableVehicles;
        }

        setInterface();
        total = number_of_customers + number_of_depots;
        GraphToStringHelper output = transformGraphToString();
        file = output.toList();
    }

    public void setInterface(){
        graph.nodes().forEach(a -> {
            if (Integer.parseInt(a.getId()) >= number_of_depots) {
                a.setAttribute("ui.class", "client");
            } else {
                a.setAttribute("ui.class", "depot");
            }
        });
        for (Node node : graph) {
            node.setAttribute("ui.label", node.getId());
        }
    }

    public GraphToStringHelper transformGraphToString() {
        GraphToStringHelper output = new GraphToStringHelper();
        output.add(number_of_depots);
        output.add(number_of_customers);
        for (int x : availableVehicles) {
            output.add(x);
        }
        output.endl();


        int[][] weight = makeMatrix();
        for (int i = 0; i < total; i++) {
            for (int j = 0; j < total; j++) {
                output.add(weight[i][j]);
            }
            output.endl();
        }
        System.out.println(output);
        return output;
    }

    private int[][] makeMatrix() {
        int m = number_of_depots;
        int n = number_of_customers;
        int[][] weight = new int[total][total];
        double[] startTime = new double[n];
        double[] endTime = new double[n];
        double[][] travelTime = new double[total][total];
        for(int i=0;i<n;i++){
            startTime[i] = ThreadLocalRandom.current().nextDouble(0, 1440);
        }
        for(int i=0;i<n;i++){
            endTime[i] = startTime[i] + 5;
        }

        graph.nodes().forEach(a -> graph.nodes().forEach(b -> {
            int i = Integer.parseInt(a.getId());
            int j = Integer.parseInt(b.getId());
            int x1 = Integer.parseInt(String.valueOf(a.getAttribute("x")));
            int y1 = Integer.parseInt(String.valueOf(a.getAttribute("y")));
            int x2 = Integer.parseInt(String.valueOf(b.getAttribute("x")));
            int y2 = Integer.parseInt(String.valueOf(b.getAttribute("y")));
            travelTime[i][j] = calculateDistanceBetweenPoints(x1, y1, x2, y2);
        }));


        graph.nodes().forEach(a -> graph.nodes().forEach(b -> {
            int i = Integer.parseInt(a.getId());
            int j = Integer.parseInt(b.getId());
            if (a.getId().equals(b.getId())) {
                weight[i][j] = -1;
            } else if (i < m && j < m) {
                weight[i][j] = -1;
            } else if(i < m || j < m) {
                weight[i][j] = (int) (travelTime[i][j] * 10);
            }
            else
                if(endTime[i-m] + travelTime[i-m][j-m] <= startTime[j-m]) {
                    int x1 = Integer.parseInt(String.valueOf(a.getAttribute("x")));
                    int y1 = Integer.parseInt(String.valueOf(a.getAttribute("y")));
                    int x2 = Integer.parseInt(String.valueOf(b.getAttribute("x")));
                    int y2 = Integer.parseInt(String.valueOf(b.getAttribute("y")));
                    weight[i][j] = (int) (travelTime[i][j]);
                }
                else{
                    weight[i][j] = -1;
                }
            }
            ));
        return weight;
    }
    double map(double x, double in_min, double in_max, double out_min, double out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
    private int[] generateAvailableVehicles() {
        int[] av = new int[number_of_depots];
        for (int i = 0; i < number_of_depots; i++) {
            int ratio = number_of_customers / number_of_depots;
            av[i] = ThreadLocalRandom.current().nextInt(ratio, ratio + 10);
        }
        return av;
    }

    public double calculateDistanceBetweenPoints(double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }

    public List<String> getFile() {
        return file;
    }
}
