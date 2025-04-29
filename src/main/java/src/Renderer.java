package src;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;

@Data
public class Renderer {
    private Camera camera;
    private java.awt.Graphics graphics;

    public Renderer(Camera camera, java.awt.Graphics graphics) {
        this.camera = camera;
        this.graphics = graphics;
    }

    public void drawShape(Shape shape) {
        for (Edge edge : shape.getEdges()) {
            drawEdge(edge);
        }
    }

    private void drawEdge(Edge edge) {
        // Konwersja punktów krawędzi na wektory jednorodne
        SimpleMatrix start = convertToHomogeneousCoordinates(edge.getStart());
        SimpleMatrix end = convertToHomogeneousCoordinates(edge.getEnd());

        // Zastosowanie macierzy widoku
        SimpleMatrix startView = camera.getViewMatrix().mult(start);
        SimpleMatrix endView = camera.getViewMatrix().mult(end);

        // Sprawdzenie, czy punkty są przed kamerą (z > 0 w przestrzeni kamery)
        if (startView.get(2) <= 0 || endView.get(2) <= 0) {
            return; // Punkt jest za kamerą, nie rysujemy
        }

        // Zastosowanie macierzy projekcji
        SimpleMatrix startClip = camera.getProjectionMatrix().mult(startView);
        SimpleMatrix endClip = camera.getProjectionMatrix().mult(endView);

        // Normalizacja współrzędnych podziałem przez w (perspektywa)
        float startXNdc = (float) (startClip.get(0) / startClip.get(3));
        float startYNdc = (float) (startClip.get(1) / startClip.get(3));
        float endXNdc = (float) (endClip.get(0) / endClip.get(3));
        float endYNdc = (float) (endClip.get(1) / endClip.get(3));

        // Przekształcenie na współrzędne ekranu
        // Środek ekranu ma być w środku okna
        int screenWidth = camera.getWIDTH();
        int screenHeight = camera.getHEIGHT();

        int startScreenX = (int) (screenWidth / 2 + startXNdc * screenHeight / 2);
        int startScreenY = (int) (screenHeight / 2 - startYNdc * screenHeight / 2);
        int endScreenX = (int) (screenWidth / 2 + endXNdc * screenHeight / 2);
        int endScreenY = (int) (screenHeight / 2 - endYNdc * screenHeight / 2);

        // Rysowanie linii na ekranie
        graphics.drawLine(startScreenX, startScreenY, endScreenX, endScreenY);
    }
    /**
     * Konwertuje punkt 3D do współrzędnych jednorodnych (4D)
     */
    private SimpleMatrix convertToHomogeneousCoordinates(SimpleMatrix point) {
        SimpleMatrix homogeneous = new SimpleMatrix(4, 1);

        // Przepisanie współrzędnych x, y, z
        homogeneous.set(0, 0, point.get(0));
        homogeneous.set(1, 0, point.get(1));
        homogeneous.set(2, 0, point.get(2));

        // Ustawienie współrzędnej w = 1
        homogeneous.set(3, 0, 1.0);

        return homogeneous;
    }
}