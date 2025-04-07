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
        int num_balls = 100;
        frame.setSize(width, height);
        frame.add(new GraphPanel(num_balls, width, height));
        frame.setVisible(true);
    }
}




