import java.awt.*;
import java.util.HashSet;

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

    public boolean contains(int mx, int my) {
        int dx = mx - x;
        int dy = my - y;
        return dx * dx + dy * dy <= radius * radius; // distance from the middle of the circle must be <= r^2
    }
}
