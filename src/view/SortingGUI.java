package com.opensort.view;

import com.opensort.controller.IController;
import com.opensort.sorting.events.SortEvent;
import com.opensort.view.events.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SortingGUI extends JFrame implements IView {

    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 600;

    // UI Components
    private JPanel panel;
    private JLabel statusLabel;
    private JButton playBtn, stepBtn;
    private JMenu algoMenu;

    // MVC State
    private int[] array;
    private final List<IController> listeners = new ArrayList<>();

    public SortingGUI() {
        initUI();
    }

    private void initUI() {
        setTitle("OpenSort Visualizer (Skeleton)");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Menu Stub
        JMenuBar menuBar = new JMenuBar();
        algoMenu = new JMenu("Algorithm");
        menuBar.add(algoMenu);
        setJMenuBar(menuBar);

        // 2. Center Panel Stub
        statusLabel = new JLabel("Waiting for data...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 24));

        panel = new JPanel(); // Placeholder panel
        panel.setBackground(Color.LIGHT_GRAY);

        add(statusLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        // 3. Bottom Controls Stub
        JPanel bottomPanel = new JPanel();
        playBtn = new JButton("Play");
        stepBtn = new JButton("Step");
        bottomPanel.add(playBtn);
        bottomPanel.add(stepBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- IView Interface Implementation ---

    @Override
    public void run() {
        SwingUtilities.invokeLater(() -> setVisible(true));
    }

    @Override
    public void setArray(int[] array) {
        this.array = array;
        // Logic to repaint panel will go here later
    }

    @Override
    public void setAlgorithms(String[] algorithms) {
        algoMenu.removeAll();
        for (String name : algorithms) {
            algoMenu.add(new JMenuItem(name));
        }
    }

    @Override
    public void onSortEvent(SortEvent event) {
        // Event handling logic will go here
    }

    @Override
    public void addEventListener(IController listener) { listeners.add(listener); }

    @Override
    public void removeEventListener(IController listener) { listeners.remove(listener); }

    // Helper to send events to Controller
    protected void fireViewEvent(ViewEvent event) {
        for (IController listener : listeners) listener.onViewEvent(event);
    }
}