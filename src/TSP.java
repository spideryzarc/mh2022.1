import jdk.jshell.spi.ExecutionControl;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * Traveler Salesman Problem
 */
public class TSP {
    /**
     * número de cidades
     */
    public final int N;
    /**
     * matriz de custo/distância
     */
    public final double c[][];
    /**
     * lista de coordenadas
     */
    public final ArrayList<Point2D.Double> pontos = new ArrayList<>();

    /**
     * @param n - Número de cidades
     */
    public TSP(int n) {
        N = n;
        c = new double[N][N];
    }

    public TSP(String filepath) throws FileNotFoundException, ExecutionControl.NotImplementedException {
        Locale.setDefault(Locale.US);
        Scanner sc = new Scanner(new File(filepath));
        String str;
        int n = 0;
        String edgeType = "";
        String weightFormat = "";
        do {
            str = sc.nextLine().strip();
            String[] a = str.split(":");
            for (int i = 0; i < a.length; i++)
                a[i] = a[i].strip().toUpperCase();
            if (a[0].equals("DIMENSION")) {
                n = Integer.parseInt(a[1].strip());
            }
            if (a[0].equals("EDGE_WEIGHT_TYPE")) {
                edgeType = a[1];
            }
            if (a[0].equals("EDGE_WEIGHT_FORMAT")) {
                weightFormat = a[1];
            }
//            System.out.println(str);
        } while (!(str.equals("NODE_COORD_SECTION") || str.equals("EDGE_WEIGHT_SECTION")));
        N = n;
        c = new double[N][N];
        if (edgeType.equals("EUC_2D")) {
            do {
                int i = sc.nextInt();
                double x = sc.nextDouble();
                double y = sc.nextDouble();
                pontos.add(new Point2D.Double(x, y));
            } while (sc.hasNextInt());
            sc.close();

            for (int i = 0; i < N; i++)
                for (int j = 0; j < i; j++)
                    c[i][j] = c[j][i] = pontos.get(i).distance(pontos.get(j));
        } else if (edgeType.equals("EXPLICIT")) {
            if (weightFormat.equals("UPPER_ROW")) {
                for (int i = 0; i < n; i++) {
                    for (int j = i + 1; j < n; j++) {
                        double d = sc.nextDouble();
                        c[i][j] = c[j][i] = d;
                    }
                }
            } else {//FULL_MATRIX
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        double d = sc.nextDouble();
                        c[i][j] = d;
                    }
                }
            }

            do {
                str = sc.nextLine();
                if (str.equalsIgnoreCase("DISPLAY_DATA_SECTION"))
                    break;

            } while (sc.hasNext());
            if (str.equalsIgnoreCase("DISPLAY_DATA_SECTION")) {
                do {
                    int i = sc.nextInt();
                    double x = sc.nextDouble();
                    double y = sc.nextDouble();
                    pontos.add(new Point2D.Double(x, y));
                } while (sc.hasNextInt());
            }
            sc.close();
        }else{
//            System.err.println("tipo de aresta não implementado: ");
            throw new ExecutionControl.NotImplementedException("tipo de aresta não implementado: "+edgeType);
        }
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

    /**
     * @param v - uma sequência de cidades
     * @return a soma das distâncias da sequência 'v'
     */
    public final double cost(int[] v) {
        double d = c[v[v.length - 1]][v[0]];
        for (int i = 1; i < v.length; i++) {
            d += c[v[i - 1]][v[i]];
        }
        return d;
    }
}
