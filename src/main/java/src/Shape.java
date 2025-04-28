package src;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public abstract class Shape {
    protected List<Edge> edges;

    public Shape() {
        this.edges = new ArrayList<>();
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public void clearEdges() {
        edges.clear();
    }
}