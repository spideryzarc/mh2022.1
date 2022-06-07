/**
 * Guided Local Search
 */
public class GLS implements Solver {
    final TSP tsp;
    final int ite;
    final double A;
    final double B;
    private int runTime;
    private double c_backup[][];

    /**
     * @param tsp instância do PCV
     * @param ite número de iterações
     * @param A   taxa máxima de penalidade das arestas dos ótimos locais (A > 0) (ex. A=0.1)
     * @param B   taxa de 'esquecimento' da  penalidade (0 <= B <= 1) (ex. B=0.9)
     */
    public GLS(TSP tsp, int ite, double A, double B) {
        if (B < 0 || B > 1)
            throw new IllegalArgumentException("condição (0 <= B <= 1) não respeitada");
        if (A <= 0)
            throw new IllegalArgumentException("condição (A > 0)");
        this.tsp = tsp;
        this.ite = ite;
        this.A = A;
        this.B = B;

        c_backup = new double[tsp.N][tsp.N];
        for (int i = 0; i < c_backup.length; i++)
            for (int j = 0; j < c_backup.length; j++)
                c_backup[i][j] = tsp.c[i][j];
    }

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
        return "GLS{" +
                ", ite=" + ite +
                ", A=" + A +
                ", B=" + B +
                ", runTime=" + runTime +
                ", bestSol=" + bestSol.cost +
                '}';
    }

    public void run() {
        long t = System.currentTimeMillis();

        Route currentSol = new Route(tsp);
        currentSol.defaultSolConstructor();
        VND vnd = new VND(tsp);//vnd adaptado
        vnd.run(currentSol);
        bestSol = new Route(tsp);
        bestSol.copy(currentSol);
        System.out.println(" GLS " + bestSol.cost);

        for (int i = 0; i < ite; i++) {
            updatePenalties(currentSol);
            vnd.run(currentSol);
            double real_cost = realCost(currentSol.v);
            if (real_cost < bestSol.cost - Utils.EPS) {
                bestSol.copy(currentSol);
                bestSol.cost = real_cost;
                System.out.println(i + " GLS " + bestSol.cost);
                resetC();
            }
        }
        resetC();
        assert Utils.equals(bestSol.cost, tsp.cost(bestSol.v)) : "variável 'cost' está inconsistente";
        runTime = (int) (System.currentTimeMillis() - t);
    }

    /**
     * restaura a matriz de custos da instância
     * com seus valores originais
     */
    private void resetC() {
        for (int i = 0; i < c_backup.length; i++)
            for (int j = 0; j < c_backup.length; j++)
                tsp.c[i][j] = c_backup[i][j];
    }


    /**
     * Calcula o custo real de uma rota, ou seja, utilizando a
     * matriz de custos original, sem penalidades, da instância.
     *
     * @param v sequência de vértices da rota
     * @return custo da rota
     */
    private final double realCost(int[] v) {
        double d = c_backup[v[v.length - 1]][v[0]];
        for (int i = 1; i < v.length; i++) {
            d += c_backup[v[i - 1]][v[i]];
        }
        return d;
    }

    /**
     * atualiza a alteração na função de avalização através
     * de penalidades adicionadas na matriz de custos.
     *
     * @param currentSol ótimo local
     */
    private void updatePenalties(Route currentSol) {
        //suaviza penalidades passadas
        for (int i = 0; i < c_backup.length; i++)
            for (int j = 0; j < c_backup.length; j++)
                tsp.c[i][j] = tsp.c[i][j] * (1 - B) + c_backup[i][j] * B;

        //adiciona novas penalidades
        int v[] = currentSol.v;
        for (int i = 1; i < v.length; i++) {
            double x = c_backup[v[i - 1]][v[i]] * A * Utils.rd.nextDouble();
            tsp.c[v[i - 1]][v[i]] += x;
            tsp.c[v[i]][v[i - 1]] += x;
        }
        currentSol.cost = tsp.cost(v);
    }

}
