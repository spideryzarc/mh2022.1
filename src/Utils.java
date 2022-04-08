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

}
