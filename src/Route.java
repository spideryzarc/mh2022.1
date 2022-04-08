import java.util.ArrayList;
import java.util.Arrays;

public class Route {
    final TSP tsp;
    final int[] v;
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
        cost = tsp.cost(v);
    }

    /**
     * Constroi uma solução com o algoritmo do vizinho mais próximo
     */
    public void nearestNeighbor() {
        boolean[] vizitado = new boolean[tsp.N];
        v[0] = 0;
        vizitado[0] = true;
        for (int i = 1; i < tsp.N; i++) {
            double min = Double.POSITIVE_INFINITY;
            int arg_j = -1;
            for (int j = 1; j < tsp.N; j++)
                if (!vizitado[j] && min > tsp.c[v[i - 1]][j]) {
                    min = tsp.c[v[i - 1]][j];
                    arg_j = j;
                }
            v[i] = arg_j;
            vizitado[arg_j] = true;
        }
        cost = tsp.cost(v);
    }

    /**
     * Constroi uma solução com o algoritmo do inserção do mais distante
     */
    public void furtherInsertion() {
        final int n = tsp.N;
        ArrayList<Integer> lista = new ArrayList<>();
        lista.add(0);
        /**distância minima para rota parcial*/
        double dist[] = new double[n];
        boolean[] vizitado = new boolean[n];
        vizitado[0] = true;
        for (int i = 0; i < n; i++)
            dist[i] = tsp.c[0][i];
        int pivot = -1;
        double max = 0;
        for (int i = 0; i < n; i++)
            if (!vizitado[i] && max < dist[i]) {
                pivot = i;
                max = dist[i];
            }
        lista.add(pivot);
        vizitado[pivot] = true;
        for (int k = 2; k < n; k++) {
            for (int i = 0; i < n; i++)//atualiza distancia para rota parcial
                if (!vizitado[i])
                    dist[i] = Math.min(dist[i], tsp.c[i][pivot]);

            pivot = -1;
            max = 0;
            for (int i = 0; i < n; i++)
                if (!vizitado[i] && max < dist[i]) {
                    pivot = i;
                    max = dist[i];
                }
            //lista.add(pivot);
            //inserir na melhor posição
            double min = Double.POSITIVE_INFINITY;
            int arg_i = -1;
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
            vizitado[pivot] = true;
        }
        for (int i = 0; i < n; i++)
            v[i] = lista.get(i);

        cost = tsp.cost(v);
    }
}
