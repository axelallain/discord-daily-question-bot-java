import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import model.Freequestions;
import model.Question;
import model.SChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SendDailyRandomQuestion implements Job {

    private JDA jda;
    private final QuestionDaoImpl questionDaoImpl = new QuestionDaoImpl();
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private EventWaiter waiter;
    private final SChannelDaoImpl sChannelDaoImpl = new SChannelDaoImpl();
    private final PremiumguildsDaoImpl premiumguildsDaoImpl = new PremiumguildsDaoImpl();
    private final FreequestionsDaoImpl freequestionsDaoImpl = new FreequestionsDaoImpl();

    public SendDailyRandomQuestion() {
        jda = MyJda.getJda();
        waiter = MyWaiter.getWaiter();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("execute SendDailyRandomQuestion..");
        try {
            Random random = new Random();

            List<Freequestions> freequestionsListCheckUsed = freequestionsDaoImpl.findAll();
            List<Freequestions> freequestionsListUsed = freequestionsDaoImpl.findAllByUsed(true);

            if (freequestionsListUsed.size() == freequestionsListCheckUsed.size()) {
                for (Freequestions freequestions : freequestionsListUsed) {
                    freequestionsDaoImpl.updateByContent(false, freequestions.getContent());
                }
            }

            List<Freequestions> freequestionsList = freequestionsDaoImpl.findAllByUsed(false);

            Freequestions randomFreeQuestion = null;

            if (freequestionsList.isEmpty()) {
                LOGGER.error("Free questions list is empty.");
            } else {
                randomFreeQuestion = freequestionsList.get(random.nextInt(freequestionsList.size()));
            }

            for (Guild guild : jda.getGuilds()) {

                /*

                // TODO : Remove this condition for production purpose.
                if (guild.getOwnerIdLong() != 219721599346016266L) {
                    continue;
                }

                 */

                List randomQuestionList;
                String question2;
                String randomQuestionContent;

                if (!premiumguildsDaoImpl.findByGuildid(guild.getIdLong()).isPremium()) {
                    Freequestions randomQuestion = randomFreeQuestion;
                    randomQuestionContent = randomQuestion.getContent();
                    question2 = randomQuestionContent;
                } else {
                    randomQuestionList = questionDaoImpl.findAllByGuildid(guild.getIdLong());

                    if(randomQuestionList.isEmpty()) {
                        Freequestions randomQuestionEmpty = randomFreeQuestion;
                        randomQuestionContent = randomQuestionEmpty.getContent();
                        question2 = randomQuestionContent;
                    } else {
                        Question randomQuestion = (Question) randomQuestionList.get(random.nextInt(randomQuestionList.size()));
                        randomQuestionContent = randomQuestion.getContent();
                        question2 = randomQuestionContent;
                    }
                }

                SChannel sChannel = sChannelDaoImpl.findByGuildidAndType(guild.getIdLong(), "answers");
                final Long answersChannelId = sChannel.getChannelid();
                for (Member member : guild.getMembers()) {

                        if (member.getUser().isBot()) {
                            continue;
                        }

                        String question1;
                        if (DayOfWeek.from(LocalDate.now()) == DayOfWeek.MONDAY) {
                            question1 = "Hello " + member.getEffectiveName() + ", what did you do this weekend?";
                        } else {
                            question1 = "Hello " + member.getEffectiveName() + ", how are you today?";
                        }

                        String finalQuestion1 = question1;
                        member.getUser().openPrivateChannel().queue(privateChannel -> { // this is a lambda expression
                            // the channel is the successful response
                            privateChannel.sendMessage(finalQuestion1).queue();
                            LOGGER.info("First question sent to " + member.getEffectiveName());
                        });

                    String finalQuestion = question2;
                    waiter.waitForEvent(PrivateMessageReceivedEvent.class,
                                (event) -> event.getMessage().getAuthor().getIdLong() == member.getUser().getIdLong(),
                                (event) -> {
                                        event.getChannel().sendMessage(finalQuestion).queue();
                                        LOGGER.info("Second question sent to " + member.getEffectiveName());

                                        waiter.waitForEvent(PrivateMessageReceivedEvent.class,
                                                (event2) -> event2.getMessage().getAuthor().getIdLong() == member.getUser().getIdLong(),
                                                (event2) -> {
                                                    event.getChannel().sendMessage("If you don't want me to ask you any more questions, block me!").queue();
                                                    LOGGER.info(member.getEffectiveName() + " answered questions.");

                                                    EmbedBuilder embedBuilder = new EmbedBuilder();
                                                    embedBuilder.setTitle("\uD83D\uDD14 " + member.getEffectiveName() + " answered the questions of the day :", null);
                                                    embedBuilder.setColor(new Color(0x97DDDD));

                                                    if (event.getMessage().getAttachments().isEmpty()) {
                                                        embedBuilder.addField(question1, event.getMessage().getContentRaw(), false);
                                                    } else {
                                                        for (Message.Attachment attachment : event.getMessage().getAttachments()) {
                                                            embedBuilder.addField(question1, attachment.getUrl(), false);
                                                        }
                                                    }

                                                    if (event2.getMessage().getAttachments().isEmpty()) {
                                                        embedBuilder.addField(finalQuestion, event2.getMessage().getContentRaw(), false);
                                                    } else {
                                                        for (Message.Attachment attachment2 : event2.getMessage().getAttachments()) {
                                                            embedBuilder.addField(finalQuestion, attachment2.getUrl(), false);
                                                        }
                                                    }

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
                if (freequestionsDaoImpl.findByContent(question2) != null) {
                    freequestionsDaoImpl.updateByContent(true, question2);
                }
                // questionDaoImpl = question premium donc cette méthode ne supprime pas les questions gratuites.
                questionDaoImpl.delete(question2);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
