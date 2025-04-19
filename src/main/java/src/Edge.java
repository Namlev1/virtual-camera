package src;

import org.ejml.simple.SimpleMatrix;

/**
 * Klasa reprezentująca krawędź w przestrzeni 3D
 */
public class Edge {
    private SimpleMatrix start;
    private SimpleMatrix end;

    /**
     * Konstruktor krawędzi
     * @param start Punkt początkowy krawędzi (3D)
     * @param end Punkt końcowy krawędzi (3D)
     */
    public Edge(SimpleMatrix start, SimpleMatrix end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Konstruktor krawędzi ze współrzędnych
     * @param x1 Współrzędna x punktu początkowego
     * @param y1 Współrzędna y punktu początkowego
     * @param z1 Współrzędna z punktu początkowego
     * @param x2 Współrzędna x punktu końcowego
     * @param y2 Współrzędna y punktu końcowego
     * @param z2 Współrzędna z punktu końcowego
     */
    public Edge(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.start = new SimpleMatrix(3, 1, true, new float[]{x1, y1, z1});
        this.end = new SimpleMatrix(3, 1, true, new float[]{x2, y2, z2});
    }

    /**
     * Zwraca punkt początkowy krawędzi
     * @return Punkt początkowy
     */
    public SimpleMatrix getStart() {
        return start;
    }

    /**
     * Zwraca punkt końcowy krawędzi
     * @return Punkt końcowy
     */
    public SimpleMatrix getEnd() {
        return end;
    }

    /**
     * Ustawia punkt początkowy krawędzi
     * @param start Nowy punkt początkowy
     */
    public void setStart(SimpleMatrix start) {
        this.start = start;
    }

    /**
     * Ustawia punkt końcowy krawędzi
     * @param end Nowy punkt końcowy
     */
    public void setEnd(SimpleMatrix end) {
        this.end = end;
    }
}