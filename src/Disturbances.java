import java.util.ArrayList;

public class Disturbances {
    private int[] v_aux;
    private ArrayList<Integer> lista;
    public Disturbances(TSP tsp) {
        v_aux = new int[tsp.N];
        lista = new ArrayList<>();
    }


    /** Move uma janela de k vértices consecutivos para o fim da rota
     * @param r rota a ser perturbada
     * @param min_k menor valor para k , (>=2)
     * @param max_k maior valor para k , (< r.tsp.N)
     */
    public void moveWindowsToEnd(Route r, int min_k, int max_k) {
        assert ( min_k >= 2 && max_k < r.tsp.N && min_k<=max_k): "Valores inconsistentes para k";

        int v[] = r.v;
        int size = min_k+ Utils.rd.nextInt(max_k-min_k+1);
        int ini = Utils.rd.nextInt(v.length - size);

        System.arraycopy(v, 0, v_aux, 0, v_aux.length);
        int i = ini;
        for (int j = ini + size; j < v.length; i++, j++)
            v[i] = v_aux[j];
        for (int j = ini; i < v.length; i++, j++)
            v[i] = v_aux[j];
        Utils.rollZero(v);
        r.cost = r.tsp.cost(r.v);

    }

    /** Embaralha os vértices de uma janela de k vértices consecutivos
     * @param r rota a ser perturbada
     * @param min_k menor valor para k , (>=2)
     * @param max_k maior valor para k , (< r.tsp.N)
     */
    public void shufflerWindows(Route r, int min_k, int max_k) {
        assert ( min_k >= 2 && max_k < r.tsp.N && min_k<=max_k): "Valores inconsistentes para k";
        int v[] = r.v;
        int size = min_k+ Utils.rd.nextInt(max_k-min_k+1);
        int ini = Utils.rd.nextInt(v.length - size);

        for (int i = size - 1; i > 0; i--) {
            int x = Utils.rd.nextInt(i);
            int id = ini + i;
            int xd = ini + x;
            //swap
            int aux = v[id];
            v[id] = v[xd];
            v[xd] = aux;
        }
        Utils.rollZero(v);
        r.cost = r.tsp.cost(r.v);

    }


    /** Seleciona k vértice e os move para o início da rota
     * @param r rota a ser perturbada
     * @param min_k menor valor para k , (>=2)
     * @param max_k maior valor para k , (< r.tsp.N)
     */
    public void moveRandomToBegin(Route r, int min_k, int max_k) {
        assert ( min_k >= 2 && max_k < r.tsp.N && min_k<=max_k): "Valores inconsistentes para k";
        int v[] = r.v;
        int size = min_k+ Utils.rd.nextInt(max_k-min_k+1);

        for (int i = 0; i < v.length; i++)
            v_aux[i] = i;
        Utils.shuffler(v_aux);

        lista.clear();
        for (int i = 0; i < size; i++) {
            lista.add(v[v_aux[i]]);
            v[v_aux[i]] = -1;
        }
        for (int i = 0, j = 0; i < v.length; i++) {
            if (v[i] != -1) {
                v_aux[j] = v[i];
                j++;
            }
        }
        //colocar no início
        for (int i = 0; i < size; i++)
            v[i] = lista.get(i);
        for (int i = size, j = 0; i < v.length; i++, j++)
            v[i] = v_aux[j];

        r.cost = r.tsp.cost(v);

    }
}
