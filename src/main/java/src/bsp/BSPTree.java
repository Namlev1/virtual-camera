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

    public BSPTree(List<Face> faces) {
        if (faces == null || faces.isEmpty()) {
            root = null;
            return;
        }

        root = buildTree(new ArrayList<>(faces));
    }

    private BSPNode buildTree(List<Face> faces) {
        if (faces.isEmpty()) {
            return null;
        }

        Face partitioner = faces.get(0);
        faces.remove(0);

        BSPNode node = new BSPNode(partitioner);
        Plane plane = node.getPlane();

        List<Face> frontList = new ArrayList<>();
        List<Face> backList = new ArrayList<>();

        for (Face face : faces) {
            int classification = plane.classifyFace(face);

            switch (classification) {
                case 0:
                    node.getCoplanarFaces().add(face);
                    break;
                case 1:
                    frontList.add(face);
                    break;
                case -1:
                    backList.add(face);
                    break;
                case 2:
                    Face[] splitFaces = plane.splitFace(face);
                    frontList.add(splitFaces[0]);
                    backList.add(splitFaces[1]);
                    break;
            }
        }

        if (!frontList.isEmpty()) {
            node.setFront(buildTree(frontList));
        }

        if (!backList.isEmpty()) {
            node.setBack(buildTree(backList));
        }

        return node;
    }

    public void render(Renderer renderer, SimpleMatrix cameraPosition) {
        if (root != null) {
            root.render(renderer, cameraPosition);
        }
    }
}