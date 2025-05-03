package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Main extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final Camera camera;
    private final Renderer renderer;
    private final List<Face> allFaces; // Store all faces from all cubes
    private boolean bspEnabled = true; // Enable BSP by default
    private boolean wireframeMode = false; // Toggle for wireframe mode

    public Main() {
        // Inicjalizacja kamery z początkowym parametrem d = 2.0
        camera = new Camera(2.0f, WIDTH, HEIGHT);

        // Set up camera position to see all cubes
        camera.setCameraPosition(new org.ejml.simple.SimpleMatrix(3, 1, true,
                new float[]{0.0f, 0.0f, 0.0f}));
        camera.recalculateViewMatrix();

        renderer = new Renderer(camera, null);

        // Create a list to store all faces
        allFaces = new ArrayList<>();

        // Add cubes to the scene
        addCubeToScene(-1f, -1f, 4.0f, 1.0f, 1.0f, 1.0f);  // Lewa dolna
        addCubeToScene(1f, -1f, 4.0f, 1.0f, 1.0f, 1.0f);   // Prawa dolna
        addCubeToScene(-1f, 1f, 4.0f, 1.0f, 1.0f, 1.0f);   // Lewa górna
        addCubeToScene(1f, 1f, 4.0f, 1.0f, 1.0f, 1.0f);    // Prawa górna

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }

    /**
     * Adds a colored cube to the scene at the specified position
     */
    private void addCubeToScene(float x, float y, float z, float width, float height, float depth) {
        // Create a cube with random colors for each face
        ColoredCube cube = new ColoredCube(x, y, z, width, height, depth);

        // Add all faces from this cube to our list of faces
        allFaces.addAll(cube.getFaces());
    }

    private void handleKeyPress(KeyEvent e) {
        int keyCode = e.getKeyCode();

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
            case KeyEvent.VK_Q:  // Obrót zgodnie z wskazówkami zegara (q)
                camera.rotateCamera(0, 0, -5);
                break;

            // Zmiana FOV (zoom)
            case KeyEvent.VK_CLOSE_BRACKET:  // Zoom in ]
                camera.changeZoom(1);
                break;
            case KeyEvent.VK_OPEN_BRACKET:  // Zoom out [
                camera.changeZoom(-1);
                break;

            // Toggle BSP on/off
            case KeyEvent.VK_B:  // Toggle BSP
                bspEnabled = !bspEnabled;
                System.out.println("BSP " + (bspEnabled ? "enabled" : "disabled"));
                break;

            // Toggle wireframe mode
            case KeyEvent.VK_W:  // Toggle wireframe
                wireframeMode = !wireframeMode;
                System.out.println("Wireframe mode " + (wireframeMode ? "enabled" : "disabled"));
                break;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Czyszczenie ekranu
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);

        renderer.setGraphics(g);

        // Enable/disable BSP based on toggle
        renderer.setBSPEnabled(bspEnabled);

        // Render the scene
        if (wireframeMode) {
            // In wireframe mode, just draw the edges
            for (Face face : allFaces) {
                for (Edge edge : face.getEdges()) {
                    renderer.drawEdge(edge);
                }
            }
        } else {
            // In solid mode, use BSP or direct rendering
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

        // Draw information text
        g.setColor(Color.BLACK);
        g.drawString("BSP: " + (bspEnabled ? "ON" : "OFF") + " (B to toggle)", 10, 20);
        g.drawString("Mode: " + (wireframeMode ? "Wireframe" : "Solid") + " (W to toggle)", 10, 40);
        g.drawString("Use arrow keys to rotate, []/,'/. to move", 10, HEIGHT - 20);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Wirtualna Kamera 3D with BSP");
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