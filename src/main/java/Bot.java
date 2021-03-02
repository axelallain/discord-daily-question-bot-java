import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Bot {

    EventWaiter waiter = new EventWaiter();

    public Bot() throws LoginException {
        JDABuilder.createDefault(Config.get("TOKEN")).enableIntents(GatewayIntent.GUILD_MEMBERS).addEventListeners(new Listener(), waiter).build();
    }

    public static void main(String[] args) throws LoginException {
        new Bot();
        JdbcConfig.main(null);
    }
}
