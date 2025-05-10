import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A JPanel containing controls for graph operations.
 */
public class ControlPanel extends JPanel {

    private DfsWorker currentDfsWorker = null;
    private BfsWorker currentBfsWorker = null;
    private DijkstrasWorker currentDijkstrasWorker = null;
    private AstarWorker currentAstarWorker = null;
    private BidirectionalAstarWorker currentBidirectionalAstarWorker = null;

    public ControlPanel(GraphPanel graphPanel) {

        AtomicReference<Graph> graph = new AtomicReference<>(graphPanel.graph); // get the graph

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Traversal Algorithms"));
        add(Box.createRigidArea(new Dimension(0, 5)));

        JButton dfsButton = new JButton("Run DFS");
        dfsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        dfsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelRunningThreads(graphPanel);

                currentDfsWorker = graphPanel.startDfs();

                if (currentDfsWorker != null) {
                    currentDfsWorker.execute();
                }
            }
        });
        add(dfsButton);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // BFS
        JButton bfsButton = new JButton("Run BFS");
        bfsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        bfsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelRunningThreads(graphPanel);

                currentBfsWorker = graphPanel.startBfs();

                if (currentBfsWorker != null) {
                    currentBfsWorker.execute();
                }
            }
        });
        add(bfsButton);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // DIJKSTRA's
        JButton dijkstrasButton = new JButton("Run Dijkstra's");
        dijkstrasButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        dijkstrasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelRunningThreads(graphPanel);

                currentDijkstrasWorker = graphPanel.startDijkstras();

                if (currentDijkstrasWorker != null) {
                    currentDijkstrasWorker.execute();
                }
            }
        });
        add(dijkstrasButton);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // A*
        JButton AstarButton = new JButton("Run A*");
        AstarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        AstarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelRunningThreads(graphPanel);

                currentAstarWorker = graphPanel.startAstar();

                if (currentAstarWorker != null) {
                    graphPanel.currentAstarWorker = currentAstarWorker;
                    currentAstarWorker.execute();
                }
            }
        });
        add(AstarButton);
        add(Box.createRigidArea(new Dimension(0, 10)));

        // Bidirectional A*
        JButton BidirectionalAstarButton = new JButton("Run Bidirectional A*");
        BidirectionalAstarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        BidirectionalAstarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelRunningThreads(graphPanel);

                currentBidirectionalAstarWorker = graphPanel.startBidirectionalAstar();

                if (currentBidirectionalAstarWorker != null) {
                    graphPanel.currentBidirectionalAstarWorker = currentBidirectionalAstarWorker;
                    currentBidirectionalAstarWorker.execute();
                }
            }
        });
        add(BidirectionalAstarButton);
        add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel numVerticesLabel = new JLabel("Number of Vertices:");
        numVerticesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(numVerticesLabel);

        JTextField numVerticesField = new JTextField("50", 5);
        numVerticesField.setMaximumSize(numVerticesField.getPreferredSize());
        numVerticesField.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(numVerticesField);
        add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel vertexSizeLabel = new JLabel("Vertex Size:");
        vertexSizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(vertexSizeLabel);

        JTextField vertexSizeField = new JTextField("25", 5);
        vertexSizeField.setMaximumSize(vertexSizeField.getPreferredSize());
        vertexSizeField.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(vertexSizeField);
        add(Box.createRigidArea(new Dimension(0, 10)));

        JCheckBox showWeightsCheckbox = new JCheckBox("Show Edge Weights", graphPanel.showEdgeWeights);
        showWeightsCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        showWeightsCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox source = (JCheckBox) e.getSource();
                graphPanel.showEdgeWeights = source.isSelected();
                graphPanel.repaint();
            }
        });
        add(showWeightsCheckbox);

        add(Box.createRigidArea(new Dimension(0, 5)));
        JButton newGraphButton = new JButton("Create Graph");
        newGraphButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGraphButton.addActionListener(e -> {
            int numOfVertices = Integer.parseInt(numVerticesField.getText());
            int vertexSize = Integer.parseInt(vertexSizeField.getText());
            graphPanel.newGraph(numOfVertices, vertexSize);
            graph.set(graphPanel.graph);
        });
        add(newGraphButton);


        add(Box.createRigidArea(new Dimension(0, 15)));

        ////////////////////////////////////Slider
        JSlider animationSpeedSlider = new JSlider(JSlider.HORIZONTAL, 50, 1000, 250); //min, max, init
        animationSpeedSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel animationSpeedLabel = new JLabel("Animation Delay: " + animationSpeedSlider.getValue() + "ms");
        animationSpeedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(animationSpeedLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));

        animationSpeedSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();
                int delay = source.getValue();
                animationSpeedLabel.setText("Animation Delay: " + delay + "ms");
                graphPanel.animationDelay = delay;
            }
        });
        add(animationSpeedSlider);
        add(Box.createRigidArea(new Dimension(0, 10))); //spacing after slider
        graphPanel.animationDelay = animationSpeedSlider.getValue(); //set it to the initial value the first time


        ////////////////////////////////////Slider 2
        JSlider graphDensitySlider = new JSlider(JSlider.HORIZONTAL, 0,  50, 0); //min, max, init
        JLabel graphDensityLabel = new JLabel("Graph density: " + graphDensitySlider.getValue());
        graphDensityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(graphDensityLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        graphDensitySlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        int[] prev_density = {0};
        graphDensitySlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider)e.getSource();

                int density = source.getValue();
                if (density % 10 == 0 && density != prev_density[0]) {
                    graph.get().setDensity(density);
                    graphDensityLabel.setText("Graph density: " + density);
                    prev_density[0] = density;
                    graphPanel.repaint(); //repaint after updating the density
                }
            }
        });
        add(graphDensitySlider);
        add(Box.createRigidArea(new Dimension(0, 10)));
    }

    public void cancelRunningThreads(GraphPanel graphPanel) {
        if (currentBfsWorker != null && !currentBfsWorker.isDone()) {
            currentBfsWorker.cancel(true);
            currentBfsWorker = null;
        }
        if (currentDfsWorker != null && !currentDfsWorker.isDone()) {
            currentDfsWorker.cancel(true);
            currentDfsWorker = null;
        }
        if (currentDijkstrasWorker != null && !currentDijkstrasWorker.isDone()) {
            currentDijkstrasWorker.cancel(true);
            currentDijkstrasWorker = null;
        }
        if (currentAstarWorker != null && !currentAstarWorker.isDone()) {
            currentAstarWorker.cancel(true);
            currentAstarWorker = null;
            graphPanel.currentAstarWorker = null;
        }
        if (currentBidirectionalAstarWorker != null && !currentBidirectionalAstarWorker.isDone()) {
            currentBidirectionalAstarWorker.cancel(true);
            currentBidirectionalAstarWorker = null;
            graphPanel.currentBidirectionalAstarWorker = null;
        }

        // reset colors
        for (Vertex v : graphPanel.graph.vertices) {
            v.color = Color.GRAY;
        }
    }
}