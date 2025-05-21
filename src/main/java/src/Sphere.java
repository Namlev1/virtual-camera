package src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.ejml.simple.SimpleMatrix;

public class Sphere {
    private List<Face> faces;
    private float radius;
    private float centerX, centerY, centerZ;
    private int latitudeBands, longitudeBands;
    private Color color;

    public Sphere(float centerX, float centerY, float centerZ, float radius,
                  int latitudeBands, int longitudeBands) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.radius = radius;
        this.latitudeBands = latitudeBands;
        this.longitudeBands = longitudeBands;
        this.faces = new ArrayList<>();

        // Można użyć stałego koloru lub losować kolory dla każdej ściany
        this.color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());

        generateSphere();
    }

    private void generateSphere() {
        // Tablice do przechowywania współrzędnych wierzchołków kuli
        SimpleMatrix[][] vertices = new SimpleMatrix[latitudeBands + 1][longitudeBands + 1];

        // Generowanie wierzchołków kuli
        for (int lat = 0; lat <= latitudeBands; lat++) {
            // Kąt theta - od 0 do PI (od bieguna do bieguna)
            float theta = (float) (lat * Math.PI / latitudeBands);
            float sinTheta = (float) Math.sin(theta);
            float cosTheta = (float) Math.cos(theta);

            for (int lon = 0; lon <= longitudeBands; lon++) {
                // Kąt phi - od 0 do 2*PI (dookoła kuli)
                float phi = (float) (lon * 2 * Math.PI / longitudeBands);
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);

                // Współrzędne x, y, z na powierzchni kuli o promieniu 1
                float x = cosPhi * sinTheta;
                float y = cosTheta;
                float z = sinPhi * sinTheta;

                // Przeskalowanie do odpowiedniego promienia i przesunięcie do środka kuli
                float scaledX = centerX + radius * x;
                float scaledY = centerY + radius * y;
                float scaledZ = centerZ + radius * z;

                // Zapisanie wierzchołka
                vertices[lat][lon] = new SimpleMatrix(3, 1, true, new float[]{scaledX, scaledY, scaledZ});
            }
        }

        // Generowanie ścian (trójkątów) kuli
        for (int lat = 0; lat < latitudeBands; lat++) {
            for (int lon = 0; lon < longitudeBands; lon++) {
                // Wierzchołki tworzące kwadrat na powierzchni kuli
                SimpleMatrix v1 = vertices[lat][lon];
                SimpleMatrix v2 = vertices[lat + 1][lon];
                SimpleMatrix v3 = vertices[lat + 1][lon + 1];
                SimpleMatrix v4 = vertices[lat][lon + 1];

                // Tworzymy dwa trójkąty z tego kwadratu
                createTriangleFace(v1, v2, v3);
                createTriangleFace(v1, v3, v4);
            }
        }
    }

    private void createTriangleFace(SimpleMatrix v1, SimpleMatrix v2, SimpleMatrix v3) {
        // Tworzymy ścianę (trójkąt) z trzech wierzchołków
        float[] vertices = new float[12]; // 12 = 3 wierzchołki * 3 współrzędne + 3 dla powtórzonego wierzchołka

        // Pierwszy wierzchołek
        vertices[0] = (float) v1.get(0);
        vertices[1] = (float) v1.get(1);
        vertices[2] = (float) v1.get(2);

        // Drugi wierzchołek
        vertices[3] = (float) v2.get(0);
        vertices[4] = (float) v2.get(1);
        vertices[5] = (float) v2.get(2);

        // Trzeci wierzchołek
        vertices[6] = (float) v3.get(0);
        vertices[7] = (float) v3.get(1);
        vertices[8] = (float) v3.get(2);

        // Powtarzamy pierwszy wierzchołek, aby zamknąć czworokąt (wymagane przez Face)
        vertices[9] = (float) v1.get(0);
        vertices[10] = (float) v1.get(1);
        vertices[11] = (float) v1.get(2);

        // Generujemy nowy kolor dla każdej ściany lub używamy ustalonego koloru kuli
        Color faceColor = color;

        // Tworzymy nową ścianę i dodajemy do listy
        faces.add(new Face(vertices, faceColor));
    }

    public List<Face> getFaces() {
        return faces;
    }
}