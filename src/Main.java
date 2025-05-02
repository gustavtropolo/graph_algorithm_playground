import javax.swing.*;

/**
 * This is where we enter the program and initialize
 * the frame and the graph panel with the height and
 * width that we want. (And the right amount of balls)
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Draggable Circles");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int width = 1000;
        int height = 800;
        int num_balls = 90;
        int ball_radius = 25;
        int max_edge_dist = 100;
        frame.setSize(width, height);
        frame.add(new GraphPanel(num_balls, ball_radius, width, height, max_edge_dist));
        frame.setVisible(true);
    }
}




