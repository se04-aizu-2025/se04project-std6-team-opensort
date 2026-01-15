package com.opensort.view;

import com.opensort.controller.IController;
import com.opensort.sorting.events.*;
import com.opensort.view.events.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A robust, efficient, and user-friendly Swing GUI for visualizing sorting algorithms.
 * Implements the MVC View interface to communicate with the Controller.
 */
public class SortingGUI extends JFrame implements IView {

    //Constants
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 600;
    private static final int BASE_DELAY_MS = 500; // Base speed (1.0x)

    // Drawing Settings
    private static final int BOX_SIZE = 70;
    private static final int BOX_GAP = 25;
    private static final int BOX_ARC = 15;
    private static final Font FONT_NUMBERS = new Font("Arial", Font.BOLD, 28);
    private static final Font FONT_STATUS = new Font("SansSerif", Font.PLAIN, 24);
    private static final Font FONT_CONTROLS = new Font("SansSerif", Font.BOLD, 16);
    private static final Stroke STROKE_BORDER = new BasicStroke(3);

    // Color Palette
    private static final Color COL_BACKGROUND = new Color(240, 240, 245);
    private static final Color COL_DEFAULT = new Color(100, 149, 237); // Cornflower Blue
    private static final Color COL_COMPARE = new Color(255, 165, 0);   // Orange
    private static final Color COL_SWAP = new Color(220, 20, 60);      // Crimson
    private static final Color COL_SORTED = new Color(50, 205, 50);    // Lime Green
    private static final Color COL_TEXT = Color.WHITE;

    //Components
    private VisualizerPanel panel;
    private JLabel statusLabel;
    private JButton playBtn, stepBtn;
    private JComboBox<String> speedBox;
    private JMenu algoMenu;

    //State
    private int[] array;
    private boolean[] sortedFlags;
    private final List<IController> listeners = new ArrayList<>();

    //Concurrency Control
    private final Object pauseLock = new Object();
    private volatile boolean isPaused = true;
    private volatile boolean stepOnce = false;
    private volatile int currentDelay = BASE_DELAY_MS;

    public SortingGUI() {
        initUI();
    }

    //UI Setup Methods
    private void initUI() {
        setTitle("OpenSort Visualizer");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COL_BACKGROUND);

        setJMenuBar(createMenuBar());
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Algorithm Menu
        algoMenu = new JMenu("Algorithm");

        //Settings Menu
        JMenu settingsMenu = new JMenu("Settings");

        JMenuItem randomizeItem = new JMenuItem("Randomize Data");
        randomizeItem.addActionListener(e -> generateNewData(8, 1, 99));

        JMenuItem customInputItem = new JMenuItem("Input Custom Data...");
        customInputItem.addActionListener(e -> promptForCustomData());

        settingsMenu.add(randomizeItem);
        settingsMenu.add(customInputItem);

        menuBar.add(algoMenu);
        menuBar.add(settingsMenu);
        return menuBar;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        statusLabel = new JLabel("Select an Algorithm to start", SwingConstants.CENTER);
        statusLabel.setFont(FONT_STATUS);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        panel = new VisualizerPanel();

        centerPanel.add(statusLabel, BorderLayout.NORTH);
        centerPanel.add(panel, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        playBtn = new JButton("▶ Play");
        playBtn.setFont(FONT_CONTROLS);
        playBtn.addActionListener(e -> togglePlay());

        stepBtn = new JButton("⏭ Step");
        stepBtn.setFont(FONT_CONTROLS);
        stepBtn.setEnabled(false);
        stepBtn.addActionListener(e -> triggerStep());

        String[] speeds = { "0.5x", "1.0x", "1.5x", "2.0x", "2.5x" };
        speedBox = new JComboBox<>(speeds);
        speedBox.setFont(FONT_CONTROLS);
        speedBox.setSelectedIndex(1); // Default to 1.0x
        speedBox.setToolTipText("Animation Speed");
        speedBox.addActionListener(e -> updateSpeed());

        bottomPanel.add(playBtn);
        bottomPanel.add(Box.createHorizontalStrut(15));
        bottomPanel.add(stepBtn);
        bottomPanel.add(Box.createHorizontalStrut(15));
        bottomPanel.add(new JLabel("Speed: "));
        bottomPanel.add(speedBox);
        return bottomPanel;
    }

    //Core Logic & Events
    private void updateSpeed() {
        String selected = (String) speedBox.getSelectedItem();
        if (selected == null) return;
        double multiplier = Double.parseDouble(selected.replace("x", ""));
        this.currentDelay = (int) (BASE_DELAY_MS / multiplier);
    }

    private void generateNewData(int size, int min, int max) {
        Random rand = new Random();
        int[] newArr = new int[size];
        for (int i = 0; i < size; i++) {
            newArr[i] = rand.nextInt(max - min + 1) + min;
        }
        fireViewEvent(new ArrayChangeEvent(newArr));
        resetControls();
    }

    private void promptForCustomData() {
        String input = JOptionPane.showInputDialog(this,
                "Enter numbers separated by commas (e.g. 5, 1, 4, 2):",
                "Input Custom Data", JOptionPane.PLAIN_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                String[] parts = input.split(",");
                int[] newArr = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    newArr[i] = Integer.parseInt(parts[i].trim());
                }
                fireViewEvent(new ArrayChangeEvent(newArr));
                resetControls();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid input! Use numbers separated by commas.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //IView Implementation
    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> setVisible(true));
        generateNewData(8, 1, 99); // Generate initial data
    }

    @Override
    public void setArray(int[] array) {
        this.array = array;
        if (array != null) {
            this.sortedFlags = new boolean[array.length];
        }
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
                updateStatus(name + " Selected. Press Play.");
                resetControls();
                // Reset sorted flags just in case
                if (array != null) sortedFlags = new boolean[array.length];
                panel.repaint();
            });
            algoMenu.add(item);
        }
    }

    @Override
    public void onSortEvent(SortEvent event) {
        //Update State based on event
        if (event instanceof CompareEvent) {
            CompareEvent e = (CompareEvent) event;
            panel.updateState(e.getA(), e.getB(), COL_COMPARE);
            updateStatus("Comparing " + e.getA() + " and " + e.getB());
        }
        else if (event instanceof SwapEvent) {
            SwapEvent e = (SwapEvent) event;
            panel.updateState(e.getA(), e.getB(), COL_SWAP);
            updateStatus("Swapping!");
        }
        else if (event instanceof MarkEvent) {
            MarkEvent e = (MarkEvent) event;
            panel.updateState(e.getA(), e.getA(), COL_SORTED);
            updateStatus(e.getMessage());

            if (e.getType() == MarkEventType.Sorted) {
                markAsSorted(e.getA());
            }
        }

        panel.repaint();

        //Control Animation Flow
        synchronized (pauseLock) {
            if (isPaused) {
                while (isPaused && !stepOnce) {
                    try { pauseLock.wait(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
                stepOnce = false;
            } else {
                try { Thread.sleep(currentDelay); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }

    //Helper Methods
    private void markAsSorted(int index) {
        if (sortedFlags == null || index < 0 || index >= sortedFlags.length) return;
        sortedFlags[index] = true;
        // Check if fully sorted
        boolean allSorted = true;
        for (boolean b : sortedFlags) if (!b) allSorted = false;

        if (allSorted) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Sorting Complete!");
                JOptionPane.showMessageDialog(this, "The array has been fully sorted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetControls();
            });
        }
    }
    private void updateStatus(String text) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(text));
    }

    private void resetControls() {
        isPaused = true;
        playBtn.setText("▶ Play");
        stepBtn.setEnabled(true);
    }

    private void togglePlay() {
        isPaused = !isPaused;
        playBtn.setText(isPaused ? "▶ Play" : "⏸ Pause");
        stepBtn.setEnabled(isPaused);
        if (!isPaused) synchronized (pauseLock) { pauseLock.notifyAll(); }
    }

    private void triggerStep() {
        if (!isPaused) return;
        stepOnce = true;
        synchronized (pauseLock) { pauseLock.notifyAll(); }
    }

    private void fireViewEvent(ViewEvent event) {
        for (IController listener : listeners) listener.onViewEvent(event);
    }

    @Override
    public void addEventListener(IController listener) { listeners.add(listener); }

    @Override
    public void removeEventListener(IController listener) { listeners.remove(listener); }

    //Visualizer Component
    private class VisualizerPanel extends JPanel {
        private int idx1 = -1, idx2 = -1;
        private Color activeColor = COL_DEFAULT;
        public VisualizerPanel() {
            setOpaque(false);
        }

        public void updateState(int a, int b, Color c) {
            this.idx1 = a;
            this.idx2 = b;
            this.activeColor = c;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (array == null) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2.setFont(FONT_NUMBERS);

            // Calculate layout centering
            int totalWidth = (array.length * BOX_SIZE) + ((array.length - 1) * BOX_GAP);
            int startX = (getWidth() - totalWidth) / 2;
            int startY = (getHeight() - BOX_SIZE) / 2;

            for (int i = 0; i < array.length; i++) {
                int x = startX + i * (BOX_SIZE + BOX_GAP);

                // Determine color
                if (sortedFlags != null && sortedFlags[i]) {
                    g2.setColor(COL_SORTED);
                } else if (i == idx1 || i == idx2) {
                    g2.setColor(activeColor);
                } else {
                    g2.setColor(COL_DEFAULT);
                }

                // Draw Box
                g2.fillRoundRect(x, startY, BOX_SIZE, BOX_SIZE, BOX_ARC, BOX_ARC);

                // Draw Border
                g2.setColor(g2.getColor().darker());
                g2.setStroke(STROKE_BORDER);
                g2.drawRoundRect(x, startY, BOX_SIZE, BOX_SIZE, BOX_ARC, BOX_ARC);

                // Draw Number
                g2.setColor(COL_TEXT);
                String num = String.valueOf(array[i]);
                FontMetrics fm = g2.getFontMetrics();
                int textX = x + (BOX_SIZE - fm.stringWidth(num)) / 2;
                int textY = startY + (BOX_SIZE - fm.getHeight()) / 2 + fm.getAscent() - 2;
                g2.drawString(num, textX, textY);
            }
        }
    }
}