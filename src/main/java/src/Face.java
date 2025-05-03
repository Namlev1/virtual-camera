package src;

import lombok.Data;

import java.awt.*;

@Data
public class Face extends Shape {
    private final Color color;
    
    public Face(float x1, float y1, float x2, float y2, float z, Color color) {
        super();
        this.color = color;
        createFace(x1, y1, x2, y2, z);
    }

    private void createFace(float x1, float y1, float x2, float y2, float z) {
        float[][] vertices = new float[][]{
                {x1, y1, z}, // lewy górny
                {x2, y1, z}, // prawy górny
                {x2, y2, z}, // prawy dolny
                {x1, y2, z} // lewy dolny
        };

        addEdge(new Edge(vertices[0][0], vertices[0][1], vertices[0][2], vertices[1][0], vertices[1][1], vertices[1][2]));
        addEdge(new Edge(vertices[1][0], vertices[1][1], vertices[1][2], vertices[2][0], vertices[2][1], vertices[2][2]));
        addEdge(new Edge(vertices[2][0], vertices[2][1], vertices[2][2], vertices[3][0], vertices[3][1], vertices[3][2]));
        addEdge(new Edge(vertices[3][0], vertices[3][1], vertices[3][2], vertices[0][0], vertices[0][1], vertices[0][2]));
    }
}
