package src.lighting;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;
import java.awt.Color;

@Data
public class Light {
    private SimpleMatrix position;
    private Color color;
    private float intensity;
    private float ambientIntensity;

    public Light(float x, float y, float z, Color color, float intensity, float ambientIntensity) {
        this.position = new SimpleMatrix(3, 1, true, new float[]{x, y, z});
        this.color = color;
        this.intensity = intensity;
        this.ambientIntensity = ambientIntensity;
    }
}