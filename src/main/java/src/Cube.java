package src;

/**
 * Klasa reprezentująca prostopadłościan
 */
public class Cube extends Shape {
    /**
     * Konstruktor prostopadłościanu
     * @param centerX Współrzędna x środka
     * @param centerY Współrzędna y środka
     * @param centerZ Współrzędna z środka
     * @param width Szerokość
     * @param height Wysokość
     * @param depth Głębokość
     */
    public Cube(float centerX, float centerY, float centerZ, float width, float height, float depth) {
        super();
        createCube(centerX, centerY, centerZ, width, height, depth);
    }

    /**
     * Tworzy krawędzie prostopadłościanu
     */
    private void createCube(float centerX, float centerY, float centerZ, float width, float height, float depth) {
        float halfWidth = width / 2;
        float halfHeight = height / 2;
        float halfDepth = depth / 2;

        // Wierzchołki prostopadłościanu
        float[][] vertices = new float[][] {
                {centerX - halfWidth, centerY - halfHeight, centerZ - halfDepth}, // 0: lewy dolny tył
                {centerX + halfWidth, centerY - halfHeight, centerZ - halfDepth}, // 1: prawy dolny tył
                {centerX + halfWidth, centerY + halfHeight, centerZ - halfDepth}, // 2: prawy górny tył
                {centerX - halfWidth, centerY + halfHeight, centerZ - halfDepth}, // 3: lewy górny tył
                {centerX - halfWidth, centerY - halfHeight, centerZ + halfDepth}, // 4: lewy dolny przód
                {centerX + halfWidth, centerY - halfHeight, centerZ + halfDepth}, // 5: prawy dolny przód
                {centerX + halfWidth, centerY + halfHeight, centerZ + halfDepth}, // 6: prawy górny przód
                {centerX - halfWidth, centerY + halfHeight, centerZ + halfDepth}  // 7: lewy górny przód
        };

        // Dolna ściana
        addEdge(new Edge(vertices[0][0], vertices[0][1], vertices[0][2], vertices[1][0], vertices[1][1], vertices[1][2]));
        addEdge(new Edge(vertices[1][0], vertices[1][1], vertices[1][2], vertices[5][0], vertices[5][1], vertices[5][2]));
        addEdge(new Edge(vertices[5][0], vertices[5][1], vertices[5][2], vertices[4][0], vertices[4][1], vertices[4][2]));
        addEdge(new Edge(vertices[4][0], vertices[4][1], vertices[4][2], vertices[0][0], vertices[0][1], vertices[0][2]));

        // Górna ściana
        addEdge(new Edge(vertices[3][0], vertices[3][1], vertices[3][2], vertices[2][0], vertices[2][1], vertices[2][2]));
        addEdge(new Edge(vertices[2][0], vertices[2][1], vertices[2][2], vertices[6][0], vertices[6][1], vertices[6][2]));
        addEdge(new Edge(vertices[6][0], vertices[6][1], vertices[6][2], vertices[7][0], vertices[7][1], vertices[7][2]));
        addEdge(new Edge(vertices[7][0], vertices[7][1], vertices[7][2], vertices[3][0], vertices[3][1], vertices[3][2]));

        // Pionowe krawędzie
        addEdge(new Edge(vertices[0][0], vertices[0][1], vertices[0][2], vertices[3][0], vertices[3][1], vertices[3][2]));
        addEdge(new Edge(vertices[1][0], vertices[1][1], vertices[1][2], vertices[2][0], vertices[2][1], vertices[2][2]));
        addEdge(new Edge(vertices[5][0], vertices[5][1], vertices[5][2], vertices[6][0], vertices[6][1], vertices[6][2]));
        addEdge(new Edge(vertices[4][0], vertices[4][1], vertices[4][2], vertices[7][0], vertices[7][1], vertices[7][2]));
    }
}