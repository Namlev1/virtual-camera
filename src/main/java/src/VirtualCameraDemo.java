package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class VirtualCameraDemo extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private Camera camera;
    private Renderer renderer;
    private List<Shape> shapes;

    public VirtualCameraDemo() {
        // Inicjalizacja kamery z początkowym parametrem d = 2.0
        camera = new Camera(2.0f, WIDTH, HEIGHT);

        // Inicjalizacja renderera
        renderer = new Renderer(camera, null);

        // Stworzenie kształtów do wyświetlenia (logo code blocks - 4 kostki)
        shapes = new ArrayList<>();

        // Dodanie czterech kostek imitujących logo Code::Blocks
        shapes.add(new Cube(-1.5f, -1.5f, 4.0f, 1.0f, 1.0f, 1.0f));  // Lewa dolna
        shapes.add(new Cube(1.5f, -1.5f, 4.0f, 1.0f, 1.0f, 1.0f));   // Prawa dolna
        shapes.add(new Cube(-1.5f, 1.5f, 4.0f, 1.0f, 1.0f, 1.0f));   // Lewa górna
        shapes.add(new Cube(1.5f, 1.5f, 4.0f, 1.0f, 1.0f, 1.0f));    // Prawa górna

        // Ustawienie obsługi klawiatury
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
    }

    /**
     * Obsługa naciśnięć klawiszy sterujących kamerą
     */
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
            case KeyEvent.VK_CLOSE_BRACKET:  // Przybliżenie (zmniejszenie FOV) (])
                camera.stepD(1);
                break;
            case KeyEvent.VK_OPEN_BRACKET:  // Oddalenie (zwiększenie FOV) ([)
                camera.stepD(-1);
                break;
        }

        // Odświeżenie widoku
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Czyszczenie ekranu
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Rysowanie siatki dla lepszej orientacji
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < getWidth(); i += 50) {
            g.drawLine(i, 0, i, getHeight());
        }
        for (int i = 0; i < getHeight(); i += 50) {
            g.drawLine(0, i, getWidth(), i);
        }

        // Ustawienie koloru rysowania
        g.setColor(Color.BLACK);

        // Aktualizacja renderera z aktualnym kontekstem graficznym
        renderer.setGraphics(g);

        // Renderowanie wszystkich kształtów
        for (Shape shape : shapes) {
            renderer.drawShape(shape);
        }

        // Wyświetlenie informacji o sterowaniu
        drawControlsInfo(g);
    }

    /**
     * Wyświetla informacje o sterowaniu
     */
    private void drawControlsInfo(Graphics g) {
        g.setColor(Color.BLACK);
        int y = 20;

        g.drawString("Sterowanie kamerą:", 10, y); y += 20;
        g.drawString("W/S/A/D/Q/E - Translacja (przód/tył/lewo/prawo/dół/góra)", 10, y); y += 20;
        g.drawString("Strzałki - Obrót góra/dół/lewo/prawo", 10, y); y += 20;
        g.drawString("Z/X - Obrót wokół osi Z", 10, y); y += 20;
        g.drawString("+/- - Zmiana FOV (zoom)", 10, y); y += 20;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Wirtualna Kamera 3D");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(WIDTH, HEIGHT);
            frame.setResizable(false);

            VirtualCameraDemo cameraDemo = new VirtualCameraDemo();
            frame.add(cameraDemo);

            frame.setVisible(true);
            cameraDemo.requestFocusInWindow();
        });
    }
}