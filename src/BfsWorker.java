import java.awt.*;
import java.util.*;


public class BfsWorker extends DfsWorker { // all the same functionality as Dfsworker but with different

    public BfsWorker(Vertex start, Vertex end, GraphPanel graphPanel) {
        super(start, end, graphPanel);
    }

    private boolean bfsHelper() throws InterruptedException {
        HashMap<Vertex, Vertex> path = new HashMap<>();
        Deque<Vertex> queue = new ArrayDeque<>(); // queue for next elem to process
        queue.add(startVertex);

        while(!queue.isEmpty()) {
            Vertex currentVertex = queue.poll();
            visited.add(currentVertex);
            // the publish method sends updates to the EDT
            if (currentVertex == endVertex) {
                publish(new VertexUpdate(currentVertex, Color.CYAN)); // found it

                makeAllBlack();

                currentVertex = path.get(currentVertex);
                while (currentVertex != startVertex && currentVertex != endVertex) {
                    publish(new VertexUpdate(currentVertex, Color.BLUE));
                    Thread.sleep(graphPanel.animationDelay);
                    currentVertex = path.get(currentVertex);
                }
                return true; // we found a path
            }

            if (currentVertex != startVertex && currentVertex != endVertex) {
                publish(new VertexUpdate(currentVertex, Color.LIGHT_GRAY)); // done processing all of its neighbors
            }
            for (Vertex neighbor : currentVertex.neighbors.keySet()) {
                if (visited.contains(neighbor)) {
                    continue;
                }
                path.put(neighbor, currentVertex);
                queue.add(neighbor);
                if (neighbor != startVertex && neighbor != endVertex) {
                    publish(new VertexUpdate(neighbor, Color.ORANGE));
                }
                Thread.sleep(graphPanel.animationDelay); // pause immediately after doing the color change
            }

        }
        return false;
    }

    /**
     * Runs on background thread.
     */
    @Override
    protected Boolean doInBackground() throws Exception { // handles InterruptedException from sleep
        publish(new VertexUpdate(startVertex, Color.GREEN));
        publish(new VertexUpdate(endVertex, Color.RED));
        Thread.sleep(100);

        return bfsHelper();
    }
}
