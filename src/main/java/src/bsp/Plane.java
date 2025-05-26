package src.bsp;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;
import src.Edge;
import src.Face;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class Plane {
    // Równanie płaszczyzny: ax + by + cz + d = 0
    private float a, b, c, d;

    public Plane(float a, float b, float c, float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        normalize();
    }

    public static Plane fromPoints(SimpleMatrix p1, SimpleMatrix p2, SimpleMatrix p3) {
        // Wektory płaszczyzny
        SimpleMatrix v1 = p2.minus(p1);
        SimpleMatrix v2 = p3.minus(p1);

        // Uzyskanie wektora normalnego
        float nx = (float) (v1.get(1) * v2.get(2) - v1.get(2) * v2.get(1));
        float ny = (float) (v1.get(2) * v2.get(0) - v1.get(0) * v2.get(2));
        float nz = (float) (v1.get(0) * v2.get(1) - v1.get(1) * v2.get(0));

        // Obliczenie d
        float d = -(nx * (float)p1.get(0) + ny * (float)p1.get(1) + nz * (float)p1.get(2));

        return new Plane(nx, ny, nz, d);
    }

    public static Plane fromFace(Face face) {
        Edge edge1 = face.getEdges().get(0);
        Edge edge2 = face.getEdges().get(1);

        SimpleMatrix p1 = edge1.getStart();
        SimpleMatrix p2 = edge1.getEnd();
        SimpleMatrix p3 = edge2.getEnd();

        return fromPoints(p1, p2, p3);
    }

    private void normalize() {
        float length = (float) Math.sqrt(a * a + b * b + c * c);
        if (length > 1e-6f) {
            a /= length;
            b /= length;
            c /= length;
            d /= length;
        }
    }

    public float distanceToPoint(SimpleMatrix point) {
        return a * (float)point.get(0) + b * (float)point.get(1) + c * (float)point.get(2) + d;
    }

    /**
     * @return:
     *   1: ściana całkowicie przed płaszczyzną
     *   0: ściana zawarta w płaszczyźnie
     *  -1: ściana całkowicie za płaszczyzną
     *   2: ściana przecina płaszczyznę
     */
    public int classifyFace(Face face) {
        int frontCount = 0;
        int backCount = 0;
        float epsilon = 0.0001f;

        for (Edge edge : face.getEdges()) {
            SimpleMatrix vertex = edge.getStart();
            float distance = distanceToPoint(vertex);

            if (distance > epsilon) {
                frontCount++;
            } else if (distance < -epsilon) {
                backCount++;
            }
        }

        if (frontCount == 0 && backCount == 0) {
            return 0;
        }
        else if (backCount == 0) {
            return 1;
        }
        else if (frontCount == 0) {
            return -1;
        }
        else {
            return 2;
        }
    }

    private SimpleMatrix findIntersection(SimpleMatrix p1, SimpleMatrix p2) {
        float d1 = distanceToPoint(p1);
        float d2 = distanceToPoint(p2);

        if (Math.abs(d1) < 0.0001f) {
            d1 = 0.0001f;
        }

        float t = d1 / (d1 - d2);

        float x = (float)(p1.get(0) + t * (p2.get(0) - p1.get(0)));
        float y = (float)(p1.get(1) + t * (p2.get(1) - p1.get(1)));
        float z = (float)(p1.get(2) + t * (p2.get(2) - p1.get(2)));

        return new SimpleMatrix(3, 1, true, new float[] {x, y, z});
    }

    public Face[] splitFace(Face face) {
        List<SimpleMatrix> vertices = new ArrayList<>();
        for (Edge edge : face.getEdges()) {
            vertices.add(edge.getStart());
        }

        List<SimpleMatrix> frontVertices = new ArrayList<>();
        List<SimpleMatrix> backVertices = new ArrayList<>();

        int numVertices = vertices.size();
        for (int i = 0; i < numVertices; i++) {
            SimpleMatrix current = vertices.get(i);
            SimpleMatrix next = vertices.get((i + 1) % numVertices);

            float currentDist = distanceToPoint(current);
            float nextDist = distanceToPoint(next);

            if (currentDist >= 0) {
                frontVertices.add(current);
            } else {
                backVertices.add(current);
            }

            if ((currentDist > 0 && nextDist < 0) || (currentDist < 0 && nextDist > 0)) {
                SimpleMatrix intersection = findIntersection(current, next);

                frontVertices.add(intersection);
                backVertices.add(intersection);
            }
        }

        Face frontFace = createFaceFromVertices(frontVertices, face.getColor());
        Face backFace = createFaceFromVertices(backVertices, face.getColor());

        return new Face[] { frontFace, backFace };
    }

    private Face createFaceFromVertices(List<SimpleMatrix> vertices, Color color) {
        if (vertices.size() == 3) {
            // Trójkąt
            float[] coords = new float[9];
            for (int i = 0; i < 3; i++) {
                SimpleMatrix v = vertices.get(i);
                coords[i*3] = (float)v.get(0);
                coords[i*3 + 1] = (float)v.get(1);
                coords[i*3 + 2] = (float)v.get(2);
            }

            float[] quadCoords = new float[12];
            System.arraycopy(coords, 0, quadCoords, 0, 9);
            quadCoords[9] = coords[6];
            quadCoords[10] = coords[7];
            quadCoords[11] = coords[8];

            return new Face(quadCoords, color);
        }
        else if (vertices.size() >= 4) {
            // Czworokąt
            float[] coords = new float[12];
            for (int i = 0; i < 4; i++) {
                SimpleMatrix v = vertices.get(i);
                coords[i*3] = (float)v.get(0);
                coords[i*3 + 1] = (float)v.get(1);
                coords[i*3 + 2] = (float)v.get(2);
            }

            return new Face(coords, color);
        }
        else {
            throw new IllegalStateException("Not enough vertices to create a face after splitting");
        }
    }
}