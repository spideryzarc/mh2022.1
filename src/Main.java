public class Main {
    public static void main(String args[]) {
        TSP tsp = new TSP(100);
        tsp.randomize();
        int ite = 100000;

        Solver solvers[] = new Solver[]{
//                new RMS(tsp, ite),
//                new ILS(tsp, ite, tsp.N/50),
//                new VNS(tsp, ite),
//                new GRASP(tsp, ite, 5, true),
//                new SA(tsp, ite, 0.1e-2, 10e-2, .0001e-2),
//                new TABU(tsp, ite, 100),
//                new GLS(tsp,ite,0.1,0.1)
                new GA(tsp,ite,100,50,true)
        };

        for (Solver s : solvers) {
            s.run();
        }

        for (Solver s : solvers) {
            System.out.println(s);
            View.plot(s.getBestSol(), s.getClass().getSimpleName() + ".svg");
        }

//        Route r = solvers.getBestSol();
//        View.plot(r, "plot.csv");

    }
}