package src;

import java.util.ArrayList;
import java.util.List;

/**
 * Klasa abstrakcyjna reprezentująca kształt 3D
 */
public abstract class Shape {
    protected List<Edge> edges;

    /**
     * Konstruktor kształtu
     */
    public Shape() {
        this.edges = new ArrayList<>();
    }

    /**
     * Zwraca listę krawędzi kształtu
     * @return Lista krawędzi
     */
    public List<Edge> getEdges() {
        return edges;
    }

    /**
     * Dodaje krawędź do kształtu
     * @param edge Krawędź do dodania
     */
    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    /**
     * Usuwa wszystkie krawędzie kształtu
     */
    public void clearEdges() {
        edges.clear();
    }
}