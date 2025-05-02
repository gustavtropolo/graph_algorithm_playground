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
    private Graph graph;
    private Vertex dragged = null;
    private int offsetX, offsetY;


    /**
     * The main logic that constructs the panel
     *
     * @param n      The number of vertices
     * @param width  The width where we can place vertices
     * @param height The height where we can place vertices
     */
    public GraphPanel(int n, int width, int height) {

        graph = new Graph(n, width, height - 28); // subtract the constant amount of height taken by the top bar

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
        for (Vertex c : graph.vertices) {
            g.setColor(c.color);
            g.fillOval(c.x - c.radius, c.y - c.radius, c.radius * 2, c.radius * 2);
        }
    }
}