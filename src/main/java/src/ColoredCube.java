package src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ColoredCube {
    private List<Face> faces;

    /**
     * Creates a cube made of 6 colored faces for proper BSP rendering
     */
    public ColoredCube(float centerX, float centerY, float centerZ,
                       float width, float height, float depth,
                       Color frontColor, Color backColor, Color leftColor,
                       Color rightColor, Color topColor, Color bottomColor) {
        faces = new ArrayList<>();
        createCubeFaces(centerX, centerY, centerZ, width, height, depth,
                frontColor, backColor, leftColor, rightColor, topColor, bottomColor);
    }

    /**
     * Creates a cube with all faces the same color
     */
    public ColoredCube(float centerX, float centerY, float centerZ,
                       float width, float height, float depth, Color color) {
        this(centerX, centerY, centerZ, width, height, depth,
                color, color, color, color, color, color);
    }

    /**
     * Creates a cube with randomly colored faces
     */
    public ColoredCube(float centerX, float centerY, float centerZ,
                       float width, float height, float depth) {
        faces = new ArrayList<>();

        // Generate random colors for each face
        Color frontColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f);
        Color backColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f);
        Color leftColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f);
        Color rightColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f);
        Color topColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f);
        Color bottomColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1.0f);

        createCubeFaces(centerX, centerY, centerZ, width, height, depth,
                frontColor, backColor, leftColor, rightColor, topColor, bottomColor);
    }

    private void createCubeFaces(float centerX, float centerY, float centerZ,
                                 float width, float height, float depth,
                                 Color frontColor, Color backColor, Color leftColor,
                                 Color rightColor, Color topColor, Color bottomColor) {
        float halfWidth = width / 2;
        float halfHeight = height / 2;
        float halfDepth = depth / 2;

        // Calculate the eight vertices of the cube
        float x1 = centerX - halfWidth;  // Left
        float x2 = centerX + halfWidth;  // Right
        float y1 = centerY - halfHeight; // Bottom
        float y2 = centerY + halfHeight; // Top
        float z1 = centerZ - halfDepth;  // Back
        float z2 = centerZ + halfDepth;  // Front

        // Front face (Z+) - vertices in counter-clockwise order
        faces.add(new Face(new float[] {
                x1, y2, z2,  // top-left
                x2, y2, z2,  // top-right
                x2, y1, z2,  // bottom-right
                x1, y1, z2   // bottom-left
        }, frontColor));

        // Back face (Z-) - vertices in counter-clockwise order (when viewed from outside)
        faces.add(new Face(new float[] {
                x2, y2, z1,  // top-right
                x1, y2, z1,  // top-left
                x1, y1, z1,  // bottom-left
                x2, y1, z1   // bottom-right
        }, backColor));

        // Left face (X-)
        faces.add(new Face(new float[] {
                x1, y2, z1,  // top-back
                x1, y2, z2,  // top-front
                x1, y1, z2,  // bottom-front
                x1, y1, z1   // bottom-back
        }, leftColor));

        // Right face (X+)
        faces.add(new Face(new float[] {
                x2, y2, z2,  // top-front
                x2, y2, z1,  // top-back
                x2, y1, z1,  // bottom-back
                x2, y1, z2   // bottom-front
        }, rightColor));

        // Top face (Y+)
        faces.add(new Face(new float[] {
                x1, y2, z1,  // left-back
                x2, y2, z1,  // right-back
                x2, y2, z2,  // right-front
                x1, y2, z2   // left-front
        }, topColor));

        // Bottom face (Y-)
        faces.add(new Face(new float[] {
                x1, y1, z2,  // left-front
                x2, y1, z2,  // right-front
                x2, y1, z1,  // right-back
                x1, y1, z1   // left-back
        }, bottomColor));
    }

    /**
     * Returns all the faces of this cube
     */
    public List<Face> getFaces() {
        return faces;
    }
}