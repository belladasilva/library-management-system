package ui;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {
    public MainMenu(Runnable showBooks, Runnable showBorrowers, Runnable showTransactions) {
        super(new BorderLayout());

        JPanel page = UiStyles.page();
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(UiStyles.accentLabel("LIBRARY / WORKSPACE"));
        header.add(Box.createVerticalStrut(7));
        header.add(UiStyles.title("Library Management System"));
        header.add(Box.createVerticalStrut(5));
        header.add(UiStyles.muted("Manage books, borrowers and lending activity."));
        page.add(header, BorderLayout.NORTH);

        JPanel navigation = new JPanel(new GridLayout(1, 3, 16, 0));
        navigation.setOpaque(false);
        navigation.add(navigationCard("Books", "Manage catalog and<br>available copies.",
                "Open Books", showBooks));
        navigation.add(navigationCard("Borrowers", "Manage registered<br>library users.",
                "Open Borrowers", showBorrowers));
        navigation.add(navigationCard("Borrow / Return", "Manage lending<br>transactions.",
                "Open Transactions", showTransactions));
        page.add(navigation, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        JButton aboutButton = UiStyles.button("About", false);
        aboutButton.setPreferredSize(new Dimension(92, 34));
        aboutButton.addActionListener(event -> JOptionPane.showMessageDialog(this,
                "Created by Isabella Da Silva", "About", JOptionPane.INFORMATION_MESSAGE));
        JButton quitButton = UiStyles.button("Quit", false);
        quitButton.setPreferredSize(new Dimension(92, 34));
        quitButton.addActionListener(event -> System.exit(0));
        footer.add(aboutButton);
        footer.add(quitButton);
        page.add(footer, BorderLayout.SOUTH);

        add(page);
    }

    private JPanel navigationCard(String title, String description, String action, Runnable onOpen) {
        JPanel card = UiStyles.card(new BorderLayout(0, 10));
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(UiStyles.sectionTitle(title));
        content.add(Box.createVerticalStrut(8));
        content.add(UiStyles.muted("<html>" + description + "</html>"));
        card.add(content, BorderLayout.NORTH);

        JButton openButton = UiStyles.button(action, true);
        openButton.addActionListener(event -> onOpen.run());
        card.add(openButton, BorderLayout.SOUTH);
        return card;
    }
}
