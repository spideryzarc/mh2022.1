public class Main {
    public static void main(String args[]){

        Queens q = new Queens(8);
        q.random(1000);
        q.print();
        System.out.println(q.fo());


    }
}
