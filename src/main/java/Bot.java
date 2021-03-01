import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Bot {

    private Bot() throws LoginException {
        JDABuilder.createDefault(Config.get("TOKEN")).enableIntents(GatewayIntent.GUILD_MEMBERS).addEventListeners(new Listener()).build();
    }

    public static void main(String[] args) throws LoginException {
        new Bot();
        JdbcConfig.main(null);
    }
}
