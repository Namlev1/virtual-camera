package src;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;
import src.bsp.BSPTree;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class Renderer {
    private Camera camera;
    private java.awt.Graphics graphics;
    private List<Face> faces;
    private BSPTree bspTree;
    private boolean bspEnabled = false;
    private boolean rebuildRequired = true;

    public Renderer(Camera camera, java.awt.Graphics graphics) {
        this.camera = camera;
        this.graphics = graphics;
        this.faces = new ArrayList<>();
    }

    /**
     * Enable or disable BSP-based hidden surface removal
     */
    public void setBSPEnabled(boolean enabled) {
        this.bspEnabled = enabled;
        // Ensure BSP tree is built if we're enabling it
        if (enabled) {
            ensureBSPTreeBuilt();
        }
    }

    /**
     * Add a face to be rendered with BSP
     */
    public void addFace(Face face) {
        faces.add(face);
        rebuildRequired = true;
    }

    /**
     * Clear all faces from the renderer
     */
    public void clearFaces() {
        faces.clear();
        bspTree = null;
        rebuildRequired = false;
    }

    /**
     * Rebuild the BSP tree if necessary
     */
    private void ensureBSPTreeBuilt() {
        if (rebuildRequired || bspTree == null) {
            bspTree = new BSPTree(new ArrayList<>(faces));
            rebuildRequired = false;
        }
    }

    /**
     * Draw a shape with or without BSP depending on settings
     */
    public void drawShape(Shape shape) {
        if (shape instanceof Face) {
            Face face = (Face) shape;

            // If BSP is enabled, add to list for BSP rendering
            if (bspEnabled) {
                // This face will be rendered later with BSP
                addFace(face);
            } else {
                // No BSP, render immediately
                drawFaceDirectly(face);
            }
        } else {
            // For non-Face shapes (like lines), just draw directly
            for (Edge edge : shape.getEdges()) {
                drawEdge(edge);
            }
        }
    }

    /**
     * Draw a face directly without BSP (original implementation)
     */
    private void drawFaceDirectly(Face face) {
        // First fill the face
        fillFace(face);

        // Then draw edges
        for (Edge edge : face.getEdges()) {
            drawEdge(edge);
        }
    }

    /**
     * Render all faces using BSP tree for correct depth sorting
     * Call this after all shapes have been added
     */
    public void renderWithBSP() {
        // Make sure BSP tree is up to date
        ensureBSPTreeBuilt();

        if (bspTree != null && !faces.isEmpty()) {
            // Use the BSP tree to render faces in correct order
            bspTree.render(this, camera.getCameraPosition());

            // Clear the face list since they've been rendered
            clearFaces();
        }
    }

    /**
     * Normal drawing methods (from original Renderer)
     */
    public void fillFace(Face face) {
        // Get all edges of the face
        List<Edge> edges = face.getEdges();
        int numVertices = edges.size();

        if (numVertices < 3) {
            return; // Need at least 3 vertices to form a face
        }

        // Arrays to store screen coordinates
        int[] xPoints = new int[numVertices];
        int[] yPoints = new int[numVertices];

        // Project each vertex to screen space
        for (int i = 0; i < numVertices; i++) {
            SimpleMatrix point = convertToHomogeneousCoordinates(edges.get(i).getStart());
            SimpleMatrix pointView = camera.getViewMatrix().mult(point);

            // Check if point is behind camera
            if (pointView.get(2) <= 0) {
                return; // Don't render faces with any vertices behind camera
            }

            SimpleMatrix pointClip = camera.getProjectionMatrix().mult(pointView);
            float xNdc = (float) (pointClip.get(0) / pointClip.get(3));
            float yNdc = (float) (pointClip.get(1) / pointClip.get(3));

            int screenWidth = camera.getWIDTH();
            int screenHeight = camera.getHEIGHT();

            xPoints[i] = (int) (screenWidth / 2 + xNdc * screenHeight / 2);
            yPoints[i] = (int) (screenHeight / 2 - yNdc * screenHeight / 2);
        }

        // Set the face color
        Color oldColor = graphics.getColor();
        graphics.setColor(face.getColor());

        // Fill the polygon
        graphics.fillPolygon(xPoints, yPoints, numVertices);

        // Restore the original color
        graphics.setColor(oldColor);
    }

    public void drawEdge(Edge edge) {
        SimpleMatrix start = convertToHomogeneousCoordinates(edge.getStart());
        SimpleMatrix end = convertToHomogeneousCoordinates(edge.getEnd());
        // Macierz widoku
        SimpleMatrix startView = camera.getViewMatrix().mult(start);
        SimpleMatrix endView = camera.getViewMatrix().mult(end);
        // Sprawdzenie, czy punkty są przed kamerą (z > 0 w przestrzeni kamery)
        if (startView.get(2) <= 0 || endView.get(2) <= 0) {
            return; // Punkt jest za kamerą, nie rysujemy
        }
        // Macierz projekcji
        SimpleMatrix startClip = camera.getProjectionMatrix().mult(startView);
        SimpleMatrix endClip = camera.getProjectionMatrix().mult(endView);
        float startXNdc = (float) (startClip.get(0) / startClip.get(3));
        float startYNdc = (float) (startClip.get(1) / startClip.get(3));
        float endXNdc = (float) (endClip.get(0) / endClip.get(3));
        float endYNdc = (float) (endClip.get(1) / endClip.get(3));
        int screenWidth = camera.getWIDTH();
        int screenHeight = camera.getHEIGHT();
        int startScreenX = (int) (screenWidth / 2 + startXNdc * screenHeight / 2);
        int startScreenY = (int) (screenHeight / 2 - startYNdc * screenHeight / 2);
        int endScreenX = (int) (screenWidth / 2 + endXNdc * screenHeight / 2);
        int endScreenY = (int) (screenHeight / 2 - endYNdc * screenHeight / 2);
        graphics.drawLine(startScreenX, startScreenY, endScreenX, endScreenY);
    }

    private SimpleMatrix convertToHomogeneousCoordinates(SimpleMatrix point) {
        SimpleMatrix homogeneous = new SimpleMatrix(4, 1);
        homogeneous.set(0, 0, point.get(0));
        homogeneous.set(1, 0, point.get(1));
        homogeneous.set(2, 0, point.get(2));
        homogeneous.set(3, 0, 1.0);
        return homogeneous;
    }
}