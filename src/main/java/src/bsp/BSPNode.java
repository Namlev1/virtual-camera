package src.bsp;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;
import src.Face;
import src.Renderer;

import java.util.ArrayList;
import java.util.List;

@Data
public class BSPNode {
    private Face partition;
    private BSPNode front;
    private BSPNode back;
    private List<Face> coplanarFaces;
    private Plane plane;

    public BSPNode(Face partitionFace) {
        this.partition = partitionFace;
        this.front = null;
        this.back = null;
        this.coplanarFaces = new ArrayList<>();
        this.coplanarFaces.add(partitionFace);

        // Extract plane equation from the face
        this.plane = Plane.fromFace(partitionFace);
    }

    public void render(Renderer renderer, SimpleMatrix cameraPosition) {
        float distance = plane.distanceToPoint(cameraPosition);

        if (distance >= 0) {
            if (back != null) {
                back.render(renderer, cameraPosition);
            }

            for (Face face : coplanarFaces) {
                renderer.fillFace(face);
            }

            if (front != null) {
                front.render(renderer, cameraPosition);
            }
        }
        else {
            if (front != null) {
                front.render(renderer, cameraPosition);
            }

            for (Face face : coplanarFaces) {
                renderer.fillFace(face);
            }

            if (back != null) {
                back.render(renderer, cameraPosition);
            }
        }
    }
}