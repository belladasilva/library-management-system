package ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;

public final class LibraryAppFrame extends JFrame {
    private static final String DASHBOARD = "dashboard";
    private static final String BOOKS = "books";
    private static final String BORROWERS = "borrowers";
    private static final String TRANSACTIONS = "transactions";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel screens = new JPanel(cardLayout);
    private final BookUI books;
    private final BorrowerUI borrowers;
    private final TransactionUI transactions;

    public LibraryAppFrame() {
        super("Library Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(930, 580);
        setMinimumSize(new Dimension(820, 560));

        MainMenu dashboard = new MainMenu(this::showBooks, this::showBorrowers, this::showTransactions);
        books = new BookUI(this::showDashboard);
        borrowers = new BorrowerUI(this::showDashboard);
        transactions = new TransactionUI(this::showDashboard);

        screens.add(centered(dashboard, new Dimension(790, 420)), DASHBOARD);
        screens.add(books, BOOKS);
        screens.add(borrowers, BORROWERS);
        screens.add(centered(transactions, new Dimension(550, 470)), TRANSACTIONS);
        setContentPane(screens);

        showDashboard();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private static JPanel centered(JPanel content, Dimension size) {
        JPanel container = UiStyles.page();
        container.setBorder(null);
        container.setLayout(new GridBagLayout());
        content.setPreferredSize(size);
        container.add(content);
        return container;
    }

    private void showDashboard() {
        cardLayout.show(screens, DASHBOARD);
    }

    private void showBooks() {
        cardLayout.show(screens, BOOKS);
        books.onShow();
    }

    private void showBorrowers() {
        cardLayout.show(screens, BORROWERS);
        borrowers.onShow();
    }

    private void showTransactions() {
        cardLayout.show(screens, TRANSACTIONS);
        transactions.onShow();
    }
}
