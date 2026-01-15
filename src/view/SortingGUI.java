package com.opensort.view;

import com.opensort.controller.IController;
import com.opensort.sorting.events.*;
import com.opensort.view.events.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SortingGUI extends JFrame implements IView {

    //Constants
    private static final int ANIMATION_DELAY_MS = 500; // Fixed speed

    //UI Components
    private VisualizerPanel panel;
    private JLabel statusLabel;
    private JButton playBtn, stepBtn;
    private JMenu algoMenu;

    //State
    private int[] array;
    private final List<IController> listeners = new ArrayList<>();

    //Concurrency Control
    private final Object pauseLock = new Object();
    private volatile boolean isPaused = true;
    private volatile boolean stepOnce = false;

    public SortingGUI() {
        initUI();
    }

    private void initUI() {
        setTitle("OpenSort Visualizer (Prototype)");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //Menu Bar
        JMenuBar menuBar = new JMenuBar();
        algoMenu = new JMenu("Algorithm"); // Populated by Controller later

        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem randomizeItem = new JMenuItem("Randomize Data");
        randomizeItem.addActionListener(e -> {
            // Generate simple random data
            int[] newArr = new Random().ints(8, 1, 99).toArray();
            fireViewEvent(new ArrayChangeEvent(newArr));
            resetControls();
        });

        settingsMenu.add(randomizeItem);
        menuBar.add(algoMenu);
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);

        //Center Panel
        statusLabel = new JLabel("Select an Algorithm", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        panel = new VisualizerPanel();

        add(statusLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        //Bottom Controls
        JPanel bottomPanel = new JPanel();
        playBtn = new JButton("Play");
        playBtn.addActionListener(e -> togglePlay());

        stepBtn = new JButton("Step");
        stepBtn.addActionListener(e -> triggerStep());
        stepBtn.setEnabled(false);

        bottomPanel.add(playBtn);
        bottomPanel.add(stepBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    //Control Logic
    private void togglePlay() {
        isPaused = !isPaused;
        playBtn.setText(isPaused ? "Play" : "Pause");
        stepBtn.setEnabled(isPaused);
        if (!isPaused) synchronized (pauseLock) { pauseLock.notifyAll(); }
    }

    private void triggerStep() {
        if (!isPaused) return;
        stepOnce = true;
        synchronized (pauseLock) { pauseLock.notifyAll(); }
    }

    private void resetControls() {
        isPaused = true;
        playBtn.setText("Play");
        stepBtn.setEnabled(true);
    }

    //IView Implementation
    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> setVisible(true));
        int[] startData = new Random().ints(8, 1, 99).toArray();
        fireViewEvent(new ArrayChangeEvent(startData));
    }

    @Override
    public void setArray(int[] array) {
        this.array = array;
        panel.repaint();
    }

    @Override
    public void setAlgorithms(String[] algorithms) {
        algoMenu.removeAll();
        for (int i = 0; i < algorithms.length; i++) {
            String name = algorithms[i];
            int id = i;
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(e -> {
                fireViewEvent(new AlgorithmChangeEvent(id));
                statusLabel.setText(name + " Selected");
                resetControls();
            });
            algoMenu.add(item);
        }
    }

    @Override
    public void onSortEvent(SortEvent event) {
        //Update Visual State
        if (event instanceof CompareEvent) {
            CompareEvent e = (CompareEvent) event;
            panel.updateState(e.getA(), e.getB());
            statusLabel.setText("Comparing indices " + e.getA() + " & " + e.getB());
        }
        else if (event instanceof SwapEvent) {
            SwapEvent e = (SwapEvent) event;
            panel.updateState(e.getA(), e.getB());
            statusLabel.setText("Swapping!");
        }

        panel.repaint();

        //Handle Pausing & Speed
        synchronized (pauseLock) {
            if (isPaused) {
                while (isPaused && !stepOnce) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                stepOnce = false;
            } else {
                try {
                    Thread.sleep(ANIMATION_DELAY_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void addEventListener(IController listener) { listeners.add(listener); }

    @Override
    public void removeEventListener(IController listener) { listeners.remove(listener); }

    private void fireViewEvent(ViewEvent event) {
        for (IController listener : listeners) listener.onViewEvent(event);
    }

    //Visualizer Component
    private class VisualizerPanel extends JPanel {
        private int idx1 = -1, idx2 = -1;

        public void updateState(int a, int b) {
            this.idx1 = a;
            this.idx2 = b;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (array == null) return;

            // Simple box drawing settings
            int boxSize = 70;
            int gap = 20;
            int startX = (getWidth() - (array.length * (boxSize + gap))) / 2;
            int startY = (getHeight() - boxSize) / 2;

            g.setFont(new Font("Arial", Font.BOLD, 24));

            for (int i = 0; i < array.length; i++) {
                int x = startX + i * (boxSize + gap);

                if (i == idx1 || i == idx2) {
                    g.setColor(Color.LIGHT_GRAY);
                } else {
                    g.setColor(Color.WHITE);
                }

                // Draw filled box
                g.fillRect(x, startY, boxSize, boxSize);

                // Draw outline
                g.setColor(Color.BLACK);
                g.drawRect(x, startY, boxSize, boxSize);

                // Draw number
                String num = String.valueOf(array[i]);
                g.drawString(num, x + 25, startY + 45);
            }
        }
    }
}