import java.util.Arrays;

public class Queens {
    /**
     * número de de rainhas
     */
    int N;
    /**
     * pos[i] - em qual coluna está a rainha da linha i
     */
    int pos[];

    /**
     * @param n - número de rainhas a serem posicionadas
     */
    public Queens(int n) {
        N = n;
        pos = new int[n];
    }

    /**
     * @return número de conflitos no tabuleiro
     */
    public int fo() {
        int c = 0;
        //colunas
        for (int i = 0; i < N; i++)
            for (int j = i + 1; j < N; j++)
                if (pos[i] == pos[j]) {
                    c++;
                    break;
                }
        //diagonal \
        for (int i = 0; i < N; i++)
            for (int j = i + 1; j < N; j++)
                if (i - pos[i] == j - pos[j]) {
                    c++;
                    break;
                }
        //diagonal /
        for (int i = 0; i < N; i++)
            for (int j = i + 1; j < N; j++)
                if (i + pos[i] == j + pos[j]) {
                    c++;
                    break;
                }
        return c;
    }

    /**
     * imprime a disposição das rainhas no tabuleiro
     */
    public void print() {
        for (int i = 0; i < N; i++) {
            System.out.print("|");
            for (int j = 0; j < N; j++) {
                if (pos[i] == j)
                    System.out.print(" x |");
                else
                    System.out.print("   |");
            }
            System.out.println();
        }
        System.out.println(Arrays.toString(pos));
    }

    /**
     * Gera permutações aleatórias e mantém a melhor (menos conflitos)
     * no * campo 'pos'.
     *
     * @param tries - número de tentativas*
     */
    public void random(int tries) {
        int best = Integer.MAX_VALUE;
        int bestPos[] = new int[N];
        for (int i = 0; i < tries; i++) {
            randomize();
            int x = fo();
            if (x < best) {
                best = x;
                System.arraycopy(pos, 0, bestPos, 0, pos.length);
                if (x == 0) {
                    System.out.println(i);
                    break;
                }
            }
        }
        if (best > 0) {
            System.arraycopy(bestPos, 0, pos, 0, pos.length);
        }
    }

    /**
     * Gera uma permutação eleatória para o 'pos'
     */
    private void randomize() {
        for (int i = 0; i < N; i++)
            pos[i] = i;
        for (int i = N - 1; i > 0; i--) {
            int x = Utils.rd.nextInt(i);
            //swap
            int aux = pos[i];
            pos[i] = pos[x];
            pos[x] = aux;
        }

    }

    /**
     * @param k linha do tabuleiro (0 <= k < N)
     * @return true - se e somente se não há conflitos
     * entre rainha na linha k e as anteriores
     */
    private boolean temConflitoAte(int k) {
        for (int i = 0; i < k; i++) {
            //colunas
            if (pos[k] == pos[i])
                return true;
            //diagonal \
            if (k - pos[k] == i - pos[i])
                return true;
            //diagonal /
            if (k + pos[k] == i + pos[i])
                return true;
        }
        return false;
    }

    /**
     * coloca uma rainha na linha k de forma que não haja conflitos.
     *
     * @param k linha do tabuleiro
     * @return true - se foi possível posicionar a rainha na linha k
     * sem conflitos.
     */
    private boolean coloca(int k) {
        for (int i = 0; i < N; i++) {
            pos[k] = i;
            if (temConflitoAte(k))
                continue;
            else {
//                System.out.println(Arrays.toString(pos));
                if (k == N - 1)
                    return true;
                if (coloca(k + 1))
                    return true;
            }
        }
        return false;
    }

    /**
     * Enumera todas as permutações de 'pos' até encontrar uma que
     * não possua conflitos
     */
    public void forcaBruta() {
        Arrays.fill(pos, -1);
        for (int i = 0; i < N; i++) {
            pos[0] = i;
            if (coloca(1))
                break;
        }
    }

    /**
     * Random  Multistart
     *
     * @param tries número de pontos de partida aleatórios
     */
    public void RMS(int tries) {
        int best_cost = Integer.MAX_VALUE;
        int best_pos[] = new int[N];
        for (int k = 0; k < tries; k++) {
            random(1);
            int current_cost = fo();
            if (current_cost == 0)
                return;
            for (int i = 0; i < N; i++)
                for (int j = 0; j < i; j++) {
                    Utils.swap(pos, i, j);
                    int pertubed_cost = fo();
                    if (pertubed_cost < current_cost) {
                        current_cost = pertubed_cost;
                        if (current_cost == 0)
                            return;
                        if (current_cost < best_cost) {
                            best_cost = current_cost;
                            System.out.println(k + "  " + best_cost);
                            System.arraycopy(pos, 0, best_pos, 0, pos.length);
                        }

                    } else
                        Utils.swap(pos, i, j);

                }
        }
        System.arraycopy(best_pos, 0, pos, 0, pos.length);
    }
}
