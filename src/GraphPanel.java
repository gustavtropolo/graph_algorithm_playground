import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This is basically a JPanel component to display the Graph
 * and allows me to drag the vertices around interactively.
 * Later this will also include a menu for different graph traversals.
 */
class GraphPanel extends JPanel {
    public Graph graph;
    private Vertex dragged = null;
    private int offsetX, offsetY;
    private final int EDGE_WEIGHT_FONT_SIZE = 10; // define font size for edges
    public int animationDelay;
    public boolean showEdgeWeights = false;
    public AstarWorker currentAstarWorker = null;
    public BidirectionalAstarWorker currentBidirectionalAstarWorker = null;

    int n, ball_radius, width, height, maxDist;

    /**
     * The main logic that constructs the panel
     *
     * @param n      The number of vertices
     * @param width  The width where we can place vertices
     * @param height The height where we can place vertices
     */
    public GraphPanel(int n, int ball_radius, int width, int height, int maxDist) {
        this.n = n; // store the parameters we used to initialize
        this.ball_radius = ball_radius;
        this.width = width;
        this.height = height - 28;
        this.maxDist = maxDist;
        graph = new Graph(n, ball_radius, width, height - 28, maxDist); // subtract the constant amount of height taken by the top bar

        // a listener for dragging
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (Vertex v : graph.vertices) {
                    if (v.contains(e.getX(), e.getY())) {
                        dragged = v;
                        offsetX = e.getX() - v.x;
                        offsetY = e.getY() - v.y;
                        break; // once we find a circle, break, we don't want to move more than 1 at a time
                    }
                }
            }

            /**
             * These listeners handle all of the events that we might care about.
             */
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragged != null) {
                    dragged.x = e.getX() - offsetX;
                    dragged.y = e.getY() - offsetY;
                    updateEdgeWeights(dragged);
                    if (currentAstarWorker != null) {
                        currentAstarWorker.initHeuristic();
                    } else if (currentBidirectionalAstarWorker != null) {
                        currentBidirectionalAstarWorker.initForwardHeuristic();
                        currentBidirectionalAstarWorker.initBackwardHeuristic();
                    }
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragged = null; // set dragged to null
            }
        };

        addMouseListener(mouse); // add the listener in swing
        addMouseMotionListener(mouse);
    }

    /**
     * Changes the graph to a new graph with the same parameters
     * */
    public void newGraph(int numOfVertices, int vertexSize) {
        this.graph = new Graph(numOfVertices, vertexSize, width, height - 28, maxDist);
        repaint(); // need to call repaint explicitly so swing knows to run paintComponent
    }

    public void updateEdgeWeights(Vertex v) {
        for (Vertex n : v.neighbors.keySet()) {
            v.neighbors.put(n, graph.dist(v, n));
            n.neighbors.put(v, graph.dist(v, n));
        } // updates all the distances between them
    }


    /**
     * Overrides paintComponent() so we can draw the graph's vertices and edges.
     * */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g; //graphics2d for anti-aliasing

        // anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.BLACK);
        Font edgeFont = new Font(Font.SANS_SERIF, Font.PLAIN, EDGE_WEIGHT_FONT_SIZE); // creates the font
        g2d.setFont(edgeFont);
        FontMetrics fm = g2d.getFontMetrics(); // for positioning text

        // iterate over every vertex in the graph and its neighbors
        for (Vertex startVertex : graph.vertices) {
            for (Vertex endVertex : startVertex.neighbors.keySet()) {
                double weight = startVertex.neighbors.get(endVertex); // get the weight of this edge
                if (startVertex.color.getRGB() == endVertex.color.getRGB()) {
                    g2d.setColor(startVertex.color);
                } else if (startVertex == graph.vertices.get(0)) {
                    g2d.setColor(endVertex.color);
                } else if (endVertex == graph.vertices.get(0) && startVertex.color != Color.BLACK) {
                    g2d.setColor(startVertex.color);
                } else if(startVertex == graph.vertices.get(graph.vertices.size()-1) && endVertex.color == Color.BLUE) {
                    g2d.setColor(endVertex.color);
                }
                g2d.drawLine(startVertex.x, startVertex.y, endVertex.x, endVertex.y);

                if (showEdgeWeights) {
                    int midX = (startVertex.x + endVertex.x) / 2;
                    int midY = (startVertex.y + endVertex.y) / 2;
                    String weightStr = String.valueOf(weight);
                    int stringWidth = fm.stringWidth(weightStr);
                    g2d.drawString(weightStr, midX - stringWidth / 2, midY - fm.getAscent() / 2 + 5);
                }
                g2d.setColor(Color.GRAY);

            }
        }

        // draw vertices after so they are on top of edges
        for (Vertex c : graph.vertices) {
            g2d.setColor(c.color);
            g2d.fillOval(c.x - c.radius, c.y - c.radius, c.radius * 2, c.radius * 2);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(c.x - c.radius, c.y - c.radius, c.radius * 2, c.radius * 2);
        }
    }

    public DfsWorker startDfs() {
        Vertex start = graph.vertices.get(0);
        Vertex end = graph.vertices.get(graph.vertices.size() - 1);

        return new DfsWorker(start, end, this);  //we call worker.execute() from ControlPanel
    }

    public BfsWorker startBfs() {
        Vertex start = graph.vertices.get(0);
        Vertex end = graph.vertices.get(graph.vertices.size() - 1);

        return new BfsWorker(start, end, this);  //we call worker.execute() from ControlPanel
    }

    public DijkstrasWorker startDijkstras() {
        Vertex start = graph.vertices.get(0);
        Vertex end = graph.vertices.get(graph.vertices.size() - 1);

        return new DijkstrasWorker(start, end, this);  //we call worker.execute() from ControlPanel
    }

    public AstarWorker startAstar() {
        Vertex start = graph.vertices.get(0);
        Vertex end = graph.vertices.get(graph.vertices.size() - 1);

        return new AstarWorker(start, end, this);  //we call worker.execute() from ControlPanel
    }

    public BidirectionalAstarWorker startBidirectionalAstar() {
        Vertex start = graph.vertices.get(0);
        Vertex end = graph.vertices.get(graph.vertices.size() - 1);

        return new BidirectionalAstarWorker(start, end, this);  //we call worker.execute() from ControlPanel
    }
}