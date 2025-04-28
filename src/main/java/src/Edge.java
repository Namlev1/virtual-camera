package src;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;

@Data
public class Edge {
    private SimpleMatrix start;
    private SimpleMatrix end;

    public Edge(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.start = new SimpleMatrix(3, 1, true, new float[]{x1, y1, z1});
        this.end = new SimpleMatrix(3, 1, true, new float[]{x2, y2, z2});
    }
}