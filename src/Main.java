public class Main {
    public static void main(String args[]) {

        Knapsack k = new Knapsack(13,
                new int[]{4, 2, 2, 1, 10}, //peso
                new int[]{12, 2, 1, 1, 4}); //prêmio

//        System.out.println(k.recursive_solve());
        System.out.println(k.DP_solve());


//        Queens q = new Queens(30);
////        q.random(1000);
//        long t = System.currentTimeMillis();
//        q.forcaBruta();
////        q.busca(1000);
//        t = System.currentTimeMillis()-t;
//        q.print();
//        System.out.println(q.fo());
//        System.out.println("tempo: "+t);


    }
}
