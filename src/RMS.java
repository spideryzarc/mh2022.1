/**
 * Random Multi-Start
 */
public class RMS implements Solver {
    final TSP tsp;
    final int ite;
    private int runTime;

    public Route getBestSol() {
        return bestSol;
    }

    @Override
    public String toString() {
        return "RMS{" +
                "ite=" + ite +
                ", runTime=" + runTime +
                ", bestSol=" + bestSol.cost +
                '}';
    }

    @Override
    public int getRunTime() {
        return runTime;
    }

    private Route bestSol;

    public RMS(TSP tsp, int ite) {
        this.ite = ite;
        this.tsp = tsp;
    }

    public void run() {
        long t = System.currentTimeMillis();
        Route currentSol = new Route(tsp);
        bestSol = new Route(tsp);
        bestSol.cost = Double.POSITIVE_INFINITY;
        VND vnd = new VND(tsp);
        for (int i = 0; i < ite; i++) {
            currentSol.randomize();
            vnd.run(currentSol);
            if (currentSol.cost < bestSol.cost - Utils.EPS) {
                bestSol.copy(currentSol);
                System.out.println(i + " RMS " + bestSol.cost);
                assert Utils.equals(bestSol.cost, tsp.cost(bestSol.v)) : "variável 'cost' está inconsistente";
            }
        }
        runTime = (int) (System.currentTimeMillis() - t);
    }
}
