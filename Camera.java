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

    // Metoda zwracająca macierz rzutowania perspektywicznego
    public double[][] getProjectionMatrix() {
        double f = 1.0 / Math.tan(Math.toRadians(fov) / 2.0);
        double rangeInv = 1.0 / (near - far);

        // Macierz rzutowania perspektywicznego
        return new double[][] {
                {f / aspectRatio, 0, 0, 0},
                {0, f, 0, 0},
                {0, 0, (near + far) * rangeInv, 2 * near * far * rangeInv},
                {0, 0, -1, 0}
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