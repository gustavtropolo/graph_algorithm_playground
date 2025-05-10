import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class DfsWorker extends SwingWorker<Boolean, DfsWorker.VertexUpdate> {

    public final Vertex startVertex;
    public final Vertex endVertex;
    public final Set<Vertex> visited;
    public GraphPanel graphPanel;

    /**
     * Constructor for the DFS background task.
     * @param start The starting vertex for DFS.
     * @param end The target vertex for DFS.
     */
    DfsWorker(Vertex start, Vertex end, GraphPanel graphPanel) {
        this.startVertex = start;
        this.endVertex = end;
        this.graphPanel = graphPanel;
        this.visited = new HashSet<>();
    }

    /**
     * This method runs on the Event Dispatch Thread (EDT).
     * Process intermediate results published from doInBackground().
     * Safe to interact with Swing components here.
     */
    @Override
    protected void process(List<VertexUpdate> updates) {
        if (isCancelled()) {
            return;
        }

        for (VertexUpdate update : updates) { // go through every VertexUpdate and get the vertex from it and update its color
            update.vertex.updateColor(update.color);
        }

        graphPanel.repaint(); //repaint from graphpanel
    }


    /**
     * This method runs on the Event Dispatch Thread.
     * Executed after doInBackground() completes (successfully, cancelled, or with error).
     * Safe to interact with Swing components here for final updates/cleanup.
     */
    @Override
    protected void done() {
        //empty
    }

    /**
     * Runs on background thread.
     */
    @Override
    protected Boolean doInBackground() throws Exception { // handles InterruptedException from sleep
        publish(new VertexUpdate(startVertex, Color.GREEN)); // this is where the thread goes to execute the dfs
        publish(new VertexUpdate(endVertex, Color.RED));
        Thread.sleep(100);

        return dfsHelper(startVertex);
    }

    /**
     * Runs on background thread.
     * @param currentVertex The vertex currently being explored.
     * @return true if the endVertex found.
     */
    private boolean dfsHelper(Vertex currentVertex) throws InterruptedException {
        if (isCancelled()) {
            return false;
        }

        visited.add(currentVertex);

        // the publish method sends updates to the EDT
        if (currentVertex != startVertex && currentVertex != endVertex) {
            publish(new VertexUpdate(currentVertex, Color.ORANGE)); // other visited nodes in yellow
        }
        Thread.sleep(graphPanel.animationDelay); // pause immediately after doing the color change

        if (currentVertex == endVertex) {
            publish(new VertexUpdate(currentVertex, Color.CYAN));
            return true; // we found a path
        }

        for (Vertex neighbor : currentVertex.neighbors.keySet()) {
            if (visited.contains(neighbor)) {
                continue;
            }
            if (dfsHelper(neighbor)) { // blue path once we find it
                if (currentVertex != startVertex) { // keep start color
                    publish(new VertexUpdate(currentVertex, Color.BLUE));
                    Thread.sleep(graphPanel.animationDelay / 2); //mark the path
                }
                return true;
            }
        }

        if (currentVertex != startVertex && currentVertex != endVertex) {
            publish(new VertexUpdate(currentVertex, Color.LIGHT_GRAY)); // gray for dead ends
            Thread.sleep(graphPanel.animationDelay / 2);
        }
        return false;
    }

    public void makeAllBlack() {
        //make everything gray that's not start, end, or black
        for (Vertex v : graphPanel.graph.vertices) {
            if (v != startVertex && v != endVertex && v.color != Color.gray) {
                publish(new VertexUpdate(v, Color.GRAY));
            }
        }
    }

    public static class VertexUpdate {
        final Vertex vertex;
        final Color color;

        VertexUpdate(Vertex vertex, Color color) {
            this.vertex = vertex;
            this.color = color;
        }
    }
}


