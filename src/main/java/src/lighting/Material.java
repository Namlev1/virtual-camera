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
        // Oblicz normalną ściany
        Plane plane = Plane.fromFace(face);
        SimpleMatrix normal = new SimpleMatrix(3, 1, true, new float[]{plane.getA(), plane.getB(), plane.getC()});

        // Oblicz centroid (środek) ściany
        SimpleMatrix centroid = calculateFaceCentroid(face);

        // Upewnij się, że normalna jest poprawnie zorientowana
        SimpleMatrix fromCenter = centroid.minus(new SimpleMatrix(3, 1, true,
                new float[]{0.0f, 0.0f, 4.0f}));
        if (dotProduct(normal, fromCenter) < 0) {
            normal = normal.scale(-1);
        }

        // Kierunek światła (od punktu do światła)
        SimpleMatrix lightVector = light.getPosition().minus(centroid);
        float distanceToLight = vectorLength(lightVector);
        SimpleMatrix lightDirection = normalizeVector(lightVector);

        // Kierunek do obserwatora
        SimpleMatrix viewVector = cameraPosition.minus(centroid);
        SimpleMatrix viewDirection = normalizeVector(viewVector);

        // Składowa ambient (otoczenia)
        float ambient = light.getAmbientIntensity() * ambientCoef * 0.2f;

        // POPRAWIONY współczynnik tłumienia - zwiększamy znacznie wpływ odległości
        // Stosujemy tutaj fizyczny model tłumienia światła - odwrotność kwadratu odległości
        // constant + linear*d + quadratic*d^2
        float constant = 1.0f;       // Stała wartość
        float linear = 0.5f;         // Liniowe tłumienie
        float quadratic = 0.2f;      // Kwadratowe tłumienie (fizycznie poprawne)

        float attenuationFactor = 1.0f / (constant + linear * distanceToLight + quadratic * distanceToLight * distanceToLight);

        // Ograniczanie attenuationFactor, aby uniknąć zbyt dużych wartości dla małych odległości
        attenuationFactor = Math.min(1.0f, attenuationFactor);

        // Składowa diffuse (rozproszenia) z nowym tłumieniem
        float dotNL = (float) Math.max(0, dotProduct(normal, lightDirection));
        float diffuse = dotNL * diffuseCoef * light.getIntensity() * attenuationFactor;

        // Obliczanie wektora odbicia
        SimpleMatrix reflection = normal.scale(2 * dotNL).minus(lightDirection);
        reflection = normalizeVector(reflection);

        // Składowa specular (odbicia) z nowym tłumieniem
        float dotRV = (float) Math.max(0, dotProduct(reflection, viewDirection));
        float specular = 0;
        if (dotNL > 0) {
            specular = (float) Math.pow(dotRV, shininess) * specularCoef * light.getIntensity() * attenuationFactor;
        }

        // Połącz składowe z kolorem bazowym
        float baseR = face.getColor().getRed() / 255f;
        float baseG = face.getColor().getGreen() / 255f;
        float baseB = face.getColor().getBlue() / 255f;

        float r = baseR * ambient;
        float g = baseG * ambient;
        float b = baseB * ambient;

        // Dodajemy składową diffuse
        r += baseR * diffuse * light.getColor().getRed() / 255f;
        g += baseG * diffuse * light.getColor().getGreen() / 255f;
        b += baseB * diffuse * light.getColor().getBlue() / 255f;

        // Dodajemy składową specular
        float specR = specular * light.getColor().getRed() / 255f;
        float specG = specular * light.getColor().getGreen() / 255f;
        float specB = specular * light.getColor().getBlue() / 255f;

        r += specR;
        g += specG;
        b += specB;

        return new Color(clamp(r), clamp(g), clamp(b));
    }
    
    // Pomocnicza metoda do obliczania długości wektora
    private float vectorLength(SimpleMatrix vector) {
        return (float) Math.sqrt(
                vector.get(0) * vector.get(0) +
                        vector.get(1) * vector.get(1) +
                        vector.get(2) * vector.get(2)
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