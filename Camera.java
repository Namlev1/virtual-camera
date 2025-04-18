package v3;
public class Camera {
    private double x, y, z;           // Pozycja kamery
    private double rotX, rotY, rotZ;  // Kąty rotacji wokół osi X, Y, Z (w radianach)
    private double fov;               // Pole widzenia (w stopniach)
    private double aspectRatio;       // Stosunek szerokości do wysokości ekranu
    private double near, far;         // Płaszczyzny przycięcia

    public Camera(double x, double y, double z, double fov, double aspectRatio, double near, double far) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotX = 0.0;
        this.rotY = 0.0;
        this.rotZ = 0.0;
        this.fov = fov;
        this.aspectRatio = aspectRatio;
        this.near = near;
        this.far = far;
    }

    // Metoda zwracająca macierz widoku
    public double[][] getViewMatrix() {
        // Macierz translacji
        double[][] translationMatrix = {
                {1, 0, 0, -x},
                {0, 1, 0, -y},
                {0, 0, 1, -z},
                {0, 0, 0, 1}
        };

        // Na razie używamy tylko macierzy translacji
        // W pełnej implementacji należałoby dodać również macierze rotacji
        return translationMatrix;
    }

    // Metoda zwracająca macierz rzutowania perspektywicznego zgodną ze slajdem
    public double[][] getProjectionMatrix() {
        // Macierz rzutowania perspektywicznego M_p2 ze slajdu
        // Gdzie d to odległość środka rzutowania od rzutni (d = |z|)
        double d = Math.abs(z);  // Używamy wartości bezwzględnej z pozycji kamery

        return new double[][] {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 1/d, 1}
        };
    }

    // Gettery i settery
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public double getRotX() { return rotX; }
    public double getRotY() { return rotY; }
    public double getRotZ() { return rotZ; }
    public double getFov() { return fov; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }
    public void setRotX(double rotX) { this.rotX = rotX; }
    public void setRotY(double rotY) { this.rotY = rotY; }
    public void setRotZ(double rotZ) { this.rotZ = rotZ; }
    public void setFov(double fov) { this.fov = fov; }
}