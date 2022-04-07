import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Problema da Mochila 0-1
 */
public class Knapsack {
    /**
     * número  de itens
     */
    int N;
    /**
     * Capacidade da mochila
     */
    int C;
    /**
     * Peso de cada item
     */
    int w[];
    /**
     * Prêmio de cada item
     */
    int p[];

    /**
     * @param c capacidade
     * @param w peso de cada item
     * @param p prêmio de cada item
     */
    public Knapsack(int c, int w[], int p[]) {
        C = c;
        N = w.length;
        this.w = w;
        this.p = p;
    }

    private Sol S(int C, ArrayList<Integer> lista) {

        if (lista.size() == 1) { //caso trivial -- âncora da recursão
            Sol a = new Sol();
            if (C >= w[lista.get(0)]) { // o item cabe na mochila?
                a.lista.add(lista.get(0));
                a.premioTotal = p[lista.get(0)];
            }
            return a;
        }

        int item = lista.remove(0);
        //o item vai
        Sol a;
        if (C >= w[item]) {
            a = S(C - w[item], lista);
            a.premioTotal += p[item];
            a.lista.add(item);
        } else
            a = new Sol();

        //o item não vai
        Sol b = S(C, lista);

        lista.add(0, item);//refazer a lista original

        if (a.premioTotal > b.premioTotal)
            return a;
        else
            return b;

    }

    /**
     * resolve recursivamente por divisão e conquista
     */
    public Sol recursive_solve() {
        ArrayList<Integer> lista = new ArrayList<>();
        for (int i = 0; i < N; i++)
            lista.add(i);
        return S(C, lista);
    }


    /**
     * resolve por programação dinâmica
     * TA ERRADO!!
     */
    public Sol DP_solve() {
        int m[] = new int[C + 1];
        int x[] = new int[C + 1];
        Arrays.fill(x, -1);
        Integer idx[] = new Integer[N];
        for (int i = 0; i < N; i++)
            idx[i] = i;
        Arrays.sort(idx, Comparator.comparingInt(a -> w[a]));

        for (int i : idx) {
            for (int j = C; j >= w[i]; j--) {
                if (m[j] > m[j - w[i]] + p[i]) {

                } else {
                    m[j] = m[j - w[i]] + p[i];
                    x[j] = i;
                }
            }
        }

//  versão usando uma matrix
//        for (int i = 0; i < m.length; i++) {
//            for (int j = 0; j < m[i].length; j++) {
//                if (i == 0) {
//                    m[i][j] = (j < w[i]) ? 0 : p[i];
//                } else {
//                    m[i][j] = (j < w[i]) ? m[i - 1][j] :
//                            Math.max(m[i - 1][j], m[i - 1][j - w[i]] + p[i]);
//                }
//            }
//        }
        System.out.println(Arrays.toString(m));
        System.out.println(Arrays.toString(x));
        //decodificação da solução
        Sol sol = new Sol();
        int c = C;
        while (c > 0) {
            int item = x[c];
            if (sol.lista.isEmpty()
                    || sol.lista.get(sol.lista.size() - 1) != item) {
                sol.lista.add(item);
                sol.premioTotal += p[item];
                c -= w[item];
            } else
                c--;
        }
        return sol;
    }

    class Sol {
        ArrayList<Integer> lista = new ArrayList<>();
        int premioTotal;

        @Override
        public String toString() {
            return "Sol{" +
                    "premioTotal=" + premioTotal +
                    ",lista=" + lista +
                    '}';
        }
    }
}
