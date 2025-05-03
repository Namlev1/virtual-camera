package src;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;

import java.awt.*;

@Data
public class Renderer {
    private Camera camera;
    private java.awt.Graphics graphics;

    public Renderer(Camera camera, java.awt.Graphics graphics) {
        this.camera = camera;
        this.graphics = graphics;
    }

    public void drawShape(Shape shape) {
        if (shape instanceof Face) {
            Face face = (Face) shape;
            fillFace(face);
        }
        
        for (Edge edge : shape.getEdges()) {
            drawEdge(edge);
        }
    }
    
    private void fillFace(Face face) {
        // Wierzchołki ściany
        Edge[] edges = face.getEdges().toArray(new Edge[0]);

        // Rzutowanie punktów na płaszczyznę
        int[] xPoints = new int[4];
        int[] yPoints = new int[4];

        for (int i = 0; i < 4; i++) {
            SimpleMatrix point = convertToHomogeneousCoordinates(edges[i].getStart());
            SimpleMatrix pointView = camera.getViewMatrix().mult(point);

            if (pointView.get(2) <= 0) {
                return;
            }

            SimpleMatrix pointClip = camera.getProjectionMatrix().mult(pointView);
            float xNdc = (float) (pointClip.get(0) / pointClip.get(3));
            float yNdc = (float) (pointClip.get(1) / pointClip.get(3));

            int screenWidth = camera.getWIDTH();
            int screenHeight = camera.getHEIGHT();
            xPoints[i] = (int) (screenWidth / 2 + xNdc * screenHeight / 2);
            yPoints[i] = (int) (screenHeight / 2 - yNdc * screenHeight / 2);
        }

        // Kolorowanie
        Color oldColor = graphics.getColor();
        graphics.setColor(face.getColor());
        
        graphics.fillPolygon(xPoints, yPoints, 4);

        graphics.setColor(oldColor);
    }

    private void drawEdge(Edge edge) {
        SimpleMatrix start = convertToHomogeneousCoordinates(edge.getStart());
        SimpleMatrix end = convertToHomogeneousCoordinates(edge.getEnd());

        // Macierz widoku
        SimpleMatrix startView = camera.getViewMatrix().mult(start);
        SimpleMatrix endView = camera.getViewMatrix().mult(end);

        // Sprawdzenie, czy punkty są przed kamerą (z > 0 w przestrzeni kamery)
        if (startView.get(2) <= 0 || endView.get(2) <= 0) {
            return; // Punkt jest za kamerą, nie rysujemy
        }

        // Macierz projekcji
        SimpleMatrix startClip = camera.getProjectionMatrix().mult(startView);
        SimpleMatrix endClip = camera.getProjectionMatrix().mult(endView);

        float startXNdc = (float) (startClip.get(0) / startClip.get(3));
        float startYNdc = (float) (startClip.get(1) / startClip.get(3));
        float endXNdc = (float) (endClip.get(0) / endClip.get(3));
        float endYNdc = (float) (endClip.get(1) / endClip.get(3));

        int screenWidth = camera.getWIDTH();
        int screenHeight = camera.getHEIGHT();

        int startScreenX = (int) (screenWidth / 2 + startXNdc * screenHeight / 2);
        int startScreenY = (int) (screenHeight / 2 - startYNdc * screenHeight / 2);
        int endScreenX = (int) (screenWidth / 2 + endXNdc * screenHeight / 2);
        int endScreenY = (int) (screenHeight / 2 - endYNdc * screenHeight / 2);

        graphics.drawLine(startScreenX, startScreenY, endScreenX, endScreenY);
    }
    
    private SimpleMatrix convertToHomogeneousCoordinates(SimpleMatrix point) {
        SimpleMatrix homogeneous = new SimpleMatrix(4, 1);

        homogeneous.set(0, 0, point.get(0));
        homogeneous.set(1, 0, point.get(1));
        homogeneous.set(2, 0, point.get(2));

        homogeneous.set(3, 0, 1.0);

        return homogeneous;
    }
}