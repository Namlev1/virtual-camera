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
    private final List<Face> allFaces;
    private boolean bspEnabled = true;
    private boolean wireframeMode = false;

    public Main() {
        // Inicjalizacja kamery z początkowym parametrem d = 2.0
        camera = new Camera(2.0f, WIDTH, HEIGHT);

        camera.setCameraPosition(new org.ejml.simple.SimpleMatrix(3, 1, true,
                new float[]{0.0f, 0.0f, 0.0f}));
        camera.recalculateViewMatrix();

        renderer = new Renderer(camera, null);

        allFaces = new ArrayList<>();

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

    private void addCubeToScene(float x, float y, float z, float width, float height, float depth) {
        ColoredCube cube = new ColoredCube(x, y, z, width, height, depth);

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

        renderer.setBSPEnabled(bspEnabled);

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
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Wirtualna Kamera 3D");
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