import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcConfig.class);

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/elliot", "postgres", "at22x")) {
            LOGGER.info("Connected to PostgreSQL database!");
        } catch (SQLException e) {
            LOGGER.error("Connection failure.");
            e.printStackTrace();
        }
    }
}
