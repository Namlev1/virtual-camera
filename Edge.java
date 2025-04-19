package v3;

public record Edge(Point3D start, Point3D end) {

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