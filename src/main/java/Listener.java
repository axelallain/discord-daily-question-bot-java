import dao.QuestionDaoImpl;
import model.Question;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class Listener extends ListenerAdapter {

    private JDA jda;
    private final QuestionDaoImpl questionDaoImpl = new QuestionDaoImpl();
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    // TODO : Add new member name in the private welcome message.
    public String privateWelcomeMessage() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/privateWelcomeMessage.txt")));
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info(event.getJDA().getSelfUser().getName() + " has connected to Discord!");
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        MessageChannel channel = event.getJDA().getTextChannelById(Config.get("WELCOME_CHANNEL_ID"));
        channel.sendMessage(event.getMember().getEffectiveName() + " vient de monter à bord du Bubble de JU. Bienvenue à toi matelot !").queue();
        LOGGER.info("Public welcome message has been sent for " + event.getMember().getEffectiveName());

        // Multi lines String
        event.getMember().getUser().openPrivateChannel().queue(privateChannel -> { // this is a lambda expression
            // the channel is the successful response
            try {
                privateChannel.sendMessage(privateWelcomeMessage()).queue();
                LOGGER.info("Private welcome message has been sent to " + event.getMember().getEffectiveName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String prefix = Config.get("PREFIX");
        String contentRaw = event.getMessage().getContentRaw();

        if (contentRaw.startsWith(prefix + "add")) {
            Question question = new Question();
            question.setContent(contentRaw.substring(4));
            try {
                questionDaoImpl.add(question);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Your question has been added.").queue();
            LOGGER.info("A new question has been added.");
        }
    }

    // TODO : Schedule daily
    public void sendDailyRandomQuestion() throws SQLException {
        Random random = new Random();
        Question randomQuestion = questionDaoImpl.findAll().get(random.nextInt(questionDaoImpl.findAll().size()));
        String question2 = randomQuestion.getContent();
        for (Guild guild : jda.getGuilds()) {
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

                // TODO : wait for reply
            }
        }
    }
}
