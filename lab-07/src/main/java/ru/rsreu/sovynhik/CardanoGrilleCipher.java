package ru.rsreu.sovynhik;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CardanoGrilleCipher extends JFrame {
    private JPanel mainPanel;
    private JPanel topLeftPanel;
    private JPanel topMiddlePanel;
    private JPanel topRightPanel;
    private JPanel bottomLeftPanel;
    private JPanel bottomMiddlePanel;
    private JPanel bottomRightPanel;

    private JTextField sizeField;
    private JButton resetButton;
    private JButton rotateButton;
    private JButton complexGridButton;
    private JButton clearSelectionButton;
    private JButton generateStencilButton;
    private JButton rotateStencilButton;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton clearTextButton;
    private JLabel statusLabel;

    private JPanel complexGridContainer;
    private List<JButton> currentButtons;
    private int currentGridSize;
    private boolean isComplexGrid;
    private int smallGridSize;

    private Set<Integer> selectedNumbers;
    private Set<JButton> selectedButtons;
    private Color originalButtonColor = new Color(220, 240, 255);
    private Color selectedButtonColor = new Color(100, 200, 100);
    private Color disabledButtonColor = new Color(200, 200, 200);

    private List<SelectedCell> selectedCells;
    private int currentRotation;
    private char[][] currentEncryptedGrid;

    private class SelectedCell {
        int row;
        int col;
        int number;
        int block;

        SelectedCell(int row, int col, int number, int block) {
            this.row = row;
            this.col = col;
            this.number = number;
            this.block = block;
        }
    }

    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JPanel encryptedGridPanel;
    private JTextArea stencilSequenceArea;

    public CardanoGrilleCipher() {
        setTitle("Шифр Решетка Кардано");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        currentButtons = new ArrayList<>();
        currentGridSize = 0;
        isComplexGrid = false;
        selectedNumbers = new HashSet<>();
        selectedButtons = new HashSet<>();
        smallGridSize = 0;
        selectedCells = new ArrayList<>();
        currentRotation = 0;
        currentEncryptedGrid = null;

        mainPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));

        topLeftPanel = createPanelWithTitle("Решетка Кардано (выберите ячейки)", new Color(255, 255, 255));
        topMiddlePanel = createPanelWithTitle("Трафарет", new Color(255, 255, 255));
        topRightPanel = createPanelWithTitle("Ввод текста", new Color(255, 255, 255));
        bottomLeftPanel = createPanelWithTitle("Зашифрованная сетка", new Color(255, 255, 255));
        bottomMiddlePanel = createPanelWithTitle("Результат", new Color(255, 255, 255));
        bottomRightPanel = createPanelWithTitle("Управление", new Color(245, 245, 245));

        setupTopLeftPanel();
        setupTopMiddlePanel();
        setupTopRightPanel();
        setupBottomLeftPanel();
        setupBottomMiddlePanel();
        setupBottomRightPanel();

        mainPanel.add(topLeftPanel);
        mainPanel.add(topMiddlePanel);
        mainPanel.add(topRightPanel);
        mainPanel.add(bottomLeftPanel);
        mainPanel.add(bottomMiddlePanel);
        mainPanel.add(bottomRightPanel);

        add(mainPanel, BorderLayout.CENTER);

        setSize(1600, 900);
        setLocationRelativeTo(null);
    }

    private JPanel createPanelWithTitle(String title, Color bgColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    private void setupTopLeftPanel() {
        complexGridContainer = new JPanel();
        complexGridContainer.setBackground(Color.WHITE);
        complexGridContainer.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(complexGridContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        topLeftPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void setupTopMiddlePanel() {
        JPanel stencilPanel = new JPanel(new BorderLayout());
        stencilPanel.setBackground(Color.WHITE);

        JPanel stencilGridPanel = new JPanel();
        stencilGridPanel.setBackground(Color.WHITE);

        JScrollPane gridScroll = new JScrollPane(stencilGridPanel);
        gridScroll.setBorder(BorderFactory.createEmptyBorder());
        gridScroll.setPreferredSize(new Dimension(300, 300));

        JPanel sequencePanel = new JPanel(new BorderLayout());
        sequencePanel.setBorder(BorderFactory.createTitledBorder("Последовательность выбранных ячеек"));
        stencilSequenceArea = new JTextArea();
        stencilSequenceArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        stencilSequenceArea.setEditable(false);
        stencilSequenceArea.setBackground(new Color(245, 245, 245));
        JScrollPane textScroll = new JScrollPane(stencilSequenceArea);
        textScroll.setPreferredSize(new Dimension(280, 100));
        sequencePanel.add(textScroll, BorderLayout.CENTER);

        stencilPanel.add(gridScroll, BorderLayout.CENTER);
        stencilPanel.add(sequencePanel, BorderLayout.SOUTH);

        topMiddlePanel.add(stencilPanel, BorderLayout.CENTER);
        topMiddlePanel.putClientProperty("stencilGridPanel", stencilGridPanel);
    }

    private void setupTopRightPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);

        inputTextArea = new JTextArea();
        inputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(inputTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        inputPanel.add(scrollPane, BorderLayout.CENTER);
        topRightPanel.add(inputPanel, BorderLayout.CENTER);
    }

    private void setupBottomLeftPanel() {
        encryptedGridPanel = new JPanel();
        encryptedGridPanel.setBackground(Color.WHITE);
        encryptedGridPanel.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(encryptedGridPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        bottomLeftPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void setupBottomMiddlePanel() {
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBackground(Color.WHITE);

        outputTextArea = new JTextArea();
        outputTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        outputPanel.add(scrollPane, BorderLayout.CENTER);
        bottomMiddlePanel.add(outputPanel, BorderLayout.CENTER);
    }

    private void setupBottomRightPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        controlPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        controlPanel.add(new JLabel("Размер малой сетки (N x N):"), gbc);

        gbc.gridx = 1;
        sizeField = new JTextField(5);
        sizeField.setText("2");
        controlPanel.add(sizeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        complexGridButton = new JButton("Создать решетку");
        complexGridButton.addActionListener(new GenerateButtonListener());
        controlPanel.add(complexGridButton, gbc);

        gbc.gridy = 2;
        rotateButton = new JButton("Повернуть решетку на 90°");
        rotateButton.addActionListener(new RotateButtonListener());
        rotateButton.setEnabled(false);
        controlPanel.add(rotateButton, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0;
        clearSelectionButton = new JButton("Снять выделение");
        clearSelectionButton.addActionListener(e -> clearSelection());
        clearSelectionButton.setEnabled(false);
        controlPanel.add(clearSelectionButton, gbc);

        gbc.gridx = 1;
        generateStencilButton = new JButton("Создать трафарет");
        generateStencilButton.addActionListener(e -> generateStencil());
        generateStencilButton.setEnabled(false);
        controlPanel.add(generateStencilButton, gbc);

        gbc.gridwidth = 2;
        gbc.gridy = 4; gbc.gridx = 0;
        rotateStencilButton = new JButton("Повернуть трафарет на 90°");
        rotateStencilButton.addActionListener(e -> rotateStencil());
        rotateStencilButton.setEnabled(false);
        controlPanel.add(rotateStencilButton, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 5; gbc.gridx = 0;
        encryptButton = new JButton("Зашифровать текст");
        encryptButton.addActionListener(e -> encryptText());
        encryptButton.setEnabled(false);
        controlPanel.add(encryptButton, gbc);

        gbc.gridx = 1;
        decryptButton = new JButton("Расшифровать текст");
        decryptButton.addActionListener(e -> decryptText());
        decryptButton.setEnabled(false);
        controlPanel.add(decryptButton, gbc);

        gbc.gridy = 6; gbc.gridx = 0;
        clearTextButton = new JButton("Очистить текст");
        clearTextButton.addActionListener(e -> clearText());
        controlPanel.add(clearTextButton, gbc);

        gbc.gridx = 1;
        resetButton = new JButton("Сбросить всё");
        resetButton.addActionListener(e -> clearGrid());
        controlPanel.add(resetButton, gbc);

        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 2;
        statusLabel = new JLabel("Готов к работе");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        controlPanel.add(statusLabel, gbc);

        bottomRightPanel.add(controlPanel, BorderLayout.CENTER);
    }

    private void updateEncryptedGridDisplay() {
        encryptedGridPanel.removeAll();

        if (currentEncryptedGrid == null) {
            JLabel emptyLabel = new JLabel("Зашифруйте текст", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            encryptedGridPanel.setLayout(new BorderLayout());
            encryptedGridPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            int size = currentEncryptedGrid.length;
            encryptedGridPanel.setLayout(new GridLayout(size, size, 3, 3));

            Dimension panelSize = encryptedGridPanel.getParent().getSize();
            int buttonSize = Math.min(panelSize.width / size, panelSize.height / size) - 10;
            buttonSize = Math.min(buttonSize, 60);
            buttonSize = Math.max(buttonSize, 35);

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    char ch = currentEncryptedGrid[i][j];
                    String displayText = (ch == '\0') ? " " : String.valueOf(ch);

                    JButton button = new JButton(displayText);
                    button.setPreferredSize(new Dimension(buttonSize, buttonSize));
                    button.setFont(new Font("Monospaced", Font.BOLD, 14));
                    button.setEnabled(false);
                    button.setOpaque(true);
                    button.setBorderPainted(false);

                    if (ch != '\0') {
                        button.setBackground(new Color(200, 230, 255));
                    } else {
                        button.setBackground(new Color(240, 240, 240));
                    }

                    encryptedGridPanel.add(button);
                }
            }
        }
        encryptedGridPanel.revalidate();
        encryptedGridPanel.repaint();
    }

    private void updateStencilDisplay() {
        JPanel stencilGridPanel = (JPanel) topMiddlePanel.getClientProperty("stencilGridPanel");
        if (stencilGridPanel != null) {
            stencilGridPanel.removeAll();

            if (selectedCells.isEmpty()) {
                JLabel emptyLabel = new JLabel("Трафарет не создан", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                emptyLabel.setForeground(Color.GRAY);
                stencilGridPanel.setLayout(new BorderLayout());
                stencilGridPanel.add(emptyLabel, BorderLayout.CENTER);
            } else {
                int size = currentGridSize;
                stencilGridPanel.setLayout(new GridLayout(size, size, 3, 3));

                Dimension panelSize = stencilGridPanel.getParent().getSize();
                int buttonSize = Math.min(panelSize.width / size, panelSize.height / size) - 10;
                buttonSize = Math.min(buttonSize, 60);
                buttonSize = Math.max(buttonSize, 35);

                String[][] display = new String[size][size];
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        display[i][j] = " ";
                    }
                }

                for (SelectedCell cell : selectedCells) {
                    int[] rotatedCoords = rotateCoordinates(cell.row, cell.col, currentRotation, size);
                    display[rotatedCoords[0]][rotatedCoords[1]] = String.valueOf(cell.number);
                }

                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        JButton button = new JButton(display[i][j]);
                        button.setPreferredSize(new Dimension(buttonSize, buttonSize));
                        button.setFont(new Font("Monospaced", Font.BOLD, 14));
                        button.setEnabled(false);
                        button.setOpaque(true);
                        button.setBorderPainted(false);

                        if (!display[i][j].equals(" ")) {
                            button.setBackground(new Color(100, 200, 100));
                            button.setForeground(Color.WHITE);
                        } else {
                            button.setBackground(new Color(220, 220, 220));
                            button.setForeground(Color.GRAY);
                        }

                        stencilGridPanel.add(button);
                    }
                }
            }

            stencilGridPanel.revalidate();
            stencilGridPanel.repaint();
        }
        updateStencilSequence();
    }

    private void updateStencilSequence() {
        if (selectedCells.isEmpty()) {
            stencilSequenceArea.setText("Трафарет не создан");
            return;
        }

        selectedCells.sort((a, b) -> Integer.compare(a.number, b.number));

        StringBuilder sequence = new StringBuilder();
        sequence.append("Последовательность выбранных ячеек (по номерам):\n");
        sequence.append("Номер ячейки → Блок\n");
        sequence.append("-------------------\n");

        for (SelectedCell cell : selectedCells) {
            sequence.append(String.format("    %d → блок %d\n", cell.number, cell.block));
        }

        sequence.append("\nТекущий поворот трафарета: ").append(currentRotation).append("°\n");
        stencilSequenceArea.setText(sequence.toString());
    }

    private int[] rotateCoordinates(int row, int col, int rotation, int size) {
        int[] coords = new int[2];
        switch (rotation) {
            case 0:
                coords[0] = row;
                coords[1] = col;
                break;
            case 90:
                coords[0] = col;
                coords[1] = size - 1 - row;
                break;
            case 180:
                coords[0] = size - 1 - row;
                coords[1] = size - 1 - col;
                break;
            case 270:
                coords[0] = size - 1 - col;
                coords[1] = row;
                break;
        }
        return coords;
    }

    // ==================== ИСПРАВЛЕННЫЙ encryptText ====================
    private void encryptText() {
        if (selectedCells.isEmpty()) {
            statusLabel.setText("Ошибка: сначала создайте трафарет");
            return;
        }

        String text = inputTextArea.getText();
        if (text.isEmpty()) {
            statusLabel.setText("Ошибка: введите текст для шифрования");
            return;
        }

        int size = currentGridSize;
        char[][] grid = new char[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = '\0';
            }
        }

        int textIndex = 0;
        int rotation = currentRotation;   // ← Теперь используем текущий угол трафарета

        selectedCells.sort((a, b) -> Integer.compare(a.number, b.number));

        while (textIndex < text.length()) {
            boolean progress = false;

            for (SelectedCell cell : selectedCells) {
                if (textIndex >= text.length()) break;
                int[] coords = rotateCoordinates(cell.row, cell.col, rotation, size);
                if (grid[coords[0]][coords[1]] == '\0') {
                    grid[coords[0]][coords[1]] = text.charAt(textIndex);
                    textIndex++;
                    progress = true;
                }
            }

            if (!progress) break;
            rotation = (rotation + 90) % 360;
        }

        currentEncryptedGrid = grid;
        updateEncryptedGridDisplay();

        StringBuilder encrypted = new StringBuilder();
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                encrypted.append(grid[i][j] == '\0' ? ' ' : grid[i][j]);
            }
        }

        outputTextArea.setText(encrypted.toString());

        String msg = String.format("Зашифровано с угла %d°. Использовано %d из %d символов",
                currentRotation, textIndex, text.length());
        if (textIndex < text.length()) msg += " (остальные обрезаны)";
        statusLabel.setText(msg);
    }

    // ==================== ИСПРАВЛЕННЫЙ decryptText ====================
    private void decryptText() {
        if (selectedCells.isEmpty()) {
            statusLabel.setText("Ошибка: сначала создайте трафарет");
            return;
        }

        String encryptedText = inputTextArea.getText();
        if (encryptedText.isEmpty()) {
            statusLabel.setText("Ошибка: введите зашифрованный текст");
            return;
        }

        int size = currentGridSize;
        if (encryptedText.length() != size * size) {
            statusLabel.setText("Ошибка: длина текста должна быть " + (size * size) + " символов");
            return;
        }

        char[][] grid = new char[size][size];
        int index = 0;
        for (int j = 0; j < size; j++) {
            for (int i = 0; i < size; i++) {
                grid[i][j] = encryptedText.charAt(index++);
            }
        }

        currentEncryptedGrid = grid;
        updateEncryptedGridDisplay();

        StringBuilder decrypted = new StringBuilder();
        selectedCells.sort((a, b) -> Integer.compare(a.number, b.number));

        int totalChars = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                char c = grid[i][j];
                if (c != '\0' && c != ' ') totalChars++;
            }
        }

        int readCount = 0;
        int rot = currentRotation;   // ← Тот же угол, что использовался при шифровании

        while (readCount < totalChars) {
            boolean progress = false;

            for (SelectedCell cell : selectedCells) {
                if (readCount >= totalChars) break;
                int[] coords = rotateCoordinates(cell.row, cell.col, rot, size);
                char ch = grid[coords[0]][coords[1]];
                if (ch != '\0' && ch != ' ') {
                    decrypted.append(ch);
                    readCount++;
                    progress = true;
                }
            }

            if (!progress) break;
            rot = (rot + 90) % 360;
        }

        outputTextArea.setText(decrypted.toString());
        statusLabel.setText(String.format("Текст расшифрован с угла %d° ✓", currentRotation));
    }

    private void rotateStencil() {
        if (selectedCells.isEmpty()) {
            statusLabel.setText("Ошибка: сначала создайте трафарет");
            return;
        }

        currentRotation = (currentRotation + 90) % 360;
        updateStencilDisplay();
        statusLabel.setText("Трафарет повернут на 90°. Текущий угол: " + currentRotation + "°");
    }

    private void generateStencil() {
        if (!isComplexGrid || selectedButtons.isEmpty()) {
            statusLabel.setText("Ошибка: сначала выберите ячейки в решетке");
            return;
        }

        selectedCells.clear();
        for (JButton button : selectedButtons) {
            int number = (int) button.getClientProperty("number");
            int block = (int) button.getClientProperty("block");
            int row = -1, col = -1;

            for (int i = 0; i < currentButtons.size(); i++) {
                if (currentButtons.get(i) == button) {
                    row = i / currentGridSize;
                    col = i % currentGridSize;
                    break;
                }
            }
            selectedCells.add(new SelectedCell(row, col, number, block));
        }

        selectedCells.sort((a, b) -> Integer.compare(a.number, b.number));
        currentRotation = 0;                    // Сбрасываем угол при создании нового трафарета
        updateStencilDisplay();

        encryptButton.setEnabled(true);
        decryptButton.setEnabled(true);
        rotateStencilButton.setEnabled(true);

        statusLabel.setText("Трафарет создан (угол 0°). Выбрано " + selectedCells.size() + " ячеек");
    }

    private void updateTopLeftGrid() {
        complexGridContainer.removeAll();

        if (currentButtons.isEmpty()) {
            JLabel emptyLabel = new JLabel("Создайте решетку", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            complexGridContainer.setLayout(new BorderLayout());
            complexGridContainer.add(emptyLabel, BorderLayout.CENTER);
        } else {
            int size = currentGridSize;
            complexGridContainer.setLayout(new GridLayout(size, size, 5, 5));

            Dimension panelSize = complexGridContainer.getParent().getSize();
            int buttonSize = Math.min(panelSize.width / size, panelSize.height / size) - 14;
            buttonSize = Math.min(buttonSize, 72);
            buttonSize = Math.max(buttonSize, 42);

            int fontSize = switch (size) {
                case 2, 3, 4 -> 18;
                case 5, 6 -> 15;
                case 7, 8 -> 13;
                default -> 11;
            };
            fontSize = Math.max(fontSize, 9);

            for (JButton button : currentButtons) {
                button.setPreferredSize(new Dimension(buttonSize, buttonSize));
                button.setFont(new Font("Arial", Font.BOLD, fontSize));
                button.setMargin(new Insets(2, 2, 2, 2));
                button.setOpaque(true);
                button.setBorderPainted(false);
                button.setHorizontalAlignment(SwingConstants.CENTER);
                button.setVerticalAlignment(SwingConstants.CENTER);
                complexGridContainer.add(button);
            }
        }

        complexGridContainer.revalidate();
        complexGridContainer.repaint();
    }

    private class GenerateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int size = Integer.parseInt(sizeField.getText().trim());
                if (size >= 1 && size <= 4) {
                    clearSelection();
                    smallGridSize = size;
                    generateComplexGrid(size);
                    statusLabel.setText("Создана решетка Кардано " + (size * 2) + "x" + (size * 2));
                    isComplexGrid = true;
                    generateStencilButton.setEnabled(true);
                    clearSelectionButton.setEnabled(true);
                    rotateButton.setEnabled(true);
                    selectedCells.clear();
                    currentRotation = 0;
                    currentEncryptedGrid = null;
                    encryptButton.setEnabled(false);
                    decryptButton.setEnabled(false);
                    rotateStencilButton.setEnabled(false);
                    updateStencilDisplay();
                    updateTopLeftGrid();
                    updateEncryptedGridDisplay();
                } else {
                    statusLabel.setText("Ошибка: размер малой сетки должен быть от 1 до 4");
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Ошибка: введите корректное число");
            }
        }
    }

    private void generateComplexGrid(int smallSize) {
        currentButtons.clear();
        currentGridSize = smallSize * 2;

        int[][] block1 = createNumberMatrix(smallSize, 0);
        int[][] block2 = createNumberMatrix(smallSize, 90);
        int[][] block3 = createNumberMatrix(smallSize, 180);
        int[][] block4 = createNumberMatrix(smallSize, 270);

        int[][] fullMatrix = new int[currentGridSize][currentGridSize];

        for (int i = 0; i < smallSize; i++) {
            for (int j = 0; j < smallSize; j++) {
                fullMatrix[i][j] = block1[i][j];
                fullMatrix[i][j + smallSize] = block2[i][j];
                fullMatrix[i + smallSize][j + smallSize] = block3[i][j];
                fullMatrix[i + smallSize][j] = block4[i][j];
            }
        }

        for (int i = 0; i < currentGridSize; i++) {
            for (int j = 0; j < currentGridSize; j++) {
                int number = fullMatrix[i][j];
                int blockNumber = (i < smallSize ? (j < smallSize ? 1 : 2) : (j < smallSize ? 4 : 3));
                int rotationAngle = (blockNumber == 1 ? 0 : blockNumber == 2 ? 90 : blockNumber == 3 ? 180 : 270);

                JButton button = createButtonForComplexGrid(String.valueOf(number), number, rotationAngle);
                button.putClientProperty("block", blockNumber);
                currentButtons.add(button);
            }
        }
    }

    private int[][] createNumberMatrix(int size, int rotationAngle) {
        int[][] numbers = new int[size][size];
        int counter = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                numbers[i][j] = counter++;
            }
        }

        if (rotationAngle > 0) {
            numbers = rotateMatrix(numbers, rotationAngle);
        }
        return numbers;
    }

    private JButton createButtonForComplexGrid(String text, int number, int rotationAngle) {
        JButton button = new JButton(text);
        button.setBackground(originalButtonColor);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

        button.putClientProperty("rotation", rotationAngle);
        button.putClientProperty("number", number);

        button.addActionListener(new ComplexGridButtonListener(button, number));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled() && !selectedButtons.contains(button)) {
                    button.setBackground(new Color(180, 220, 255));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled() && !selectedButtons.contains(button)) {
                    button.setBackground(originalButtonColor);
                }
            }
        });

        return button;
    }

    private class ComplexGridButtonListener implements ActionListener {
        private final JButton button;
        private final int number;

        public ComplexGridButtonListener(JButton button, int number) {
            this.button = button;
            this.number = number;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!isComplexGrid) return;

            if (selectedButtons.contains(button)) {
                deselectButton(button);
                statusLabel.setText("Снято выделение с ячейки №" + number);
            } else {
                if (selectedNumbers.contains(number)) {
                    statusLabel.setText("Ошибка: номер " + number + " уже выбран!");
                    button.setBackground(new Color(255, 100, 100));
                    Timer timer = new Timer(300, evt -> {
                        if (!selectedButtons.contains(button)) button.setBackground(originalButtonColor);
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    selectButton(button);
                    int block = (int) button.getClientProperty("block");
                    statusLabel.setText("Выбрана ячейка №" + number + " (блок " + block + ")");
                }
            }
            updateTopLeftGrid();
        }
    }

    private void selectButton(JButton button) {
        int number = (int) button.getClientProperty("number");
        button.setBackground(selectedButtonColor);
        button.setEnabled(false);
        selectedButtons.add(button);
        selectedNumbers.add(number);

        for (JButton other : currentButtons) {
            if ((int) other.getClientProperty("number") == number && other != button) {
                other.setBackground(disabledButtonColor);
                other.setEnabled(false);
                other.putClientProperty("disabledForNumber", true);
            }
        }

        updateSelectionStatus();
        selectedCells.clear();
        currentRotation = 0;
        currentEncryptedGrid = null;
        encryptButton.setEnabled(false);
        decryptButton.setEnabled(false);
        rotateStencilButton.setEnabled(false);
        updateStencilDisplay();
        updateEncryptedGridDisplay();
    }

    private void deselectButton(JButton button) {
        int number = (int) button.getClientProperty("number");
        button.setBackground(originalButtonColor);
        button.setEnabled(true);
        selectedButtons.remove(button);
        selectedNumbers.remove(number);

        for (JButton other : currentButtons) {
            if ((int) other.getClientProperty("number") == number && other != button &&
                    other.getClientProperty("disabledForNumber") != null) {
                other.setBackground(originalButtonColor);
                other.setEnabled(true);
                other.putClientProperty("disabledForNumber", null);
            }
        }

        updateSelectionStatus();
        selectedCells.clear();
        currentRotation = 0;
        currentEncryptedGrid = null;
        encryptButton.setEnabled(false);
        decryptButton.setEnabled(false);
        rotateStencilButton.setEnabled(false);
        updateStencilDisplay();
        updateEncryptedGridDisplay();
    }

    private void clearSelection() {
        for (JButton button : currentButtons) {
            button.setBackground(originalButtonColor);
            button.setEnabled(true);
            button.putClientProperty("disabledForNumber", null);
        }
        selectedNumbers.clear();
        selectedButtons.clear();

        updateSelectionStatus();
        selectedCells.clear();
        currentRotation = 0;
        currentEncryptedGrid = null;
        encryptButton.setEnabled(false);
        decryptButton.setEnabled(false);
        rotateStencilButton.setEnabled(false);
        updateStencilDisplay();
        updateEncryptedGridDisplay();
    }

    private void updateSelectionStatus() {
        if (isComplexGrid) {
            int total = smallGridSize * smallGridSize;
            int selected = selectedNumbers.size();
            statusLabel.setText(selected == total ?
                    "Отлично! Выбраны все " + total + " уникальных номеров!" :
                    "Выбрано " + selected + " из " + total + " уникальных номеров");
        }
    }

    private int[][] rotateMatrix(int[][] matrix, int angle) {
        int size = matrix.length;
        int[][] result = copyMatrix(matrix);
        int rotations = angle / 90;
        for (int r = 0; r < rotations; r++) {
            int[][] temp = new int[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    temp[j][size - 1 - i] = result[i][j];
                }
            }
            result = temp;
        }
        return result;
    }

    private class RotateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentGridSize > 0 && !currentButtons.isEmpty()) {
                clearSelection();
                rotateComplexGrid();
                statusLabel.setText("Решетка повернута на 90°");
                updateTopLeftGrid();
                selectedCells.clear();
                currentRotation = 0;
                currentEncryptedGrid = null;
                encryptButton.setEnabled(false);
                decryptButton.setEnabled(false);
                rotateStencilButton.setEnabled(false);
                updateStencilDisplay();
                updateEncryptedGridDisplay();
            }
        }
    }

    private void rotateComplexGrid() {
        int smallSize = currentGridSize / 2;
        int[][] currentMatrix = new int[currentGridSize][currentGridSize];
        for (int i = 0; i < currentButtons.size(); i++) {
            int row = i / currentGridSize;
            int col = i % currentGridSize;
            currentMatrix[row][col] = Integer.parseInt(currentButtons.get(i).getText());
        }

        int[][] rotatedMatrix = new int[currentGridSize][currentGridSize];
        for (int i = 0; i < currentGridSize; i++) {
            for (int j = 0; j < currentGridSize; j++) {
                rotatedMatrix[j][currentGridSize - 1 - i] = currentMatrix[i][j];
            }
        }

        List<JButton> newButtons = new ArrayList<>();
        for (int i = 0; i < currentGridSize; i++) {
            for (int j = 0; j < currentGridSize; j++) {
                int number = rotatedMatrix[i][j];
                int blockNumber = (i < smallSize ? (j < smallSize ? 1 : 2) : (j < smallSize ? 4 : 3));
                int rotationAngle = (blockNumber == 1 ? 0 : blockNumber == 2 ? 90 : blockNumber == 3 ? 180 : 270);

                JButton button = createButtonForComplexGrid(String.valueOf(number), number, rotationAngle);
                button.putClientProperty("block", blockNumber);
                newButtons.add(button);
            }
        }
        currentButtons = newButtons;
    }

    private int[][] copyMatrix(int[][] matrix) {
        int size = matrix.length;
        int[][] copy = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(matrix[i], 0, copy[i], 0, size);
        }
        return copy;
    }

    private void clearText() {
        inputTextArea.setText("");
        outputTextArea.setText("");
        currentEncryptedGrid = null;
        updateEncryptedGridDisplay();
        statusLabel.setText("Текст очищен");
    }

    private void clearGrid() {
        currentButtons.clear();
        currentGridSize = 0;
        isComplexGrid = false;
        clearSelection();
        selectedCells.clear();
        currentRotation = 0;
        currentEncryptedGrid = null;
        sizeField.setText("2");
        statusLabel.setText("Готов к работе");
        rotateButton.setEnabled(false);
        clearSelectionButton.setEnabled(false);
        generateStencilButton.setEnabled(false);
        encryptButton.setEnabled(false);
        decryptButton.setEnabled(false);
        rotateStencilButton.setEnabled(false);
        inputTextArea.setText("");
        outputTextArea.setText("");
        updateTopLeftGrid();
        updateStencilDisplay();
        updateEncryptedGridDisplay();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new CardanoGrilleCipher().setVisible(true);
        });
    }
}