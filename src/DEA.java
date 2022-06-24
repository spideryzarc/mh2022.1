import java.util.*;

/**
 * Distribution Estimation Algorithm
 */
public class DEA implements Solver {
    final TSP tsp;
    final int ite;
    final double alpha;
    final int sample_size;
    double D[][];
    double DM[][];
    Route bestSol;
    long runTime;
    final int elite_size;
    final int select_mode;
    final int tournoment_k;
    private GRASP grasp;
    private VND vnd;

    public DEA(TSP tsp, int ite, double alpha, int sample_size, int elite_size, int select_mode, int tournoment_k) {
        this.tsp = tsp;
        this.ite = ite;
        this.alpha = alpha;
        this.sample_size = sample_size;
        D = new double[tsp.N][tsp.N];
        DM = new double[tsp.N][tsp.N];
        this.elite_size = elite_size;
        this.select_mode = select_mode;
        this.tournoment_k = tournoment_k;
        used = new boolean[tsp.N];
        weight = new double[tsp.N];
        grasp = new GRASP(tsp, 0, 5, true);
        vnd = new VND(tsp);
    }

    private void initialize(ArrayList<Route> pop) {
        for (int i = 0; i < sample_size; i++) {
            Route r = new Route(tsp);
            r.randomize();
            grasp.greedyRandom(r);
//            vnd.run(r);
            pop.add(r);
            System.out.println(i + " inipop " + r.cost);
        }
        Collections.sort(pop, (a, b) -> Double.compare(a.cost, b.cost));
    }

    private void marginal(ArrayList<Route> pop, double[][] d) {
        for (int i = 0; i < d.length; i++)
            for (int j = 0; j < i; j++)
                d[i][j] = d[j][i] = 0;
        for (Route s : pop) {
            for (int i = 1; i < s.v.length; i++) {
                d[s.v[i - 1]][s.v[i]]++;
                d[s.v[i]][s.v[i - 1]]++;
            }
            d[s.v[s.v.length - 1]][s.v[0]]++;
            d[s.v[0]][s.v[s.v.length - 1]]++;
        }
        for (int i = 0; i < d.length; i++)
            for (int j = 0; j < i; j++) {
                d[i][j] /= pop.size();
                d[j][i] /= pop.size();
            }

    }

    private void select(ArrayList<Route> pop) {
        //eliminar os repetidos
        HashSet<Route> aux = new HashSet<>();
        aux.addAll(pop);
        List<Route> os = new ArrayList<>();
        os.addAll(aux);
        //selecionar campeões
        if (os.size() <= elite_size) {
            pop.clear();
            pop.addAll(os);
            return;
        }
        pop.clear();
        Collections.sort(os, (a, b) -> Double.compare(a.cost, b.cost));
        pop.add(os.remove(0));
        if (select_mode == 0) { //elitista
            pop.addAll(os.subList(0, Math.min(elite_size, os.size())));
        } else if (select_mode == 1) {//roleta
            double[] weight = new double[os.size()];
            for (int i = 0; i < weight.length; i++)
                weight[i] = 1 / os.get(i).cost;

            for (int i = 1; i < elite_size; i++) {
                int x = Utils.roulette(weight);
                pop.add(os.get(x));
                weight[x] = 0;
            }
        } else if (select_mode == 2) {//rank
            double[] weight = new double[os.size()];
            for (int i = 0; i < weight.length; i++)
                weight[i] = weight.length - i;

            for (int i = 1; i < elite_size; i++) {
                int x = Utils.roulette(weight);
                pop.add(os.get(x));
                weight[x] = 0;
            }
        } else if (select_mode == 3) {//torneio
            for (int i = 1; i < elite_size; i++) {
                int argmin = -1;
                double min = Double.POSITIVE_INFINITY;
                for (int j = 0; j < tournoment_k; j++) {
                    int x = Utils.rd.nextInt(os.size());
                    if (os.get(x).cost < min) {
                        argmin = x;
                        min = os.get(x).cost;
                    }
                }
                pop.add(os.remove(argmin));
            }
        }


    }

    @Override
    public void run() {
        runTime = System.currentTimeMillis();
        ArrayList<Route> pop = new ArrayList<>();

        initialize(pop);
        select(pop);
        bestSol = new Route(tsp);
        bestSol.copy(pop.get(0));
        System.out.println(" DEA " + bestSol.cost);
        marginal(pop, D);
        for (int k = 0; k < ite; k++) {
            sample(D, pop);
            select(pop);
            if (bestSol.cost > pop.get(0).cost) {
                bestSol.copy(pop.get(0));
                System.out.println(k + " DEA " + bestSol.cost);
            }
            marginal(pop, DM);
            //D = D*(1-a)+DM*a
            for (int i = 0; i < D.length; i++)
                for (int j = 0; j < i; j++) {
                    D[j][i] = D[i][j] = D[i][j] * (1 - alpha) + DM[i][j] * alpha;
                }

        }


        runTime = System.currentTimeMillis() - runTime;
    }

    private boolean[] used;
    private double[] weight;

    private void sample(double[][] d, ArrayList<Route> pop) {
        for (int i = 0; i < sample_size; i++) {
            Route r = new Route(tsp);
            Arrays.fill(used, false);
            int x = Utils.rd.nextInt(tsp.N);
            r.v[0] = x;
            used[x] = true;
            for (int j = 1; j < tsp.N; j++) {
                for (int k = 0; k < tsp.N; k++) {
                    if (used[k])
                        weight[k] = 0;
                    else
                        weight[k] = Math.max(d[x][k], 1e-9);
                }
                int y = Utils.roulette(weight);
                r.v[j] = y;
                used[y] = true;
            }
            Utils.rollZero(r.v);
            r.cost = tsp.cost(r.v);
//            System.out.println(r.cost);
//            vnd.run(r);
            pop.add(r);
        }
    }

    @Override
    public String toString() {
        return "DEA{" +
                "ite=" + ite +
                ", cost=" + bestSol.cost +
                ", alpha=" + alpha +
                ", sample_size=" + sample_size +
                ", runTime=" + runTime +
                ", elite_size=" + elite_size +
                ", select_mode=" + select_mode +
                ", tournoment_k=" + tournoment_k +
                '}';
    }

    @Override
    public Route getBestSol() {
        return bestSol;
    }

    @Override
    public int getRunTime() {
        return (int) runTime;
    }
}
