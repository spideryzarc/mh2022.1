import java.util.Random;

public class Queens {
    int N;
    int pos[];
    Random rd = new Random(7);

    public Queens(int n) {
        N = n;
        pos = new int[n];
    }

    public int fo(){
        int c = 0;
        //colunas
        for (int i = 0; i < N; i++)
            for (int j = i+1; j < N; j++)
                if(pos[i]==pos[j])
                    c++;
        //diagonal \
        for (int i = 0; i < N; i++)
            for (int j = i+1; j < N; j++)
                if(i-pos[i]==j-pos[j])
                    c++;
        //diagonal /
        for (int i = 0; i < N; i++)
            for (int j = i+1; j < N; j++)
                if(i+pos[i]==j+pos[j])
                    c++;
        return c;
    }

    public void print(){
        for (int i = 0; i < N; i++) {
            System.out.print("|");
            for (int j = 0; j < N; j++) {
                if(pos[i]==j)
                    System.out.print(" x |");
                else
                    System.out.print("   |");
            }
            System.out.println();
        }
    }

    public void random(int tries){
        int best = Integer.MAX_VALUE;
        int bestPos[] = new int[N];
        for (int i = 0; i < tries; i++) {
            randomize();
            int x = fo();
            if(x < best){
                best = x;
                if(x==0) {
                    System.out.println(i);
                    break;
                }

            }
        }
    }
    public void randomize(){
        for (int i = 0; i < N; i++)
            pos[i]=i;
        for (int i = N-1; i > 0 ; i--) {
            int x = rd.nextInt(i);
            //swap
            int aux = pos[i];
            pos[i] = pos[x];
            pos[x] = aux;
        }

    }

}
