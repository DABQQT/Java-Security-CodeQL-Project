
import java.sql.*;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class LoginApp {
    public static void main(String[] args) throws Exception {
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");

        if (dbUser == null || dbPassword == null) {
            System.out.println("Database configuration missing.");
            return;
        }

        try (Scanner input = new Scanner(System.in)) {
            System.out.print("Enter username: ");
            String username = input.nextLine();

            System.out.print("Enter password: ");
            String password = input.nextLine();

            String query =
                "SELECT password_hash FROM accounts WHERE username = ?";

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/users",
                    dbUser, dbPassword);
                 PreparedStatement stmt =
                    conn.prepareStatement(query)) {

                stmt.setString(1, username);

                try (ResultSet results = stmt.executeQuery()) {
                    boolean valid = false;

                    if (results.next()) {
                        String storedHash =
                            results.getString("password_hash");

                        valid = BCrypt.checkpw(password, storedHash);
                    }

                    System.out.println(valid
                        ? "Login successful!"
                        : "Login failed!");
                }
            }
        }
    }
}

