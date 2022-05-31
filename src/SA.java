import java.util.ArrayList;

/**
 * Simulated Annealing
 */
public class SA implements Solver {
    final TSP tsp;
    final int ite;
    final double s_ref, p0, pf;
    private int runTime;
    private VND vnd;

    public Route getBestSol() {
        return bestSol;
    }

    @Override
    public String toString() {
        return "SA{" +
                "ite=" + ite +
                ", s_ref=" + s_ref +
                ", p0=" + p0 +
                ", pf=" + pf +
                ", runTime=" + runTime +
                ", bestSol=" + bestSol.cost +
                '}';
    }

    @Override
    public int getRunTime() {
        return runTime;
    }

    private Route bestSol;
    private int[] aux;

    /**
     * @param tsp   instância do tsp
     * @param ite   numero de iterações
     * @param s_ref percentual de referência de piora sobre a solução inicial
     * @param p0    probabilidade inicial de aceitar uma piora de s_ref * custo solução inicial
     * @param pf    probabilidade final de aceitar uma piora de s_ref * custo solução inicial
     */
    public SA(TSP tsp, int ite, double s_ref, double p0, double pf) {
        this.tsp = tsp;
        this.ite = ite;
        this.s_ref = s_ref;
        this.p0 = p0;
        this.pf = pf;
        vnd = new VND(tsp);
        aux = new int[tsp.N];
    }


    public void run() {
        long t = System.currentTimeMillis();
        Route currentSol = new Route(tsp);
        Route tmp = new Route(tsp);
        currentSol.defaultSolConstructor();
        bestSol = new Route(tsp);
        bestSol.copy(currentSol);
        double T0 = currentSol.cost * s_ref / Math.log(1 / p0);
        double Tf = currentSol.cost * s_ref / Math.log(1 / pf);
        double lambda = Math.pow(Tf / T0, 1.0 / ite);
        for (double temp = T0; temp > Tf; temp *= lambda) {
            tmp.copy(currentSol);
            shake(tmp);
            if (tmp.cost < currentSol.cost) {
                currentSol.copy(tmp);

                System.out.printf("%.2f\t SA\t%.1f\t%.1f\n", temp, currentSol.cost,bestSol.cost);
                if (currentSol.cost < bestSol.cost - Utils.EPS) {
                    bestSol.copy(currentSol);
                    System.out.printf("%.2f\t*SA\t%.1f\t%.1f\n", temp, currentSol.cost,bestSol.cost);
                    assert Utils.equals(bestSol.cost, tsp.cost(bestSol.v)) : "variável 'cost' está inconsistente";
                }

            } else if (Utils.rd.nextDouble() < P(tmp.cost - currentSol.cost, temp)) {
                currentSol.copy(tmp);
                System.out.printf("%.2f\t#SA\t%.1f\t%.1f\n", temp, currentSol.cost,bestSol.cost);
            }

        }


        runTime = (int) (System.currentTimeMillis() - t);
    }

    /** distribuição de Bolzman-Gibbs
     * @param delta variação na função de avaliação (tem que ser maior q zero)
     * @param t temperatura do sistema
     * @return
     */
    private double P(double delta, double t) {
        return 1 / Math.exp(delta / t);
    }

    private void shake(Route r) {
        int v[] = r.v;
        int size = Math.min(2 + Utils.rd.nextInt(v.length / 10), 50);
        int ini = Utils.rd.nextInt(v.length - size);

        for (int i = size - 1; i > 0; i--) {
            int x = Utils.rd.nextInt(i);
            int id = ini + i;
            int xd = ini + x;
            //swap
            int aux = v[id];
            v[id] = v[xd];
            v[xd] = aux;
        }
        r.cost = tsp.cost(r.v);
        vnd.run(r);
    }

}
