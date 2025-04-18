package v3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private Camera camera;
    private Cube cube;
    private Renderer renderer;

    public Main() {
        setTitle("Wirtualna Kamera 3D");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicjalizacja kamery
        // Początkowa pozycja kamery to (0, 0, -5), czyli 5 jednostek od środka sześcianu
        // FOV = 60 stopni, proporcje ekranu = szerokość/wysokość, near = 0.1, far = 100
        camera = new Camera(0, 0, -5, 60, (double)WIDTH / HEIGHT, 0.1, 100);

        // Inicjalizacja sześcianu o wielkości 2
        cube = new Cube(2.0);

        // Inicjalizacja renderera
        renderer = new Renderer(camera, WIDTH, HEIGHT);

        // Panel do rysowania
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Czyszczenie ekranu
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Rysowanie sześcianu
                renderer.renderCube(g, cube);
            }
        };

        // Dodanie obsługi klawiatury (na razie bez implementacji)
        panel.setFocusable(true);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
                panel.repaint();
            }
        });

        add(panel);
    }

    private void handleKeyPress(KeyEvent e) {
        // Na razie bez implementacji ruchów kamery
        // Będzie to dodane w następnym kroku
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}