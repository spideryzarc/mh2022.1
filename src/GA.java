import java.sql.Array;
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
    int pop_size;
    int K;
    boolean rank;
    TSP tsp;

    public GA(TSP tsp, int ite, int pop_size, int k, boolean rank) {
        this.tsp = tsp;
        this.ite = ite;
        this.pop_size = pop_size;
        K = k;
        this.rank = rank;
        used = new boolean[tsp.N];
    }

    @Override
    public void run() {
        ArrayList<Route> pop = new ArrayList<>();
        ArrayList<Route> os = new ArrayList<>();
        initialize(pop);
        bestSol =pop.get(0);

        for (int i = 0; i < ite; i++) {
            offspring(os, pop);
            os.addAll(pop);
            select(pop,os);
            if(bestSol.cost > pop.get(0).cost){
                bestSol.copy(pop.get(0));
                System.out.println(i+" GA "+bestSol.cost);
            }
        }

    }

    private void select(ArrayList<Route> pop, ArrayList<Route> os) {
        pop.clear();
        //eliminar os repetidos
        HashSet<Route> aux = new HashSet<>();
        aux.addAll(os);
        os.clear();
        os.addAll(aux);
        if(rank){
            Collections.sort(os,(a,b)->Double.compare(a.cost, b.cost));
            for (int i = 0; i < K; i++)
                pop.add(os.get(i));

        }else {//torneio

        }


    }

    private void offspring(ArrayList<Route> os, ArrayList<Route> pop) {
        os.clear();
        for (int i = 1; i < pop.size(); i++)
            for (int j = 0; j < i; j++) {
                Route r = crossover(pop.get(i), pop.get(j));
                os.add(r);
            }
    }

    private boolean used[];

    private Route crossover(Route A, Route B) {
        Route C = new Route(tsp);
        int a = tsp.N / 3;
        int b = a * 2;
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

    private void initialize(ArrayList<Route> pop) {
        for (int i = 0; i < pop_size; i++) {
            Route r = new Route(tsp);
            r.randomize();
            pop.add(r);
        }
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
