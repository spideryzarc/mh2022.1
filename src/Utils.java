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

    /**
     * Embaralha um vetor
     * @param v vetor que será embaralhado
     */
    public static void shuffler(int v[]) {
        for (int i = v.length - 1; i > 0; i--) {
            int x = Utils.rd.nextInt(i);
            //swap
            int aux = v[i];
            v[i] = v[x];
            v[x] = aux;
        }
    }

    /**
     * Altera um vetor v de forma circula de modo que o zero seja o valor de v[0]
     * @param v vetor com um zero
     * @throws IllegalArgumentException se o vetor não possui um zero
     */
    public static void rollZero(int v[]) {
        if (v[0] == 0)
            return;
        int p = -1;
        for (int i = 1; i < v.length; i++)
            if (v[i] == 0) {
                p = i;
                break;
            }

        if (p == -1) {
            throw new IllegalArgumentException("O vetor não possui um zero");
        }
        int a[] = v.clone();
        for (int i = 0; i < v.length; i++)
            v[i] = a[(i + p) % v.length];

    }

    public static int roulette(double weight[]) {
        double acc[] = new double[weight.length];
        acc[0] = weight[0];
        for (int i = 1; i < acc.length; i++)
            acc[i] = acc[i - 1] + weight[i];
        double x = rd.nextDouble() * acc[acc.length - 1];
        if (x < acc[0])
            return 0;
        for (int i = 1; i < acc.length; i++)
            if (x < acc[i]) // implícito && x >= acc[i - 1]
                return i;

        return -1;
    }

    public static final double EPS = 0.001;

    public static boolean equals(double a, double b) {
        return Math.abs(a - b) < EPS;
    }

}
