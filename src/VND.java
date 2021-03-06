/**
 * Variable Neighborhood Descendant
 */
public class VND {
    final TSP tsp;

    public VND(TSP tsp) {
        this.tsp = tsp;
    }

    public boolean run(Route r) {
        boolean chg = false;
        boolean imp = true;
        while (imp) {
            imp = false;
            if (!imp)
                imp = r.swap_2_first_imp();
            if (!imp)
                imp = r.opt_2_first_imp();
            if (!imp)
                imp = r.replace_first_imp();
            if(imp)
                chg = true;
        }
        return chg;
    }

    public void run(Neigh... n) {
        boolean imp = true;
        while (imp) {
            imp = false;
            for (int i = 0; i < n.length; i++) {
                imp = n[i].run();
                if (imp)
                    break;
            }

        }
    }

    public interface Neigh {
        boolean run();
    }
}
