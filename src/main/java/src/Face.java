package src;

import lombok.Data;
import java.awt.*;

@Data
public class Face extends Shape {
    private final Color color;

    public Face(float[] vertices, Color color) {
        super();
        this.color = color;
        createFaceFromVertices(vertices);
    }

    private void createFaceFromVertices(float[] vertices) {
        if (vertices.length != 12) {
            throw new IllegalArgumentException("A face must have exactly 4 vertices (12 coordinates)");
        }

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