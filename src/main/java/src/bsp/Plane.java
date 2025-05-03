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
    // Plane equation: ax + by + cz + d = 0
    private float a, b, c, d;

    public Plane(float a, float b, float c, float d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        normalize();
    }

    /**
     * Creates a plane from three points
     */
    public static Plane fromPoints(SimpleMatrix p1, SimpleMatrix p2, SimpleMatrix p3) {
        // Calculate two vectors in the plane
        SimpleMatrix v1 = p2.minus(p1);
        SimpleMatrix v2 = p3.minus(p1);

        // Cross product to get normal vector
        float nx = (float) (v1.get(1) * v2.get(2) - v1.get(2) * v2.get(1));
        float ny = (float) (v1.get(2) * v2.get(0) - v1.get(0) * v2.get(2));
        float nz = (float) (v1.get(0) * v2.get(1) - v1.get(1) * v2.get(0));

        // Calculate d value
        float d = -(nx * (float)p1.get(0) + ny * (float)p1.get(1) + nz * (float)p1.get(2));

        return new Plane(nx, ny, nz, d);
    }

    /**
     * Creates a plane from a face (using first three vertices)
     */
    public static Plane fromFace(Face face) {
        // Get first three vertices from the face
        Edge edge1 = face.getEdges().get(0);
        Edge edge2 = face.getEdges().get(1);

        SimpleMatrix p1 = edge1.getStart();
        SimpleMatrix p2 = edge1.getEnd();
        SimpleMatrix p3 = edge2.getEnd();

        return fromPoints(p1, p2, p3);
    }

    /**
     * Normalizes the plane equation so that the normal vector (a,b,c) has length 1
     */
    private void normalize() {
        float length = (float) Math.sqrt(a * a + b * b + c * c);
        if (length > 1e-6f) {
            a /= length;
            b /= length;
            c /= length;
            d /= length;
        }
    }

    /**
     * Calculates the signed distance from a point to this plane
     * Positive: in front of plane, Negative: behind plane, Zero: on the plane
     */
    public float distanceToPoint(SimpleMatrix point) {
        return a * (float)point.get(0) + b * (float)point.get(1) + c * (float)point.get(2) + d;
    }

    /**
     * Classifies a face relative to this plane
     * @return:
     *   1: face is entirely in front of the plane
     *   0: face is coplanar with the plane
     *  -1: face is entirely behind the plane
     *   2: face intersects the plane (straddles)
     */
    public int classifyFace(Face face) {
        int frontCount = 0;
        int backCount = 0;
        float epsilon = 0.0001f;

        // Check all vertices of the face
        for (Edge edge : face.getEdges()) {
            SimpleMatrix vertex = edge.getStart();
            float distance = distanceToPoint(vertex);

            if (distance > epsilon) {
                frontCount++;
            } else if (distance < -epsilon) {
                backCount++;
            }
        }

        // If all points are on the plane (within epsilon)
        if (frontCount == 0 && backCount == 0) {
            return 0; // Coplanar
        }
        // If all points are in front of the plane
        else if (backCount == 0) {
            return 1; // Front
        }
        // If all points are behind the plane
        else if (frontCount == 0) {
            return -1; // Back
        }
        // Otherwise, the face straddles the plane
        else {
            return 2; // Straddling
        }
    }

    /**
     * Finds the intersection point between a line segment and this plane
     */
    private SimpleMatrix findIntersection(SimpleMatrix p1, SimpleMatrix p2) {
        float d1 = distanceToPoint(p1);
        float d2 = distanceToPoint(p2);

        // Ensure d1 is not zero to avoid division by zero
        if (Math.abs(d1) < 0.0001f) {
            d1 = 0.0001f;
        }

        // Calculate the interpolation parameter t
        float t = d1 / (d1 - d2);

        // Interpolate to find the intersection point
        float x = (float)(p1.get(0) + t * (p2.get(0) - p1.get(0)));
        float y = (float)(p1.get(1) + t * (p2.get(1) - p1.get(1)));
        float z = (float)(p1.get(2) + t * (p2.get(2) - p1.get(2)));

        return new SimpleMatrix(3, 1, true, new float[] {x, y, z});
    }

    /**
     * Splits a face that straddles this plane into two parts
     * @return an array containing [frontFace, backFace]
     */
    public Face[] splitFace(Face face) {
        // Get all vertices of the face
        List<SimpleMatrix> vertices = new ArrayList<>();
        for (Edge edge : face.getEdges()) {
            vertices.add(edge.getStart());
        }

        // Initialize lists for front and back vertices
        List<SimpleMatrix> frontVertices = new ArrayList<>();
        List<SimpleMatrix> backVertices = new ArrayList<>();

        // Process each edge to find intersections and classify vertices
        int numVertices = vertices.size();
        for (int i = 0; i < numVertices; i++) {
            SimpleMatrix current = vertices.get(i);
            SimpleMatrix next = vertices.get((i + 1) % numVertices);

            float currentDist = distanceToPoint(current);
            float nextDist = distanceToPoint(next);

            // Add current vertex to appropriate list
            if (currentDist >= 0) {
                frontVertices.add(current);
            } else {
                backVertices.add(current);
            }

            // Check if edge crosses the plane
            if ((currentDist > 0 && nextDist < 0) || (currentDist < 0 && nextDist > 0)) {
                // Find intersection point
                SimpleMatrix intersection = findIntersection(current, next);

                // Add intersection to both lists
                frontVertices.add(intersection);
                backVertices.add(intersection);
            }
        }

        // Create front and back faces
        // For simplicity in this implementation, we'll create triangular or quadrilateral faces
        Face frontFace = createFaceFromVertices(frontVertices, face.getColor());
        Face backFace = createFaceFromVertices(backVertices, face.getColor());

        return new Face[] { frontFace, backFace };
    }

    /**
     * Creates a face from a list of vertices
     */
    private Face createFaceFromVertices(List<SimpleMatrix> vertices, Color color) {
        // For simplicity, we'll handle only cases with 3 or 4 vertices
        // A more sophisticated implementation would handle N-sided polygons

        if (vertices.size() == 3) {
            // Triangle case
            float[] coords = new float[9];
            for (int i = 0; i < 3; i++) {
                SimpleMatrix v = vertices.get(i);
                coords[i*3] = (float)v.get(0);
                coords[i*3 + 1] = (float)v.get(1);
                coords[i*3 + 2] = (float)v.get(2);
            }

            // Add a fourth point to make it a quadrilateral (required by our Face class)
            // We'll duplicate the third point
            float[] quadCoords = new float[12];
            System.arraycopy(coords, 0, quadCoords, 0, 9);
            quadCoords[9] = coords[6];
            quadCoords[10] = coords[7];
            quadCoords[11] = coords[8];

            return new Face(quadCoords, color);
        }
        else if (vertices.size() >= 4) {
            // Quadrilateral or higher - simplify to a quad by using first 4 points
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
            // Should not happen with proper BSP tree construction
            throw new IllegalStateException("Not enough vertices to create a face after splitting");
        }
    }
}