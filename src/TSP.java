import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Traveler Salesman Problem
 */
public class TSP {
    public final int N;
    public final double c[][];
    public final ArrayList<Point2D.Double> pontos = new ArrayList<>();

    public TSP(int n) {
        N = n;
        c = new double[N][N];
    }

    /**
     * Gera uma instância aleatória
     */
    public void randomize() {
        pontos.clear();
        for (int i = 0; i < N; i++)
            pontos.add(new Point2D.Double(Utils.rd.nextInt(1000), Utils.rd.nextInt(1000)));

        for (int i = 0; i < N; i++)
            for (int j = 0; j < i; j++)
                c[i][j] = c[j][i] = pontos.get(i).distance(pontos.get(j));
    }

    @Override
    public String toString() {
        return "TSP{" +
                "N=" + N +
                ", pontos=" + pontos +
                '}';
    }

    public double cost(int[] v) {
        double d = c[v[v.length - 1]][v[0]];
        for (int i = 1; i < v.length; i++) {
            d += c[v[i - 1]][v[i]];
        }
        return d;
    }
}