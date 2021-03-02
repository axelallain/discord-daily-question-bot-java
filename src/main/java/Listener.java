import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dao.QuestionDaoImpl;
import model.Question;
import net.dv8tion.jda.api.EmbedBuilder;
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
import java.util.Timer;
import java.util.TimerTask;

public class Listener extends ListenerAdapter {

    private final QuestionDaoImpl questionDaoImpl = new QuestionDaoImpl();
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private EventWaiter waiter;
    EmbedBuilder embedBuilder = new EmbedBuilder();

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
            question.setContent(contentRaw.substring(5));
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
}
