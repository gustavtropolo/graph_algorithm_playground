import java.awt.*;
import java.util.*;

public class BidirectionalAstarWorker extends DfsWorker{

    private double[] forwardDistTo = new double[graphPanel.graph.vertices.size()];
    private double[] backwardDistTo = new double[graphPanel.graph.vertices.size()];

    private double[] forwardHeuristic = new double[graphPanel.graph.vertices.size()];
    private double[] backwardHeuristic = new double[graphPanel.graph.vertices.size()];


    public BidirectionalAstarWorker(Vertex start, Vertex end, GraphPanel graphPanel) {
        super(start, end, graphPanel);
    }

    private boolean BidirectionalAstarHelper() throws InterruptedException {
        int n = graphPanel.graph.vertices.size();
        initDistTo();
        initForwardHeuristic();
        initBackwardHeuristic();

        HashSet<Integer> forward_visited = new HashSet<>();
        HashSet<Integer> backward_visited = new HashSet<>();

        HashMap<Vertex, Vertex> forward_path = new HashMap<>();
        HashMap<Vertex, Vertex> backward_path = new HashMap<>();
        PriorityQueue<Integer> forward_pq = new PriorityQueue<>(n, new ShortestPathForwardComparator()); // indices of the vertices we are comparing
        PriorityQueue<Integer> backward_pq = new PriorityQueue<>(n, new ShortestPathBackwardComparator()); // indices of the vertices we are comparing
        forward_pq.add(0); // 0 is the index of the start vertex
        backward_pq.add(n-1);
        if (graphPanel.graph.vertices.get(0).neighbors.containsKey(graphPanel.graph.vertices.get(n - 1))) {
            return true;
        }

        while (!forward_pq.isEmpty() || !backward_pq.isEmpty()) {
            Integer curr_forward = forward_pq.poll();
            Integer curr_backward = backward_pq.poll();
            Vertex currVertex_forward = graphPanel.graph.vertices.get(curr_forward);
            Vertex currVertex_backward = graphPanel.graph.vertices.get(curr_backward);

            forward_visited.add(curr_forward);
            backward_visited.add(curr_backward);

            if (backward_visited.contains(curr_forward) || forward_visited.contains(curr_backward)) { // we found a match
                makeAllBlack();
                int meetVertexIndex;
                if (forward_visited.contains(curr_backward)) { // we found it moving backwards
                    meetVertexIndex = curr_backward;
                } else { // found moving forwards
                    meetVertexIndex = curr_forward;
                }
                Vertex meetVertex = graphPanel.graph.vertices.get(meetVertexIndex);
                publish(new VertexUpdate(meetVertex, Color.BLUE));
                currVertex_forward = forward_path.get(meetVertex);
                currVertex_backward = backward_path.get(meetVertex);
                while ((currVertex_forward != startVertex && currVertex_forward != endVertex) || (currVertex_backward != startVertex && currVertex_backward != endVertex)) {
                    if (currVertex_forward != startVertex && currVertex_forward != endVertex) {
                        publish(new VertexUpdate(currVertex_forward, Color.BLUE));
                        currVertex_forward = forward_path.get(currVertex_forward);
                    }
                    if (currVertex_backward != startVertex && currVertex_backward != endVertex) {
                        publish(new VertexUpdate(currVertex_backward, Color.BLUE));
                        currVertex_backward = backward_path.get(currVertex_backward);
                    }
                    Thread.sleep(graphPanel.animationDelay);
                }
                break; // we are completely done
            }

            if (currVertex_forward != startVertex) {
                publish(new VertexUpdate(currVertex_forward, Color.ORANGE));
            }
            if (currVertex_backward != endVertex) {
                publish(new VertexUpdate(currVertex_backward, new Color(0, 234, 255)));
            }
            Thread.sleep(graphPanel.animationDelay);

            for (Vertex neigh : currVertex_forward.neighbors.keySet()) { // indices of the neighbors
                int neighborIndex = neigh.index;
                if (forward_visited.contains(neighborIndex)) {
                    continue; // skip visited nodes
                }
                double weight = graphPanel.graph.vertices.get(curr_forward).neighbors.get(neigh); //get the dist to neighbor
                if (forwardDistTo[curr_forward] + weight < forwardDistTo[neighborIndex]) { // found shorter path
                    forwardDistTo[neighborIndex] = forwardDistTo[curr_forward] + weight;
                    forward_path.put(neigh, currVertex_forward); // update path back
                    forward_pq.add(neighborIndex); //reprocess it in the pq
                }
            }

            for (Vertex neigh : currVertex_backward.neighbors.keySet()) { // indices of the neighbors
                int neighborIndex = neigh.index;
                if (backward_visited.contains(neighborIndex)) {
                    continue; // skip visited nodes
                }
                double weight = graphPanel.graph.vertices.get(curr_backward).neighbors.get(neigh); //get the dist to neighbor
                if (backwardDistTo[curr_backward] + weight < backwardDistTo[neighborIndex]) { // found shorter path
                    backwardDistTo[neighborIndex] = backwardDistTo[curr_backward] + weight;
                    backward_path.put(neigh, currVertex_backward); // update path back
                    backward_pq.add(neighborIndex); //reprocess it in the pq
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

        return BidirectionalAstarHelper();
    }

    private class ShortestPathForwardComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer index1, Integer index2) {
            Double distance1 = forwardDistTo[index1] + forwardHeuristic[index1];
            Double distance2 = forwardDistTo[index2] + forwardHeuristic[index2];
            int dist1 = (int) Math.round(distance1 * 10); // taking no chances with this
            int dist2 = (int) Math.round(distance2 * 10);
            return dist1 - dist2;
        }
    }

    private class ShortestPathBackwardComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer index1, Integer index2) {
            Double distance1 = backwardDistTo[index1] + backwardHeuristic[index1];
            Double distance2 = backwardDistTo[index2] + backwardHeuristic[index2];
            int dist1 = (int) Math.round(distance1 * 10); // taking no chances with this
            int dist2 = (int) Math.round(distance2 * 10);
            return dist1 - dist2;
        }
    }

    public void initDistTo() {
        forwardDistTo[0] = 0;
        backwardDistTo[graphPanel.graph.vertices.size()-1] = 0;

        for (int i = 1; i < graphPanel.graph.vertices.size(); i++) {
            forwardDistTo[i] = Integer.MAX_VALUE; // initialize to infinity
        }
        for (int i = 0; i < graphPanel.graph.vertices.size() - 1; i++) {
            backwardDistTo[i] = Integer.MAX_VALUE; // initialize to infinity
        }
    }

    public void initForwardHeuristic() {
        int n = graphPanel.graph.vertices.size();
        Vertex endVertex = graphPanel.graph.vertices.get(n - 1);
        for (int i = 1; i < graphPanel.graph.vertices.size(); i++) {
            Vertex currVertex = graphPanel.graph.vertices.get(i);
            forwardHeuristic[i] = graphPanel.graph.dist(currVertex, endVertex);
        }
        forwardHeuristic[n - 1] = 0; // 0 for the finish
    }

    public void initBackwardHeuristic() {
        int n = graphPanel.graph.vertices.size();
        Vertex startVertex = graphPanel.graph.vertices.get(0);
        for (int i = 1; i < graphPanel.graph.vertices.size(); i++) {
            Vertex currVertex = graphPanel.graph.vertices.get(i);
            backwardHeuristic[i] = graphPanel.graph.dist(currVertex, startVertex);
        }
        backwardHeuristic[0] = 0; // 0 for the finish
    }
}
