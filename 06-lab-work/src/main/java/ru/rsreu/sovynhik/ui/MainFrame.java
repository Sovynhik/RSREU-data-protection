package ru.rsreu.sovynhik.ui;

import ru.rsreu.sovynhik.utils.ImageContainer;
import ru.rsreu.sovynhik.algorithm.LSBDecoder;
import ru.rsreu.sovynhik.algorithm.LSBEncoder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class MainFrame extends JFrame {

    private final JTextField messageField;
    private final JTextField fileField;

    private JButton loadButton;
    private JButton encodeButton;
    private JButton decodeButton;
    private JButton saveButton;
    private JButton clearButton;

    private JLabel originalLabel;
    private JLabel stegoLabel;

    private ImageContainer container;
    private BufferedImage stegoImage;

    public MainFrame() {
        setTitle("LSB Стеганография");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Основной layout
        setLayout(new BorderLayout(10, 10));

        // === Верхняя панель (управление) ===
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Панель выбора файла
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        fileField = new JTextField(30);
        loadButton = createColoredButton("Загрузить BMP", new Color(180, 180, 180));
        filePanel.add(new JLabel("Файл:"));
        filePanel.add(fileField);
        filePanel.add(loadButton);

        // Панель ввода сообщения и действий
        JPanel msgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        messageField = new JTextField(30);
        messageField.setToolTipText("Введите текст для встраивания");
        encodeButton = createColoredButton("Встроить", new Color(76, 175, 80));      // зелёный
        decodeButton = createColoredButton("Извлечь", new Color(255, 152, 0));       // оранжевый
        msgPanel.add(new JLabel("Сообщение:"));
        msgPanel.add(messageField);
        msgPanel.add(encodeButton);
        msgPanel.add(decodeButton);

        topPanel.add(filePanel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(msgPanel);

        add(topPanel, BorderLayout.NORTH);

        // === Центральная панель (изображения) ===
        JPanel imagesPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        imagesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        originalLabel = new JLabel("Оригинал не загружен", SwingConstants.CENTER);
        originalLabel.setBorder(BorderFactory.createTitledBorder("Оригинальное изображение"));
        imagesPanel.add(new JScrollPane(originalLabel));

        stegoLabel = new JLabel("Изменённое изображение появится здесь", SwingConstants.CENTER);
        stegoLabel.setBorder(BorderFactory.createTitledBorder("Изображение со встроенным сообщением"));
        imagesPanel.add(new JScrollPane(stegoLabel));

        add(imagesPanel, BorderLayout.CENTER);

        // === Нижняя панель (сохранение / очистка) ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        saveButton = createColoredButton("Сохранить как...", new Color(33, 150, 243)); // синий
        clearButton = createColoredButton("Очистить", new Color(244, 67, 54));          // красный
        bottomPanel.add(saveButton);
        bottomPanel.add(clearButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Привязка обработчиков
        loadButton.addActionListener(e -> loadImage());
        encodeButton.addActionListener(e -> encodeAndShow());
        decodeButton.addActionListener(e -> decodeAndShow());
        saveButton.addActionListener(e -> saveStego());
        clearButton.addActionListener(e -> clearAll());

        setVisible(true);
    }

    /**
     * Создаёт кнопку с заданным цветом фона.
     */
    private JButton createColoredButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setOpaque(true);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE); // Белый текст для контраста
        button.setFocusPainted(false);     // Убираем рамку фокуса
        button.setFont(button.getFont().deriveFont(Font.BOLD));
        return button;
    }

    private void loadImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("BMP Images", "bmp"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String path = chooser.getSelectedFile().getAbsolutePath();
        fileField.setText(path);

        try {
            container = new ImageContainer(path);
            showImage(originalLabel, container.getImage(), 400, 300);
            stegoLabel.setIcon(null);
            stegoLabel.setText("Изменённое изображение появится здесь");
            stegoImage = null;
            JOptionPane.showMessageDialog(this, "Изображение загружено");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка загрузки: " + ex.getMessage());
        }
    }

    private void encodeAndShow() {
        if (container == null) {
            JOptionPane.showMessageDialog(this, "Сначала загрузите изображение");
            return;
        }
        String msg = messageField.getText().trim();
        if (msg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите сообщение");
            return;
        }

        try {
            BufferedImage working = deepCopy(container.getImage());
            LSBEncoder encoder = new LSBEncoder(1);
            encoder.encode(working, msg);
            stegoImage = working;
            showImage(stegoLabel, stegoImage, 400, 300);
            JOptionPane.showMessageDialog(this, "Сообщение встроено (показано выше)");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка встраивания: " + ex.getMessage());
        }
    }

    private void decodeAndShow() {
        if (stegoImage == null && container == null) {
            JOptionPane.showMessageDialog(this, "Нет изображения для извлечения");
            return;
        }

        BufferedImage source = (stegoImage != null) ? stegoImage : container.getImage();

        try {
            LSBDecoder decoder = new LSBDecoder(1);
            String msg = decoder.decode(source);
            JOptionPane.showMessageDialog(this, "Извлечённое сообщение:\n" + msg);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка извлечения: " + ex.getMessage());
        }
    }

    private void saveStego() {
        if (stegoImage == null) {
            JOptionPane.showMessageDialog(this, "Нет изменённого изображения для сохранения");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("stego.bmp"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try {
            ImageIO.write(stegoImage, "bmp", chooser.getSelectedFile());
            JOptionPane.showMessageDialog(this, "Сохранено как:\n" + chooser.getSelectedFile().getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка сохранения: " + ex.getMessage());
        }
    }

    private void clearAll() {
        container = null;
        stegoImage = null;
        fileField.setText("");
        messageField.setText("");
        originalLabel.setIcon(null);
        originalLabel.setText("Оригинал не загружен");
        stegoLabel.setIcon(null);
        stegoLabel.setText("Изменённое изображение появится здесь");
    }

    private void showImage(JLabel label, BufferedImage img, int maxW, int maxH) {
        if (img == null) return;
        double scale = Math.min((double) maxW / img.getWidth(), (double) maxH / img.getHeight());
        int newW = (int) (img.getWidth() * scale);
        int newH = (int) (img.getHeight() * scale);
        Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
        label.setText("");
    }

    private BufferedImage deepCopy(BufferedImage bi) {
        BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        Graphics g = copy.getGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return copy;
    }
}