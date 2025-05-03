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

    /**
     * Constructs a BSP node with a partitioning face
     */
    public BSPNode(Face partitionFace) {
        this.partition = partitionFace;
        this.front = null;
        this.back = null;
        this.coplanarFaces = new ArrayList<>();
        this.coplanarFaces.add(partitionFace);

        // Extract plane equation from the face
        this.plane = Plane.fromFace(partitionFace);
    }

    /**
     * Recursively renders faces in BSP tree in correct back-to-front order
     * based on camera position
     */
    public void render(Renderer renderer, SimpleMatrix cameraPosition) {
        // Check which side of partition plane the camera is on
        float distance = plane.distanceToPoint(cameraPosition);

        // If camera is in front of the plane, render back faces first
        if (distance >= 0) {
            // Render back tree recursively (if exists)
            if (back != null) {
                back.render(renderer, cameraPosition);
            }

            // Render partitioning and coplanar faces
            for (Face face : coplanarFaces) {
                // Use the direct face drawing method
                renderer.fillFace(face);

                // Draw edges
                for (src.Edge edge : face.getEdges()) {
                    renderer.drawEdge(edge);
                }
            }

            // Render front tree recursively (if exists)
            if (front != null) {
                front.render(renderer, cameraPosition);
            }
        }
        // If camera is behind the plane, render front faces first
        else {
            // Render front tree recursively (if exists)
            if (front != null) {
                front.render(renderer, cameraPosition);
            }

            // Render partitioning and coplanar faces
            for (Face face : coplanarFaces) {
                // Use the direct face drawing method
                renderer.fillFace(face);

                // Draw edges
                for (src.Edge edge : face.getEdges()) {
                    renderer.drawEdge(edge);
                }
            }

            // Render back tree recursively (if exists)
            if (back != null) {
                back.render(renderer, cameraPosition);
            }
        }
    }
}