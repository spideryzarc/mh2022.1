public class Main {
    public static void main(String args[]) {
        TSP tsp = new TSP(1000);
        tsp.randomize();

        RMS rms = new RMS(tsp,60);
        ILS ils = new ILS(tsp,60);

        long t = System.currentTimeMillis();
        ils.run();
        t = System.currentTimeMillis() - t;

        System.out.println("time: " + t);

        Route r = ils.getBestSol();
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