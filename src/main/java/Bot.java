import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.Timer;
import java.util.TimerTask;

public class Bot {

    EventWaiter waiter = new EventWaiter();
    static JDA jda;

    private Bot() throws LoginException {
        jda = JDABuilder.createDefault(Config.get("TOKEN")).enableIntents(GatewayIntent.GUILD_MEMBERS).addEventListeners(new Listener(), waiter).build();
    }

    public static void main(String[] args) throws LoginException {
        new Bot();
        JdbcConfig.main(null);
        Timer timer = new Timer();
        TimerTask task = new SendDailyRandomQuestion(jda);
        timer.schedule(task, 0, 86400000);
    }
}
