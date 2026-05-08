import java.sql.*;
import java.util.*;
import java.io.FileWriter;

public class Event {

    // Database connection details
    static final String URL = "jdbc:mysql://localhost:3306/eventdb";
    static final String USER = "root";      
    static final String PASS = "asif06";    

    static Connection conn;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);

            System.out.println("=== 🌟 ENHANCED EVENT MANAGEMENT SYSTEM 🌟 ===");

            if (login()) {
                int choice;
                do {
                    System.out.println("\n------ MENU ------");
                    System.out.println("1. Add Event");
                    System.out.println("2. View All Events");
                    System.out.println("3. Update Event");
                    System.out.println("4. Search Event");
                    System.out.println("5. Delete Event");
                    System.out.println("6. Filter Events by Date Range");
                    System.out.println("7. Manage Users");
                    System.out.println("8. Register for Event");
                    System.out.println("9. Show Upcoming Events");
                    System.out.println("10. View Event Statistics");
                    System.out.println("11. Export Events to CSV");
                    System.out.println("12. Exit");
                    System.out.print("Enter your choice: ");
                    choice = sc.nextInt();
                    sc.nextLine();

                    switch (choice) {
                        case 1 -> addEvent();
                        case 2 -> viewEvents();
                        case 3 -> updateEvent();
                        case 4 -> searchEvent();
                        case 5 -> deleteEvent();
                        case 6 -> filterByDateRange();
                        case 7 -> manageUsers();
                        case 8 -> registerForEvent();
                        case 9 -> showUpcomingEvents();
                        case 10 -> eventStatistics();
                        case 11 -> exportToCSV();
                        case 12 -> System.out.println("Goodbye!");
                        default -> System.out.println("Invalid choice!");
                    }
                } while (choice != 12);
            } else {
                System.out.println("Invalid login credentials. Exiting...");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---------- LOGIN ----------
    static boolean login() throws SQLException {
        System.out.print("Enter username: ");
        String uname = sc.nextLine();
        System.out.print("Enter password: ");
        String pass = sc.nextLine();

        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, uname);
        pst.setString(2, pass);
        ResultSet rs = pst.executeQuery();
        return rs.next();
    }

    // ---------- ADD EVENT ----------
    static void addEvent() throws SQLException {
        System.out.print("Enter Event Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Event Date (YYYY-MM-DD): ");
        String date = sc.nextLine();
        System.out.print("Enter Location: ");
        String loc = sc.nextLine();
        System.out.print("Enter Organizer: ");
        String org = sc.nextLine();
        System.out.print("Enter Capacity: ");
        int capacity = sc.nextInt();
        sc.nextLine();

        String sql = "INSERT INTO events (event_name, event_date, location, organizer, capacity) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, name);
        pst.setString(2, date);
        pst.setString(3, loc);
        pst.setString(4, org);
        pst.setInt(5, capacity);
        pst.executeUpdate();
        System.out.println("✅ Event added successfully!");
    }

    // ---------- VIEW EVENTS ----------
    static void viewEvents() throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM events ORDER BY event_date ASC");

        System.out.println("\n------ ALL EVENTS ------");
        while (rs.next()) {
            System.out.println("ID: " + rs.getInt("id") +
                    " | Name: " + rs.getString("event_name") +
                    " | Date: " + rs.getString("event_date") +
                    " | Location: " + rs.getString("location") +
                    " | Organizer: " + rs.getString("organizer") +
                    " | Capacity: " + rs.getInt("capacity"));
        }
    }

    // ---------- UPDATE EVENT ----------
    static void updateEvent() throws SQLException {
        System.out.print("Enter Event ID to update: ");
        int id = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter new Event Name: ");
        String name = sc.nextLine();
        System.out.print("Enter new Date (YYYY-MM-DD): ");
        String date = sc.nextLine();
        System.out.print("Enter new Location: ");
        String loc = sc.nextLine();
        System.out.print("Enter new Organizer: ");
        String org = sc.nextLine();
        System.out.print("Enter new Capacity: ");
        int capacity = sc.nextInt();
        sc.nextLine();

        String sql = "UPDATE events SET event_name=?, event_date=?, location=?, organizer=?, capacity=? WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, name);
        pst.setString(2, date);
        pst.setString(3, loc);
        pst.setString(4, org);
        pst.setInt(5, capacity);
        pst.setInt(6, id);
        int rows = pst.executeUpdate();

        if (rows > 0)
            System.out.println("✅ Event updated successfully!");
        else
            System.out.println("⚠️ Event not found!");
    }

    // ---------- SEARCH EVENT ----------
    static void searchEvent() throws SQLException {
        System.out.print("Search by (name/location/date): ");
        String type = sc.nextLine().toLowerCase();
        System.out.print("Enter search keyword: ");
        String keyword = sc.nextLine();

        String sql;
        if (type.equals("name"))
            sql = "SELECT * FROM events WHERE event_name LIKE ?";
        else if (type.equals("location"))
            sql = "SELECT * FROM events WHERE location LIKE ?";
        else
            sql = "SELECT * FROM events WHERE event_date LIKE ?";

        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, "%" + keyword + "%");
        ResultSet rs = pst.executeQuery();

        System.out.println("\n------ SEARCH RESULTS ------");
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println("ID: " + rs.getInt("id") +
                    " | Name: " + rs.getString("event_name") +
                    " | Date: " + rs.getString("event_date") +
                    " | Location: " + rs.getString("location") +
                    " | Organizer: " + rs.getString("organizer"));
        }
        if (!found)
            System.out.println("No events found matching your search.");
    }

    // ---------- DELETE EVENT ----------
    static void deleteEvent() throws SQLException {
        System.out.print("Enter Event ID to delete: ");
        int id = sc.nextInt();
        sc.nextLine();

        String sql = "DELETE FROM events WHERE id=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setInt(1, id);
        int rows = pst.executeUpdate();

        if (rows > 0)
            System.out.println("✅ Event deleted successfully!");
        else
            System.out.println("⚠️ Event not found!");
    }

    // ---------- FILTER BY DATE RANGE ----------
    static void filterByDateRange() throws SQLException {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        String start = sc.nextLine();
        System.out.print("Enter end date (YYYY-MM-DD): ");
        String end = sc.nextLine();

        String sql = "SELECT * FROM events WHERE event_date BETWEEN ? AND ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, start);
        pst.setString(2, end);
        ResultSet rs = pst.executeQuery();

        System.out.println("\n------ EVENTS IN DATE RANGE ------");
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println("ID: " + rs.getInt("id") +
                    " | Name: " + rs.getString("event_name") +
                    " | Date: " + rs.getString("event_date") +
                    " | Location: " + rs.getString("location"));
        }
        if (!found)
            System.out.println("No events found between these dates.");
    }

    // ---------- MANAGE USERS ----------
    static void manageUsers() throws SQLException {
        System.out.println("\n1. Add User");
        System.out.println("2. View Users");
        System.out.print("Choose: ");
        int opt = sc.nextInt();
        sc.nextLine();

        if (opt == 1) {
            System.out.print("Enter new username: ");
            String uname = sc.nextLine();
            System.out.print("Enter password: ");
            String pass = sc.nextLine();
            String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, uname);
            pst.setString(2, pass);
            pst.executeUpdate();
            System.out.println("✅ User added successfully!");
        } else {
            ResultSet rs = conn.createStatement().executeQuery("SELECT id, username FROM users");
            System.out.println("\n------ USER LIST ------");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + " | Username: " + rs.getString("username"));
            }
        }
    }

    // ---------- REGISTER FOR EVENT ----------
    static void registerForEvent() throws SQLException {
        System.out.print("Enter your username: ");
        String uname = sc.nextLine();
        System.out.print("Enter Event ID to register: ");
        int eventId = sc.nextInt();
        sc.nextLine();

        String sql = "INSERT INTO registrations (username, event_id) VALUES (?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, uname);
        pst.setInt(2, eventId);
        pst.executeUpdate();
        System.out.println("🎟️ Successfully registered for event!");
    }

    // ---------- SHOW UPCOMING EVENTS ----------
    static void showUpcomingEvents() throws SQLException {
        String sql = "SELECT * FROM events WHERE event_date >= CURDATE() ORDER BY event_date ASC LIMIT 5";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        System.out.println("\n------ UPCOMING EVENTS ------");
        while (rs.next()) {
            System.out.println("📅 " + rs.getString("event_date") + " | " + rs.getString("event_name"));
        }
    }

    // ---------- EVENT STATISTICS ----------
    static void eventStatistics() throws SQLException {
        Statement st = conn.createStatement();

        ResultSet total = st.executeQuery("SELECT COUNT(*) AS total FROM events");
        if (total.next()) System.out.println("Total Events: " + total.getInt("total"));

        ResultSet organizer = st.executeQuery("SELECT organizer, COUNT(*) AS count FROM events GROUP BY organizer ORDER BY count DESC LIMIT 1");
        if (organizer.next()) System.out.println("Most Active Organizer: " + organizer.getString("organizer"));
    }

    // ---------- EXPORT EVENTS TO CSV ----------
    static void exportToCSV() throws SQLException {
        try (FileWriter fw = new FileWriter("events_export.csv")) {
            fw.write("ID,Name,Date,Location,Organizer,Capacity\n");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM events");

            while (rs.next()) {
                fw.write(rs.getInt("id") + "," +
                        rs.getString("event_name") + "," +
                        rs.getString("event_date") + "," +
                        rs.getString("location") + "," +
                        rs.getString("organizer") + "," +
                        rs.getInt("capacity") + "\n");
            }
            fw.flush();
            System.out.println("📁 Events exported successfully to events_export.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
