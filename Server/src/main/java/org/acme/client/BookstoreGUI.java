package org.acme.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BookstoreGUI extends JFrame {

    private JTextField txtRegisterId;
    private JTextField txtBookIds;
    private JTextArea txtLog;
    private HttpClient client;
    private static final String SERVER_URL = "http://localhost:8080";
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Color PRIMARY_COLOR = new Color(60, 90, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color BG_COLOR = new Color(245, 247, 250);

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> {
            BookstoreGUI frame = new BookstoreGUI();
            frame.setVisible(true);
        });
    }

    public BookstoreGUI() {
        client = HttpClient.newHttpClient();

        setTitle("Librărie - Casa de Marcat");
        setSize(600, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_COLOR);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Casa de Marcat", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel lblReg = new JLabel("ID Casă de Marcat");
        lblReg.setFont(BOLD_FONT);
        lblReg.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblReg);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        txtRegisterId = new JTextField("1");
        txtRegisterId.setFont(MAIN_FONT);
        txtRegisterId.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        txtRegisterId.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtRegisterId.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        txtRegisterId.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(txtRegisterId);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblBooks = new JLabel("ID-uri Cărți (ex: 1, 2, 3)");
        lblBooks.setFont(BOLD_FONT);
        lblBooks.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblBooks);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        txtBookIds = new JTextField("1, 2");
        txtBookIds.setFont(MAIN_FONT);
        txtBookIds.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        txtBookIds.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtBookIds.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        txtBookIds.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(txtBookIds);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JButton btnSell = new JButton("Procesează Vânzarea");
        styleButton(btnSell, SUCCESS_COLOR);
        btnSell.addActionListener(e -> processSale());
        centerPanel.add(btnSell);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnReport = new JButton("Raport Global (Nota 10)");
        styleButton(btnReport, PRIMARY_COLOR);
        btnReport.addActionListener(e -> getGlobalReport());
        centerPanel.add(btnReport);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        bottomPanel.setBackground(BG_COLOR);

        JLabel lblLog = new JLabel("Jurnal Activitate:");
        lblLog.setFont(BOLD_FONT);
        lblLog.setBorder(new EmptyBorder(0, 0, 10, 0));
        bottomPanel.add(lblLog, BorderLayout.NORTH);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtLog.setBackground(new Color(40, 44, 52));
        txtLog.setForeground(new Color(152, 195, 121));
        txtLog.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(txtLog);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        scrollPane.setPreferredSize(new Dimension(0, 200));
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(BOLD_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
    }

    private void processSale() {
        String registerId = txtRegisterId.getText().trim();
        String bookIdsInput = txtBookIds.getText().trim();

        if (registerId.isEmpty() || bookIdsInput.isEmpty()) {
            log("EROARE: Completează toate câmpurile!");
            return;
        }

        try {
            String jsonBody = String.format(
                    "{\"registerId\": %s, \"items\": [%s]}",
                    registerId,
                    formatItems(bookIdsInput)
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "/sales"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            log(">>> Trimitere vânzare...");
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> SwingUtilities.invokeLater(() -> {
                        if (response.statusCode() == 201) {
                            log("SUCCES (201): Vânzare înregistrată!");
                            log("Răspuns: " + formatJson(response.body()));
                        } else {
                            log("EROARE (" + response.statusCode() + "): " + response.body());
                        }
                    }));

        } catch (Exception ex) {
            log("EXCEPȚIE: " + ex.getMessage());
        }
    }

    private void getGlobalReport() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + "/sales/report"))
                    .GET()
                    .build();

            log(">>> Solicitare Raport Global...");
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> SwingUtilities.invokeLater(() -> {
                        log("RAPORT PRIMIT:");
                        log(formatJson(response.body()));
                    }));

        } catch (Exception ex) {
            log("EXCEPȚIE: " + ex.getMessage());
        }
    }

    private String formatItems(String input) {
        String[] ids = input.split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.length; i++) {
            try {
                String id = ids[i].trim();
                Integer.parseInt(id);
                sb.append(String.format("{ \"bookId\": %s, \"quantity\": 1 }", id));
                if (i < ids.length - 1) sb.append(",");
            } catch (NumberFormatException e) {
                log("ATENȚIE: ID invalid ignorat: " + ids[i]);
            }
        }
        return sb.toString();
    }

    private void log(String message) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        txtLog.append("[" + time + "] " + message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    private String formatJson(String json) {
        return json.replace("{", "{\n  ")
                .replace("},", "},\n  ")
                .replace("\",\"", "\",\n   \"");
    }
}