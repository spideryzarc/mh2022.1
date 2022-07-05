import java.util.Arrays;
import java.util.LinkedList;

/**
 * Tabu Search
 */
public class TABU implements Solver {
    final TSP tsp;
    final int tenure;
    final int ite;
    private int runTime;
    private LinkedList<Integer> listaTabu;
    private Disturbances dist;

    public TABU(TSP tsp, int ite, int tenure) {
        this.tsp = tsp;
        this.ite = ite;
        this.tenure = tenure;
        aux = new int[tsp.N];
        listaTabu = new LinkedList<>();
        dist = new Disturbances(tsp);
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
        return "TABU{" +
                "tenure=" + tenure +
                ", ite=" + ite +
                ", runTime=" + runTime +
                ", bestSol=" + bestSol.cost +
                '}';
    }

    public String parametros() {
        return String.join(",","ite=" + ite,
                "tenure=" + tenure);
    }

    public void run() {
        long t = System.currentTimeMillis();
        Route currentSol = new Route(tsp);
        currentSol.defaultSolConstructor();
        VND vnd = new TABU.VND(tsp);//vnd adaptado
        vnd.run(currentSol);
        bestSol = new Route(tsp);
        bestSol.copy(currentSol);

        listaTabu.clear();
        listaTabu.add(Arrays.hashCode(currentSol.v));

        for (int i = 0; i < ite; i++) {
            dist.moveRandomToBegin(currentSol, 1, 2);
//            dist.shufflerWindows(currentSol, 2, 100);
//            dist.moveWindowsToEnd(currentSol, 2, 10);
            vnd.run(currentSol);

            listaTabu.add(Arrays.hashCode(currentSol.v));
            if (listaTabu.size() > tenure)
                listaTabu.pollFirst();//remove o tabu mais antigo

            if (currentSol.cost < bestSol.cost - Utils.EPS) {
                bestSol.copy(currentSol);
                System.out.println(i + " TABU " + bestSol.cost);
                assert Utils.equals(bestSol.cost, tsp.cost(bestSol.v)) : "variável 'cost' está inconsistente";
            }
        }
        runTime = (int) (System.currentTimeMillis() - t);
    }

    private boolean isTabu(int v[]) {
        return listaTabu.contains(Arrays.hashCode(v));
    }

    /**
     * Variable Neighborhood Descendant -- adaptado para o tabu
     */
    public class VND {
        final TSP tsp;
        private int v_aux[];

        public VND(TSP tsp) {
            this.tsp = tsp;
            v_aux = new int[tsp.N];
        }

        public void run(Route r) {
            boolean imp = true;
            while (imp) {
                imp = swap_2_first_imp(r);
                if (!imp)
                    imp = opt_2_first_imp(r);
                if (!imp)
                    imp = replace_first_imp(r);
            }
            Utils.rollZero(r.v);
        }

        /**
         * Vizinhança: Troca dois vertices de posição
         * first_imp: pesquisa até encontrar o PRIMEIRO vizinho melhor e realiza a troca
         * best_imp: pesquisa todos os vizinhos e realiza a MELHOR troca
         *
         * @return true se e somente se uma troca foi realizada
         */
        private boolean swap_2_first_imp(Route r) {
            boolean imp = false;
            for (int i = 1; i < tsp.N; i++)
                for (int j = 0; j < i; j++) {
                    double delta = r.getSwapDelta(i, j);
                    if (delta < -0.001) {
                        Utils.swap(r.v, i, j);
                        if (isTabu(r.v)) {
//                            System.out.println("tabu1");
                            Utils.swap(r.v, i, j);
                            continue;
                        }
                        r.cost += delta;
                        assert Utils.equals(r.cost, tsp.cost(r.v)) : "variável 'cost' está inconsistente";
//                        if (trace)
//                            System.out.println("swap " + cost);
                        imp = true;
                    }
                }
            return imp;
        }

        /**
         * Vizinhança: (2 - optimum exchange) Troca duas arestas (i,pi),(j,pj)
         * por (i,j),(pi,pj)
         * first_imp: pesquisa até encontrar o PRIMEIRO vizinho melhor e realiza a troca
         * best_imp: pesquisa todos os vizinhos e realiza a MELHOR troca
         *
         * @return true - se e somente se uma troca foi realizada
         */
        public boolean opt_2_first_imp(Route r) {
            final double c[][] = tsp.c;
            boolean imp = false;
            for (int i = 0; i < tsp.N; i++) {
                int lenj = (i == 0) ? tsp.N - 1 : tsp.N;
                for (int j = i + 2; j < lenj; j++) {
                    //i j não podem ser adjacentes
                    int vi = r.v[i];
                    int p_vi = (i < tsp.N - 1) ? r.v[i + 1] : r.v[0];
                    int vj = r.v[j];
                    int p_vj = (j < tsp.N - 1) ? r.v[j + 1] : r.v[0];
                    double delta = c[vi][vj] + c[p_vi][p_vj]
                            - c[vi][p_vi] - c[vj][p_vj];
                    if (delta < -Utils.EPS) {
                        for (int k = i + 1, h = j; k < h; k++, h--) {
                            int aux = r.v[k];
                            r.v[k] = r.v[h];
                            r.v[h] = aux;
                        }
                        if (isTabu(r.v)) {
//                            System.out.println("tabu2");
                            for (int k = i + 1, h = j; k < h; k++, h--) {
                                int aux = r.v[k];
                                r.v[k] = r.v[h];
                                r.v[h] = aux;
                            }
                            continue;
                        }
                        r.cost += delta;
//                        if (trace)
//                            System.out.println("2opt " + cost + toString());
                        assert Utils.equals(r.cost, tsp.cost(r.v)) : "variável 'cost' está inconsistente";
                        imp = true;
                    }
                }
            }
            return imp;
        }

        /**
         * Vizinhança: Desloca um vértice de posição na rota
         * first_imp: pesquisa até encontrar o PRIMEIRO vizinho melhor e realiza a troca
         * best_imp: pesquisa todos os vizinhos e realiza a MELHOR troca
         *
         * @return true - se e somente se uma troca foi realizada
         */
        public boolean replace_first_imp(Route r) {
            final double c[][] = tsp.c;
            int v[] = r.v;
            boolean imp = false;
            for (int i = 0; i < tsp.N; i++) {
                int vi = v[i];
                int ant_vi = (i > 0) ? v[i - 1] : v[v.length - 1];
                int prx_vi = (i == v.length - 1) ? v[0] : v[i + 1];
                double delta_rem = c[ant_vi][prx_vi] - c[ant_vi][vi] - c[vi][prx_vi];
                for (int j = 0; j < tsp.N; j++) {
                    int vj = v[j];
                    if (ant_vi == vj || vj == vi)
                        continue;

                    int prx_vj = (j == v.length - 1) ? v[0] : v[j + 1];

                    double delta = delta_rem + c[vj][vi] + c[vi][prx_vj] - c[vj][prx_vj];
                    if (delta < -0.001) {

                        System.arraycopy(r.v, 0, v_aux, 0, v_aux.length);
                        if (i < j) {
                            for (int k = i; k < j; k++)
                                v[k] = v[k + 1];
                            v[j] = vi;
                        } else {
                            for (int k = i; k > j; k--)
                                v[k] = v[k - 1];
                            v[j + 1] = vi;
                        }
                        if (isTabu(v)) {
                            System.arraycopy(v_aux, 0, r.v, 0, v_aux.length);
//                            System.out.println("tabu3");
                            continue;
                        }
                        r.cost += delta;
                        assert Utils.equals(r.cost, tsp.cost(v)) : "variável 'cost' está inconsistente";
//                        if (trace)
//                            System.out.println("replace " + cost);
                        imp = true;
                        break;//evita inconsistência na variável vi

                    }
                }
            }
            return imp;
        }
    }


}
