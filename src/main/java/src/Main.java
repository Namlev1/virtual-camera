package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import src.lighting.Light;

public class Main extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final Camera camera;
    private final Renderer renderer;
    private final List<Face> allFaces;
    private boolean bspEnabled = true;
    private boolean wireframeMode = false;
    private boolean lightingEnabled = true;
    private Light movableLight;

    public Main() {
        // Inicjalizacja kamery z początkowym parametrem d = 2.0
        camera = new Camera(2.0f, WIDTH, HEIGHT);

        camera.setCameraPosition(new org.ejml.simple.SimpleMatrix(3, 1, true,
                new float[]{0.0f, 0.0f, -5.0f}));  // Odsunięcie kamery, aby lepiej widzieć kulę
        camera.recalculateViewMatrix();

        renderer = new Renderer(camera, null);

        // Dodanie poruszalnego światła
        movableLight = new Light(5.0f, 5.0f, 2.0f, Color.WHITE, 1.0f, 0.2f);
        renderer.addLight(movableLight);

        allFaces = new ArrayList<>();

        // Dodanie kuli zamiast czterech sześcianów
        addSphereToScene(0f, 0f, 4.0f, 2.0f, 20, 20);  // Kula o promieniu 2.0 na środku sceny

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }

    private void addSphereToScene(float x, float y, float z, float radius, int latitudeBands, int longitudeBands) {
        Sphere sphere = new Sphere(x, y, z, radius, latitudeBands, longitudeBands);
        allFaces.addAll(sphere.getFaces());
    }

    private void handleKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // Modyfikator Shift - kontrola światła zamiast kamery
        boolean lightControl = e.isShiftDown();

        if (lightControl) {
            handleLightControls(keyCode);
        } else {
            handleCameraControls(keyCode);
        }

        // Dodatkowe kontrolki
        switch (keyCode) {
            case KeyEvent.VK_L:  // Włączanie/wyłączanie oświetlenia
                lightingEnabled = !lightingEnabled;
                renderer.setLightingEnabled(lightingEnabled);
                break;
            case KeyEvent.VK_B:  // Włączanie/wyłączanie BSP
                bspEnabled = !bspEnabled;
                renderer.setBSPEnabled(bspEnabled);
                break;
            case KeyEvent.VK_W:  // Włączanie/wyłączanie trybu wireframe
                wireframeMode = !wireframeMode;
                break;
        }

        repaint();
    }

    private void handleLightControls(int keyCode) {
        float step = 0.5f;
        org.ejml.simple.SimpleMatrix position = movableLight.getPosition();
        float x = (float) position.get(0);
        float y = (float) position.get(1);
        float z = (float) position.get(2);

        switch (keyCode) {
            case KeyEvent.VK_A:  // Lewo
                x -= step;
                break;
            case KeyEvent.VK_E:  // Prawo
                x += step;
                break;
            case KeyEvent.VK_COMMA:  // Góra
                y += step;
                break;
            case KeyEvent.VK_O:  // Dół
                y -= step;
                break;
            case KeyEvent.VK_QUOTE:  // Przód
                z += step;
                break;
            case KeyEvent.VK_PERIOD:  // Tył
                z -= step;
                break;
        }

        movableLight.setPosition(new org.ejml.simple.SimpleMatrix(3, 1, true, new float[]{x, y, z}));
    }

    private void handleCameraControls(int keyCode) {
        switch (keyCode) {
            // Translacja kamery
            case KeyEvent.VK_QUOTE:  // Przód (');
                camera.moveCameraForward(1);
                break;
            case KeyEvent.VK_PERIOD:  // Tył (.)
                camera.moveCameraForward(-1);
                break;
            case KeyEvent.VK_A:  // Lewo (a)
                camera.moveCameraRight(-1);
                break;
            case KeyEvent.VK_E:  // Prawo (e)
                camera.moveCameraRight(1);
                break;
            case KeyEvent.VK_O:  // Dół (o)
                camera.moveCameraUp(-1);
                break;
            case KeyEvent.VK_COMMA:  // Góra (,)
                camera.moveCameraUp(1);
                break;

            // Rotacja kamery
            case KeyEvent.VK_UP:  // Obrót w górę
                camera.rotateCamera(-5, 0, 0);
                break;
            case KeyEvent.VK_DOWN:  // Obrót w dół
                camera.rotateCamera(5, 0, 0);
                break;
            case KeyEvent.VK_LEFT:  // Obrót w lewo
                camera.rotateCamera(0, -5, 0);
                break;
            case KeyEvent.VK_RIGHT:  // Obrót w prawo
                camera.rotateCamera(0, 5, 0);
                break;
            case KeyEvent.VK_SEMICOLON:  // Obrót przeciwnie do wskazówek zegara (;)
                camera.rotateCamera(0, 0, 5);
                break;
            case KeyEvent.VK_Q:  // Obrót zgodnie ze wskazówkami zegara (q)
                camera.rotateCamera(0, 0, -5);
                break;

            // Zmiana FOV (zoom)
            case KeyEvent.VK_CLOSE_BRACKET:  // Zoom in ]
                camera.changeZoom(1);
                break;
            case KeyEvent.VK_OPEN_BRACKET:  // Zoom out [
                camera.changeZoom(-1);
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Czyszczenie ekranu
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);

        renderer.setGraphics(g);

        renderer.setBSPEnabled(bspEnabled);
        renderer.setLightingEnabled(lightingEnabled);

        // Rendering
        if (wireframeMode) {
            for (Face face : allFaces) {
                for (Edge edge : face.getEdges()) {
                    renderer.drawEdge(edge);
                }
            }
        } else {
            if (bspEnabled) {
                for (Face face : allFaces) {
                    renderer.addFace(face);
                }
                renderer.renderWithBSP();
            } else {
                for (Face face : allFaces) {
                    renderer.drawShape(face);
                }
            }
        }

        // Wyświetl informacje o stanie
        g.setColor(Color.BLACK);
        g.drawString("BSP: " + (bspEnabled ? "ON" : "OFF") + " (B)", 10, 20);
        g.drawString("Lighting: " + (lightingEnabled ? "ON" : "OFF") + " (L)", 10, 40);
        g.drawString("Wireframe: " + (wireframeMode ? "ON" : "OFF") + " (W)", 10, 60);
        g.drawString("Light position: " + formatVector(movableLight.getPosition()) + " (Shift+AOEQ',)", 10, 80);
    }

    private String formatVector(org.ejml.simple.SimpleMatrix v) {
        return String.format("(%.1f, %.1f, %.1f)", v.get(0), v.get(1), v.get(2));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Wirtualna Kamera 3D z oświetleniem Phonga");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(WIDTH, HEIGHT);
            frame.setResizable(false);

            Main cameraDemo = new Main();
            frame.add(cameraDemo);

            frame.setVisible(true);
            cameraDemo.requestFocusInWindow();
        });
    }
}