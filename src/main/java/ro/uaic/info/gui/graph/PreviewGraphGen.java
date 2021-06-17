package ro.uaic.info.gui.graph;

import ro.uaic.info.helpers.Pair;

import java.util.List;

public class PreviewGraphGen extends RandomGraphGen {
    final int number_of_points_outside;
    final int number_of_points_inside;
    final int stdOutside;
    final int stdInside;

    public PreviewGraphGen(int number_of_points_outside, int number_of_points_inside, int stdOutside1, int stdInside1, int maxX, int maxY) {
        super(number_of_points_inside + number_of_points_outside, maxX, maxY);
        this.number_of_points_inside = number_of_points_inside;
        this.number_of_points_outside = number_of_points_outside;
        this.stdOutside = stdOutside1;
        this.stdInside = stdInside1;
        List<Pair<Integer, Integer>> coords = getPointsNearCircleSmart(number_of_points_outside, maxX, maxY, stdOutside);
        addVertexes(graph, coords, 0);
        coords = getInsidePoints(number_of_points_inside, maxX, maxY, stdInside);
        addVertexes(graph, coords, number_of_points_outside);
        setInterface();

    }

    public void setInterface() {
        graph.nodes().forEach(a -> {
            if (Integer.parseInt(a.getId()) >= number_of_points_outside) {
                a.setAttribute("ui.class", "inside");
            } else {
                a.setAttribute("ui.class", "outside");
            }
        });
    }
}
