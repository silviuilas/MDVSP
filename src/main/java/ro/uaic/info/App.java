package ro.uaic.info;

import ro.uaic.info.gui.MenuGui;
import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {

        MenuGui menuGui = new MenuGui();
        menuGui.run();





//        ProblemIO problemIO = new ProblemIO();
//        problemIO.read("src/main/java/ro/uaic/info/Dataset/Mdvsp_4dep_500trips/m4n500s0.inp");
//        //problemIO.read("src/main/java/ro/uaic/info/generator/MDVSP-data/m4n200s0.inp");
//        Solver solver = new AcoSolver();
//        Problem problem = new Problem(problemIO, solver);
//        for (int i = 0; i < 30; i++) {
//            problem.run();
//            solver.saveLogs("run" + i);
//        }
    }
}
