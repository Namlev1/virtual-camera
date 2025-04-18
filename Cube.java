package v3;

import java.util.ArrayList;
import java.util.List;

public class Cube {
    private List<Point3D> vertices;
    private List<Edge> edges;

    // Tworzymy sześcian o środku w punkcie (0,0,0) i o określonej wielkości
    public Cube(double size) {
        double halfSize = size / 2.0;

        // Inicjalizacja wierzchołków sześcianu
        vertices = new ArrayList<>();

        // Przód
        vertices.add(new Point3D(-halfSize, -halfSize, halfSize));  // 0: lewy dolny przedni
        vertices.add(new Point3D(halfSize, -halfSize, halfSize));   // 1: prawy dolny przedni
        vertices.add(new Point3D(halfSize, halfSize, halfSize));    // 2: prawy górny przedni
        vertices.add(new Point3D(-halfSize, halfSize, halfSize));   // 3: lewy górny przedni

        // Tył
        vertices.add(new Point3D(-halfSize, -halfSize, -halfSize)); // 4: lewy dolny tylny
        vertices.add(new Point3D(halfSize, -halfSize, -halfSize));  // 5: prawy dolny tylny
        vertices.add(new Point3D(halfSize, halfSize, -halfSize));   // 6: prawy górny tylny
        vertices.add(new Point3D(-halfSize, halfSize, -halfSize));  // 7: lewy górny tylny

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

    public List<Point3D> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}