public class Disturbances {
    private int[] aux;

    public Disturbances(TSP tsp) {
        this.aux = new int[tsp.N];
    }

    private void shake0(Route r, int max_k) {
        int v[] = r.v;
        System.arraycopy(v, 0, aux, 0, aux.length);
        int size = Math.min(2 + Utils.rd.nextInt(v.length / 10), max_k);
        int ini = Utils.rd.nextInt(v.length - size);

        int i = ini;
        for (int j = ini + size; j < v.length; i++, j++)
            v[i] = aux[j];
        for (int j = ini; i < v.length; i++, j++)
            v[i] = aux[j];

        r.cost = r.tsp.cost(r.v);

    }

    private void shake3(Route r) {
        int v[] = r.v;
        int x = 1 + Utils.rd.nextInt(v.length - 1);
        for (int i = 0; i < v.length; i++) {
            v[i] = (v[i] + x) % v.length;
        }
        r.cost = r.tsp.cost(r.v);
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

        r.cost = r.tsp.cost(r.v);

    }
}
