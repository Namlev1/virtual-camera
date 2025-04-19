package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final Camera camera;
    private final List<Cube> cubes;
    private final Renderer renderer;
    private final JPanel panel;

    public Main() {
        setTitle("Wirtualna Kamera 3D");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicjalizacja kamery
        camera = new Camera(0, 0, -10, 60);

        // Inicjalizacja sześcianów - układ podobny do logo Code::Blocks
        cubes = createCodeBlocksLogo();

        // Inicjalizacja renderera
        renderer = new Renderer(camera, WIDTH, HEIGHT);

        // Panel do rysowania
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Czyszczenie ekranu
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Rysowanie sześcianów
                for (Cube cube : cubes) {
                    renderer.renderCube(g, cube);
                }

                // Wyświetlanie informacji o projekcji
                g.setColor(Color.BLACK);
                g.drawString("Wirtualna Kamera 3D - Rzutowanie perspektywiczne", 10, 20);
                g.drawString("Środek rzutowania: [" + camera.getX() + ", " + camera.getY() + ", " + camera.getZ() + "]", 10, 60);
                g.drawString("Rotacja X: " + String.format("%.2f", camera.getRotX()) + " rad", 10, 80);
                g.drawString("Rotacja Y: " + String.format("%.2f", camera.getRotY()) + " rad", 10, 100);
                g.drawString("Rotacja Z: " + String.format("%.2f", camera.getRotZ()) + " rad", 10, 120);
                g.drawString("FOV: " + String.format("%.1f", camera.getFov()) + "°", 10, 140);
            }
        };

        // Dodanie obsługi klawiatury
        panel.setFocusable(true);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });

        add(panel);
    }

    private List<Cube> createCodeBlocksLogo() {
        List<Cube> cubeList = new ArrayList<>();

        double size = 1.5;
        double spacing = 1;

        // Lewy górny
        cubeList.add(new Cube(size, -spacing, spacing, 0));

        // Prawy górny
        cubeList.add(new Cube(size, spacing, spacing, 0));

        // Lewy dolny
        cubeList.add(new Cube(size, -spacing, -spacing, 0));

        // Prawy dolny
        cubeList.add(new Cube(size, spacing, -spacing, 0));

        return cubeList;
    }

    private void handleKeyPress(KeyEvent e) {
        double moveSpeed = 0.5;
        double rotateSpeed = 0.1;
        double fovChangeSpeed = 5.0;

        switch (e.getKeyCode()) {
            // Podstawowe sterowanie (WASD + QE)
            case KeyEvent.VK_A: // Lewo
                camera.setX(camera.getX() - moveSpeed);
                break;
            case KeyEvent.VK_E: // Prawo
                camera.setX(camera.getX() + moveSpeed);
                break;
            case KeyEvent.VK_COMMA: // Góra
                camera.setY(camera.getY() + moveSpeed);
                break;
            case KeyEvent.VK_O: // Dół
                camera.setY(camera.getY() - moveSpeed);
                break;
            case KeyEvent.VK_QUOTE: // Przód
                camera.setZ(camera.getZ() - moveSpeed);
                break;
            case KeyEvent.VK_PERIOD: // Tył
                camera.setZ(camera.getZ() + moveSpeed);
                break;

            // Sterowanie obrotem kamery wokół osi Y (lewo/prawo)
            case KeyEvent.VK_LEFT: // Obrót w lewo
                camera.setRotY(camera.getRotY() + rotateSpeed);
                break;
            case KeyEvent.VK_RIGHT: // Obrót w prawo
                camera.setRotY(camera.getRotY() - rotateSpeed);
                break;

            // Sterowanie obrotem kamery wokół osi X (góra/dół)
            case KeyEvent.VK_UP: // Obrót w górę
                camera.setRotX(camera.getRotX() + rotateSpeed);
                break;
            case KeyEvent.VK_DOWN: // Obrót w dół
                camera.setRotX(camera.getRotX() - rotateSpeed);
                break;

            // Sterowanie obrotem kamery wokół osi Z (przechylanie)
            case KeyEvent.VK_SEMICOLON: // Przechylanie w lewo
                camera.setRotZ(camera.getRotZ() - rotateSpeed);
                break;
            case KeyEvent.VK_Q: // Przechylanie w prawo
                camera.setRotZ(camera.getRotZ() + rotateSpeed);
                break;

            // Sterowanie polem widzenia (FOV)
            case KeyEvent.VK_CLOSE_BRACKET:
                camera.setFov(Math.max(camera.getFov() - fovChangeSpeed, 30)); // Minimalnie 30 stopni
                break;
            case KeyEvent.VK_OPEN_BRACKET:
                camera.setFov(Math.min(camera.getFov() + fovChangeSpeed, 120)); // Maksymalnie 120 stopni
                break;
        }

        panel.repaint();

        System.out.println("Pozycja kamery: [" + camera.getX() + ", " +
                camera.getY() + ", " +
                camera.getZ() + "], Rotacja: [" +
                camera.getRotX() + ", " +
                camera.getRotY() + ", " +
                camera.getRotZ() + "], FOV: " +
                camera.getFov());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}