import java.util.Objects;

public class Edge {
    private int i, j;

    public Edge(int i, int j) {
        set(i, j);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return i == edge.i &&
                j == edge.j;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }

    public void set(int i, int j) {
        if (i < j) {
            this.i = i;
            this.j = j;
        } else {
            this.i = j;
            this.j = i;
        }
    }
}
