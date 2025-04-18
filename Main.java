package v3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private Camera camera;
    private List<Cube> cubes; // Zmiana na listę sześcianów
    private Renderer renderer;
    private JPanel panel;  // Dodana deklaracja panelu jako pole klasy

    public Main() {
        setTitle("Wirtualna Kamera 3D");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicjalizacja kamery
        // Początkowa pozycja kamery to (0, 0, -10), czyli kamery jest w punkcie [0,0,-10]
        // Co odpowiada środkowi rzutowania [0,0,-d] ze slajdu, gdzie d = 10
        // FOV = 60 stopni, proporcje ekranu = szerokość/wysokość, near = 0.1, far = 100
        camera = new Camera(0, 0, -5, 60, (double)WIDTH / HEIGHT, 0.1, 100);

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
                g.drawString("Macierz rzutowania ze slajdu: M_p2", 10, 40);
                g.drawString("Środek rzutowania: [" + camera.getX() + ", " + camera.getY() + ", " + camera.getZ() + "]", 10, 60);
                g.drawString("Rotacja X: " + String.format("%.2f", camera.getRotX()) + " rad", 10, 80);
                g.drawString("Rotacja Y: " + String.format("%.2f", camera.getRotY()) + " rad", 10, 100);
                g.drawString("Rotacja Z: " + String.format("%.2f", camera.getRotZ()) + " rad", 10, 120);
                g.drawString("Rzutnia: płaszczyzna z = 0", 10, 140);
                g.drawString("Sterowanie: WASD - poruszanie, QE - przód/tył, ←→↑↓ - obrót X/Y, ;/Q - obrót Z", 10, 160);
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

    // Metoda tworząca sześciany ułożone podobnie do logo Code::Blocks
    private List<Cube> createCodeBlocksLogo() {
        List<Cube> cubeList = new ArrayList<>();

        // Rozmiar sześcianu
        double size = 1.5;
        // Odległość między sześcianami - zmniejszona z 2.5 na 2.0
        double spacing = 1.5;

        // Sześcian w lewym górnym rogu (C)
        cubeList.add(new Cube(size, -spacing, spacing, 0));

        // Sześcian w prawym górnym rogu (B)
        cubeList.add(new Cube(size, spacing, spacing, 0));

        // Sześcian w lewym dolnym rogu (pierwszy :)
        cubeList.add(new Cube(size, -spacing, -spacing, 0));

        // Sześcian w prawym dolnym rogu (drugi :)
        cubeList.add(new Cube(size, spacing, -spacing, 0));

        return cubeList;
    }

    private void handleKeyPress(KeyEvent e) {
        // Stała określająca o ile jednostek przesuwać kamerę przy każdym naciśnięciu klawisza
        double moveSpeed = 0.5;
        // Stała określająca o ile radianów obracać kamerę przy każdym naciśnięciu klawisza
        double rotateSpeed = 0.1;

        switch (e.getKeyCode()) {
            // Podstawowe sterowanie (WASD + QE)
            case KeyEvent.VK_A: // Lewo
                camera.setX(camera.getX() - moveSpeed);
                break;
            case KeyEvent.VK_E: // Prawo
                camera.setX(camera.getX() + moveSpeed);
                break;
            case KeyEvent.VK_COMMA:
                camera.setY(camera.getY() + moveSpeed);
                break;
            case KeyEvent.VK_O: // Dół
                camera.setY(camera.getY() - moveSpeed);
                break;
            case KeyEvent.VK_QUOTE: // Do przodu
                camera.setZ(camera.getZ() - moveSpeed);
                break;
            case KeyEvent.VK_PERIOD:
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
        }

        // Odświeżenie panelu po każdej zmianie
        panel.repaint();

        // Aktualizacja informacji o pozycji kamery
        System.out.println("Pozycja kamery: [" + camera.getX() + ", " +
                camera.getY() + ", " +
                camera.getZ() + "], Rotacja: [" +
                camera.getRotX() + ", " +
                camera.getRotY() + ", " +
                camera.getRotZ() + "]");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}