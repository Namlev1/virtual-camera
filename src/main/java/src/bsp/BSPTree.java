package src.bsp;

import lombok.Data;
import org.ejml.simple.SimpleMatrix;
import src.Face;
import src.Renderer;

import java.util.ArrayList;
import java.util.List;

@Data
public class BSPTree {
    private BSPNode root;

    /**
     * Build a BSP tree from a list of faces
     */
    public BSPTree(List<Face> faces) {
        if (faces == null || faces.isEmpty()) {
            root = null;
            return;
        }

        root = buildTree(new ArrayList<>(faces));
    }

    /**
     * Recursively build the BSP tree
     */
    private BSPNode buildTree(List<Face> faces) {
        if (faces.isEmpty()) {
            return null;
        }

        // Select first face as partitioner (more sophisticated selection methods could be used)
        Face partitioner = faces.get(0);
        faces.remove(0);

        BSPNode node = new BSPNode(partitioner);
        Plane plane = node.getPlane();

        List<Face> frontList = new ArrayList<>();
        List<Face> backList = new ArrayList<>();

        // Classify each face against the partition plane
        for (Face face : faces) {
            int classification = plane.classifyFace(face);

            switch (classification) {
                case 0:  // Coplanar
                    node.getCoplanarFaces().add(face);
                    break;
                case 1:  // Front
                    frontList.add(face);
                    break;
                case -1: // Back
                    backList.add(face);
                    break;
                case 2:  // Straddling
                    Face[] splitFaces = plane.splitFace(face);
                    frontList.add(splitFaces[0]);
                    backList.add(splitFaces[1]);
                    break;
            }
        }

        // Recursively build front and back subtrees
        if (!frontList.isEmpty()) {
            node.setFront(buildTree(frontList));
        }

        if (!backList.isEmpty()) {
            node.setBack(buildTree(backList));
        }

        return node;
    }

    /**
     * Render the scene using BSP tree ordering
     */
    public void render(Renderer renderer, SimpleMatrix cameraPosition) {
        if (root != null) {
            root.render(renderer, cameraPosition);
        }
    }
}