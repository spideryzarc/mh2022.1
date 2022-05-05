import java.util.ArrayList;
import java.util.Arrays;

/**
 * Uma solução do TSP
 */
public class Route {

    public static final boolean trace = false;

    /**
     * A instância relacionada
     */
    final TSP tsp;
    /**
     * Ordem de visitação das cidades
     */
    final int[] v;
    /**
     * custo da solução (ATENÇÃO - pode estar inconsistente com 'v')
     */
    double cost;

    /**
     * @param tsp instância associada da rota
     */
    public Route(TSP tsp) {
        this.tsp = tsp;
        v = new int[tsp.N];
        cost = 0;
    }

    @Override
    public String toString() {
        return "Route{" +
                "cost=" + cost +
                ", v=" + Arrays.toString(v) +
                '}';
    }

    /**
     * Gera uma solução aleatória
     */
    public void randomize() {
        for (int i = 0; i < tsp.N; i++)
            v[i] = i;
        Utils.shuffler(v);
        Utils.roll(v);
        cost = tsp.cost(v);
    }

    /**
     * Constroi uma solução com o algoritmo do vizinho mais próximo
     *
     * @param ini cidade inicial do algoritmo
     */
    public void nearestNeighbor(int ini) {
        boolean[] visitado = new boolean[tsp.N];
        v[0] = ini;
        visitado[ini] = true;
        for (int i = 1; i < tsp.N; i++) {
            double min = Double.POSITIVE_INFINITY;
            int arg_j = -1;
            for (int j = 0; j < tsp.N; j++)
                if (!visitado[j] && min > tsp.c[v[i - 1]][j]) {
                    min = tsp.c[v[i - 1]][j];
                    arg_j = j;
                }
            v[i] = arg_j;
            visitado[arg_j] = true;
        }
        Utils.roll(v);
        cost = tsp.cost(v);
    }

    /**
     * Constroi uma solução com o algoritmo da inserção do mais distante
     */
    public void furtherInsertion() {
        final int n = tsp.N;
        int arg_i = -1, arg_j = -1;
        double max = -1;
        for (int i = 1; i < n; i++)
            for (int j = 0; j < i; j++)
                if (max < tsp.c[i][j]) {
                    max = tsp.c[i][j];
                    arg_i = i;
                    arg_j = j;
                }
        ArrayList<Integer> lista = new ArrayList<>();
        lista.add(arg_i);
        lista.add(arg_j);
        boolean[] visitado = new boolean[n];
        visitado[arg_i] = visitado[arg_j] = true;
        /**distância minima para rota parcial*/
        double dist[] = new double[n];

        for (int i = 0; i < n; i++)
            dist[i] = Math.min(tsp.c[arg_i][i], tsp.c[arg_j][i]);
//        int pivot = -1;
//        max = -1;
//        for (int i = 0; i < n; i++)
//            if (!visitado[i] && max < dist[i]) {
//                pivot = i;
//                max = dist[i];
//            }
//        if (pivot == -1)
//            return;
//        lista.add(pivot);
//        visitado[pivot] = true;
        for (int k = 2; k < n; k++) {
            int pivot = -1;
            max = -1;
            for (int i = 0; i < n; i++)
                if (!visitado[i] && max < dist[i]) {
                    pivot = i;
                    max = dist[i];
                }

            for (int i = 0; i < n; i++)//atualiza distancia para rota parcial
                if (!visitado[i])
                    dist[i] = Math.min(dist[i], tsp.c[i][pivot]);
            //lista.add(pivot);
            //inserir na melhor posição
            double min = Double.POSITIVE_INFINITY;
            arg_i = -1;
            for (int i = 1; i < lista.size(); i++)
                if (min > tsp.c[lista.get(i - 1)][pivot]
                        + tsp.c[pivot][lista.get(i)]
                        - tsp.c[lista.get(i - 1)][lista.get(i)]) {
                    arg_i = i;
                    min = tsp.c[lista.get(i - 1)][pivot]
                            + tsp.c[pivot][lista.get(i)]
                            - tsp.c[lista.get(i - 1)][lista.get(i)];
                }
            lista.add(arg_i, pivot);
            visitado[pivot] = true;
        }
        for (int i = 0; i < n; i++)
            v[i] = lista.get(i);

        Utils.roll(v);
        cost = tsp.cost(v);
    }

    /**
     * Vizinhança: Troca dois vertices de posição
     * first_imp: pesquisa até encontrar o PRIMEIRO vizinho melhor e realiza a troca
     * best_imp: pesquisa todos os vizinhos e realiza a MELHOR troca
     *
     * @return true - se e somente se uma troca foi realizada
     */
    public boolean swap_2_best_imp() {
        double min_delta = 0;
        int arg_i = -1, arg_j = -1;
        for (int i = 1; i < tsp.N; i++)
            for (int j = 0; j < i; j++) {
                double delta = getSwapDelta(i, j);
                if (delta < min_delta - Utils.EPS) {
                    min_delta = delta;
                    arg_i = i;
                    arg_j = j;
                }
            }

        if (min_delta < -Utils.EPS) {
            Utils.swap(v, arg_i, arg_j);
            cost += min_delta;
            assert Utils.equals(cost, tsp.cost(v)) : "variável 'cost' está inconsistente";
            if (trace)
                System.out.println("swap " + cost);
            return true;
        }
        return false;
    }


    /**
     * Vizinhança: (2 - optimum exchange) Troca duas arestas (i,pi),(j,pj)
     * por (i,j),(pi,pj)
     * first_imp: pesquisa até encontrar o PRIMEIRO vizinho melhor e realiza a troca
     * best_imp: pesquisa todos os vizinhos e realiza a MELHOR troca
     *
     * @return true - se e somente se uma troca foi realizada
     */
    public boolean opt_2_first_imp() {
        final double c[][] = tsp.c;
        boolean imp = false;
        for (int i = 0; i < tsp.N; i++) {
            int lenj = (i == 0) ? tsp.N - 1 : tsp.N;
            for (int j = i + 2; j < lenj; j++) {
                //i j não podem ser adjacentes
                int vi = v[i];
                int p_vi = (i < tsp.N - 1) ? v[i + 1] : v[0];
                int vj = v[j];
                int p_vj = (j < tsp.N - 1) ? v[j + 1] : v[0];
                double delta = c[vi][vj] + c[p_vi][p_vj]
                        - c[vi][p_vi] - c[vj][p_vj];
                if (delta < Utils.EPS) {
                    for (int k = i + 1, h = j; k < h; k++, h--) {
                        int aux = v[k];
                        v[k] = v[h];
                        v[h] = aux;
                    }
                    cost += delta;
                    if (trace)
                        System.out.println("2opt " + cost + toString());
                    assert Utils.equals(cost, tsp.cost(v)) : "variável 'cost' está inconsistente";
                    imp = true;
//                    return true;
                }
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
    public boolean opt_2_best_imp() {
        final double c[][] = tsp.c;
        double min = -Utils.EPS;
        int arg_i = -1, arg_j = -1;

        for (int i = 0; i < tsp.N; i++) {
            int lenj = (i == 0) ? tsp.N - 1 : tsp.N;
            for (int j = i + 2; j < lenj; j++) {
                //i j não podem ser adjacentes
                int vi = v[i];
                int p_vi = (i < tsp.N - 1) ? v[i + 1] : v[0];
                int vj = v[j];
                int p_vj = (j < tsp.N - 1) ? v[j + 1] : v[0];
                double delta = c[vi][vj] + c[p_vi][p_vj]
                        - c[vi][p_vi] - c[vj][p_vj];
                if (delta < min) {
                    min = delta;
                    arg_i = i;
                    arg_j = j;

                }
            }
        }
        if (arg_i != -1) {

            for (int k = arg_i + 1, h = arg_j; k < h; k++, h--) {
                int aux = v[k];
                v[k] = v[h];
                v[h] = aux;
            }
            cost += min;
            if (trace)
                System.out.println("2opt " + cost);
            assert Utils.equals(cost, tsp.cost(v)) : "variável 'cost' está inconsistente";
            return true;
        }

        return false;
    }

    /**
     * Vizinhança: Desloca um vértice de posição na rota
     * first_imp: pesquisa até encontrar o PRIMEIRO vizinho melhor e realiza a troca
     * best_imp: pesquisa todos os vizinhos e realiza a MELHOR troca
     *
     * @return true - se e somente se uma troca foi realizada
     */
    public boolean replace_first_imp() {
        final double c[][] = tsp.c;
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
                    if (i < j) {
                        for (int k = i; k < j; k++)
                            v[k] = v[k + 1];
                        v[j] = vi;
                    } else {
                        for (int k = i; k > j; k--)
                            v[k] = v[k - 1];
                        v[j + 1] = vi;
                    }
                    cost += delta;
                    assert Utils.equals(cost, tsp.cost(v)) : "variável 'cost' está inconsistente";
                    if (trace)
                        System.out.println("replace " + cost);
                    imp = true;
                    break;//evita inconsistência na variável vi
//                    return true;
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
    public boolean replace_best_imp() {
        final double c[][] = tsp.c;
        double min = -Utils.EPS;
        int arg_i = -1, arg_j = -1;
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
                if (delta < min) {
                    min = delta;
                    arg_i = i;
                    arg_j = j;

                }
            }
        }

        if (arg_i != -1) {
            int vi = v[arg_i];
            int vj = v[arg_j];
            if (arg_i < arg_j) {
                for (int k = arg_i; k < arg_j; k++)
                    v[k] = v[k + 1];
                v[arg_j] = vi;
            } else {
                for (int k = arg_i; k > arg_j; k--)
                    v[k] = v[k - 1];
                v[arg_j + 1] = vi;
            }
            cost += min;
            assert Utils.equals(cost, tsp.cost(v)) : "variável 'cost' está inconsistente";
            if (trace)
                System.out.println("replace " + cost);
            return true;
        }

        return false;
    }

    /**
     * Vizinhança: Troca dois vertices de posição
     * first_imp: pesquisa até encontrar o PRIMEIRO vizinho melhor e realiza a troca
     * best_imp: pesquisa todos os vizinhos e realiza a MELHOR troca
     *
     * @return true se e somente se uma troca foi realizada
     */
    public boolean swap_2_first_imp() {
        boolean imp = false;
        for (int i = 1; i < tsp.N; i++)
            for (int j = 0; j < i; j++) {
                double delta = getSwapDelta(i, j);
                if (delta < -0.001) {
                    Utils.swap(v, i, j);
                    cost += delta;
                    assert Utils.equals(cost, tsp.cost(v)) : "variável 'cost' está inconsistente";
                    if (trace)
                        System.out.println("swap " + cost);
                    imp = true;
//                    return true;

                }
            }

        return imp;
    }

    /**
     * @return variação no custo da solução corrente se alternarmos, entre si,
     * os vertices nas posições i,j
     */
    private final double getSwapDelta(int i, int j) {
        final double[][] c = tsp.c;
        int vi = v[i];
        int vj = v[j];
        int ant_vi = v[i - 1];
        int ant_vj = v[(j > 0) ? j - 1 : tsp.N - 1];
        int prx_vi = v[(i < tsp.N - 1) ? i + 1 : 0];
        int prx_vj = v[j + 1];
        double delta;
        if (j + 1 == i)
            // antj -> j -> i -> proxi
            // antj -> i -> j -> proxi
            delta = c[ant_vj][vi] + c[vj][prx_vi] - c[ant_vj][vj] - c[vi][prx_vi];
        else if (j == 0 && i == tsp.N - 1)
            // anti -> i -> j -> proxj
            // anti -> j -> i -> proxj
            delta = c[ant_vi][vj] + c[vi][prx_vj] - c[ant_vi][vi] - c[vj][prx_vj];
        else
            // antj -> j -> proxj  ... anti-> i -> proxi
            // antj -> i -> proxj  ... anti-> j -> proxi
            delta = c[ant_vj][vi] + c[vi][prx_vj] + c[ant_vi][vj] + c[vj][prx_vi]
                    - c[ant_vj][vj] - c[vj][prx_vj] - c[ant_vi][vi] - c[vi][prx_vi];
        return delta;
    }


    public void copy(Route src) {
        assert this.tsp == src.tsp : "copia de soluções de instâncias diferentes";
        cost = src.cost;
        System.arraycopy(src.v, 0, v, 0, v.length);
    }
}
