package v3;

import java.awt.*;
import java.util.List;

public class Renderer {
    private final Camera camera;
    private final int screenWidth;
    private final int screenHeight;

    public Renderer(Camera camera, int screenWidth, int screenHeight) {
        this.camera = camera;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    // Metoda pomocnicza do mnożenia wektora przez macierz 4x4
    private double[] multiplyMatrixVector(double[][] matrix, double[] vector) {
        double[] result = new double[4];

        for (int i = 0; i < 4; i++) {
            result[i] = 0;
            for (int j = 0; j < 4; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }

        return result;
    }

    // Metoda pomocnicza do przekształcenia punktu 3D na 2D
    private java.awt.Point projectPoint(Point3D point3D) {
        // Konwersja Point3D na wektor jednorodny
        double[] pointVector = {point3D.x(), point3D.y(), point3D.z(), 1.0};

        // Pobranie macierzy widoku i rzutowania
        double[][] viewMatrix = camera.getViewMatrix();
        double[][] projectionMatrix = camera.getProjectionMatrix();

        // Zastosowanie macierzy widoku
        double[] viewVector = multiplyMatrixVector(viewMatrix, pointVector);

        // Sprawdzenie, czy punkt jest przed kamerą (z > 0 w przestrzeni kamery)
        if (viewVector[2] <= 0) {
            // Punkt jest za kamerą, zwracamy punkt poza ekranem
            return new java.awt.Point(-1000, -1000);
        }

        // Zastosowanie macierzy rzutowania
        double[] projectedVector = multiplyMatrixVector(projectionMatrix, viewVector);

        // Normalizacja współrzędnych jednorodnych
        if (projectedVector[3] != 0) {
            projectedVector[0] /= projectedVector[3];
            projectedVector[1] /= projectedVector[3];
        }

        // Przekształcenie na współrzędne ekranu
        // Środek ekranu ma być w środku okna
        int screenX = (int) (screenWidth / 2 + projectedVector[0] * screenHeight / 2);
        int screenY = (int) (screenHeight / 2 - projectedVector[1] * screenHeight / 2);

        return new java.awt.Point(screenX, screenY);
    }

    public void renderEdge(Graphics g, Edge edge) {
        java.awt.Point startPoint = projectPoint(edge.start());
        java.awt.Point endPoint = projectPoint(edge.end());

        // Sprawdzenie, czy punkty są widoczne (nie są poza ekranem)
        if (startPoint.x >= -500 && startPoint.y >= -500 &&
                endPoint.x >= -500 && endPoint.y >= -500) {
            g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }
    }

    public void renderCube(Graphics g, Cube cube) {
        List<Edge> edges = cube.getEdges();

        g.setColor(Color.BLACK);

        for (Edge edge : edges) {
            renderEdge(g, edge);
        }
    }
}