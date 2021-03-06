import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.SimpleTrigger;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import javax.security.auth.login.LoginException;
import java.net.URISyntaxException;

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

    public static void main(String[] args) throws LoginException, SchedulerException, URISyntaxException {
        Bot b = new Bot();
        MyJda.setDefaultJda(b.jda);
        MyWaiter.setDefaultWaiter(b.waiter);
        JdbcConfig.main(null);

        int hour = 11;
        int minutes = 00;
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
