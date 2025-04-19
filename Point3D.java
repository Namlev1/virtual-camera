package v3;

public record Point3D(double x, double y, double z) {

    public Point3D copy() {
        return new Point3D(x, y, z);
    }

    @Override
    public String toString() {
        return "Point3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}