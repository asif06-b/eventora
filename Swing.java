import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.sql.*;

public class Swing extends JFrame {

    // JDBC Details
    static final String URL = "jdbc:mysql://localhost:3306/eventdb";
    static final String USER = "root";
    static final String PASS = "asif06";

    // Hero image path (change to your image)
    static final String IMAGE_PATH = "\"C:\\Users\\Asif\\Downloads\\03_Why-is-Marriage-Important_-12-Powerful-Reasons-Why-You-Should-Get-Married.jpg\"";

    static Connection conn;

    CardLayout card;
    JPanel mainPanel;

    public Swing() {
        setTitle("Event Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            return;
        }

        card = new CardLayout();
        mainPanel = new JPanel(card);
        mainPanel.add(new LoginPanel(), "Login");
        mainPanel.add(new DashboardPanel(), "Dashboard");

        add(mainPanel);
        card.show(mainPanel, "Login");
    }

    // ========== LOGIN PANEL ==========
    class LoginPanel extends JPanel {
        JTextField userField;
        JPasswordField passField;

        LoginPanel() {
            setLayout(null);
            setBackground(new Color(240, 245, 255));

            JLabel title = new JLabel("Event Management System", SwingConstants.CENTER);
            title.setFont(new Font("Segoe UI", Font.BOLD, 26));
            title.setBounds(250, 60, 500, 40);
            add(title);

            JLabel userLbl = new JLabel("Username:");
            JLabel passLbl = new JLabel("Password:");
            userLbl.setBounds(380, 160, 100, 25);
            passLbl.setBounds(380, 200, 100, 25);
            add(userLbl);
            add(passLbl);

            userField = new JTextField();
            passField = new JPasswordField();
            userField.setBounds(480, 160, 150, 25);
            passField.setBounds(480, 200, 150, 25);
            add(userField);
            add(passField);

            JButton loginBtn = new JButton("Login");
            loginBtn.setBounds(430, 250, 120, 35);
            loginBtn.setBackground(new Color(66, 135, 245));
            loginBtn.setForeground(Color.WHITE);
            loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            add(loginBtn);

            loginBtn.addActionListener(e -> authenticate());
        }

        void authenticate() {
            try {
                String user = userField.getText();
                String pass = String.valueOf(passField.getPassword());
                PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
                pst.setString(1, user);
                pst.setString(2, pass);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Welcome, " + user + "!");
                    card.show(mainPanel, "Dashboard");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

 // ========== DASHBOARD PANEL (FINAL) ==========
    class DashboardPanel extends JPanel {
        private final String[] btnTexts = {
                " Add Event", " View Events", " Update Event", " Search Event",
                " Filter by Date", " Manage Users", " Register", " Upcoming",
                " Statistics", " Export CSV", " Logout"
        };

        DashboardPanel() {
            setLayout(new BorderLayout());
            setBackground(new Color(245, 247, 255));

            // ----- Left Sidebar -----
            JPanel sidebar = new JPanel(new GridBagLayout());
            sidebar.setBackground(new Color(250, 252, 255));
            sidebar.setPreferredSize(new Dimension(260, getHeight()));

            JPanel btnCol = new JPanel(new GridLayout(0, 1, 10, 10));
            btnCol.setOpaque(false);
            btnCol.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

            Color[] palette = {
                    new Color(88, 101, 242),   // indigo
                    new Color(46, 204, 113),   // green
                    new Color(230, 126, 34),   // orange
                    new Color(52, 152, 219),   // blue
                    new Color(155, 89, 182),   // purple
                    new Color(241, 196, 15),   // yellow
                    new Color(231, 76, 60),    // red
                    new Color(26, 188, 156),   // teal
                    new Color(127, 140, 141),  // gray
                    new Color(33, 150, 243),   // light blue
                    new Color(44, 62, 80)      // dark slate
            };

            JButton[] buttons = new JButton[btnTexts.length];
            for (int i = 0; i < btnTexts.length; i++) {
                buttons[i] = makeSidebarButton(btnTexts[i], palette[i]);
                btnCol.add(buttons[i]);
            }

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1; gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            sidebar.add(btnCol, gbc);

            // ----- Center Hero (image + dark scrim) -----
            JPanel center = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    // Try to draw the image "cover" style; if not available, draw a soft gradient
                    Image img = new ImageIcon(IMAGE_PATH).getImage();
                    int pw = getWidth(), ph = getHeight();
                    int iw = img.getWidth(null), ih = img.getHeight(null);

                    if (iw > 0 && ih > 0) {
                        double s = Math.max(pw / (double) iw, ph / (double) ih);
                        int w = (int) Math.round(iw * s);
                        int h = (int) Math.round(ih * s);
                        int x = (pw - w) / 2;
                        int y = (ph - h) / 2;
                        g2.drawImage(img, x, y, w, h, null);
                    } else {
                        // fallback background
                        g2.setPaint(new GradientPaint(0, 0, new Color(220, 226, 240),
                                                      pw, ph, new Color(245, 247, 255)));
                        g2.fillRect(0, 0, pw, ph);
                    }

                    // DARKER SCRIM so white text is readable on any image
                    g2.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 170),
                                                  0, ph, new Color(0, 0, 0, 235)));
                    g2.fillRect(0, 0, pw, ph);

                    g2.dispose();
                }
            };
            center.setOpaque(true);
            center.setBackground(new Color(128, 0, 128));

            // Caption card with rounded translucent background
            RoundedPanel caption = new RoundedPanel(new Color(255, 255, 255, 28), 18);
            caption.setOpaque(false);
            caption.setLayout(new BoxLayout(caption, BoxLayout.Y_AXIS));
            caption.setBorder(BorderFactory.createEmptyBorder(18, 26, 18, 26));

            JLabel big = new JLabel("Eventora");
            big.setForeground(Color.WHITE);
            big.setFont(new Font("Segoe UI", Font.BOLD, 34));
            big.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel sub = new JLabel("Plan • Organize • Track");
            sub.setForeground(new Color(255, 255, 255, 230));
            sub.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);

            caption.add(big);
            caption.add(Box.createVerticalStrut(8));
            caption.add(sub);

            center.add(caption, new GridBagConstraints());

            // Compose
            add(sidebar, BorderLayout.WEST);
            add(center, BorderLayout.CENTER);

            // ----- Actions -----
            buttons[0].addActionListener(e -> new AddEventFrame());
            buttons[1].addActionListener(e -> new ViewEventsFrame());
            buttons[2].addActionListener(e -> new UpdateEventFrame());
            buttons[3].addActionListener(e -> new SearchEventFrame());
            buttons[4].addActionListener(e -> new FilterEventsFrame());
            buttons[5].addActionListener(e -> new ManageUsersFrame());
            buttons[6].addActionListener(e -> new RegisterEventFrame());
            buttons[7].addActionListener(e -> new UpcomingEventsFrame());
            buttons[8].addActionListener(e -> new StatsFrame());
            buttons[9].addActionListener(e -> exportCSV());
            buttons[10].addActionListener(e -> card.show(mainPanel, "Login"));
        }

        private JButton makeSidebarButton(String text, Color base) {
            JButton b = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, base.brighter(), getWidth(), getHeight(), base.darker());
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setFocusPainted(false);
            b.setContentAreaFilled(false);
            b.setOpaque(false);
            b.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI", Font.BOLD, 14));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            b.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    b.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(255, 255, 255, 210), 2, true),
                            BorderFactory.createEmptyBorder(10, 14, 10, 14)
                    ));
                }
                @Override public void mouseExited(MouseEvent e) {
                    b.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
                }
            });
            return b;
        }

        // Small helper for rounded translucent panels
        private static class RoundedPanel extends JPanel {
            private final Color fill;
            private final int radius;
            RoundedPanel(Color fill, int radius) {
                this.fill = fill;
                this.radius = radius;
                setOpaque(false);
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
                super.paintComponent(g);
            }
        }
    }

    // ========== ADD EVENT ==========
    class AddEventFrame extends JFrame {
        JTextField name, date, loc, org, cap;
        AddEventFrame() {
            setTitle("Add Event");
            setSize(400, 350);
            setLayout(new GridLayout(6, 2, 10, 10));
            setLocationRelativeTo(null);

            name = new JTextField(); date = new JTextField(); loc = new JTextField();
            org = new JTextField(); cap = new JTextField();
            add(new JLabel("Event Name:")); add(name);
            add(new JLabel("Date (YYYY-MM-DD):")); add(date);
            add(new JLabel("Location:")); add(loc);
            add(new JLabel("Organizer:")); add(org);
            add(new JLabel("Capacity:")); add(cap);

            JButton addBtn = new JButton("Add Event");
            add(new JLabel()); add(addBtn);
            addBtn.addActionListener(e -> addEvent());
            setVisible(true);
        }

        void addEvent() {
            try {
                PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO events (event_name, event_date, location, organizer, capacity) VALUES (?, ?, ?, ?, ?)");
                pst.setString(1, name.getText());
                pst.setString(2, date.getText());
                pst.setString(3, loc.getText());
                pst.setString(4, org.getText());
                pst.setInt(5, Integer.parseInt(cap.getText()));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Event added successfully!");
                dispose();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // ========== VIEW EVENTS ==========
    class ViewEventsFrame extends JFrame {
        ViewEventsFrame() {
            setTitle("All Events");
            setSize(800, 400);
            setLocationRelativeTo(null);

            String[] cols = {"ID", "Name", "Date", "Location", "Organizer", "Capacity"};
            DefaultTableModel model = new DefaultTableModel(cols, 0);
            JTable table = new JTable(model);
            add(new JScrollPane(table));

            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM events ORDER BY event_date ASC");
                while (rs.next()) {
                    Object[] row = {rs.getInt(1), rs.getString(2), rs.getString(3),
                            rs.getString(4), rs.getString(5), rs.getInt(6)};
                    model.addRow(row);
                }
            } catch (Exception e) { e.printStackTrace(); }
            setVisible(true);
        }
    }

    // ========== UPDATE EVENT ==========
    class UpdateEventFrame extends JFrame {
        JTextField id, name, date, loc, org, cap;
        UpdateEventFrame() {
            setTitle("Update Event");
            setSize(400, 400);
            setLayout(new GridLayout(7, 2, 10, 10));
            setLocationRelativeTo(null);

            id = new JTextField(); name = new JTextField(); date = new JTextField();
            loc = new JTextField(); org = new JTextField(); cap = new JTextField();

            add(new JLabel("Event ID:")); add(id);
            add(new JLabel("New Name:")); add(name);
            add(new JLabel("New Date:")); add(date);
            add(new JLabel("New Location:")); add(loc);
            add(new JLabel("New Organizer:")); add(org);
            add(new JLabel("New Capacity:")); add(cap);

            JButton update = new JButton("Update Event");
            add(new JLabel()); add(update);
            update.addActionListener(e -> updateEvent());
            setVisible(true);
        }

        void updateEvent() {
            try {
                PreparedStatement pst = conn.prepareStatement(
                        "UPDATE events SET event_name=?, event_date=?, location=?, organizer=?, capacity=? WHERE id=?");
                pst.setString(1, name.getText());
                pst.setString(2, date.getText());
                pst.setString(3, loc.getText());
                pst.setString(4, org.getText());
                pst.setInt(5, Integer.parseInt(cap.getText()));
                pst.setInt(6, Integer.parseInt(id.getText()));
                int rows = pst.executeUpdate();
                JOptionPane.showMessageDialog(this, rows > 0 ? "Event updated!" : "Event not found!");
                dispose();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // ========== SEARCH EVENT ==========
    class SearchEventFrame extends JFrame {
        JTextField keyword;
        JComboBox<String> type;
        SearchEventFrame() {
            setTitle("Search Events");
            setSize(400, 200);
            setLayout(new GridLayout(3, 2, 10, 10));
            setLocationRelativeTo(null);

            type = new JComboBox<>(new String[]{"Name", "Location", "Date"});
            keyword = new JTextField();

            add(new JLabel("Search by:")); add(type);
            add(new JLabel("Keyword:")); add(keyword);

            JButton search = new JButton("Search");
            add(new JLabel()); add(search);
            search.addActionListener(e -> searchEvent());
            setVisible(true);
        }

        void searchEvent() {
            try {
                String field = switch (type.getSelectedItem().toString().toLowerCase()) {
                    case "location" -> "location";
                    case "date" -> "event_date";
                    default -> "event_name";
                };
                PreparedStatement pst = conn.prepareStatement("SELECT * FROM events WHERE " + field + " LIKE ?");
                pst.setString(1, "%" + keyword.getText() + "%");
                ResultSet rs = pst.executeQuery();
                JTable table = new JTable(buildTable(rs));
                JOptionPane.showMessageDialog(this, new JScrollPane(table), "Search Results", JOptionPane.PLAIN_MESSAGE);
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // ========== FILTER EVENTS ==========
    class FilterEventsFrame extends JFrame {
        JTextField start, end;
        FilterEventsFrame() {
            setTitle("Filter by Date");
            setSize(400, 200);
            setLayout(new GridLayout(3, 2, 10, 10));
            setLocationRelativeTo(null);

            start = new JTextField(); end = new JTextField();
            add(new JLabel("Start Date:")); add(start);
            add(new JLabel("End Date:")); add(end);

            JButton filter = new JButton("Filter");
            add(new JLabel()); add(filter);
            filter.addActionListener(e -> filterEvents());
            setVisible(true);
        }

        void filterEvents() {
            try {
                PreparedStatement pst = conn.prepareStatement(
                        "SELECT * FROM events WHERE event_date BETWEEN ? AND ?");
                pst.setString(1, start.getText());
                pst.setString(2, end.getText());
                ResultSet rs = pst.executeQuery();
                JTable table = new JTable(buildTable(rs));
                JOptionPane.showMessageDialog(this, new JScrollPane(table), "Filtered Events", JOptionPane.PLAIN_MESSAGE);
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // ========== MANAGE USERS ==========
    class ManageUsersFrame extends JFrame {
        JTextField uname, pass;
        ManageUsersFrame() {
            setTitle("Manage Users");
            setSize(400, 250);
            setLayout(new GridLayout(3, 2, 10, 10));
            setLocationRelativeTo(null);

            uname = new JTextField(); pass = new JTextField();
            add(new JLabel("Username:")); add(uname);
            add(new JLabel("Password:")); add(pass);

            JButton addUser = new JButton("Add User");
            add(new JLabel()); add(addUser);
            addUser.addActionListener(e -> addUser());
            setVisible(true);
        }

        void addUser() {
            try {
                PreparedStatement pst = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
                pst.setString(1, uname.getText());
                pst.setString(2, pass.getText());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "User added successfully!");
                dispose();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // ========== REGISTER FOR EVENT ==========
    class RegisterEventFrame extends JFrame {
        JTextField uname, eventId;
        RegisterEventFrame() {
            setTitle("Register for Event");
            setSize(400, 200);
            setLayout(new GridLayout(3, 2, 10, 10));
            setLocationRelativeTo(null);

            uname = new JTextField(); eventId = new JTextField();
            add(new JLabel("Username:")); add(uname);
            add(new JLabel("Event ID:")); add(eventId);

            JButton reg = new JButton("Register");
            add(new JLabel()); add(reg);
            reg.addActionListener(e -> register());
            setVisible(true);
        }

        void register() {
            try {
                PreparedStatement pst = conn.prepareStatement(
                        "INSERT INTO registrations (username, event_id) VALUES (?, ?)");
                pst.setString(1, uname.getText());
                pst.setInt(2, Integer.parseInt(eventId.getText()));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "🎟️ Registration successful!");
                dispose();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    // ========== UPCOMING EVENTS ==========
    class UpcomingEventsFrame extends JFrame {
        UpcomingEventsFrame() {
            setTitle("Upcoming Events");
            setSize(500, 400);
            setLocationRelativeTo(null);

            JTextArea area = new JTextArea();
            area.setEditable(false);
            add(new JScrollPane(area));

            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT * FROM events WHERE event_date >= CURDATE() ORDER BY event_date ASC LIMIT 5");
                while (rs.next()) {
                    area.append("📅 " + rs.getString("event_date") + " - " + rs.getString("event_name")
                            + " (" + rs.getString("location") + ")\n");
                }
            } catch (Exception ex) { ex.printStackTrace(); }
            setVisible(true);
        }
    }

    // ========== STATS FRAME ==========
    class StatsFrame extends JFrame {
        StatsFrame() {
            setTitle("Event Statistics");
            setSize(400, 200);
            setLocationRelativeTo(null);
            JTextArea area = new JTextArea();
            area.setEditable(false);
            add(new JScrollPane(area));

            try {
                Statement st = conn.createStatement();
                ResultSet total = st.executeQuery("SELECT COUNT(*) AS total FROM events");
                if (total.next()) area.append("Total Events: " + total.getInt("total") + "\n");

                ResultSet top = st.executeQuery(
                        "SELECT organizer, COUNT(*) AS count FROM events GROUP BY organizer ORDER BY count DESC LIMIT 1");
                if (top.next()) area.append("Top Organizer: " + top.getString("organizer"));
            } catch (Exception e) { e.printStackTrace(); }
            setVisible(true);
        }
    }

    // ========== CSV EXPORT ==========
    void exportCSV() {
        try (FileWriter fw = new FileWriter("events_export.csv")) {
            fw.write("ID,Name,Date,Location,Organizer,Capacity\n");
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM events");
            while (rs.next()) {
                fw.write(rs.getInt("id") + "," + rs.getString("event_name") + "," +
                        rs.getString("event_date") + "," + rs.getString("location") + "," +
                        rs.getString("organizer") + "," + rs.getInt("capacity") + "\n");
            }
            fw.flush();
            JOptionPane.showMessageDialog(this, "📁 Events exported to events_export.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ========== TABLE BUILDER ==========
    DefaultTableModel buildTable(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        DefaultTableModel model = new DefaultTableModel();
        for (int i = 1; i <= colCount; i++) model.addColumn(meta.getColumnName(i));
        while (rs.next()) {
            Object[] row = new Object[colCount];
            for (int i = 0; i < colCount; i++) row[i] = rs.getObject(i + 1);
            model.addRow(row);
        }
        return model;
    }

    // ========== MAIN ==========
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Swing().setVisible(true));
    }
}
