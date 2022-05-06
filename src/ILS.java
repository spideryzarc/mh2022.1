/**
 * Iterated Local Search
 */
public class ILS implements Solver {
    final TSP tsp;
    final int ite;
    private int runTime;

    public Route getBestSol() {
        return bestSol;
    }

    @Override
    public int getRunTime() {
        return runTime;
    }

    private Route bestSol;

    @Override
    public String toString() {
        return "ILS{" +
                "ite=" + ite +
                ", runTime=" + runTime +
                ", bestSol=" + bestSol.cost +
                '}';
    }

    public ILS(TSP tsp, int ite) {
        this.ite = ite;
        this.tsp = tsp;
        aux = new int[tsp.N];
    }

    private int[] aux;

    private void shake(Route r) {
        int v[] = r.v;
        System.arraycopy(v, 0, aux, 0, aux.length);
        int size = 2 + Utils.rd.nextInt(v.length / 2);
        int ini = Utils.rd.nextInt(v.length - size);

        int i = ini;
        for (int j = ini + size; j < v.length; i++, j++)
            v[i] = aux[j];
        for (int j = ini; i < v.length; i++, j++)
            v[i] = aux[j];

        r.cost = tsp.cost(r.v);

    }

    public void run() {
        long t = System.currentTimeMillis();
        Route currentSol = new Route(tsp);
        currentSol.randomize();
        bestSol = new Route(tsp);
        bestSol.copy(currentSol);
        VND vnd = new VND(tsp);
        for (int i = 0; i < ite; i++) {
            shake(currentSol);
            vnd.run(currentSol);
            if (currentSol.cost < bestSol.cost - Utils.EPS) {
                bestSol.copy(currentSol);
                System.out.println(i + " ILS " + bestSol.cost);
                assert Utils.equals(bestSol.cost, tsp.cost(bestSol.v)) : "variável 'cost' está inconsistente";
            }
        }
        runTime = (int) (System.currentTimeMillis() - t);
    }
}
