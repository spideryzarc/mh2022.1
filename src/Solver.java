public interface Solver {
    /**
     * executa o método
     */
    void run();

    /**
     * @return melhor solução encontrada
     */
    Route getBestSol();

    /**
     * @return tempo da última execução em milissegundos
     */
    int getRunTime();
}
