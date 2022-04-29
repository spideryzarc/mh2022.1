import java.util.Random;

public class Utils {
    public static Random rd = new Random(7);

    /**
     * troca o valor de duas posições do vetor
     *
     * @param v vetor de inteiros
     * @param i posição do vetor
     * @param j posição do vetor
     */
    public static void swap(int v[], int i, int j) {
        int aux = v[i];
        v[i] = v[j];
        v[j] = aux;
    }

    public static void shuffler(int v[]) {
        for (int i = v.length - 1; i > 0; i--) {
            int x = Utils.rd.nextInt(i);
            //swap
            int aux = v[i];
            v[i] = v[x];
            v[x] = aux;
        }
    }

    public static void roll(int v[]) {
        if (v[0] == 0)
            return;
        int p = -1;
        for (int i = 1; i < v.length; i++)
            if (v[i] == 0) {
                p = i;
                break;
            }
        if (p == -1) {
            System.err.println("OPS! Roll de um vetor sem zero");
            return;
        }
        int a[] = v.clone();
        for (int i = 0; i < v.length; i++)
            v[i] = a[(i + p) % v.length];

    }

    public static final double EPS = 0.001;
    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < EPS;
    }

}
