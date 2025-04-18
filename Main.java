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
    private JPanel panel;  // Dodana deklaracja panelu jako pole klasy

    public Main() {
        setTitle("Wirtualna Kamera 3D");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicjalizacja kamery
        // Początkowa pozycja kamery to (0, 0, -5), czyli kamery jest w punkcie [0,0,-5]
        // Co odpowiada środkowi rzutowania [0,0,-d] ze slajdu, gdzie d = 5
        // FOV = 60 stopni, proporcje ekranu = szerokość/wysokość, near = 0.1, far = 100
        camera = new Camera(0, 0, -5, 60, (double)WIDTH / HEIGHT, 0.1, 100);

        // Inicjalizacja sześcianu o wielkości 2
        cube = new Cube(2.0);

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

                // Rysowanie sześcianu
                renderer.renderCube(g, cube);

                // Wyświetlanie informacji o projekcji
                g.setColor(Color.BLACK);
                g.drawString("Wirtualna Kamera 3D - Rzutowanie perspektywiczne", 10, 20);
                g.drawString("Macierz rzutowania ze slajdu: M_p2", 10, 40);
                g.drawString("Środek rzutowania: [" + camera.getX() + ", " + camera.getY() + ", " + camera.getZ() + "]", 10, 60);
                g.drawString("Rotacja wokół osi Y: " + String.format("%.2f", camera.getRotY()) + " rad", 10, 80);
                g.drawString("Rzutnia: płaszczyzna z = 0", 10, 100);
                g.drawString("Sterowanie: WASD - poruszanie kamerą, QE - przód/tył, ←→ - obrót", 10, 120);
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

            // Sterowanie obrotem kamery
            case KeyEvent.VK_LEFT: // Obrót w lewo (wokół osi Y)
                camera.setRotY(camera.getRotY() + rotateSpeed);
                break;
            case KeyEvent.VK_RIGHT: // Obrót w prawo (wokół osi Y)
                camera.setRotY(camera.getRotY() - rotateSpeed);
                break;
        }

        // Odświeżenie panelu po każdej zmianie
        panel.repaint();

        // Aktualizacja informacji o pozycji kamery
        System.out.println("Pozycja kamery: [" + camera.getX() + ", " +
                camera.getY() + ", " +
                camera.getZ() + "], Rotacja Y: " +
                camera.getRotY());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}