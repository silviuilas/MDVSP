package ro.uaic.info.gui;

import org.graphstream.algorithm.Algorithm;
import ro.uaic.info.Problem;
import ro.uaic.info.gui.graph.AcoAlgorithm;
import ro.uaic.info.gui.graph.AcoGraphGen;
import ro.uaic.info.gui.graph.PreviewGraphGen;
import ro.uaic.info.gui.graph.RenderGraph;
import ro.uaic.info.prb.ProblemIO;
import ro.uaic.info.solver.AcoSolver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuGui extends JFrame {
    private JTextField depots;
    private JTextField clients;
    private JTextField standardDevDepots;
    private JTextField standardDevClients;
    private JButton start;
    private JButton preview;
    private Container c;

    private void createAndShowGUI() {
        //Create and set up the window.
        setTitle("Registration Form");
        setBounds(300, 90, 360, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        c = getContentPane();
        c.setLayout(null);

        JLabel label = new JLabel("Configs");
        label.setFont(new Font("Arial", Font.PLAIN, 30));
        label.setSize(300, 30);
        label.setLocation(100, 30);
        c.add(label);

        label = new JLabel("Nr. Depots");
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setSize(100, 20);
        label.setLocation(50, 100);
        c.add(label);

        depots = new JTextField();
        depots.setFont(new Font("Arial", Font.PLAIN, 15));
        depots.setSize(150, 20);
        depots.setLocation(160, 100);
        c.add(depots);

        label = new JLabel("Nr. Clients");
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setSize(100, 20);
        label.setLocation(50, 150);
        c.add(label);

        clients = new JTextField();
        clients.setFont(new Font("Arial", Font.PLAIN, 15));
        clients.setSize(150, 20);
        clients.setLocation(160, 150);
        c.add(clients);

        label = new JLabel("Std Depots");
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setSize(100, 20);
        label.setLocation(50, 200);
        c.add(label);

        standardDevDepots = new JTextField();
        standardDevDepots.setFont(new Font("Arial", Font.PLAIN, 15));
        standardDevDepots.setSize(150, 20);
        standardDevDepots.setLocation(160, 200);
        c.add(standardDevDepots);

        label = new JLabel("Std Clients");
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setSize(100, 20);
        label.setLocation(50, 250);
        c.add(label);

        standardDevClients = new JTextField();
        standardDevClients.setFont(new Font("Arial", Font.PLAIN, 15));
        standardDevClients.setSize(150, 20);
        standardDevClients.setLocation(160, 250);
        c.add(standardDevClients);


        start = new JButton("Start");
        start.setFont(new Font("Arial", Font.PLAIN, 15));
        start.setSize(100, 20);
        start.setLocation(50, 300);
        start.addActionListener(this::actionPerformed);
        c.add(start);

        preview = new JButton("Preview");
        preview.setFont(new Font("Arial", Font.PLAIN, 15));
        preview.setSize(100, 20);
        preview.setLocation(210, 300);
        preview.addActionListener(this::actionPerformed);
        c.add(preview);

        setVisible(true);
    }

    void preview(int number_of_points_outside, int number_of_points_inside, int stdOutside, int stdInside) {
        PreviewGraphGen randomGraphGen = new PreviewGraphGen(number_of_points_outside, number_of_points_inside, stdOutside, stdInside, 10000, 10000);
        RenderGraph renderGraph = new RenderGraph(randomGraphGen.getGraph());
    }

    public void run() {
        int number_of_points_inside = 20000;
        int number_of_points_outside = 20000;
        // preview(number_of_points_outside, number_of_points_inside);
        // preview(10,10);

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == start) {
            try {
                int numberOfDepots = Integer.parseInt(depots.getText());
                int numberOfCustomers = Integer.parseInt(clients.getText());
                int stDevDepots;
                int stDevClients;
                try{
                    stDevDepots = Integer.parseInt(standardDevDepots.getText());
                    stDevDepots = (int) map(stDevDepots, 0, 100, 10, 100);
                    stDevClients = Integer.parseInt(standardDevClients.getText());
                    stDevClients = (int) this.map(stDevClients, 0, 100, 50, 500);
                }catch (Exception e2){
                    stDevDepots = 50;
                    stDevClients = 200;
                }
                AcoGraphGen randomGraphGen = new AcoGraphGen(numberOfDepots, numberOfCustomers, null, stDevDepots, stDevClients, 1000, 1000);
                RenderGraph renderGraph = new RenderGraph(randomGraphGen.getGraph());

                ProblemIO problemIO = new ProblemIO();
                problemIO.populate(randomGraphGen.getFile());
                AcoSolver solver = new AcoSolver();
                solver.init(problemIO);
                Problem problem = new Problem(problemIO, solver);
                Algorithm algorithm = new AcoAlgorithm(problem);
                algorithm.init(renderGraph.getGraph());

                Thread thread = new Thread() {
                    public void run() {
                        algorithm.compute();
                    }
                };
                thread.start();
                System.out.println("Starting");
            } catch (Exception e1) {
                System.out.println("Param are not good");
            }
        } else if (e.getSource() == preview) {
            try {
                int stDevDepots = Integer.parseInt(standardDevDepots.getText());
                stDevDepots = (int) map(stDevDepots, 0, 100, 100, 1000);
                int stDevClients = Integer.parseInt(standardDevClients.getText());
                stDevClients = (int) this.map(stDevClients, 0, 100, 500, 5000);
                preview(10000, 10000, stDevDepots, stDevClients);
            } catch (Exception e1) {
                System.out.println("Param are not good");
            }
        }
    }

    protected long map(long x, long in_min, long in_max, long out_min, long out_max) throws Exception {
        if (x < in_min || x > in_max) {
            throw (new Exception("Invalid range"));
        }
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
