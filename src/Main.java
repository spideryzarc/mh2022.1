public class Main {
    public static void main(String args[]) {
        TSP tsp = new TSP(200);
        tsp.randomize();
        int ite = 1000;

        Solver solvers[] = new Solver[]{
//                new RMS(tsp, ite),
//                new ILS(tsp, ite),
//                new VNS(tsp, ite),
                new GRASP(tsp, ite, 15,true)
        };

        for (Solver s : solvers) {
            Utils.rd.setSeed(7);
            s.run();
        }

        for (Solver s : solvers) {
            System.out.println(s);
            View.plot(s.getBestSol(), s.getClass().getSimpleName() + ".svg");
        }


    }
}