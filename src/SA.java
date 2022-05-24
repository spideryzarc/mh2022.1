/**
 * Simulated Annealing
 */
public class SA implements Solver {
    final TSP tsp;
    final int ite;
    final double Sini, Pini;
    private int runTime;

    public Route getBestSol() {
        return bestSol;
    }


    @Override
    public int getRunTime() {
        return runTime;
    }

    private Route bestSol;

    /**
     * @param tsp
     * @param ite
     * @param sini percentual de piora sobre a solução inicial aceita sob probabilidade pini
     * @param pini probabilidade de aceitar uma piora de sini % sobre a solução inicial
     */
    public SA(TSP tsp, int ite, double sini, double pini) {
        this.tsp = tsp;
        this.ite = ite;
        Sini = sini;
        Pini = pini;
    }

    public void run() {
        long t = System.currentTimeMillis();
        Route currentSol = new Route(tsp);
        Route aux = new Route(tsp);
        currentSol.randomize();
        bestSol = new Route(tsp);
        bestSol.copy(currentSol);
        double T0 = currentSol.cost*Sini/Math.log(1/Pini);
        double Tf = 0.0000001;
        double lambda = .99;
        for (double temp = T0; temp > Tf; temp *= lambda) {
            aux.copy(currentSol);
            shake(aux);
            if (aux.cost < currentSol.cost) {
                currentSol.copy(aux);
                System.out.println(temp + "\t" + currentSol.cost);

                if (currentSol.cost < bestSol.cost - Utils.EPS) {
                    bestSol.copy(currentSol);
                    System.out.println(temp + " SA " + bestSol.cost);
                    assert Utils.equals(bestSol.cost, tsp.cost(bestSol.v)) : "variável 'cost' está inconsistente";
                }

            } else if (Utils.rd.nextDouble() < P(aux.cost - currentSol.cost, temp)) {
                currentSol.copy(aux);
                System.out.println(temp + "*SA " + currentSol.cost);
            }

        }


        runTime = (int) (System.currentTimeMillis() - t);
    }

    private double P(double delta, double t) {
        return 1 / Math.exp(delta / t);
    }

    private void shake(Route r) {
        int v[] = r.v;
        int size = 2 + Utils.rd.nextInt(5);
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
    }
}
