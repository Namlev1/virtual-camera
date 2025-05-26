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
                  int latitudeBands, int longitudeBands, Color color) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.radius = radius;
        this.latitudeBands = latitudeBands;
        this.longitudeBands = longitudeBands;
        this.faces = new ArrayList<>();
        this.color = color;

        generateSphere();
    }

    private void generateSphere() {
        SimpleMatrix[][] vertices = new SimpleMatrix[latitudeBands + 1][longitudeBands + 1];

        for (int lat = 0; lat <= latitudeBands; lat++) {
            float theta = (float) (lat * Math.PI / latitudeBands);
            float sinTheta = (float) Math.sin(theta);
            float cosTheta = (float) Math.cos(theta);

            for (int lon = 0; lon <= longitudeBands; lon++) {
                float phi = (float) (lon * 2 * Math.PI / longitudeBands);
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);

                float x = cosPhi * sinTheta;
                float y = cosTheta;
                float z = sinPhi * sinTheta;

                float scaledX = centerX + radius * x;
                float scaledY = centerY + radius * y;
                float scaledZ = centerZ + radius * z;

                vertices[lat][lon] = new SimpleMatrix(3, 1, true, new float[]{scaledX, scaledY, scaledZ});
            }
        }

        for (int lat = 0; lat < latitudeBands; lat++) {
            for (int lon = 0; lon < longitudeBands; lon++) {
                SimpleMatrix v1 = vertices[lat][lon];
                SimpleMatrix v2 = vertices[lat + 1][lon];
                SimpleMatrix v3 = vertices[lat + 1][lon + 1];
                SimpleMatrix v4 = vertices[lat][lon + 1];

                createTriangleFace(v1, v2, v3);
                createTriangleFace(v1, v3, v4);
            }
        }
    }

    private void createTriangleFace(SimpleMatrix v1, SimpleMatrix v2, SimpleMatrix v3) {
        float[] vertices = new float[12]; // 12 = 3 wierzchołki * 3 współrzędne + 3 dla powtórzonego wierzchołka

        vertices[0] = (float) v1.get(0);
        vertices[1] = (float) v1.get(1);
        vertices[2] = (float) v1.get(2);

        vertices[3] = (float) v2.get(0);
        vertices[4] = (float) v2.get(1);
        vertices[5] = (float) v2.get(2);

        vertices[6] = (float) v3.get(0);
        vertices[7] = (float) v3.get(1);
        vertices[8] = (float) v3.get(2);

        vertices[9] = (float) v1.get(0);
        vertices[10] = (float) v1.get(1);
        vertices[11] = (float) v1.get(2);

        Color faceColor = color;

        faces.add(new Face(vertices, faceColor));
    }

    public List<Face> getFaces() {
        return faces;
    }
}