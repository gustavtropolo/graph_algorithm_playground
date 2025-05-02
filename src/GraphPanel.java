import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * This is basically a JPanel component to display the Graph
 * and allows me to drag the vertices around interactively.
 * Later this will also include a menu for different graph traversals.
 */
class GraphPanel extends JPanel {
    private Graph graph;
    private Vertex dragged = null;
    private int offsetX, offsetY;
    private final int EDGE_WEIGHT_FONT_SIZE = 10; // define font size for edges


    /**
     * The main logic that constructs the panel
     *
     * @param n      The number of vertices
     * @param width  The width where we can place vertices
     * @param height The height where we can place vertices
     */
    public GraphPanel(int n, int ball_radius, int width, int height, int maxDist) {

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
     * Overrides paintComponent() so we can draw the graph's vertices and edges.
     * */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g; // Use Graphics2D for better control

        // Set rendering hints for smoother drawing (optional)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- Draw Edges and Weights ---
        g2d.setColor(Color.BLACK); // Set color for edges
        Font edgeFont = new Font(Font.SANS_SERIF, Font.PLAIN, EDGE_WEIGHT_FONT_SIZE); // Create font for weights
        g2d.setFont(edgeFont);
        FontMetrics fm = g2d.getFontMetrics(); // Get font metrics for positioning text

        // iterate over every vertex in the graph and its neighbors
        for (Vertex startVertex : graph.vertices) {
            for (Vertex endVertex : startVertex.neighbors.keySet()) {
                int weight = startVertex.neighbors.get(endVertex); // get the weight of this edge
                g2d.drawLine(startVertex.x, startVertex.y, endVertex.x, endVertex.y);

                int midX = (startVertex.x + endVertex.x) / 2;
                int midY = (startVertex.y + endVertex.y) / 2;
                String weightStr = String.valueOf(weight);
                int stringWidth = fm.stringWidth(weightStr);
                g2d.drawString(weightStr, midX - stringWidth / 2, midY - fm.getAscent() / 2 + 5);

            }
        }

        // draw vertices after so they are on top of edges
        for (Vertex c : graph.vertices) { //
            g2d.setColor(c.color); //
            g2d.fillOval(c.x - c.radius, c.y - c.radius, c.radius * 2, c.radius * 2); //
            // Optionally draw a border around vertices
            g2d.setColor(Color.BLACK);
            g2d.drawOval(c.x - c.radius, c.y - c.radius, c.radius * 2, c.radius * 2); //
        }
    }
}