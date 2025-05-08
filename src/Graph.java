import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;



/**
 * Holds a collection of vertices and can perform operations
 * with them that we will define later like various
 * different graph traversals.
 */
public class Graph {

    ArrayList<Vertex> vertices = new ArrayList<>();
    ArrayList<Pair<Vertex, Vertex>> extraEdges = new ArrayList<>();
    static final Random rand = new Random();
    int width;
    int height;
    int maxDist;
    int density = 0;

    /**
     * Constructs a Graph with a specified number of randomly placed vertices
     * within the width and height.
     *
     * @param num_vertices The number of vertices (circles) to create.
     * @param width        The maximum width for placing vertices.
     * @param height       The maximum height for placing vertices.
     */
    public Graph(int num_vertices, int ball_radius, int width, int height, int maxDist) {
        this.width = width;
        this.height = height;
        this.maxDist = maxDist;
        addVertices(num_vertices, ball_radius, width, height);
        addEdges();
    }

    private void addVertices(int num_vertices, int ball_radius, int width, int height) {
        int i = 0;
        int maxAttempts = 1000000;
        int attempts = 0;
        while (i < num_vertices) {
            int xPos = rand.nextInt(width - 2 * ball_radius) + ball_radius;
            int yPos = rand.nextInt(height - 2 * ball_radius) + ball_radius;
            Vertex v = new Vertex(xPos, yPos, ball_radius, Color.GRAY, i);
            if (attempts > maxAttempts) { // no more than a million failed attempts
                break;
            }
            if (hasCollision(v, 15)) {
                attempts++;
                continue;
            }
            vertices.add(v);
            i++;
        }
    }

    public void setDensity(int newDensity) {
        updateEdgeDensity(this.density, newDensity);
        this.density = newDensity;
    }

    /**
     * Adds edges to the graph
     */
    public void addEdges() {
        int origMaxDist = maxDist;
        WeightedQuickUnion wqu = new WeightedQuickUnion(vertices.size());
        HashSet<Integer> unvisitedVertexIndices = new HashSet<>();
        for (int i = 0; i < vertices.size(); i++) {
            unvisitedVertexIndices.add(i);
        }
        for (Integer i: unvisitedVertexIndices) {
            boolean result = connectToOtherGroup(i, maxDist, wqu); // attempt to connect vertex i to another vertex
            if (result) { //success
                continue;
            }
            // we haven't found a neighbor outside of the group that's close enough
            ArrayList<Integer> vertexIndices = findNearbyVerticesByIndex(i, wqu);
            if (vertexIndices.size() == vertices.size()) { // everything is connected
                break;
            }
            boolean not_connected = true;
            while (not_connected) {
                assert(!vertexIndices.isEmpty()); // can't be empty
                for (Integer j : vertexIndices) {
                    if (connectToOtherGroup(j, maxDist, wqu)) { //try to connect all the neighbors to another group
                        i++;
                        not_connected = false;
                        break; // success
                    }
                }
                maxDist += 10;
            }
            maxDist = origMaxDist; //restore maxDist
        }

        updateEdgeDensity(0, density);
    }

    public void updateEdgeDensity(int old_density, int new_density) {
        int densityChange = new_density - old_density;
        if (densityChange > 0) {
            increaseDensity(densityChange);
        } else {
            decreaseDensity(densityChange * -1);
        }
    }

    public void increaseDensity(int densityChange) {
        int count = 0;
        int i, dx, dy;
        int originalMaxdist = maxDist;
        while (count < densityChange) {
            i = rand.nextInt(vertices.size());
            int originalCount = count;
            Vertex startVertex = vertices.get(i);
            for (int j = 0; j < vertices.size(); j++) {
                if (i == j) continue;
                if (startVertex.neighbors.size() > 5) {
                    break;
                }
                Vertex endVertex = vertices.get(j);
                if (startVertex.neighbors.containsKey(endVertex) || endVertex.neighbors.containsKey(startVertex)) {
                    continue;
                }
                if (endVertex.neighbors.size() > 5) {
                    continue;
                }
                dx = startVertex.x - endVertex.x;
                dy = startVertex.y - endVertex.y;
                if (dx * dx + dy * dy > maxDist * maxDist) {
                    continue; // skip the vertex pairs that exceed the max distance
                }
                double weight = dist(startVertex, endVertex);
                startVertex.neighbors.put(endVertex, weight);
                endVertex.neighbors.put(startVertex, weight);
                extraEdges.add(new Pair<>(startVertex, endVertex));
                count++;
            }
            if (count == originalCount) { //we haven't successfully added any edges, increase search range
                maxDist += 50;
            }
        }
        maxDist = originalMaxdist; // restore what it was before adding edges
    }

    public void decreaseDensity(int densityChange) {
        for (int i = 0; i < densityChange; i++) {
            Pair<Vertex, Vertex> pair = extraEdges.remove(0);
            Vertex startVertex = pair.getFirst();
            Vertex endVertex = pair.getSecond();
            startVertex.neighbors.remove(endVertex);
            endVertex.neighbors.remove(startVertex);
        }
    }

    public ArrayList<Integer> findNearbyVerticesByIndex(int vertexIndex, WeightedQuickUnion wqu) {
        // make sure every vertex is connected
        ArrayList<Integer> nearbyVertices = new ArrayList<>();
        nearbyVertices.add(vertexIndex);
        for (int i = 0; i < vertices.size(); i++) {
            if (wqu.connected(vertexIndex, i) && vertexIndex != i) {
                nearbyVertices.add(i);
            }
        }
        Collections.shuffle(nearbyVertices);
        return nearbyVertices;
    }

    public boolean connectToOtherGroup(int startIndex, int maxDist, WeightedQuickUnion wqu) {
        Vertex startVertex = vertices.get(startIndex);
        for (int k = 0; k < vertices.size(); k++) {
            int j = rand.nextInt(vertices.size());
            if (startIndex == j) continue;
            Vertex endVertex = vertices.get(j);
            int dx = startVertex.x - endVertex.x;
            int dy = startVertex.y - endVertex.y;
            if (dx * dx + dy * dy > maxDist * maxDist) {
                continue; // skip the vertex pairs that exceed the max distance
            }
            if (wqu.connected(startIndex, j)) { // skip if already connected
                continue;
            }
            double weight = dist(startVertex, endVertex);
            startVertex.neighbors.put(endVertex, weight);
            endVertex.neighbors.put(startVertex, weight);
            wqu.union(startIndex, j); // union these two since they now belong to the same set
            return true;
        }
        return false;
    }

    public double dist(Vertex startVertex, Vertex endVertex) {
        int dx = startVertex.x - endVertex.x;
        int dy = startVertex.y - endVertex.y;

        double distance = Math.sqrt(dx * dx + dy * dy);
        distance = Math.round(distance * 10);
        distance = distance / 10.0; // conv back to double
        return distance;
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
