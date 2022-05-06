public interface Solver {
    void run();

    Route getBestSol();

    int getRunTime();
}
