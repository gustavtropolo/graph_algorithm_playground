import java.awt.*;
import java.util.HashSet;

/**
 * This class represents a single vertex
 * in the graph and stores all of its properties.
 */
public class Vertex {
    int x, y, radius;
    HashSet<Vertex> neighbors = new HashSet<>(); // the set of all neighboring vertices
    Color color;

    public Vertex(int x, int y, int radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    /**
     * This method checks if a given points middle x and y coord is
     * touching this circle.
     *
     * @param mx The x-coordinate of the point to check.
     * @param my The y-coordinate of the point to check.
     * @return returns true if the point is inside the circle.
     */
    public boolean contains(int mx, int my) {
        int dx = mx - x;
        int dy = my - y;
        return dx * dx + dy * dy <= radius * radius; // distance from the middle of the circle must be <= r^2
    }
}
