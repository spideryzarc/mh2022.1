import java.util.ArrayList;

public class Knapsack {
    int N;
    int C;
    int w[];
    int p[];

    public Knapsack(int n) {
        N = n;
        w = new int[n];
        p = new int[n];
    }

    private Sol S(int C, ArrayList<Integer> lista){
        int item = lista.remove(0);
        //o item vai
        Sol a = S(C-w[item],lista);
        a.cost+= p[item];
        a.lista.add(item);

        //o item não vai
        Sol b = S(C,lista);
        lista.add(0,item);

        if(a.cost > b.cost)
            return a;
        else
            return b;

    }

    public ArrayList<Integer> recursive_solve(){
        ArrayList<Integer> lista = new ArrayList<>();



        return lista;
    }

    class Sol{
        ArrayList<Integer> lista;
        int cost;
    }
}
