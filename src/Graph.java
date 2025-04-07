import java.awt.*;
import java.util.ArrayList;
import java.util.Random;



/**
 * Holds a collection of vertices and can perform operations
 * with them that we will define later like various
 * different graph traversals.
 */
public class Graph {

    private final int radius = 20;

    ArrayList<Vertex> vertices = new ArrayList<>();
    static final Random rand = new Random();

    /**
     * Constructs a Graph with a specified number of randomly placed vertices
     * within the width and height.
     *
     * @param num_vertices The number of vertices (circles) to create.
     * @param width        The maximum width for placing vertices.
     * @param height       The maximum height for placing vertices.
     */
    public Graph(int num_vertices, int width, int height) {
        int i = 0;
        while (i < num_vertices) {
            int xPos = rand.nextInt(width - 2 * radius) + radius;
            int yPos = rand.nextInt(height - 2 * radius) + radius;
            Vertex v = new Vertex(xPos, yPos, radius, Color.GRAY);
            if (hasCollision(v)) {
                continue;
            }
            vertices.add(v);
            i++;
        }
    }

    /**
     * Checks if the given vertex collides with any of the others that
     * exist in the graph.
     *
     * @param circle The Vertex to check for collisions.
     * @return returns true if the given Vertex collides
     */
    public boolean hasCollision(Vertex circle) {
        int x1 = circle.x;
        int y1 = circle.y;
        int r1 = circle.radius;
        for (Vertex c : vertices) {
            int x2 = c.x;
            int y2 = c.y;
            int r2 = c.radius;
            if (Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) <= Math.pow(r1 + r2, 2)) {
                return true; // the circle must be further than the sqrt of the sum of the radi squared
            }
        }
        return false;
    }
}
