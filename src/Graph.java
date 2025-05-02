import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;



/**
 * Holds a collection of vertices and can perform operations
 * with them that we will define later like various
 * different graph traversals.
 */
public class Graph {

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
    public Graph(int num_vertices, int ball_radius, int width, int height, int maxDist) {
        addVertices(num_vertices, ball_radius, width, height);
        int minDist = 2 * ball_radius + 5;
        int density = 100;
        addEdges(maxDist, density);
    }

    private void addVertices(int num_vertices, int ball_radius, int width, int height) {
        int i = 0;
        while (i < num_vertices) {
            int xPos = rand.nextInt(width - 2 * ball_radius) + ball_radius;
            int yPos = rand.nextInt(height - 2 * ball_radius) + ball_radius;
            Vertex v = new Vertex(xPos, yPos, ball_radius, Color.GRAY);
            if (hasCollision(v, 15)) {
                continue;
            }
            vertices.add(v);
            i++;
        }
    }

    /**
     * Adds edges to the graph
     *
     * @param maxDist The max distance that we allow edges to travel.
     */
    public void addEdges(int maxDist, int density) {
        int weight, dx, dy;
        int origMaxDist = maxDist;
        WeightedQuickUnion wqu = new WeightedQuickUnion(vertices.size());
        int i = 0;
        while (i < vertices.size()) {
            Vertex startVertex = vertices.get(i);
            int i_initial = i;
            for (int j = 0; j < vertices.size(); j++) {
                if (i == j) continue;
                Vertex endVertex = vertices.get(j);
                dx = startVertex.x - endVertex.x;
                dy = startVertex.y - endVertex.y;
                if (dx * dx + dy * dy > maxDist * maxDist) {
                    continue; // skip the vertex pairs that exceed the max distance
                }
                if (wqu.connected(i, j) && maxDist < 300) { // once we get past 150 bypass
                    continue;
                }
                weight = rand.nextInt(10) + 1;
                startVertex.neighbors.put(endVertex, weight);
                wqu.union(i, j); // union these two since they now belong to the same set
                i++; // move on to the next vertex we need to connect
                maxDist = origMaxDist; // restore the max distance
                break;
            }
            if (i == i_initial) {
                maxDist += 10;
            }
        }
        // everything is connected now, add more edges to make more dense
        int count = 0;
        while (count < density) {
            i = count % vertices.size();
            Vertex startVertex = vertices.get(i);
            for (int j = 0; j < vertices.size(); j++) {
                if (i == j) continue;
                if (startVertex.neighbors.size() > 4) {
                    break;
                }
                Vertex endVertex = vertices.get(j);
                if (startVertex.neighbors.containsKey(endVertex) || endVertex.neighbors.containsKey(startVertex)) {
                    continue;
                }
                if (endVertex.neighbors.size() > 4) {
                    continue;
                }
                dx = startVertex.x - endVertex.x;
                dy = startVertex.y - endVertex.y;
                if (dx * dx + dy * dy > maxDist * maxDist) {
                    continue; // skip the vertex pairs that exceed the max distance
                }
                weight = rand.nextInt(10) + 1;
                startVertex.neighbors.put(endVertex, weight);
                maxDist = origMaxDist; // restore the max distance
            }
            count++;
        }
    }

    /**
     * Checks if the given vertex collides with any of the others that
     * exist in the graph.
     *
     * @param circle The Vertex to check for collisions.
     * @return returns true if the given Vertex collides
     */
    public boolean hasCollision(Vertex circle, int buffer) {
        int x1 = circle.x;
        int y1 = circle.y;
        int r1 = circle.radius;
        for (Vertex c : vertices) {
            int x2 = c.x;
            int y2 = c.y;
            int r2 = c.radius;
            if (Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) <= Math.pow(r1 + r2 + buffer, 2)) {
                return true; // the circle must be further than the sqrt of the sum of the radi squared
            }
        }
        return false;
    }
}
