package ro.uaic.info.gui.graph;

import org.graphstream.algorithm.Algorithm;
import org.graphstream.graph.Graph;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class RenderGraph {


    Graph graph;
    Algorithm algorithm;


    public RenderGraph(Graph graph) {
        this.graph = graph;
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        try {
            graph.setAttribute("ui.stylesheet", String.join("\n", Files.readAllLines(Paths.get("src/main/java/ro/uaic/info/gui/graph/graphCss.css"))));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO add the functionality to zoom in and out (fix it)
        Viewer viewer = graph.display(false);
        View view = viewer.getDefaultView();
        //view.getCamera().setViewPercent(1);
        ((Component) view).setBounds(0, 0, 1000, 1000);

        ((Component) view).addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mwe) {
                zoomGraphMouseWheelMoved(mwe, (ViewPanel) view);
            }
        });
        // TODO make draggable nodes that change the graph
        // TODO make the camera draggable around


    }

    public static void zoomGraphMouseWheelMoved(MouseWheelEvent mwe, ViewPanel view_panel) {
        if (Event.ALT_MASK != 0) {
            if (mwe.getWheelRotation() > 0) {
                double new_view_percent = view_panel.getCamera().getViewPercent() + 0.05;
                view_panel.getCamera().setViewPercent(new_view_percent);
            } else if (mwe.getWheelRotation() < 0) {
                double current_view_percent = view_panel.getCamera().getViewPercent();
                if (current_view_percent > 0.05) {
                    view_panel.getCamera().setViewPercent(current_view_percent - 0.05);
                }
            }
        }
    }


    public void compute() {
        algorithm.compute();
    }

    public Graph getGraph() {
        return graph;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

}
