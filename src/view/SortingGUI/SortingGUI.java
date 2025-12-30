package com.opensort.view;

import com.opensort.sorting.*;
import com.opensort.sorting.events.*;
import com.opensort.testing.DataGenerator;

import javax.swing.*;
import java.awt.*;

// Main window for the application
public class SortingGUI extends JFrame implements IView {

    //UI Components
    private VisualizerPanel panel; // The area where bars are drawn
    private JButton generateBtn, sortBtn;
    private JComboBox<String> algoBox;
    private JSlider speedSlider;

    //Data & State
    private int[] array; // The data we are sorting
    private DataGenerator generator;
    private volatile boolean isSorting = false; // Flag to prevent running two sorts at once

    public SortingGUI() {
        // Initialize data generator and default array
        this.generator = new DataGenerator();
        // Create 50 random numbers between 10 and 400
        this.array = generator.generateRandom(50, 10, 400); 
        initUI();
    }

    // Setup the window and buttons
    private void initUI() {
        setTitle("OpenSort Visualizer");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // Layout manager for the window

        // Add the drawing panel to the center
        panel = new VisualizerPanel();
        add(panel, BorderLayout.CENTER);

        // Create the top control bar
        JPanel controls = new JPanel();
        controls.setBackground(Color.DARK_GRAY);

        // Algorithm selection
        String[] algos = {
            "Bubble Sort", 
            "Selection Sort", 
            "Insertion Sort", 
            "Quick Sort", 
            "Merge Sort" 
        };
        algoBox = new JComboBox<>(algos);
        
        generateBtn = new JButton("New Random Data");
        sortBtn = new JButton("Run Sort");

        // Slider to control animation speed
        speedSlider = new JSlider(1, 100, 50);
        speedSlider.setBackground(Color.DARK_GRAY);

        // Add components to the control bar
        controls.add(new JLabel("Algo: "));
        controls.add(algoBox);
        controls.add(generateBtn);
        controls.add(sortBtn);
        controls.add(new JLabel(" Speed: "));
        controls.add(speedSlider);

        // Place controls at the top of the window
        add(controls, BorderLayout.NORTH);

        // Action for "New Random Data" button
        generateBtn.addActionListener(e -> {
            if (isSorting) return; // Don't reset if currently sorting
            
            // Calculate max height based on window size
            int maxH = panel.getHeight() > 50 ? panel.getHeight() - 50 : 400;
            array = generator.generateRandom(50, 10, maxH);
            panel.repaint();
        });
    }

    // Stub methods to satisfy IView interface
    @Override
    public void setArray(int[] array) { }

    @Override
    public void onSortEvent(SortEvent event) { }

    // Inner class that handles the actual drawing
    private class VisualizerPanel extends JPanel {
        private int idx1 = -1; // First bar to highlight
        private int idx2 = -1; // Second bar to highlight
        private Color highlightColor = Color.RED;

        public VisualizerPanel() {
            setBackground(Color.WHITE);
        }

        // Updates which bars should be highlighted
        public void setIndices(int a, int b) {
            this.idx1 = a;
            this.idx2 = b;
        }

        public void setHighlightColor(Color c) {
            this.highlightColor = c;
        }

        public void resetIndices() {
            this.idx1 = -1;
            this.idx2 = -1;
        }

        // Draws the array as bars on the screen
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (array == null) return;

            int width = getWidth() / array.length;
            
            for (int i = 0; i < array.length; i++) {
                int height = array[i];
                int x = i * width;
                int y = getHeight() - height;

                // Highlight the active bars
                if (i == idx1 || i == idx2) {
                    g.setColor(highlightColor);
                } else {
                    g.setColor(new Color(100, 149, 237));
                }

                g.fillRect(x, y, width - 2, height); // Draw the bar
            }
        }
    }
}