package src.lighting;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;
import src.Face;
import src.bsp.Plane;
import java.awt.Color;

@Data
public class Material {
    private float ambientCoef;
    private float diffuseCoef;
    private float specularCoef;
    private int shininess;

    public Material(float ambientCoef, float diffuseCoef, float specularCoef, int shininess) {
        this.ambientCoef = ambientCoef;
        this.diffuseCoef = diffuseCoef;
        this.specularCoef = specularCoef;
        this.shininess = shininess;
    }

    public Color calculatePhongColor(Face face, SimpleMatrix cameraPosition, Light light) {
        // Oblicz normalną ściany używając istniejącej funkcjonalności
        Plane plane = Plane.fromFace(face);
        SimpleMatrix normal = new SimpleMatrix(3, 1, true, new float[]{plane.getA(), plane.getB(), plane.getC()});

        // Oblicz środek ściany (centroid)
        SimpleMatrix centroid = calculateFaceCentroid(face);

        // Wektor kierunku światła
        SimpleMatrix lightDirection = light.getPosition().minus(centroid);
        lightDirection = normalizeVector(lightDirection);

        // Wektor kierunku obserwatora
        SimpleMatrix viewDirection = cameraPosition.minus(centroid);
        viewDirection = normalizeVector(viewDirection);

        // Wektor odbicia (reflection vector)
        float dotNL = (float) dotProduct(normal, lightDirection);
        SimpleMatrix reflection = normal.scale(2 * dotNL).minus(lightDirection);
        reflection = normalizeVector(reflection);

        // Składowa ambient
        float ambient = light.getAmbientIntensity() * ambientCoef;

        // Składowa diffuse
        float diffuse = Math.max(0, dotNL) * diffuseCoef * light.getIntensity();

        // Składowa specular
        float specular = 0;
        if (dotNL > 0) {
            float dotRV = (float) Math.max(0, dotProduct(reflection, viewDirection));
            specular = (float) Math.pow(dotRV, shininess) * specularCoef * light.getIntensity();
        }

        // Połącz wszystkie składowe
        float r = clamp(ambient + diffuse + specular) * light.getColor().getRed() / 255f;
        float g = clamp(ambient + diffuse + specular) * light.getColor().getGreen() / 255f;
        float b = clamp(ambient + diffuse + specular) * light.getColor().getBlue() / 255f;

        // Modyfikuj kolor ściany na podstawie światła
        return new Color(
                clamp(face.getColor().getRed() / 255f * r),
                clamp(face.getColor().getGreen() / 255f * g),
                clamp(face.getColor().getBlue() / 255f * b)
        );
    }

    private float clamp(float value) {
        return Math.max(0, Math.min(1, value));
    }

    private SimpleMatrix calculateFaceCentroid(Face face) {
        SimpleMatrix centroid = new SimpleMatrix(3, 1);
        int vertexCount = face.getEdges().size();

        for (int i = 0; i < vertexCount; i++) {
            centroid = centroid.plus(face.getEdges().get(i).getStart());
        }

        return centroid.scale(1.0f / vertexCount);
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

    private double dotProduct(SimpleMatrix a, SimpleMatrix b) {
        return a.get(0) * b.get(0) + a.get(1) * b.get(1) + a.get(2) * b.get(2);
    }
}