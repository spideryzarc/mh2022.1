import jsvg.*;

import java.awt.geom.Point2D;
import java.io.IOException;

public class View {
    public static void plot(Route rt, String outFile) {
        Jsvg svg = new Jsvg();
        svg.setImageSize(800, 800);

        double minX, minY, maxX, maxY;

        maxX = minX = rt.tsp.pontos.get(0).x;
        maxY = minY = rt.tsp.pontos.get(0).y;
        for (Point2D.Double s : rt.tsp.pontos) {
            if (minX > s.x)
                minX = s.x;
            if (minY > s.y)
                minY = s.y;
            if (maxX < s.x)
                maxX = s.x;
            if (maxY < s.y)
                maxY = s.y;
        }

        double r = Math.max(maxX - minX, maxY - minY) / 200;

        svg.vbXmin = (int) (minX - r);
        svg.vbYmin = (int) (minY - r);
        svg.vbWidth = (int) (maxX - minX + 3 * r);
        svg.vbHeight = (int) (maxY - minY + 3 * r);
        {
            Point2D.Double a = rt.tsp.pontos.get(rt.v[rt.v.length-1]);
            Point2D.Double b = rt.tsp.pontos.get(rt.v[0]);
            svg.add(new Line(a.x, a.y, b.x, b.y, ""));
            for (int i = 1; i < rt.tsp.N; i++) {
                a = rt.tsp.pontos.get(rt.v[i - 1]);
                b = rt.tsp.pontos.get(rt.v[i]);
                svg.add(new Line(a.x, a.y, b.x, b.y, ""));
            }
        }
        int i = 0;
        for (Point2D.Double s : rt.tsp.pontos) {
            svg.add(new Circle(s.x, s.y, r, ""));
//            svg.add(new Text(s.x, s.y, "", "" + i));
            i++;
        }

        try {
            svg.add(new Style("tsp.css"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            svg.save(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
