import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Bot {

    private Bot() throws LoginException {
        JDABuilder.createDefault(Config.get("TOKEN")).addEventListeners(new Listener()).build();
    }

    public static void main(String[] args) throws LoginException {
        new Bot();
        JdbcConfig.main(null);
    }
}
