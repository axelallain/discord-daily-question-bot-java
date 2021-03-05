import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dao.QuestionDaoImpl;
import dao.SChannelDaoImpl;
import model.Question;
import model.SChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SendDailyRandomQuestion implements Job {

    private JDA jda;
    private final QuestionDaoImpl questionDaoImpl = new QuestionDaoImpl();
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private EventWaiter waiter;
    private final SChannelDaoImpl sChannelDaoImpl = new SChannelDaoImpl();

    public SendDailyRandomQuestion() {
        jda = MyJda.getJda();
        waiter = MyWaiter.getWaiter();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            for (Guild guild : jda.getGuilds()) {
                Random random = new Random();
                Question randomQuestion = questionDaoImpl.findAllByGuildid(guild.getIdLong()).get(random.nextInt(questionDaoImpl.findAll().size()));
                String question2 = randomQuestion.getContent();
                SChannel sChannel = sChannelDaoImpl.findByGuildidAndType(guild.getIdLong(), "answers");
                final Long answersChannelId = sChannel.getChannelid();
                for (Member member : guild.getMembers()) {

                        if (member.getUser().isBot()) {
                            continue;
                        }

                        String question1;
                        if (DayOfWeek.from(LocalDate.now()) == DayOfWeek.SATURDAY || DayOfWeek.from(LocalDate.now()) == DayOfWeek.SUNDAY) {
                            return;
                        } else if (DayOfWeek.from(LocalDate.now()) == DayOfWeek.MONDAY) {
                            question1 = "Hello " + member.getEffectiveName() + ", encore une belle journée à bord du Bubble de JU. Qu'as-tu fait ce week-end ?";
                        } else {
                            question1 = "Hello " + member.getEffectiveName() + ", encore une belle journée à bord du Bubble de JU. Comment ça va aujourd'hui ?";
                        }

                        String finalQuestion1 = question1;
                        member.getUser().openPrivateChannel().queue(privateChannel -> { // this is a lambda expression
                            // the channel is the successful response
                            privateChannel.sendMessage(finalQuestion1).queue();
                            LOGGER.info("First question sent to " + member.getEffectiveName());
                        });

                        waiter.waitForEvent(PrivateMessageReceivedEvent.class,
                                (event) -> event.getMessage().getAuthor().getIdLong() == member.getUser().getIdLong(),
                                (event) -> {
                                        event.getChannel().sendMessage(question2).queue();
                                        LOGGER.info("Second question sent to " + member.getEffectiveName());

                                        waiter.waitForEvent(PrivateMessageReceivedEvent.class,
                                                (event2) -> event2.getMessage().getAuthor().getIdLong() == member.getUser().getIdLong(),
                                                (event2) -> {
                                                    LOGGER.info(member.getEffectiveName() + " answered questions.");

                                                    EmbedBuilder embedBuilder = new EmbedBuilder();
                                                    embedBuilder.setTitle("\uD83D\uDD14 " + member.getEffectiveName() + " a répondu aux questions du jour :", null);
                                                    embedBuilder.setColor(new Color(0x97DDDD));
                                                    embedBuilder.addField(question1, event.getMessage().getContentRaw(), false);
                                                    embedBuilder.addField(question2, event2.getMessage().getContentRaw(), false);
                                                    embedBuilder.setThumbnail(member.getUser().getAvatarUrl());
                                                    MessageChannel channel = jda.getTextChannelById(answersChannelId);
                                                    channel.sendMessage(embedBuilder.build()).queue();
                                                    LOGGER.info("Answers have been sent.");
                                                },
                                                12, TimeUnit.HOURS,
                                                () -> member.getUser().openPrivateChannel().queue(privateChannel -> {
                                                    privateChannel.sendMessage("Tu n'as pas répondu à la seconde question. Je reviens demain matin !").queue();
                                                    LOGGER.info(member.getEffectiveName() + " did not answer questions.");
                                                })
                                        );
                                },
                                12, TimeUnit.HOURS,
                                () -> member.getUser().openPrivateChannel().queue(privateChannel -> {
                                    privateChannel.sendMessage("Tu n'as pas répondu à la première question. Je reviens demain matin !").queue();
                                    LOGGER.info(member.getEffectiveName() + " did not answer questions.");
                                })
                        );



                }
                questionDaoImpl.delete(randomQuestion.getContent());
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
