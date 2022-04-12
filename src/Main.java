public class Main {
    public static void main(String args[]) {
        TSP tsp = new TSP(1000);
        tsp.randomize();
        System.out.println(tsp);
        Route r = new Route(tsp);

        r.randomize();
        System.out.println(r);
        while (r.swap_2_first_imp()) ;
        System.out.println(r);
        View.plot(r, "plot.csv");

//        r.nearestNeighbor(5);
//        System.out.println(r);
//        View.plot(r,"plot.csv");
//
//        r.furtherInsertion();
//        System.out.println(r);
//        View.plot(r,"plot.csv");

    }
}