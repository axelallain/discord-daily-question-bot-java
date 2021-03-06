import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcConfig.class);

    public static void main(String[] args) throws URISyntaxException {
        String dbUrl = "jdbc:postgresql://" + "ec2-52-50-171-4.eu-west-1.compute.amazonaws.com" + ':' + "5432/" + "d29dfj6gvlgjar" + "?sslmode=require";
        String username = "fneyxnzadnmoxi";
        String password = "8b4cddfd1245d9894f9e80046aee0f25d97654fb66488fc67dcd2d21790a3edf";
        try (Connection connection = DriverManager.getConnection(dbUrl, username, password)) {
            LOGGER.info("Connected to PostgreSQL database!");
        } catch (SQLException e) {
            LOGGER.error("Connection failure.");
            e.printStackTrace();
        }
    }
}
