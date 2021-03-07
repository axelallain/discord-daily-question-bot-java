import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import javax.security.auth.login.LoginException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class Bot {

    JDA jda;
    EventWaiter waiter = new EventWaiter();

    private Bot() throws LoginException {
        jda = JDABuilder.createDefault(Config.get("TOKEN"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Listener(), waiter)
                .build();
    }

    public static void main(String[] args) throws LoginException, URISyntaxException, SchedulerException, SQLException {
        Bot b = new Bot();
        MyJda.setDefaultJda(b.jda);
        MyWaiter.setDefaultWaiter(b.waiter);
        JdbcConfig.main(null);

        int hour = 10;
        int minutes = 0;
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        JobDetail job = newJob(SendDailyRandomQuestion.class).withIdentity("senddailyrandomquestion").build();
        CronTrigger trigger = newTrigger()
                .withIdentity("trigger1")
                .withSchedule(dailyAtHourAndMinute(hour, minutes))
                .forJob("senddailyrandomquestion")
                .build();
        scheduler.scheduleJob(job, trigger);
    }
}
