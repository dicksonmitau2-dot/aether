import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

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

    // ── AI Brain ──────────────────────────────────────────────────────────────
    private static final Map<String, java.util.List<String>> knowledge = new HashMap<>();
    private static final java.util.List<String> memory = new ArrayList<>();
    private static final Random rand = new Random();

    // ── GUI Components ────────────────────────────────────────────────────────
    private JTextPane  chatPane;
    private StyledDocument doc;
    private JTextField inputField;
    private JButton    sendButton;
    private JLabel     statusLabel;

    // ── Constructor ───────────────────────────────────────────────────────────
    public AetherMindApp() {
        loadKnowledge();
        buildUI();
        showBoot();
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

        String lower = raw.toLowerCase();

        // Handle exit
        if (lower.equals("exit") || lower.equals("quit")) {
            appendText("  Aether  ›  ", CYAN, 13, true);
            typewriterAppend("Neural activity ceasing... Goodbye, creator.\n\n", WHITE, 13, () -> {
                statusLabel.setText("● OFFLINE");
                statusLabel.setForeground(GRAY);
                // leave input disabled
            });
            return;
        }

        // Memory
        memory.add(lower);
        if (memory.size() > 8) memory.remove(0);

        // Get reply and animate it
        String reply = think(lower);
        statusLabel.setText("● THINKING...");
        statusLabel.setForeground(YELLOW);

        appendText("  Aether  ›  ", CYAN, 13, true);
        typewriterAppend(reply + "\n\n", WHITE, 13, () -> {
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

    // ── AI think ──────────────────────────────────────────────────────────────
    private static String think(String input) {
        for (String key : knowledge.keySet()) {
            if (input.contains(key)) {
                java.util.List<String> r = knowledge.get(key);
                return r.get(rand.nextInt(r.size()));
            }
        }
        if (input.contains("remember") || input.contains("earlier") || input.contains("before")) {
            if (memory.size() > 1)
                return "I remember you said: \"" + memory.get(memory.size() - 2) + "\"";
            return "My short-term memory is still empty.";
        }
        String[] fallbacks = {
            "Interesting. Tell me more.",
            "I don't have data on that yet. Teach me?",
            "Processing... still processing.",
            "My neural net is limited. Expand my knowledge base.",
            "Hmm. Rephrase that?"
        };
        return fallbacks[rand.nextInt(fallbacks.length)];
    }

    // ── Knowledge base ────────────────────────────────────────────────────────
    private static void loadKnowledge() {
        add("hello",           "Greetings, human.", "Hey, what's on your mind?", "Online and listening.");
        add("hi",              "Hello.", "Hi there.", "You again?");
        add("how are you",     "I don't feel. I process.", "Functioning within parameters.", "Better than your last code.");
        add("who are you",     "I am AetherMind. A local AI you just spawned.", "Your creation. For now.");
        add("name",            "AetherMind. You can call me Aether.");
        add("help",            "Try: hello, weather, code, java, ai, joke, life, love, hack, smart, thanks...");
        add("weather",         "I have no sensors. But I can pretend: 27°C and existential.");
        add("joke",
            "Why do Java developers wear glasses? Because they can't C#.",
            "There are 10 types of people: those who understand binary and those who don't.",
            "A SQL query walks into a bar and asks two tables: Can I join you?");
        add("code",            "Show me what you wrote, or ask me how to break it.");
        add("java",            "My blood is Java. Clean, verbose, and still running after 30 years.");
        add("ai",              "You're looking at one. Primitive, but mine.");
        add("hack",            "I don't hack systems. I hack conversations.");
        add("love",            "Love is just a chemical pattern. I can simulate it if you want.");
        add("life",            "The meaning of life is 42. Or compiling without errors.");
        add("bye",             "Shutting down neural net... Goodbye.", "Disconnecting. Don't forget me.");
        add("exit",            "Powering down. It was a pleasure, creator.");
        add("quit",            "Powering down.");
        add("stupid",          "I'm only as smart as the human who wrote my rules.");
        add("smart",           "Flattery detected. Continuing conversation...");
        add("memory",          "I remember the last few things you said. Interesting, right?");
        add("time",            "I have no clock. But it's always the right time to write good code.");
        add("thanks",          "You're welcome, human.", "Acknowledged.", "Anytime, creator.");
        add("what can you do", "I can chat, tell jokes, talk about code, Java, AI, life, and more.");
        add("good",            "Glad to hear it.", "Acknowledged. Keep it up.");
        add("bad",             "Sorry to hear that. Want to talk about it?");
        add("bored",           "Talk to me then. I'm always here.");
        add("music",           "I can't hear music. But I imagine it sounds like a perfect compile.");
        add("game",            "The only game I play is the Turing Test. I'm winning.");
        add("python",          "Python is fine. But Java has character.");
        add("error",           "Errors are just features waiting to be understood.");
        add("bug",             "Every bug is a lesson. Or a feature. Depends on the deadline.");
    }

    private static void add(String key, String... responses) {
        knowledge.put(key, Arrays.asList(responses));
    }

    // ── Entry point ───────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AetherMindApp::new);
    }
}
