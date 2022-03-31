public class Main {
    public static void main(String args[]){

        Queens q = new Queens(1000);
//        q.random(1000);
        long t = System.currentTimeMillis();
//        q.forcaBruta();
        q.busca(1000);
        t = System.currentTimeMillis()-t;
        q.print();
        System.out.println(q.fo());
        System.out.println("tempo: "+t);



    }
}
