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

        // Rzutowanie punktów
        SimpleMatrix startClip = camera.projectPoint(start);
        SimpleMatrix endClip = camera.projectPoint(end);

        // Sprawdzenie, czy punkty są widoczne (w przestrzeni widzenia)
        boolean startVisible = startClip.get(3) > 0;
        boolean endVisible = endClip.get(3) > 0;

        // Jeśli oba punkty są widoczne
        if (startVisible && endVisible) {
            // Normalizacja współrzędnych podziałem przez w (perspektywa)
            float startXNdc = (float) (startClip.get(0) / startClip.get(3));
            float startYNdc = (float) (startClip.get(1) / startClip.get(3));
            float endXNdc = (float) (endClip.get(0) / endClip.get(3));
            float endYNdc = (float) (endClip.get(1) / endClip.get(3));

            // Przekształcenie na współrzędne ekranu
            int startScreenX = (int) ((startXNdc + 1.0f) * camera.getWidth() / 2.0f);
            int startScreenY = (int) ((1.0f - startYNdc) * camera.getHeight() / 2.0f);
            int endScreenX = (int) ((endXNdc + 1.0f) * camera.getWidth() / 2.0f);
            int endScreenY = (int) ((1.0f - endYNdc) * camera.getHeight() / 2.0f);

            // Rysowanie linii na ekranie
            graphics.drawLine(startScreenX, startScreenY, endScreenX, endScreenY);
        }
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