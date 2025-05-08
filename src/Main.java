import javax.swing.*;
import java.awt.*;

/**
 * This is where we enter the program and initialize
 * the frame and the graph panel with the height and
 * width that we want. (And the right amount of balls)
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Draggable Circles");
        frame.setLayout(new BorderLayout()); // Use BorderLayout
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int width = 1500;
        int height = 1100;
        int num_balls = 50; //90
        int ball_radius = 25; //25
        int max_edge_dist = 10; //100
        GraphPanel graphPanel = new GraphPanel(num_balls, ball_radius, width, height, max_edge_dist);
        frame.add(graphPanel);


        int controlPanelWidth = 250;
        ControlPanel controlPanel = new ControlPanel(graphPanel); // pass the graph panel so we can control it
        controlPanel.setPreferredSize(new Dimension(controlPanelWidth, height));

        frame.add(graphPanel, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.EAST);

        frame.setSize(width + controlPanelWidth, height);
        frame.setVisible(true);
    }
}




