package v3;

import java.util.ArrayList;
import java.util.List;

public class Cube {
    private final List<Edge> edges;

    public Cube(double size, double x, double y, double z) {
        double halfSize = size / 2.0;

        // Inicjalizacja wierzchołków sześcianu
        List<Point3D> vertices = new ArrayList<>();

        // Przód
        vertices.add(new Point3D(-halfSize + x, -halfSize + y, halfSize + z));  // 0: lewy dolny przedni
        vertices.add(new Point3D(halfSize + x, -halfSize + y, halfSize + z));   // 1: prawy dolny przedni
        vertices.add(new Point3D(halfSize + x, halfSize + y, halfSize + z));    // 2: prawy górny przedni
        vertices.add(new Point3D(-halfSize + x, halfSize + y, halfSize + z));   // 3: lewy górny przedni

        // Tył
        vertices.add(new Point3D(-halfSize + x, -halfSize + y, -halfSize + z)); // 4: lewy dolny tylny
        vertices.add(new Point3D(halfSize + x, -halfSize + y, -halfSize + z));  // 5: prawy dolny tylny
        vertices.add(new Point3D(halfSize + x, halfSize + y, -halfSize + z));   // 6: prawy górny tylny
        vertices.add(new Point3D(-halfSize + x, halfSize + y, -halfSize + z));  // 7: lewy górny tylny

        // Inicjalizacja krawędzi sześcianu
        edges = new ArrayList<>();

        // Krawędzie przedniej ściany
        edges.add(new Edge(vertices.get(0), vertices.get(1)));
        edges.add(new Edge(vertices.get(1), vertices.get(2)));
        edges.add(new Edge(vertices.get(2), vertices.get(3)));
        edges.add(new Edge(vertices.get(3), vertices.get(0)));

        // Krawędzie tylnej ściany
        edges.add(new Edge(vertices.get(4), vertices.get(5)));
        edges.add(new Edge(vertices.get(5), vertices.get(6)));
        edges.add(new Edge(vertices.get(6), vertices.get(7)));
        edges.add(new Edge(vertices.get(7), vertices.get(4)));

        // Krawędzie łączące przód z tyłem
        edges.add(new Edge(vertices.get(0), vertices.get(4)));
        edges.add(new Edge(vertices.get(1), vertices.get(5)));
        edges.add(new Edge(vertices.get(2), vertices.get(6)));
        edges.add(new Edge(vertices.get(3), vertices.get(7)));
    }

    public List<Edge> getEdges() {
        return edges;
    }
}