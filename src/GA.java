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
    int elite_size;
    int select_mode;
    final int tournoment_k;
    TSP tsp;
    private int map[];
    private VND vnd;
    private GRASP grasp;
    Disturbances dist;
    final private double MR;

    /**
     * @param tsp          instância de TSP
     * @param ite          número de gerações
     * @param iniPopSize   número de indivíduos na população inicial
     * @param elite_size   número de indivíduos selecionados para o cruzamento
     * @param MR           taxa de mutação
     * @param select_mode  [0: elitista, 1: roleta, 2: rank, 3: torneio]
     * @param tournoment_k número de indivíduos por torneio
     */
    public GA(TSP tsp, int ite, int iniPopSize, int elite_size, double MR, int select_mode, int tournoment_k) {
        this.tournoment_k = tournoment_k;
        this.MR = MR;
        this.tsp = tsp;
        this.ite = ite;
        this.iniPopSize = iniPopSize;
        this.elite_size = elite_size;
        this.select_mode = select_mode;
        used = new boolean[tsp.N];
        map = new int[tsp.N];
        vnd = new VND(tsp);
        grasp = new GRASP(tsp, 0, 3, true);
        dist = new Disturbances(tsp);
    }

    @Override
    public void run() {
        long t = System.currentTimeMillis();
        ArrayList<Route> pop = new ArrayList<>();
        ArrayList<Route> os = new ArrayList<>();
        initialize(pop);
        select(pop, os);
        bestSol = new Route(tsp);
        bestSol.copy(pop.get(0));
        System.out.println(" GA " + bestSol.cost);

        for (int i = 0; i < ite; i++) {
            offspring(os, pop);
            select(pop, os);
            if (bestSol.cost > pop.get(0).cost) {
                bestSol.copy(pop.get(0));
                System.out.println(i + " GA " + bestSol.cost);
            }
            mutation(pop);
//            System.out.println(pop.get(pop.size() - 1).cost);
        }
        assert Utils.equals(bestSol.cost, tsp.cost(bestSol.v)) : "variável 'cost' está inconsistente";
        runTime = (int) (System.currentTimeMillis() - t);
    }

    private void mutation(ArrayList<Route> pop) {
        for (Route r : pop.subList(1, pop.size()))
            if (Utils.rd.nextDouble() < MR) {
                int x = Utils.rd.nextInt(3);
                if (x == 0)
                    dist.shufflerWindows(r, 5, 10);
                if (x == 1)
                    dist.moveRandomToBegin(r, 5, 10);
                if (x == 2)
                    dist.moveWindowsToEnd(r, 5, 10);
                vnd.run(r);
            }
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

    private void offspring(ArrayList<Route> os, ArrayList<Route> elite) {
        os.clear();
        for (int i = 1; i < elite.size(); i++)
            for (int j = 0; j < i; j++) {
                Route a = elite.get(i);
                Route b = elite.get(j);
                Route c;
//                c = twoPointsCrossover(a, b);
//                os.add(c);
//                c = twoPointsCrossover(b, a);
//                os.add(c);
                c = orderCrossover(a, b);
                vnd.run(c);
                os.add(c);
                c = orderCrossover(b, a);
                vnd.run(c);
                os.add(c);
//                c = partiallyMappedCrossover(a, b);
//                os.add(c);
//                c = partiallyMappedCrossover(b, a);
//                os.add(c);
//                c = uniformCrossover(a, b);
//                os.add(c);
//                c = uniformCrossover(b, a);
//                os.add(c);
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
        while (Math.abs(a - b) < 0.1 * tsp.N || Math.abs(a - b) > 0.9 * tsp.N)
            b = Utils.rd.nextInt(tsp.N);
        if (a > b) {
            int aux = a;
            a = b;
            b = aux;
        }

        Route C = new Route(tsp);
        Arrays.fill(used, false);
        Arrays.fill(C.v, -1);
        //copia a começo
        for (int i = 0; i < a; i++) {
            C.v[i] = A.v[i];
            used[A.v[i]] = true;
        }
        //copia o fim
        for (int i = b; i < tsp.N; i++) {
            C.v[i] = A.v[i];
            used[A.v[i]] = true;
        }
        //copia o meio
        for (int i = a; i < b; i++) {
            if (!used[B.v[i]]) {
                C.v[i] = B.v[i];
                used[B.v[i]] = true;
            }
        }
        //completa os faltantes
        for (int i = a, j = 0; i < b; i++)
            if (C.v[i] == -1) {
                for (; j < tsp.N; j++)
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
     * Uniform crossover
     *
     * @param A a solution
     * @param B a solution
     * @return recombination (crossover) of A and B
     */
    private Route uniformCrossover(Route A, Route B) {
        Route C = new Route(tsp);
        Arrays.fill(used, false);
        Arrays.fill(C.v, -1);
        int cont = 0;
        for (int i = 0; i < tsp.N; i++)
            if (Utils.rd.nextBoolean()) {
                C.v[i] = A.v[i];
                used[A.v[i]] = true;

            } else {
                map[cont] = i;
                cont++;
            }

        for (int im = 0, j = 0; im < cont; im++) {
            int i = map[im];
            for (; j < tsp.N; j++)
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

    @Override
    public String toString() {
        return "GA{" +
                "runTime=" + runTime +
                ", bestSol=" + bestSol.cost +
                ", ite=" + ite +
                ", iniPopSize=" + iniPopSize +
                ", elite_size=" + elite_size +
                ", select_mode=" + select_mode +
                ", tournoment_k=" + tournoment_k +
                ", MR=" + MR +
                '}';
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
            grasp.greedyRandom(r);
            vnd.run(r);
            pop.add(r);
            System.out.println(i + " inipop " + r.cost);
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
