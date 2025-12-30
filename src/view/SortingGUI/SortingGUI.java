package com.opensort.view;

import com.opensort.sorting.*;
import com.opensort.sorting.events.*;
import com.opensort.testing.DataGenerator;

import javax.swing.*;
import java.awt.*;

// Main window for the application
public class SortingGUI extends JFrame implements IView {

    //UI Components
    private VisualizerPanel panel; 
    private JButton generateBtn, sortBtn;
    private JComboBox<String> algoBox;
    private JSlider speedSlider;

    //Data & State
    private int[] array; 
    private DataGenerator generator;
    private volatile boolean isSorting = false; 

    public SortingGUI() {
        // Initialize data generator and default array
        this.generator = new DataGenerator();
        this.array = generator.generateRandom(50, 10, 400); 
        initUI();
    }

    // Setup the window and buttons
    private void initUI() {
        setTitle("OpenSort Visualizer");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); 

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
            if (isSorting) return; 
            int maxH = panel.getHeight() > 50 ? panel.getHeight() - 50 : 400;
            array = generator.generateRandom(50, 10, maxH);
            panel.repaint();
        });

        // Action for "Run Sort" button
        sortBtn.addActionListener(e -> startSorting());
    }

    private void startSorting() {
        if (isSorting) return;
        isSorting = true;

        // Run sorting in a separate thread to keep UI responsive
        new Thread(() -> {
            String selected = (String) algoBox.getSelectedItem();
            SortingAlgorithm algorithm = null;

            // Instantiate the selected algorithm
            switch (selected) {
                case "Bubble Sort": algorithm = new BubbleSort(array); break;
                case "Selection Sort": algorithm = new SelectionSort(array); break;
                case "Insertion Sort": algorithm = new InsertionSort(array); break;
                case "Quick Sort": algorithm = new QuickSort(array); break;
                case "Merge Sort": algorithm = new MergeSort(array); break;
            }

            if (algorithm != null) {
                // Register this view as a listener to receive events
                algorithm.addEventListener(this);
                
                // Execute the sort
                algorithm.sort();
            }

            // Cleanup state after sorting completes
            panel.resetIndices();
            panel.repaint();
            isSorting = false;
        }).start();
    }

    @Override
    public void setArray(int[] array) {
        this.array = array;
        panel.repaint();
    }

    @Override
    public void onSortEvent(SortEvent event) {
        if (event instanceof CompareEvent) {
            CompareEvent e = (CompareEvent) event;
            panel.setIndices(e.getA(), e.getB());
            panel.setHighlightColor(Color.GREEN);
        } 
        else if (event instanceof SwapEvent) {
            SwapEvent e = (SwapEvent) event;
            panel.setIndices(e.getA(), e.getB());
            panel.setHighlightColor(Color.RED);
        }
        else if (event instanceof MarkEvent) {
            MarkEvent e = (MarkEvent) event;
            panel.setIndices(e.getA(), e.getA());
            panel.setHighlightColor(Color.ORANGE);
        }

        // Trigger redraw and wait
        panel.repaint();
        sleep();
    }

    // Helper to pause execution for animation effect
    private void sleep() {
        try {
            int delay = 101 - speedSlider.getValue();
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Inner class that handles the actual drawing
    private class VisualizerPanel extends JPanel {
        private int idx1 = -1; 
        private int idx2 = -1; 
        private Color highlightColor = Color.RED;

        public VisualizerPanel() {
            setBackground(Color.WHITE);
        }

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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (array == null) return;

            int width = getWidth() / array.length;
            
            for (int i = 0; i < array.length; i++) {
                int height = array[i];
                int x = i * width;
                int y = getHeight() - height; 

                if (i == idx1 || i == idx2) {
                    g.setColor(highlightColor);
                } else {
                    g.setColor(new Color(100, 149, 237));
                }

                g.fillRect(x, y, width - 2, height); 
            }
        }
    }
}