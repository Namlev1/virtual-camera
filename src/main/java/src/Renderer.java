package src;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;
import src.bsp.BSPTree;
import src.lighting.Light;
import src.lighting.Material;

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
    private boolean lightingEnabled = true;
    private List<Light> lights;
    private Material defaultMaterial;

    public Renderer(Camera camera, java.awt.Graphics graphics) {
        this.camera = camera;
        this.graphics = graphics;
        this.faces = new ArrayList<>();
        this.lights = new ArrayList<>();

        // Domyślny materiał
        this.defaultMaterial = new Material(0.2f, 0.7f, 0.5f, 32);

        // Domyślne światło
        addLight(new Light(5.0f, 5.0f, 0.0f, Color.WHITE, 1.0f, 0.2f));
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public void setLightingEnabled(boolean enabled) {
        this.lightingEnabled = enabled;
    }

    public void setBSPEnabled(boolean enabled) {
        this.bspEnabled = enabled;
        if (enabled) {
            ensureBSPTreeBuilt();
        }
    }

    public void addFace(Face face) {
        faces.add(face);
        rebuildRequired = true;
    }

    public void clearFaces() {
        faces.clear();
        bspTree = null;
        rebuildRequired = false;
    }

    private void ensureBSPTreeBuilt() {
        if (rebuildRequired || bspTree == null) {
            bspTree = new BSPTree(new ArrayList<>(faces));
            rebuildRequired = false;
        }
    }

    public void drawShape(Shape shape) {
        if (shape instanceof Face) {
            Face face = (Face) shape;

            if (bspEnabled) {
                addFace(face);
            } else {
                drawFaceDirectly(face);
            }
        } else {
            for (Edge edge : shape.getEdges()) {
                drawEdge(edge);
            }
        }
    }

    private void drawFaceDirectly(Face face) {
        fillFace(face);

        for (Edge edge : face.getEdges()) {
            drawEdge(edge);
        }
    }

    public void renderWithBSP() {
        ensureBSPTreeBuilt();

        if (bspTree != null && !faces.isEmpty()) {
            bspTree.render(this, camera.getCameraPosition());
            clearFaces();
        }
    }

    public void fillFace(Face face) {
        List<Edge> edges = face.getEdges();
        int numVertices = edges.size();

        if (numVertices < 3) {
            return;
        }

        int[] xPoints = new int[numVertices];
        int[] yPoints = new int[numVertices];

        for (int i = 0; i < numVertices; i++) {
            SimpleMatrix point = convertToHomogeneousCoordinates(edges.get(i).getStart());
            SimpleMatrix pointView = camera.getViewMatrix().mult(point);

            if (pointView.get(2) <= 0) {
                return;
            }

            SimpleMatrix pointClip = camera.getProjectionMatrix().mult(pointView);
            float xNdc = (float) (pointClip.get(0) / pointClip.get(3));
            float yNdc = (float) (pointClip.get(1) / pointClip.get(3));

            int screenWidth = camera.getWIDTH();
            int screenHeight = camera.getHEIGHT();

            xPoints[i] = (int) (screenWidth / 2 + xNdc * screenHeight / 2);
            yPoints[i] = (int) (screenHeight / 2 - yNdc * screenHeight / 2);
        }

        Color oldColor = graphics.getColor();

        if (lightingEnabled && !lights.isEmpty()) {
            // Oblicz kolor z modelem Phonga
            Color litColor = face.getColor();

            for (Light light : lights) {
                Color lightContribution = defaultMaterial.calculatePhongColor(face, camera.getCameraPosition(), light);

                // Łączymy kolory światła (uproszczona metoda)
                litColor = new Color(
                        Math.min(255, litColor.getRed() + lightContribution.getRed()),
                        Math.min(255, litColor.getGreen() + lightContribution.getGreen()),
                        Math.min(255, litColor.getBlue() + lightContribution.getBlue())
                );
            }

            graphics.setColor(litColor);
        } else {
            graphics.setColor(face.getColor());
        }

        graphics.fillPolygon(xPoints, yPoints, numVertices);

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