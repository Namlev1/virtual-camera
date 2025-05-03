package src;

import lombok.Data;
import java.awt.*;

@Data
public class Face extends Shape {
    private final Color color;

    /**
     * Creates a face defined by four vertices in 3D space
     * @param vertices Array of vertex coordinates: [x1,y1,z1, x2,y2,z2, x3,y3,z3, x4,y4,z4]
     * @param color The color to fill this face with
     */
    public Face(float[] vertices, Color color) {
        super();
        this.color = color;
        createFaceFromVertices(vertices);
    }

    /**
     * Creates a face with the old constraints (for backwards compatibility)
     * @param x1 Left x coordinate
     * @param y1 Top y coordinate
     * @param x2 Right x coordinate
     * @param y2 Bottom y coordinate
     * @param z Z coordinate (same for all vertices)
     * @param color The color to fill this face with
     */
    public Face(float x1, float y1, float x2, float y2, float z, Color color) {
        super();
        this.color = color;

        // Convert to the new vertex format
        float[] vertices = new float[] {
                x1, y1, z,  // Top-left
                x2, y1, z,  // Top-right
                x2, y2, z,  // Bottom-right
                x1, y2, z   // Bottom-left
        };

        createFaceFromVertices(vertices);
    }

    /**
     * Creates a face from an array of vertex coordinates
     * @param vertices Array of vertex coordinates: [x1,y1,z1, x2,y2,z2, x3,y3,z3, x4,y4,z4]
     */
    private void createFaceFromVertices(float[] vertices) {
        if (vertices.length != 12) {
            throw new IllegalArgumentException("A face must have exactly 4 vertices (12 coordinates)");
        }

        // Create edges connecting the vertices
        addEdge(new Edge(
                vertices[0], vertices[1], vertices[2],  // v1
                vertices[3], vertices[4], vertices[5]   // v2
        ));

        addEdge(new Edge(
                vertices[3], vertices[4], vertices[5],  // v2
                vertices[6], vertices[7], vertices[8]   // v3
        ));

        addEdge(new Edge(
                vertices[6], vertices[7], vertices[8],  // v3
                vertices[9], vertices[10], vertices[11] // v4
        ));

        addEdge(new Edge(
                vertices[9], vertices[10], vertices[11], // v4
                vertices[0], vertices[1], vertices[2]    // v1
        ));
    }
}