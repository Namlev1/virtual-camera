package v3;

public class Edge {
    private Point3D start;
    private Point3D end;

    public Edge(Point3D start, Point3D end) {
        this.start = start;
        this.end = end;
    }

    public Point3D getStart() {
        return start;
    }

    public Point3D getEnd() {
        return end;
    }

    // Metoda do tworzenia kopii krawędzi
    public Edge copy() {
        return new Edge(start.copy(), end.copy());
    }

    @Override
    public String toString() {
        return "Edge{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}