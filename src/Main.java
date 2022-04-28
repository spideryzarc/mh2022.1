public class Main {
    public static void main(String args[]) {
        TSP tsp = new TSP(1000);
        tsp.randomize();
        System.out.println(tsp);
        Route r = new Route(tsp);

        long t = System.currentTimeMillis();
        r.randomize();
        System.out.println(r);
        while (r.opt_2_first_imp()
                || r.replace_first_imp()
                || r.swap_2_best_imp()) {//hill climbing
            //nothing
        }

        t = System.currentTimeMillis() - t;
        System.out.println("time: " + t);

        System.out.println(r);
        View.plot(r, "plot.csv");
        System.out.println(tsp.cost(r.v));

//        r.nearestNeighbor(5);
//        System.out.println(r);
//        View.plot(r,"plot.csv");
//
//        r.furtherInsertion();
//        System.out.println(r);
//        View.plot(r,"plot.csv");

    }
}