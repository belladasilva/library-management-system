package ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

public final class UiStyles {
    public static final Color SUCCESS = new Color(119, 205, 153);
    private static final Color BACKGROUND = new Color(29, 32, 40);
    private static final Color SURFACE = new Color(39, 43, 54);
    private static final Color FIELD = new Color(32, 36, 46);
    private static final Color TABLE_ALT = new Color(43, 47, 59);
    private static final Color TABLE_HEADER = new Color(49, 53, 66);
    private static final Color BORDER = new Color(65, 70, 84);
    private static final Color TEXT = new Color(234, 236, 242);
    private static final Color MUTED = new Color(168, 174, 188);
    private static final Color ACCENT = new Color(153, 126, 214);
    private static final Color ACCENT_DARK = new Color(93, 72, 137);
    private static final Color ACCENT_HOVER = new Color(108, 84, 157);
    private static final Color BUTTON_HOVER = new Color(57, 61, 75);
    private static final Color SELECTION = new Color(70, 57, 96);
    private static final Font BODY_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 13);

    private UiStyles() {
    }

    public static void installThemeDefaults() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.background", SURFACE);
        UIManager.put("Button.foreground", TEXT);
    }

    public static JPanel page() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        return panel;
    }

    public static JPanel card(LayoutManager layout) {
        return new RoundedPanel(layout);
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        label.setForeground(TEXT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static JLabel accentLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        label.setForeground(ACCENT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static JLabel statusLabel(String text) {
        JLabel label = muted(text);
        label.setOpaque(true);
        label.setBackground(FIELD);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return label;
    }

    public static void updateStatus(JLabel label, String text, boolean success) {
        label.setText(text);
        label.setForeground(success ? SUCCESS : MUTED);
    }

    public static JButton button(String text, boolean primary) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D canvas = (Graphics2D) graphics.create();
                canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = primary ? ACCENT_DARK : SURFACE;
                if (getModel().isPressed()) {
                    fill = primary ? SELECTION : FIELD;
                } else if (getModel().isRollover()) {
                    fill = primary ? ACCENT_HOVER : BUTTON_HOVER;
                }
                canvas.setColor(fill);
                canvas.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                canvas.setColor(primary ? ACCENT_DARK : BORDER);
                canvas.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                if (isFocusOwner()) {
                    canvas.setColor(ACCENT);
                    canvas.setStroke(new BasicStroke(2));
                    canvas.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 8, 8);
                }
                canvas.dispose();
                super.paintComponent(graphics);
            }
        };
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT);
        button.setPreferredSize(new Dimension(140, 38));
        button.setBorder(new EmptyBorder(8, 12, 8, 12));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setRolloverEnabled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton backButton(Runnable showDashboard) {
        JButton button = button("← Back to Dashboard", false);
        button.setPreferredSize(new Dimension(190, 34));
        button.addActionListener(event -> showDashboard.run());
        return button;
    }

    public static JPanel screenHeader(Runnable showDashboard, String heading, String subtitle) {
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setOpaque(false);
        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        navigation.setOpaque(false);
        navigation.add(backButton(showDashboard));
        header.add(navigation, BorderLayout.NORTH);

        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new javax.swing.BoxLayout(labels, javax.swing.BoxLayout.Y_AXIS));
        labels.add(title(heading));
        labels.add(Box.createVerticalStrut(3));
        labels.add(muted(subtitle));
        header.add(labels, BorderLayout.CENTER);
        return header;
    }

    public static void field(JTextField field) {
        field.setFont(BODY_FONT);
        field.setBackground(FIELD);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setSelectionColor(SELECTION);
        field.setSelectedTextColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
        field.setPreferredSize(new Dimension(240, 36));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    public static void addField(JPanel panel, String labelText, JTextField field) {
        JLabel label = muted(labelText);
        label.setLabelFor(field);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
    }

    public static JScrollPane scrollPane(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(SURFACE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    public static JTable emptyTable(javax.swing.table.DefaultTableModel model, String message) {
        JTable table = new JTable(model) {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                if (getRowCount() == 0) {
                    Graphics2D canvas = (Graphics2D) graphics.create();
                    canvas.setFont(BODY_FONT);
                    canvas.setColor(MUTED);
                    int textWidth = canvas.getFontMetrics().stringWidth(message);
                    canvas.drawString(message, Math.max(18, (getWidth() - textWidth) / 2), getHeight() / 2);
                    canvas.dispose();
                }
            }
        };
        table(table);
        return table;
    }

    public static void table(JTable table) {
        table.setFont(BODY_FONT);
        table.setRowHeight(35);
        table.setBackground(SURFACE);
        table.setForeground(TEXT);
        table.setSelectionBackground(SELECTION);
        table.setSelectionForeground(TEXT);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(Object.class, cellRenderer(false));

        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER);
        header.setForeground(TEXT);
        header.setFont(BUTTON_FONT);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setOpaque(true);
        headerRenderer.setBackground(TABLE_HEADER);
        headerRenderer.setForeground(TEXT);
        headerRenderer.setFont(BUTTON_FONT);
        headerRenderer.setBorder(new EmptyBorder(0, 12, 0, 12));
        header.setDefaultRenderer(headerRenderer);
    }

    public static void highlightAvailableCopies(JTable table) {
        table.getColumnModel().getColumn(3).setCellRenderer(cellRenderer(true));
    }

    private static DefaultTableCellRenderer cellRenderer(boolean highlightCopies) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean selected, boolean focused, int row, int column) {
                super.getTableCellRendererComponent(table, value, selected, focused, row, column);
                setBackground(selected ? SELECTION : (row % 2 == 0 ? SURFACE : TABLE_ALT));
                setForeground(highlightCopies && !selected && value instanceof Number
                        && ((Number) value).intValue() > 0 ? SUCCESS : TEXT);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                return this;
            }
        };
    }

    private static final class RoundedPanel extends JPanel {
        private RoundedPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(new EmptyBorder(17, 17, 17, 17));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D canvas = (Graphics2D) graphics.create();
            canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            canvas.setColor(SURFACE);
            canvas.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            canvas.setColor(BORDER);
            canvas.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            canvas.dispose();
        }
    }
}
