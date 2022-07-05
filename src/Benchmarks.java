import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Benchmarks {
    private static String output = "result.txt";

    public static void main(String args[]) throws IOException {
        File dir = new File("C:\\Users\\aluno\\Documents\\albert\\projects\\mh2022.1\\tsp_lib");
        File[] instanceFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("tsp");
            }
        });
        for (File instance : instanceFiles) {
            System.out.println(instance.getName());
            try {
                TSP tsp = new TSP(instance.getPath());
                ArrayList<Solver> solvers = new ArrayList<>();

                int[] ites = new int[]{100, 500, 1000};
                for (int ite : ites)
                    solvers.add(new RMS(tsp, ite));

                int[] ks = new int[]{5, 10, 20};
                for (int ite : ites)
                    for (int k : ks)
                        solvers.add(new ILS(tsp, ite, k));
                for (int ite : ites)
                    solvers.add(new VNS(tsp, ite));
                for (int ite : ites)
                    for (int k : ks) {
                        solvers.add(new GRASP(tsp, ite, k, true));
                        solvers.add(new GRASP(tsp, ite, k, false));
                    }

                for (int ite : ites)
                    for (double p0 : new double[]{1e-1, 1e-2})
                        for (double pf : new double[]{1e-6, 1e-7})
                            solvers.add(new SA(tsp, ite, 0.1e-2, p0, pf));

                for (int ite : ites)
                    for (int k : new int[]{25, 50, 100})
                        solvers.add(new TABU(tsp, ite, k));

                for (int ite : ites)
                    for (double A : new double[]{1e-1, 1e-2})
                        for (double B : new double[]{.7, .8, .9})
                            solvers.add(new GLS(tsp, ite, A, B));
                //TODO GA SS DEA
                for (Solver s : solvers) {
                    s.run();
                    FileWriter fw = new FileWriter(new File(output), true);

                    String instanceName = instance.getName().replace(".tsp", "");

                    fw.write(String.format("%s, %s, %.1f, %d, %s\n",
                            s.getClass().getSimpleName(),
                            instanceName,
                            s.getBestSol().cost,
                            s.getRunTime(),
                            s.parametros()));
                    fw.close();
                }


            } catch (RuntimeException e) {
                System.err.println(e.getMessage());
            }

            break;
        }

    }
}

