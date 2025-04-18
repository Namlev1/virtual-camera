package v3;

import java.awt.Graphics;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Renderer {
    private Camera camera;
    private int screenWidth;
    private int screenHeight;

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
        // Konwersja Point3D na wektor homogeniczny
        double[] pointVector = {point3D.getX(), point3D.getY(), point3D.getZ(), 1.0};

        // Pobranie macierzy widoku i rzutowania
        double[][] viewMatrix = camera.getViewMatrix();
        double[][] projectionMatrix = camera.getProjectionMatrix();

        // Zastosowanie macierzy widoku
        double[] viewVector = multiplyMatrixVector(viewMatrix, pointVector);

        // Sprawdzenie, czy punkt jest przed kamerą (z > 0 w przestrzeni kamery)
        // W naszym układzie współrzędnych kamera patrzy w kierunku dodatnich wartości Z
        if (viewVector[2] <= 0) {
            // Punkt jest za kamerą, zwracamy null lub punkt poza ekranem
            return new java.awt.Point(-1000, -1000); // Punkt poza ekranem
        }

        // Zastosowanie macierzy rzutowania zgodnej ze slajdem
        double[] projectedVector = multiplyMatrixVector(projectionMatrix, viewVector);

        // Normalizacja współrzędnych homogenicznych
        if (projectedVector[3] != 0) {
            projectedVector[0] /= projectedVector[3];
            projectedVector[1] /= projectedVector[3];
            // Zauważ, że dla macierzy ze slajdu projectedVector[2] będzie zawsze 0
        }

        // Przekształcenie na współrzędne ekranu
        // Środek ekranu ma być w środku okna, więc mapujemy odpowiednio
        int screenX = (int)(screenWidth / 2 + projectedVector[0] * screenHeight / 2);
        int screenY = (int)(screenHeight / 2 - projectedVector[1] * screenHeight / 2);

        return new java.awt.Point(screenX, screenY);
    }

    // Metoda do renderowania krawędzi
    public void renderEdge(Graphics g, Edge edge) {
        java.awt.Point startPoint = projectPoint(edge.getStart());
        java.awt.Point endPoint = projectPoint(edge.getEnd());

        // Sprawdzenie, czy punkty są widoczne (nie są poza ekranem)
        if (startPoint.x >= -500 && startPoint.y >= -500 &&
                endPoint.x >= -500 && endPoint.y >= -500) {
            g.setColor(Color.BLACK);
            g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
        }
    }

    // Metoda do renderowania całego sześcianu
    public void renderCube(Graphics g, Cube cube) {
        List<Edge> edges = cube.getEdges();

        for (Edge edge : edges) {
            renderEdge(g, edge);
        }
    }
}