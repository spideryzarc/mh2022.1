public class Main {
    public static void main(String args[]) {
        TSP tsp = new TSP(1000);
        tsp.randomize();
        int ite = 100;

        Solver solvers[] = new Solver[]{
//                new RMS(tsp, ite),
//                new ILS(tsp, ite),
                new VNS(tsp,ite),
//                new GRASP(tsp, ite, 3)
        };

        for (Solver s : solvers) {
            s.run();
        }

        for (Solver s : solvers) {
            System.out.println(s);
        }

//        Route r = solvers.getBestSol();
//        View.plot(r, "plot.csv");

    }
}