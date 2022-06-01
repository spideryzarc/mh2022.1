/**
 * Iterated Local Search
 */
public class ILS implements Solver {
    final TSP tsp;
    final int ite;
    final int K;
    private int runTime;
    private Disturbances dist;

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
                ", K=" + K +
                ", runTime=" + runTime +
                ", bestSol=" + bestSol.cost +
                '}';
    }

    /**
     * @param tsp Instância
     * @param ite número de iterações
     * @param K grau da pertubação (2<= K < tsp.N)
     */
    public ILS(TSP tsp, int ite, int K) {
        this.ite = ite;
        this.tsp = tsp;
//        aux = new int[tsp.N];
        dist = new Disturbances(tsp);
        this.K = K;
    }

//    private int[] aux;
//
//    private void shake(Route r) {
//        int v[] = r.v;
//        System.arraycopy(v, 0, aux, 0, aux.length);
//        int size = 2 + Utils.rd.nextInt(v.length / 2);
//        int ini = Utils.rd.nextInt(v.length - size);
//
//        int i = ini;
//        for (int j = ini + size; j < v.length; i++, j++)
//            v[i] = aux[j];
//        for (int j = ini; i < v.length; i++, j++)
//            v[i] = aux[j];
//
//        r.cost = tsp.cost(r.v);
//
//    }

    public void run() {
        long t = System.currentTimeMillis();
        Route currentSol = new Route(tsp);
        currentSol.defaultSolConstructor();
        VND vnd = new VND(tsp);
        vnd.run(currentSol);
        bestSol = new Route(tsp);
        bestSol.copy(currentSol);
        for (int i = 0; i < ite; i++) {
            dist.moveRandomToBegin(currentSol,2,K);
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
