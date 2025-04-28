package src;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;
import org.apache.commons.math3.complex.Quaternion;

@Data
public class Camera {
    // Stałe dla kamery
    private float d;
    private float d_step = 0.05f;
    private float near = 0.1f;
    private float far = 100.0f;
    private float widthToHeightRatio;
    private int WIDTH;
    private int HEIGHT;

    // Wektory określające pozycję i orientację kamery
    private SimpleMatrix cameraPosition;
    private SimpleMatrix cameraForward;
    private SimpleMatrix cameraUp;
    private SimpleMatrix cameraRight;
    private float cameraStep = 0.2f;

    // Macierze projekcji i widoku
    private SimpleMatrix projectionMatrix;
    private SimpleMatrix viewMatrix;

    public Camera(float d, int width, int height) {
        this.d = d;
        this.WIDTH = width;
        this.HEIGHT = height;
        this.widthToHeightRatio = (float) width / (float) height;

        // Inicjalizacja wektorów kamery
        this.cameraPosition = new SimpleMatrix(3, 1, true, new float[]{0.0f, 0.0f, 0.0f});
        this.cameraForward = new SimpleMatrix(3, 1, true, new float[]{0.0f, 0.0f, 1.0f});
        this.cameraUp = new SimpleMatrix(3, 1, true, new float[]{0.0f, 1.0f, 0.0f});
        this.cameraRight = new SimpleMatrix(3, 1, true, new float[]{1.0f, 0.0f, 0.0f});

        // Inicjalizacja macierzy projekcji
        this.projectionMatrix = new SimpleMatrix(4, 4);

        // Inicjalizacja macierzy widoku jako macierzy jednostkowej
        this.viewMatrix = SimpleMatrix.identity(4);

        // Ustawienie pozycji kamery w macierzy widoku
        viewMatrix.set(0, 3, cameraPosition.get(0));
        viewMatrix.set(1, 3, cameraPosition.get(1));
        viewMatrix.set(2, 3, cameraPosition.get(2));

        // Przeliczenie macierzy projekcji
        recalculateProjectionMatrix();
    }

    public void stepD(int value) {
        if (value > 0 && d - d_step > 0) {
            d -= d_step;
        } else if (value < 0) {
            d += d_step;
        }
        recalculateProjectionMatrix();
    }

    public void recalculateProjectionMatrix() {
        float f = (float) (1.0f / Math.tan(d / 2.0f));

        // Zerowanie macierzy
        projectionMatrix = new SimpleMatrix(4, 4);

        // Ustawienie wartości macierzy projekcji
        projectionMatrix.set(0, 0, f / widthToHeightRatio);
        projectionMatrix.set(1, 1, f);
        projectionMatrix.set(2, 2, (far + near) / (far - near));
        projectionMatrix.set(2, 3, 2 * far * near / (far - near));
        projectionMatrix.set(3, 2, 1.0f);
    }

    public void recalculateViewMatrix() {
        SimpleMatrix rotation = new SimpleMatrix(3, 3);

        for (int i = 0; i < 3; i++) {
            rotation.set(i, 0, cameraRight.get(i));
            rotation.set(i, 1, cameraUp.get(i));
            rotation.set(i, 2, cameraForward.get(i));
        }

        // Transponowanie macierzy rotacji
        SimpleMatrix rotationTransposed = rotation.transpose();

        // Obliczenie części translacyjnej macierzy widoku
        SimpleMatrix translationPart = rotationTransposed.mult(cameraPosition).negative();

        // Inicjalizacja macierzy widoku jako macierzy jednostkowej
        viewMatrix = SimpleMatrix.identity(4);

        // Ustawienie części rotacyjnej (3x3 w lewym górnym rogu)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                viewMatrix.set(i, j, rotationTransposed.get(i, j));
            }
        }

        // Ustawienie części translacyjnej (pierwsze 3 elementy ostatniej kolumny)
        for (int i = 0; i < 3; i++) {
            viewMatrix.set(i, 3, translationPart.get(i));
        }
    }

    public SimpleMatrix projectPoint(SimpleMatrix point) {
        return projectionMatrix.mult(viewMatrix).mult(point);
    }

    public void moveCameraRight(int shift) {
        SimpleMatrix displacement = cameraRight.scale(cameraStep);
        if (shift > 0) {
            cameraPosition = cameraPosition.plus(displacement);
        } else {
            cameraPosition = cameraPosition.minus(displacement);
        }
        recalculateViewMatrix();
    }

    public void moveCameraForward(int shift) {
        SimpleMatrix displacement = cameraForward.scale(cameraStep);
        if (shift > 0) {
            cameraPosition = cameraPosition.plus(displacement);
        } else {
            cameraPosition = cameraPosition.minus(displacement);
        }
        recalculateViewMatrix();
    }

    public void moveCameraUp(int shift) {
        SimpleMatrix displacement = cameraUp.scale(cameraStep);
        if (shift > 0) {
            cameraPosition = cameraPosition.plus(displacement);
        } else {
            cameraPosition = cameraPosition.minus(displacement);
        }
        recalculateViewMatrix();
    }

    public void rotateCamera(int mouseX, int mouseY, int mouseZ) {
        // Konwersja kątów ze stopni na radiany
        float angleX = mouseX * (float) Math.PI / 180.0f;
        float angleY = mouseY * (float) Math.PI / 180.0f;
        float angleZ = mouseZ * (float) Math.PI / 180.0f;

        // Pobieranie aktualnych osi kamery jako osi obrotu
        double[] axisX = { cameraRight.get(0), cameraRight.get(1), cameraRight.get(2) };
        double[] axisY = { cameraUp.get(0), cameraUp.get(1), cameraUp.get(2) };
        double[] axisZ = { cameraForward.get(0), cameraForward.get(1), cameraForward.get(2) };

        Quaternion qx = createQuaternionFromAxisAngle(axisX, angleX);
        Quaternion qy = createQuaternionFromAxisAngle(axisY, angleY);
        Quaternion qz = createQuaternionFromAxisAngle(axisZ, angleZ);

        Quaternion q = qy.multiply(qx).multiply(qz);

        double[] forwardRotated = rotateVectorByQuaternion(
                new double[] { cameraForward.get(0), cameraForward.get(1), cameraForward.get(2) }, q);
        double[] rightRotated = rotateVectorByQuaternion(
                new double[] { cameraRight.get(0), cameraRight.get(1), cameraRight.get(2) }, q);
        double[] upRotated = rotateVectorByQuaternion(
                new double[] { cameraUp.get(0), cameraUp.get(1), cameraUp.get(2) }, q);

        // Aktualizacja wektorów bazowych kamery
        cameraForward = new SimpleMatrix(3, 1, true, new float[] {
                (float)forwardRotated[0], (float)forwardRotated[1], (float)forwardRotated[2]
        });
        cameraRight = new SimpleMatrix(3, 1, true, new float[] {
                (float)rightRotated[0], (float)rightRotated[1], (float)rightRotated[2]
        });
        cameraUp = new SimpleMatrix(3, 1, true, new float[] {
                (float)upRotated[0], (float)upRotated[1], (float)upRotated[2]
        });

        // Normalizacja
        cameraForward = normalizeVector(cameraForward);
        cameraUp = normalizeVector(cameraUp);
        cameraRight = normalizeVector(cameraRight);

        // Po rotacji musimy upewnić się, że wektory bazy pozostają ortogonalne
        // Poprawiamy wektor "right" jako iloczyn wektorowy "forward" i "up"
        cameraRight = crossProduct(cameraUp, cameraForward);
        cameraRight = normalizeVector(cameraRight);

        // Następnie poprawiamy wektor "up" jako iloczyn wektorowy "forward" i "right"
        cameraUp = crossProduct(cameraForward, cameraRight);
        cameraUp = normalizeVector(cameraUp);

        recalculateViewMatrix();
    }

    private Quaternion createQuaternionFromAxisAngle(double[] axis, double angle) {
        // Normalizacja osi
        double length = Math.sqrt(axis[0] * axis[0] + axis[1] * axis[1] + axis[2] * axis[2]);
        if (length > 1e-6) {
            axis[0] /= length;
            axis[1] /= length;
            axis[2] /= length;
        }

        double halfAngle = angle / 2.0;
        double sinHalfAngle = Math.sin(halfAngle);

        double w = Math.cos(halfAngle);
        double x = axis[0] * sinHalfAngle;
        double y = axis[1] * sinHalfAngle;
        double z = axis[2] * sinHalfAngle;

        return new Quaternion(w, x, y, z);
    }

    private double[] rotateVectorByQuaternion(double[] v, Quaternion q) {
        Quaternion vq = new Quaternion(0, v[0], v[1], v[2]);
        Quaternion qConj = q.getConjugate();

        // Rotacja: q * vq * q^-1
        Quaternion rotated = q.multiply(vq).multiply(qConj);

        return new double[] { rotated.getQ1(), rotated.getQ2(), rotated.getQ3() };
    }

    /**
     * Oblicza iloczyn wektorowy dwóch wektorów 3D
     */
    private SimpleMatrix crossProduct(SimpleMatrix a, SimpleMatrix b) {
        SimpleMatrix result = new SimpleMatrix(3, 1);

        result.set(0, a.get(1) * b.get(2) - a.get(2) * b.get(1));
        result.set(1, a.get(2) * b.get(0) - a.get(0) * b.get(2));
        result.set(2, a.get(0) * b.get(1) - a.get(1) * b.get(0));

        return result;
    }

    private SimpleMatrix normalizeVector(SimpleMatrix vector) {
        float length = (float) Math.sqrt(
                vector.get(0) * vector.get(0) +
                        vector.get(1) * vector.get(1) +
                        vector.get(2) * vector.get(2)
        );

        if (length < 1e-6f) {
            return vector;  // Unikanie dzielenia przez zero
        }

        return vector.scale(1.0f / length);
    }

    // Gettery
    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }
}