package src;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;
import org.apache.commons.math3.complex.Quaternion;

@Data
public class Camera {
    private float d = 5.0f;
    private float d_step = 0.1f;
    private float fov = 60.0f;
    
    private float widthToHeightRatio;
    private int WIDTH;
    private int HEIGHT;

    private SimpleMatrix cameraPosition;
    private SimpleMatrix cameraForward;
    private SimpleMatrix cameraUp;
    private SimpleMatrix cameraRight;
    private float cameraStep = 0.2f;

    private SimpleMatrix projectionMatrix;
    private SimpleMatrix viewMatrix;

    public Camera(float d, int width, int height) {
        this.d = d;
        this.WIDTH = width;
        this.HEIGHT = height;
        this.widthToHeightRatio = (float) width / (float) height;

        this.cameraPosition = new SimpleMatrix(3, 1, true, new float[]{0.0f, 0.0f, 0.0f});
        this.cameraForward = new SimpleMatrix(3, 1, true, new float[]{0.0f, 0.0f, 1.0f});
        this.cameraUp = new SimpleMatrix(3, 1, true, new float[]{0.0f, 1.0f, 0.0f});
        this.cameraRight = new SimpleMatrix(3, 1, true, new float[]{1.0f, 0.0f, 0.0f});

        this.projectionMatrix = new SimpleMatrix(4, 4);

        this.viewMatrix = SimpleMatrix.identity(4);
        viewMatrix.set(0, 3, cameraPosition.get(0));
        viewMatrix.set(1, 3, cameraPosition.get(1));
        viewMatrix.set(2, 3, cameraPosition.get(2));

        recalculateProjectionMatrix();
    }

    public void changeZoom(int value) {
        if (value > 0) {
            fov = Math.max(fov - 5.0f, 20.0f);  // Min FOV: 20 stopni
        } else if (value < 0) {
            fov = Math.min(fov + 5.0f, 120.0f);  // Max FOV: 120 stopni
        }
        recalculateProjectionMatrix();
    }
    
    public void recalculateProjectionMatrix() {
        projectionMatrix = new SimpleMatrix(4, 4);

        float fovScale = (float) (Math.tan(Math.toRadians(fov) / 2) / Math.tan(Math.toRadians(60) / 2));

        projectionMatrix.set(0, 0, 1.0f);
        projectionMatrix.set(1, 1, 1.0f);
        projectionMatrix.set(2, 2, 1.0f);
        projectionMatrix.set(3, 2, fovScale / d);  // Współczynnik perspektywy
    }
    
    public void recalculateViewMatrix() {
        SimpleMatrix rotation = new SimpleMatrix(3, 3);

        for (int i = 0; i < 3; i++) {
            rotation.set(i, 0, cameraRight.get(i));
            rotation.set(i, 1, cameraUp.get(i));
            rotation.set(i, 2, cameraForward.get(i));
        }

        SimpleMatrix rotationTransposed = rotation.transpose();
        SimpleMatrix translationPart = rotationTransposed.mult(cameraPosition).negative();
        viewMatrix = SimpleMatrix.identity(4);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                viewMatrix.set(i, j, rotationTransposed.get(i, j));
            }
        }

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
        float angleX = mouseX * (float) Math.PI / 180.0f;
        float angleY = mouseY * (float) Math.PI / 180.0f;
        float angleZ = mouseZ * (float) Math.PI / 180.0f;

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

        cameraForward = new SimpleMatrix(3, 1, true, new float[] {
                (float)forwardRotated[0], (float)forwardRotated[1], (float)forwardRotated[2]
        });
        cameraRight = new SimpleMatrix(3, 1, true, new float[] {
                (float)rightRotated[0], (float)rightRotated[1], (float)rightRotated[2]
        });
        cameraUp = new SimpleMatrix(3, 1, true, new float[] {
                (float)upRotated[0], (float)upRotated[1], (float)upRotated[2]
        });

        cameraForward = normalizeVector(cameraForward);
        cameraUp = normalizeVector(cameraUp);
        cameraRight = normalizeVector(cameraRight);

        cameraRight = crossProduct(cameraUp, cameraForward);
        cameraRight = normalizeVector(cameraRight);

        cameraUp = crossProduct(cameraForward, cameraRight);
        cameraUp = normalizeVector(cameraUp);

        recalculateViewMatrix();
    }

    private Quaternion createQuaternionFromAxisAngle(double[] axis, double angle) {
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

        // q * vq * q^-1
        Quaternion rotated = q.multiply(vq).multiply(qConj);

        return new double[] { rotated.getQ1(), rotated.getQ2(), rotated.getQ3() };
    }

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
            return vector;
        }

        return vector.scale(1.0f / length);
    }
}