/**
 * Random Multi-Start
 */
public class RMS {
    final TSP tsp;
    final int ite;

    public Route getBestSol() {
        return bestSol;
    }

    private Route bestSol;

    public RMS(TSP tsp, int ite) {
        this.ite = ite;
        this.tsp = tsp;
    }

    public void run() {
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
    }
}
