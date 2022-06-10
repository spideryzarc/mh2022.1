import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 * Genetic Algorithm
 */
public class GA implements Solver {
    private int runTime;
    private Route bestSol;
    int ite;
    int iniPopSize;
    int K;
    boolean rank;
    TSP tsp;
    private int map[];

    public GA(TSP tsp, int ite, int iniPopSize, int k, boolean rank) {
        this.tsp = tsp;
        this.ite = ite;
        this.iniPopSize = iniPopSize;
        K = k;
        this.rank = rank;
        used = new boolean[tsp.N];
        map = new int[tsp.N];
    }

    @Override
    public void run() {
        long t = System.currentTimeMillis();
        ArrayList<Route> pop = new ArrayList<>();
        ArrayList<Route> os = new ArrayList<>();
        initialize(pop);
        select(pop, os);
        bestSol = pop.get(0);
        System.out.println(" GA " + bestSol.cost);

        for (int i = 0; i < ite; i++) {
            offspring(os, pop);
            select(pop, os);
            if (bestSol.cost > pop.get(0).cost) {
                bestSol.copy(pop.get(0));
                System.out.println(i + " GA " + bestSol.cost);
            }
            System.out.println(pop.get(pop.size()-1).cost);
        }
        assert Utils.equals(bestSol.cost, tsp.cost(bestSol.v)) : "variável 'cost' está inconsistente";
        runTime = (int) (System.currentTimeMillis() - t);
    }

    private void select(ArrayList<Route> pop, ArrayList<Route> os) {
        //eliminar os repetidos
        HashSet<Route> aux = new HashSet<>();
        aux.addAll(os);
        aux.addAll(pop);
        os.clear();
        os.addAll(aux);
        //selecionar campeões
        pop.clear();
        if (rank) {
            Collections.sort(os, (a, b) -> Double.compare(a.cost, b.cost));
            pop.addAll(os.subList(0, Math.min(K, os.size())));
        } else {//torneio

        }


    }

    private void offspring(ArrayList<Route> os, ArrayList<Route> elite) {
        os.clear();
        for (int i = 1; i < elite.size(); i++)
            for (int j = 0; j < i; j++) {
                Route a = elite.get(i);
                Route b = elite.get(j);
                os.add(twoPointsCrossover(a, b));
                os.add(twoPointsCrossover(b, a));
                os.add(orderCrossover(a, b));
                os.add(orderCrossover(b, a));
                os.add(partiallyMappedCrossover(a, b));
                os.add(partiallyMappedCrossover(b, a));
            }
    }

    private boolean used[];

    /**
     * Two points crossover
     *
     * @param A a solution
     * @param B a solution
     * @return recombination (crossover) of A and B
     */
    private Route twoPointsCrossover(Route A, Route B) {
        //seleciona dois pontos de corte
        int a = Utils.rd.nextInt(tsp.N);
        int b = Utils.rd.nextInt(tsp.N);
        while (a == b)
            b = Utils.rd.nextInt(tsp.N);
        if (a > b) {
            int aux = a;
            a = b;
            b = aux;
        }

        Route C = new Route(tsp);
        Arrays.fill(used, false);
        Arrays.fill(C.v, -1);
        for (int i = 0; i < a; i++) {
            C.v[i] = A.v[i];
            used[A.v[i]] = true;
        }
        for (int i = b; i < tsp.N; i++) {
            C.v[i] = A.v[i];
            used[A.v[i]] = true;
        }
        for (int i = a; i < b; i++) {
            if (!used[B.v[i]]) {
                C.v[i] = B.v[i];
                used[B.v[i]] = true;
            }
        }
        for (int i = a; i < b; i++)
            if (C.v[i] == -1) {
                for (int j = 0; j < tsp.N; j++)
                    if (!used[B.v[j]]) {
                        C.v[i] = B.v[j];
                        used[B.v[j]] = true;
                        break;
                    }
            }
        C.cost = tsp.cost(C.v);
        return C;
    }

    /**
     * order crossover
     *
     * @param A a solution
     * @param B a solution
     * @return recombination (crossover) of A and B
     */
    private Route orderCrossover(Route A, Route B) {
        //seleciona dois pontos de corte
        int a = Utils.rd.nextInt(tsp.N);
        int b = Utils.rd.nextInt(tsp.N);
        while (a == b)
            b = Utils.rd.nextInt(tsp.N);
        if (a > b) {
            int aux = a;
            a = b;
            b = aux;
        }

        Route C = new Route(tsp);
        Arrays.fill(used, false);
        Arrays.fill(C.v, -1);
        for (int i = a; i < b; i++) {
            C.v[i] = B.v[i];
            used[C.v[i]] = true;
        }
        int j = b;
        for (int i = b; i < tsp.N; i++) {
            if (!used[A.v[i]]) {
                C.v[j] = A.v[i];
                used[C.v[j]] = true;
                j++;
                if (j == tsp.N)
                    j = 0;
            }
        }
        for (int i = 0; i < b; i++) {
            if (!used[A.v[i]]) {
                C.v[j] = A.v[i];
                used[C.v[j]] = true;
                j++;
                if (j == tsp.N)
                    j = 0;
                if (j == a)
                    break;
            }
        }

        Utils.rollZero(C.v);
        C.cost = tsp.cost(C.v);
        return C;
    }

    /**
     * Partially mapped crossover
     *
     * @param A a solution
     * @param B a solution
     * @return recombination (crossover) of A and B
     */
    private Route partiallyMappedCrossover(Route A, Route B) {
        //seleciona dois pontos de corte
        int a = Utils.rd.nextInt(tsp.N);
        int b = Utils.rd.nextInt(tsp.N);
        while (a == b)
            b = Utils.rd.nextInt(tsp.N);
        if (a > b) {
            int aux = a;
            a = b;
            b = aux;
        }

        Route C = new Route(tsp);
        Arrays.fill(used, false);
        Arrays.fill(C.v, -1);
//        Arrays.fill(map, -1);//não precisa, só por segurança
        //copiar centro
        for (int i = a; i < b; i++) {
            C.v[i] = B.v[i];
            used[C.v[i]] = true;
            map[B.v[i]] = A.v[i];
        }

        //copiar extremidades
        for (int i = 0; i < tsp.N; i++) {
            if (!used[A.v[i]] && C.v[i] == -1) {
                C.v[i] = A.v[i];
                used[C.v[i]] = true;
            }
        }

        //fechar permutação usando o mapa
        for (int i = 0; i < tsp.N; i++) {
            if (C.v[i] == -1) {
                int x = map[A.v[i]];
                while (used[x])
                    x = map[x];
                C.v[i] = x;
                used[x] = true;
            }
        }
        Utils.rollZero(C.v);
        C.cost = tsp.cost(C.v);
        return C;
    }

    private void initialize(ArrayList<Route> pop) {
        for (int i = 0; i < iniPopSize; i++) {
            Route r = new Route(tsp);
            r.randomize();
            pop.add(r);
        }
        Collections.sort(pop, (a, b) -> Double.compare(a.cost, b.cost));
    }


    @Override
    public Route getBestSol() {
        return bestSol;
    }

    @Override
    public int getRunTime() {
        return runTime;
    }
}