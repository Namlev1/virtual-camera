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

    // Metoda zwracająca macierz widoku z uwzględnieniem rotacji wokół wszystkich osi
    public double[][] getViewMatrix() {
        // Tworzymy macierze rotacji wokół osi X, Y, Z

        // Macierz rotacji wokół osi X (góra/dół)
        double sinX = Math.sin(rotX);
        double cosX = Math.cos(rotX);
        double[][] rotationMatrixX = {
                {1, 0,    0,     0},
                {0, cosX, -sinX, 0},
                {0, sinX, cosX,  0},
                {0, 0,    0,     1}
        };

        // Macierz rotacji wokół osi Y (lewo/prawo)
        double sinY = Math.sin(rotY);
        double cosY = Math.cos(rotY);
        double[][] rotationMatrixY = {
                {cosY,  0, sinY, 0},
                {0,     1, 0,    0},
                {-sinY, 0, cosY, 0},
                {0,     0, 0,    1}
        };

        // Macierz rotacji wokół osi Z (przechylanie)
        double sinZ = Math.sin(rotZ);
        double cosZ = Math.cos(rotZ);
        double[][] rotationMatrixZ = {
                {cosZ, -sinZ, 0, 0},
                {sinZ, cosZ,  0, 0},
                {0,    0,     1, 0},
                {0,    0,     0, 1}
        };

        // Macierz translacji
        double[][] translationMatrix = {
                {1, 0, 0, -x},
                {0, 1, 0, -y},
                {0, 0, 1, -z},
                {0, 0, 0, 1}
        };

        // Mnożymy macierze rotacji i translacji w odpowiedniej kolejności
        // Kolejność ma znaczenie: najpierw rotacja Z, potem X, potem Y, na końcu translacja
        double[][] rotationMatrix = multiplyMatrices(rotationMatrixY,
                multiplyMatrices(rotationMatrixX,
                        rotationMatrixZ));

        return multiplyMatrices(rotationMatrix, translationMatrix);
    }

    // Metoda pomocnicza do mnożenia macierzy 4x4
    private double[][] multiplyMatrices(double[][] matrixA, double[][] matrixB) {
        double[][] result = new double[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    result[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }

        return result;
    }

    // Metoda zwracająca macierz rzutowania perspektywicznego zgodną ze slajdem 1 (M_p1)
    public double[][] getProjectionMatrix() {
        // Na slajdzie 1 mamy macierz M_p1 o strukturze:
        // [1  0  0  0]
        // [0  1  0  0]
        // [0  0  1  0]
        // [0  0  1/d 0]
        //
        // Gdzie:
        // - Rzutnia: z = d, d > 0
        // - Środek rzutowania: [0,0,0]

        // Stała odległość rzutni od środka rzutowania
        double d = 5.0;

        // Uwzględnienie FOV (pole widzenia)
        // Im większy FOV, tym silniejszy efekt perspektywy
        double fovScale = Math.tan(Math.toRadians(fov) / 2) / Math.tan(Math.toRadians(60) / 2);

        // Macierz rzutowania perspektywicznego M_p1 ze slajdu
        return new double[][] {
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, fovScale/d, 0}
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