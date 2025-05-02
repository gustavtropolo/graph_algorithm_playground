import java.awt.*;
import java.util.HashSet;

/**
 * This class represents a single vertex
 * in the graph and stores all of its properties.
 */
public class Vertex {
    int x, y;
    HashSet<Vertex> neighbors = new HashSet<>(); // the set of all neighboring vertices
    Color color;
    static final int radius = 5;


    public Vertex(int x, int y, Color color) {
        this.x = x;
        this.y = y;
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
