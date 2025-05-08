import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represents a single vertex
 * in the graph and stores all of its properties.
 */
public class Vertex {
    int x, y;
    HashMap<Vertex, Double> neighbors = new HashMap<>(); // the set of all neighboring vertices and edge weights
    Color color;
    final int radius;
    final int index;


    public Vertex(int x, int y, int ball_radius, Color color, int index) {
        this.x = x;
        this.y = y;
        this.radius = ball_radius;
        this.color = color;
        this.index = index;
    }

    public void updateColor(Color c) {
        this.color = c;
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
