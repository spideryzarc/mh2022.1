public class Main {
    public static void main(String args[]) {
        TSP tsp = new TSP(100);
        tsp.randomize();
        System.out.println(tsp);
        Route r = new Route(tsp);
        r.randomize();
        System.out.println(r);

        r.nearestNeighbor();
        System.out.println(r);

        r.furtherInsertion();
        System.out.println(r);

    }
}
