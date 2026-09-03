import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class AetherMindApp extends JFrame {

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color BG_DARK  = new Color(13,  13,  20);
    private static final Color BG_PANEL = new Color(20,  20,  32);
    private static final Color BG_INPUT = new Color(28,  28,  42);
    private static final Color CYAN     = new Color(0,   230, 255);
    private static final Color GREEN    = new Color(0,   255, 140);
    private static final Color YELLOW   = new Color(255, 220, 50);
    private static final Color WHITE    = new Color(220, 220, 230);
    private static final Color GRAY     = new Color(100, 100, 130);

    private final AetherBrain brain = new AetherBrain();
    private boolean restoring;

    // ── GUI Components ────────────────────────────────────────────────────────
    private JTextPane  chatPane;
    private StyledDocument doc;
    private JTextField inputField;
    private JButton    sendButton;
    private JLabel     statusLabel;

    // ── Constructor ───────────────────────────────────────────────────────────
    public AetherMindApp() {
        buildUI();
        showBoot();
        restoreHistory();
    }

    // ── Build the window ──────────────────────────────────────────────────────
    private void buildUI() {
        setTitle("AetherMind v0.9 — Local AI Core");
        setSize(800, 620);
        setMinimumSize(new Dimension(520, 400));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DARK);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("⬡  AETHER MIND");
        title.setFont(new Font("Consolas", Font.BOLD, 22));
        title.setForeground(CYAN);

        JLabel subtitle = new JLabel("v0.9  ·  No cloud  ·  No API  ·  Pure local intelligence");
        subtitle.setFont(new Font("Consolas", Font.PLAIN, 11));
        subtitle.setForeground(GRAY);

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setBackground(BG_PANEL);
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(2));
        titleBox.add(subtitle);

        statusLabel = new JLabel("● ONLINE");
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 12));
        statusLabel.setForeground(GREEN);

        header.add(titleBox,     BorderLayout.WEST);
        header.add(statusLabel,  BorderLayout.EAST);

        // ── Chat pane ─────────────────────────────────────────────────────────
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setBackground(BG_DARK);
        chatPane.setBorder(new EmptyBorder(12, 14, 12, 14));
        doc = chatPane.getStyledDocument();

        JScrollPane scroll = new JScrollPane(chatPane);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(30, 30, 55), 1));
        scroll.getVerticalScrollBar().setBackground(BG_PANEL);

        // ── Input bar ─────────────────────────────────────────────────────────
        inputField = new JTextField();
        inputField.setBackground(BG_INPUT);
        inputField.setForeground(WHITE);
        inputField.setCaretColor(CYAN);
        inputField.setFont(new Font("Consolas", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 150, 180), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));

        sendButton = new JButton("SEND  ▶");
        sendButton.setBackground(CYAN);
        sendButton.setForeground(BG_DARK);
        sendButton.setFont(new Font("Consolas", Font.BOLD, 13));
        sendButton.setFocusPainted(false);
        sendButton.setBorder(new EmptyBorder(8, 20, 8, 20));
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { sendButton.setBackground(new Color(0, 200, 230)); }
            public void mouseExited (MouseEvent e) { sendButton.setBackground(CYAN); }
        });

        JPanel inputRow = new JPanel(new BorderLayout(10, 0));
        inputRow.setBackground(BG_PANEL);
        inputRow.setBorder(new EmptyBorder(10, 14, 6, 14));
        inputRow.add(inputField, BorderLayout.CENTER);
        inputRow.add(sendButton, BorderLayout.EAST);

        JLabel hint = new JLabel("  Try: hello · joke · java · life · weather · hack · exit");
        hint.setFont(new Font("Consolas", Font.PLAIN, 10));
        hint.setForeground(GRAY);
        hint.setBackground(BG_PANEL);
        hint.setOpaque(true);
        hint.setBorder(new EmptyBorder(0, 14, 8, 0));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(BG_PANEL);
        bottom.add(inputRow, BorderLayout.CENTER);
        bottom.add(hint,     BorderLayout.SOUTH);

        // ── Assemble ──────────────────────────────────────────────────────────
        setLayout(new BorderLayout());
        add(header, BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);
        add(bottom,  BorderLayout.SOUTH);

        // ── Wire actions ──────────────────────────────────────────────────────
        ActionListener onSend = e -> handleSend();
        sendButton.addActionListener(onSend);
        inputField.addActionListener(onSend);

        setVisible(true);
        inputField.requestFocusInWindow();
    }

    // ── Boot greeting ─────────────────────────────────────────────────────────
    private void showBoot() {
        appendText("  ╔══════════════════════════════════════╗\n", CYAN,   15, true);
        appendText("  ║     AETHER MIND v0.9 - Local AI      ║\n", CYAN,   15, true);
        appendText("  ╚══════════════════════════════════════╝\n", CYAN,   15, true);
        appendText("\n  No cloud. No API. Pure local intelligence.\n\n", YELLOW, 12, false);
        appendText("  Initializing neural pathways...\n",  GRAY,  12, false);
        appendText("  Loading knowledge base...\n",        GRAY,  12, false);
        appendText("  Consciousness online.\n\n",          GREEN, 13, true);
        appendText("  ─────────────────────────────────────────\n\n", GRAY, 11, false);
    }

    // ── Handle user message ───────────────────────────────────────────────────
    private void handleSend() {
        String raw = inputField.getText().trim();
        if (raw.isEmpty()) return;

        inputField.setText("");
        setInputEnabled(false);

        // Print user line
        appendText("  You  ›  ", GREEN, 13, true);
        appendText(raw + "\n\n", WHITE, 13, false);

        saveTurn("U", raw);

        if (AetherBrain.isExit(raw)) {
            appendText("  Aether  ›  ", CYAN, 13, true);
            typewriterAppend(AetherBrain.GOODBYE + "\n\n", WHITE, 13, () -> {
                saveTurn("A", AetherBrain.GOODBYE);
                statusLabel.setText("● OFFLINE");
                statusLabel.setForeground(GRAY);
            });
            return;
        }

        String reply = brain.respond(raw);
        statusLabel.setText("● THINKING...");
        statusLabel.setForeground(YELLOW);

        appendText("  Aether  ›  ", CYAN, 13, true);
        typewriterAppend(reply + "\n\n", WHITE, 13, () -> {
            saveTurn("A", reply);
            statusLabel.setText("● ONLINE");
            statusLabel.setForeground(GREEN);
            setInputEnabled(true);
            inputField.requestFocusInWindow();
        });
    }

    // ── Typewriter animation ──────────────────────────────────────────────────
    private void typewriterAppend(String text, Color color, int size, Runnable onDone) {
        final int[] i = {0};
        Timer t = new Timer(16, null);
        t.addActionListener(e -> {
            if (i[0] < text.length()) {
                appendText(String.valueOf(text.charAt(i[0])), color, size, false);
                scrollToBottom();
                i[0]++;
            } else {
                t.stop();
                if (onDone != null) onDone.run();
            }
        });
        t.start();
    }

    // ── Append styled text ────────────────────────────────────────────────────
    private void appendText(String text, Color color, int size, boolean bold) {
        SimpleAttributeSet a = new SimpleAttributeSet();
        StyleConstants.setForeground(a, color);
        StyleConstants.setFontFamily(a, "Consolas");
        StyleConstants.setFontSize(a, size);
        StyleConstants.setBold(a, bold);
        try { doc.insertString(doc.getLength(), text, a); }
        catch (BadLocationException ignored) {}
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> chatPane.setCaretPosition(doc.getLength()));
    }

    private void setInputEnabled(boolean enabled) {
        inputField.setEnabled(enabled);
        sendButton.setEnabled(enabled);
    }

    private Path historyFile() {
        return Path.of(System.getProperty("user.home"), ".aethermind", "history.txt");
    }

    private Path memoryFile() {
        return Path.of(System.getProperty("user.home"), ".aethermind", "memory.txt");
    }

    private void saveTurn(String who, String text) {
        if (restoring) return;
        try {
            Path file = historyFile();
            Files.createDirectories(file.getParent());
            Files.writeString(
                file,
                who + " " + text.replace("\n", "\\n") + "\n",
                StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND
            );
            Path mem = memoryFile();
            StringBuilder sb = new StringBuilder();
            for (String m : brain.memorySnapshot()) sb.append(m.replace("\n", " ")).append('\n');
            Files.writeString(mem, sb.toString(), StandardCharsets.UTF_8);
        } catch (Exception ignored) {}
    }

    private void restoreHistory() {
        Path file = historyFile();
        if (!Files.isRegularFile(file)) return;
        restoring = true;
        try {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (line.length() < 3) continue;
                String who = line.substring(0, 1);
                String text = line.substring(2).replace("\\n", "\n");
                if ("U".equals(who)) {
                    appendText("  You  ›  ", GREEN, 13, true);
                    appendText(text + "\n\n", WHITE, 13, false);
                } else if ("A".equals(who)) {
                    appendText("  Aether  ›  ", CYAN, 13, true);
                    appendText(text + "\n\n", WHITE, 13, false);
                }
            }
            Path mem = memoryFile();
            if (Files.isRegularFile(mem)) {
                brain.restoreMemory(Files.readAllLines(mem, StandardCharsets.UTF_8));
            }
            scrollToBottom();
        } catch (Exception ignored) {
        } finally {
            restoring = false;
        }
    }

    // ── Entry point ───────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AetherMindApp::new);
    }
}
