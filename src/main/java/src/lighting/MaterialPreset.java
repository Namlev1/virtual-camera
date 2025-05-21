package src.lighting;

import lombok.Data;
import java.awt.Color;

@Data
public class MaterialPreset {
    private String name;
    private Color baseColor;
    private Material material;

    public MaterialPreset(String name, Color baseColor, float ambient, float diffuse, float specular, int shininess) {
        this.name = name;
        this.baseColor = baseColor;
        this.material = new Material(ambient, diffuse, specular, shininess);
    }
}