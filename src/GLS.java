import java.util.Hashtable;

/**
 * Guided Local Search
 */
public class GLS implements Solver {
    final TSP tsp;
    final int ite;
    private int runTime;
    private double c_backup[][];

    public GLS(TSP tsp, int ite) {
        this.tsp = tsp;
        this.ite = ite;
        aux = new int[tsp.N];
        c_backup = new double[tsp.N][tsp.N];

        for (int i = 0; i < c_backup.length; i++)
            for (int j = 0; j < c_backup.length; j++)
                c_backup[i][j] = tsp.c[i][j];
    }

    private int[] aux;

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
        addPenalties(currentSol);

        for (int i = 0; i < ite; i++) {
            vnd.run(currentSol);
            double real_cost = realCost(currentSol.v);
            if (real_cost < bestSol.cost - Utils.EPS) {
                bestSol.copy(currentSol);
                bestSol.cost = real_cost;
                System.out.println(i + " GLS " + bestSol.cost);
                resetC();
            }else{
                addPenalties(currentSol);
            }
        }
        resetC();
        assert Utils.equals(bestSol.cost, tsp.cost(bestSol.v)) : "variável 'cost' está inconsistente";
        runTime = (int) (System.currentTimeMillis() - t);
    }

    private void resetC() {
        for (int i = 0; i < c_backup.length; i++)
            for (int j = 0; j < c_backup.length; j++)
                tsp.c[i][j] = c_backup[i][j];
    }

    private final double realCost(int[] v) {
        double d = c_backup[v[v.length - 1]][v[0]];
        for (int i = 1; i < v.length; i++) {
            d += c_backup[v[i - 1]][v[i]];
        }
        return d;
    }

    private void addPenalties(Route currentSol) {
        int v[] = currentSol.v;
        for (int i = 1; i < v.length; i++) {
            double x = c_backup[v[i - 1]][v[i]] * 0.01;
            tsp.c[v[i - 1]][v[i]] += x;
            tsp.c[v[i]][v[i - 1]] += x;
        }
        currentSol.cost = tsp.cost(v);
    }

}
