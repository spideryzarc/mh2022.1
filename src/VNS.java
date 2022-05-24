import java.util.ArrayList;

/**
 * Variable Neighborhood Search
 */
public class VNS implements Solver {
    final TSP tsp;

    /**
     * número de iterações
     */
    final int ite;


    /**
     * tempo da última execução em milissegundos
     */
    private int runTime;

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
        return "VNS{" +
                "ite=" + ite +
                ", runTime=" + runTime +
                ", bestSol=" + bestSol.cost +
                '}';
    }

    public VNS(TSP tsp, int ite) {
        this.ite = ite;
        this.tsp = tsp;
        aux = new int[tsp.N];
    }

    private int[] aux;

    private void shake0(Route r) {
        int v[] = r.v;
        System.arraycopy(v, 0, aux, 0, aux.length);
        int size = Math.min(2 + Utils.rd.nextInt(v.length / 10), 10);//2 + Utils.rd.nextInt(v.length / 2);
        int ini = Utils.rd.nextInt(v.length - size);

        int i = ini;
        for (int j = ini + size; j < v.length; i++, j++)
            v[i] = aux[j];
        for (int j = ini; i < v.length; i++, j++)
            v[i] = aux[j];

        r.cost = tsp.cost(r.v);

    }

    private void shake3(Route r) {
        int v[] = r.v;
        int x = 1 + Utils.rd.nextInt(v.length - 1);
        for (int i = 0; i < v.length; i++) {
            v[i] = (v[i] + x) % v.length;
        }
        r.cost = tsp.cost(r.v);
    }

    private void shake2(Route r) {
        int v[] = r.v;
        int size = 2 + Utils.rd.nextInt(v.length / 10);
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

        r.cost = tsp.cost(r.v);

    }

    public void run() {
        long t = System.currentTimeMillis();
        Route currentSol = new Route(tsp);
        currentSol.defaultSolConstructor();
        VND vnd = new VND(tsp);
        vnd.run(currentSol);
        bestSol = new Route(tsp);
        bestSol.copy(currentSol);

        int cont_stuck = 0;// número de iterações desde o último melhoramento
        final int limite = ite / 10; // máximo de cont_stuck antes de trocar a vizinhança
        int neigh = 0; // vizinhança da pertubação atual
        for (int i = 0; i < ite; i++, cont_stuck++) {
            switch (neigh) {
                case 0:
                    shake1(currentSol);
                    break;
                case 1:
                    shake2(currentSol);
                    break;
//                case 2:
//                    shake3(currentSol);
//                    break;
                default:
                    shake0(currentSol);
            }
            vnd.run(currentSol);
            if (currentSol.cost < bestSol.cost - Utils.EPS) {
                bestSol.copy(currentSol);
                System.out.println(i + " VNS " + "neigh " + neigh + " - " + bestSol.cost);
                assert Utils.equals(bestSol.cost, tsp.cost(bestSol.v)) : "variável 'cost' está inconsistente";
                cont_stuck = 0;
                neigh = 0;
//                View.plot(bestSol, "plot.csv");
            }
            if (cont_stuck >= limite) {
                neigh++;
                cont_stuck = 0;
                currentSol.copy(bestSol);
            }
        }
        runTime = (int) (System.currentTimeMillis() - t);
    }

    private void shake1(Route currentSol) {
        int v[] = currentSol.v;
        int k = Math.min(2 + Utils.rd.nextInt(v.length / 10), 10);
//        int k = Utils.rd.nextInt(v.length);
        for (int i = 0; i < v.length; i++)
            aux[i] = i;
        Utils.shuffler(aux);
        ArrayList<Integer> lista = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            lista.add(v[aux[i]]);
            v[aux[i]] = -1;
        }
        for (int i = 0, j = 0; i < v.length; i++) {
            if (v[i] != -1) {
                aux[j] = v[i];
                j++;
            }
        }
//colocar no final
//        for (int i = 0; i < v.length - k; i++)
//            v[i] = aux[i];
//        for (int i = v.length - k, j = 0; i < v.length; i++, j++) {
//            v[i] = lista.get(j);
//        }
//colocar no início
        for (int i = 0; i < k; i++)
            v[i] = lista.get(i);
        for (int i = k, j = 0; i < v.length; i++, j++) {
            v[i] = aux[j];
        }
        currentSol.cost = tsp.cost(v);
//        View.plot(currentSol, "plot.csv");
    }
}
