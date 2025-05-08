import java.awt.*;
import java.util.*;

public class DijkstrasWorker extends DfsWorker{

    private double[] distTo = new double[graphPanel.graph.vertices.size()];

    public DijkstrasWorker(Vertex start, Vertex end, GraphPanel graphPanel) {
        super(start, end, graphPanel);
    }

    private boolean dijkstrasHelper() throws InterruptedException {
        int n = graphPanel.graph.vertices.size();
        distTo[0] = 0;
        for (int i = 1; i < n; i++) {
            distTo[i] = Integer.MAX_VALUE; // initialize to infinity
        }

        HashSet<Integer> visited = new HashSet<>();
        HashMap<Vertex, Vertex> path = new HashMap<>();
        PriorityQueue<Integer> pq = new PriorityQueue<>(n, new ShortestPathComparator()); // indices of the vertices we are comparing
        pq.add(0); // 0 is the index of the start vertex

        while (!pq.isEmpty()) {
            Integer curr = pq.poll(); // index of the current
            Vertex currVertex = graphPanel.graph.vertices.get(curr); // paint it orange
            visited.add(curr);

            if (currVertex == endVertex) {
                makeAllBlack();
                currVertex = path.get(currVertex);
                while (currVertex != startVertex && currVertex != endVertex) {
                    publish(new VertexUpdate(currVertex, Color.BLUE));
                    Thread.sleep(graphPanel.animationDelay);
                    currVertex = path.get(currVertex);
                }
                break;
            }
            if (currVertex != startVertex) {
                publish(new VertexUpdate(currVertex, Color.ORANGE));
                Thread.sleep(graphPanel.animationDelay);
            }

            for (Vertex neigh : currVertex.neighbors.keySet()) { // indices of the neighbors
                int neighborIndex = neigh.index;
                if (visited.contains(neighborIndex)) {
                    continue; // skip visited nodes
                }
                double weight = graphPanel.graph.vertices.get(curr).neighbors.get(neigh); //get the dist to neighbor
                if (distTo[curr] + weight < distTo[neighborIndex]) { // found shorter path
                    distTo[neighborIndex] = distTo[curr] + weight;
                    path.put(neigh, currVertex); // update path back
                    pq.add(neighborIndex); //reprocess it in the pq
                }
            }
        }



        return false;
    }

    /**
     * This method runs on a background thread.
     * Perform the long-running DFS task here.
     * Cannot interact directly with Swing components here.
     */
    @Override
    protected Boolean doInBackground() throws Exception { // handles InterruptedException from sleep
        publish(new VertexUpdate(startVertex, Color.GREEN)); //this is where the thread goes to execute the dfs
        publish(new VertexUpdate(endVertex, Color.RED));
        Thread.sleep(100);

        return dijkstrasHelper();
    }

    private class ShortestPathComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer index1, Integer index2) {
            Double distance1 = distTo[index1];
            Double distance2 = distTo[index2];
            int dist1 = (int) Math.round(distance1 * 10); // taking no chances with this
            int dist2 = (int) Math.round(distance2 * 10);
            return dist1 - dist2;
        }
    }
}
