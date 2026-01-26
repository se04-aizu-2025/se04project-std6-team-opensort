package com.opensort.view;

import com.opensort.controller.IController;
import com.opensort.sorting.events.*;
import com.opensort.view.events.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A robust efficient and user friendly Swing GUI for visualizing sorting algorithms
 * Implements the MVC View interface to communicate with the Controller
 * Uses a local display state to decouple visualization from algorithm speed
 */
public class SortingGUI extends JFrame implements IView {

    // Constants
    private static final int INITIAL_WIDTH = 1000;
    private static final int INITIAL_HEIGHT = 600;
    private static final int BASE_DELAY_MS = 500;

    // Theme Configuration
    private static class Theme {
        static final Color BG = new Color(240, 240, 245);
        static final Color BOX_DEFAULT = new Color(100, 149, 237);  // Cornflower Blue
        static final Color BOX_COMPARE = new Color(255, 165, 0);    // Orange
        static final Color BOX_SWAP = new Color(220, 20, 60);       // Crimson
        static final Color BOX_SORTED = new Color(50, 205, 50);     // Lime Green
        static final Color BOX_HIGHLIGHT = new Color(0, 51, 102);   // Midnight blue
        static final Color TEXT = Color.WHITE;

        static final Font FONT_NUM = new Font("Arial", Font.BOLD, 24);
        static final Font FONT_UI = new Font("SansSerif", Font.PLAIN, 18);
        static final Stroke BORDER_STROKE = new BasicStroke(3);
        static final int MAX_ARC = 15;
    }

    // Components
    private VisualizerPanel panel;
    private JLabel statusLabel;
    private JButton playBtn, stepBtn;
    private JComboBox<String> speedBox;
    private JMenu algoMenu;

    // State
    // NOTE displayArray is the visual source of truth preventing threading artifacts
    private int[] displayArray;
    private boolean[] sortedFlags;
    private final List<IController> listeners = new ArrayList<>();

    // Queue and Threading
    private final ConcurrentLinkedQueue<SortEvent> eventQueue = new ConcurrentLinkedQueue<>();
    private final Thread eventProcessor;
    private volatile int dataVersion = 0;

    // Flow Control
    private final Object pauseLock = new Object();
    private volatile boolean isPaused = true;
    private volatile boolean stepOnce = false;
    private volatile int currentDelay = BASE_DELAY_MS;

    public SortingGUI() {
        initUI();
        eventProcessor = new Thread(this::processEventQueue);
        eventProcessor.setDaemon(true);
        eventProcessor.start();
    }

    // UI Setup

    private void initUI() {
        setTitle("OpenSort Visualizer");
        setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Theme.BG);
        setLocationRelativeTo(null);

        setJMenuBar(createMenuBar());
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        algoMenu = new JMenu("Algorithm");
        algoMenu.setFont(Theme.FONT_UI);

        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.setFont(Theme.FONT_UI);

        JMenuItem randomizeItem = new JMenuItem("Randomize Data");
        randomizeItem.addActionListener(e -> generateNewData(8, 1, 99));

        JMenuItem customInputItem = new JMenuItem("Input Custom Data");
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
        statusLabel.setFont(Theme.FONT_UI.deriveFont(24f));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel = new VisualizerPanel();
        centerPanel.add(statusLabel, BorderLayout.NORTH);
        centerPanel.add(panel, BorderLayout.CENTER);
        return centerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        playBtn = new JButton("▶ Play");
        playBtn.setFont(Theme.FONT_UI);
        playBtn.addActionListener(e -> togglePlay());

        stepBtn = new JButton("⏭ Step");
        stepBtn.setFont(Theme.FONT_UI);
        stepBtn.setEnabled(false);
        stepBtn.addActionListener(e -> triggerStep());

        String[] speeds = { "0.5x", "1.0x", "1.5x", "2.0x", "2.5x" };
        speedBox = new JComboBox<>(speeds);
        speedBox.setFont(Theme.FONT_UI);
        speedBox.setSelectedIndex(1);
        speedBox.addActionListener(e -> updateSpeed());

        bottomPanel.add(playBtn);
        bottomPanel.add(Box.createHorizontalStrut(15));
        bottomPanel.add(stepBtn);
        bottomPanel.add(Box.createHorizontalStrut(15));
        bottomPanel.add(new JLabel("Speed: "));
        bottomPanel.add(speedBox);
        return bottomPanel;
    }

    // Logic

    private void updateSpeed() {
        String selected = (String) speedBox.getSelectedItem();
        if (selected == null) return;
        double multiplier = Double.parseDouble(selected.replace("x", ""));
        this.currentDelay = (int) (BASE_DELAY_MS / multiplier);
    }

    private void generateNewData(int size, int min, int max) {
        int[] newArr = new Random().ints(size, min, max + 1).toArray();
        fireViewEvent(new ArrayChangeEvent(newArr));
        resetControls();
    }

    private boolean promptForCustomData() {
        String input = JOptionPane.showInputDialog(this, "Enter numbers e g five one four");
        if (input != null && !input.trim().isEmpty()) {
            try {
                String[] parts = input.split(",");
                int[] newArr = new int[parts.length];
                for (int i = 0; i < parts.length; i++) newArr[i] = Integer.parseInt(parts[i].trim());
                fireViewEvent(new ArrayChangeEvent(newArr));
                resetControls();
                return true;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return false;
    }

    // IView Implementation

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            if (displayArray == null || displayArray.length == 0) {
                String[] options = {"Input Custom Data", "Generate Random"};
                int choice = JOptionPane.showOptionDialog(this, "No data detected Initialize", "Welcome",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

                if (choice == JOptionPane.YES_OPTION) {
                    if (!promptForCustomData()) generateNewData(8, 1, 99);
                } else {
                    generateNewData(8, 1, 99);
                }
            }
        });
    }

    @Override
    public void setArray(int[] array) {
        if (array != null) {
            this.displayArray = array;
            this.sortedFlags = new boolean[array.length];
            this.dataVersion++;
            this.eventQueue.clear();
        }
        resetControls();
        panel.reset();
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
                updateStatus(name + " Selected Press Play");
                resetControls();
                panel.repaint();
            });
            algoMenu.add(item);
        }
    }

    @Override
    public void onSortEvent(SortEvent event) {
        eventQueue.add(event);
    }

    private void processEventQueue() {
        while (true) {
            try {
                synchronized (pauseLock) {
                    while (isPaused && !stepOnce) {
                        pauseLock.wait();
                    }
                    stepOnce = false;
                }

                SortEvent event = eventQueue.poll();
                processSingleEvent(event);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processSingleEvent(SortEvent event) {
        int startVersion = this.dataVersion;
        if (displayArray == null) return;

        // Bounds Check
        int maxIdx = displayArray.length - 1;
        if ((event instanceof CompareEvent && (((CompareEvent)event).getA() > maxIdx || ((CompareEvent)event).getB() > maxIdx)) ||
                (event instanceof SwapEvent && (((SwapEvent)event).getA() > maxIdx || ((SwapEvent)event).getB() > maxIdx))) {
            return;
        }

        if (event instanceof CompareEvent) {
            CompareEvent e = (CompareEvent) event;
            panel.updateState(e.getA(), e.getB(), Theme.BOX_COMPARE);
            updateStatus("Comparing " + displayArray[e.getA()] + " and " + displayArray[e.getB()]);
            panel.repaint();
            sleep(currentDelay);
        }
        else if (event instanceof SwapEvent) {
            SwapEvent e = (SwapEvent) event;
            updateStatus("Swapping " + displayArray[e.getA()] + " and " + displayArray[e.getB()]);

            // Animation
            panel.startSwapAnimation(e.getA(), e.getB());
            int frames = 20;
            for (int i = 0; i <= frames; i++) {
                if (this.dataVersion != startVersion) break;
                panel.setAnimationProgress((float) i / frames);
                panel.repaint();
                sleep(currentDelay / frames);
            }
            panel.stopAnimation();

            // Update local display state after animation
            int temp = displayArray[e.getA()];
            displayArray[e.getA()] = displayArray[e.getB()];
            displayArray[e.getB()] = temp;

            panel.updateState(e.getA(), e.getB(), Theme.BOX_SWAP);
        }
        else if (event instanceof MarkEvent) {
            MarkEvent e = (MarkEvent) event;
            int a = e.getA();
            if (a < displayArray.length && a >= 0) {
                switch (e.getType()){
                    case Sorted -> {
                        panel.updateState(a, a, Theme.BOX_SORTED);
                        markAsSorted(a);
                    }
                    case Highlight -> {
                        panel.updateState(a, a, Theme.BOX_HIGHLIGHT);
                    }
                }
            }
            updateStatus(e.getMessage());
            panel.repaint();
            sleep(currentDelay);
        }

        if (isPaused) {
            SwingUtilities.invokeLater(() -> stepBtn.setEnabled(true));
        }
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    private void markAsSorted(int index) {
        if (sortedFlags == null || index < 0 || index >= sortedFlags.length) return;
        sortedFlags[index] = true;
        boolean allSorted = true;
        for (boolean b : sortedFlags) if (!b) allSorted = false;
        if (allSorted) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Sorting Complete");
                JOptionPane.showMessageDialog(this, "The array has been fully sorted", "Success", JOptionPane.INFORMATION_MESSAGE);
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
        stepBtn.setEnabled(false);
        stepOnce = true;
        synchronized (pauseLock) { pauseLock.notifyAll(); }
    }

    private void fireViewEvent(ViewEvent event) {
        for (IController listener : listeners) listener.onViewEvent(event);
    }

    @Override public void addEventListener(IController l) { listeners.add(l); }
    @Override public void removeEventListener(IController l) { listeners.remove(l); }

    // Visualizer Component

    private class VisualizerPanel extends JPanel {
        private int idx1, idx2;
        private Color activeColor;
        private boolean isAnimating;
        private int swapIdx1, swapIdx2;
        private float progress;

        public VisualizerPanel() { setOpaque(false); reset(); }

        public void reset(){
            idx1 = -1;
            idx2 = -1;
            activeColor = Theme.BOX_DEFAULT;
            isAnimating = false;
            swapIdx1 = -1;
            swapIdx2 = -1;
            progress = 0f;
        }

        public void updateState(int a, int b, Color c) {
            this.idx1 = a; this.idx2 = b; this.activeColor = c;
        }

        public void startSwapAnimation(int a, int b) {
            this.isAnimating = true;
            this.swapIdx1 = a; this.swapIdx2 = b;
            this.progress = 0f;
            this.activeColor = Theme.BOX_SWAP;
            this.idx1 = -1; this.idx2 = -1;
        }

        public void setAnimationProgress(float p) { this.progress = p; }
        public void stopAnimation() { this.isAnimating = false; }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int[] arr = displayArray;
            if (arr == null || arr.length == 0) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setFont(Theme.FONT_NUM);

            int boxSize = Math.min(70, (getWidth() - 100) / arr.length);
            int gap = Math.max(5, boxSize / 4);
            int totalWidth = (arr.length * boxSize) + ((arr.length - 1) * gap);
            int startX = (getWidth() - totalWidth) / 2;
            int startY = (getHeight() - boxSize) / 2;
            int arc = Math.min(Theme.MAX_ARC, boxSize / 3);

            for (int i = 0; i < arr.length; i++) {
                int x = startX + i * (boxSize + gap);
                int y = startY;
                Color boxColor = Theme.BOX_DEFAULT;

                // Animation Logic
                if (isAnimating) {
                    if (i == swapIdx1) {
                        int targetX = startX + swapIdx2 * (boxSize + gap);
                        int sourceX = x;
                        x = (int) (sourceX + (targetX - sourceX) * progress);
                        y -= (int) (Math.sin(progress * Math.PI) * (boxSize / 3.0));
                        boxColor = Theme.BOX_SWAP;
                    }
                    else if (i == swapIdx2) {
                        int targetX = startX + swapIdx1 * (boxSize + gap);
                        int sourceX = x;
                        x = (int) (sourceX + (targetX - sourceX) * progress);
                        y += (int) (Math.sin(progress * Math.PI) * (boxSize / 3.0));
                        boxColor = Theme.BOX_SWAP;
                    }
                }

                // Coloring
                if (!isAnimating) {
                    if (sortedFlags != null && sortedFlags[i]) boxColor = Theme.BOX_SORTED;
                    else if (i == idx1 || i == idx2) boxColor = activeColor;
                } else if (i != swapIdx1 && i != swapIdx2) {
                    if (sortedFlags != null && sortedFlags[i]) boxColor = Theme.BOX_SORTED;
                }

                g2.setColor(boxColor);
                g2.fillRoundRect(x, y, boxSize, boxSize, arc, arc);

                g2.setColor(g2.getColor().darker());
                g2.setStroke(Theme.BORDER_STROKE);
                g2.drawRoundRect(x, y, boxSize, boxSize, arc, arc);

                g2.setColor(Theme.TEXT);
                String num = String.valueOf(arr[i]);
                FontMetrics fm = g2.getFontMetrics();
                java.awt.geom.Rectangle2D rect = fm.getStringBounds(num, g2);
                int textX = x + (boxSize - (int)rect.getWidth()) / 2;
                int textY = y + (boxSize - (int)rect.getHeight()) / 2 + fm.getAscent();
                g2.drawString(num, textX, textY);
            }
        }
    }
}