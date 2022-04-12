import java.util.ArrayList;
import java.util.Arrays;

/**
 * Uma solução do TSP
 */
public class Route {
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
        double max = 0;
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
        int pivot = -1;
        max = 0;
        for (int i = 0; i < n; i++)
            if (!visitado[i] && max < dist[i]) {
                pivot = i;
                max = dist[i];
            }
        if (pivot == -1)
            return;
        lista.add(pivot);
        visitado[pivot] = true;
        for (int k = 3; k < n; k++) {
            for (int i = 0; i < n; i++)//atualiza distancia para rota parcial
                if (!visitado[i])
                    dist[i] = Math.min(dist[i], tsp.c[i][pivot]);

            pivot = -1;
            max = 0;
            for (int i = 0; i < n; i++)
                if (!visitado[i] && max < dist[i]) {
                    pivot = i;
                    max = dist[i];
                }
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

    public boolean swap_2_first_imp() {
        final double c[][] = tsp.c;
        for (int idx_i = 1; idx_i < tsp.N; idx_i++)
            for (int idx_j = 0; idx_j < idx_i; idx_j++) {
                int i = v[idx_i];
                int j = v[idx_j];
                int ant_i = v[idx_i - 1];
                int ant_j = v[(idx_j > 0) ? idx_j - 1 : tsp.N - 1];
                int prox_i = v[(idx_i < tsp.N - 1) ? idx_i + 1 : 0];
                int prox_j = v[idx_j + 1];

                double delta;
                if (idx_j + 1 == idx_i)
                    // antj -> j -> i -> proxi
                    // antj -> i -> j -> proxi
                    delta = c[ant_j][i] + c[j][prox_i] - c[ant_j][j] - c[i][prox_i];
                else if (idx_j == 0 && idx_i == tsp.N - 1)
                    // anti -> i -> j -> proxj
                    // anti -> j -> i -> proxj
                    delta = c[ant_i][j] + c[i][prox_j] - c[ant_i][i] - c[j][prox_j];
                else
                    // antj -> j -> proxj  ... anti-> i -> proxi
                    // antj -> i -> proxj  ... anti-> j -> proxi
                    delta = c[ant_j][i] + c[i][prox_j] + c[ant_i][j] + c[j][prox_i]
                            - c[ant_j][j] - c[j][prox_j] - c[ant_i][i] - c[i][prox_i];
                if (delta < -0.001) {
                    Utils.swap(v, idx_i, idx_j);
                    cost += delta;
                    System.out.println("swap " + cost);
                    return true;
                }
            }
        return false;
    }

}
