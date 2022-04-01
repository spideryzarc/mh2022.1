import java.util.Random;

public class Utils {
    public static Random rd = new Random(7);

    /**troca o valor de duas posições do vetor
     * @param v vetor de inteiros
     * @param i posição do vetor
     * @param j posição do vetor*/
    public static void swap(int v[], int i, int j) {
        int aux = v[i];
        v[i] = v[j];
        v[j] = aux;
    }
}
