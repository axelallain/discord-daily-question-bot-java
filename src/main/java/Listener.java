import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import dao.QuestionDaoImpl;
import dao.SChannelDaoImpl;
import model.Question;
import model.SChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
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
    private final SChannelDaoImpl sChannelDaoImpl = new SChannelDaoImpl();

    public String privateWelcomeMessage() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/privateWelcomeMessage.txt")));
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info(event.getJDA().getSelfUser().getName() + " has connected to Discord!");
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        SChannel sChannel = new SChannel();
        try {
            sChannel = sChannelDaoImpl.findByGuildidAndType(event.getGuild().getIdLong(), "welcome");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        MessageChannel channel = event.getJDA().getTextChannelById(sChannel.getChannelid());
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
            question.setGuildid(event.getMessage().getGuild().getIdLong());
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

        if (contentRaw.startsWith(prefix + "welcome")) {
            SChannel sChannel = new SChannel();
            try {
                if (sChannelDaoImpl.findByGuildidAndType(event.getGuild().getIdLong(), "welcome") == null) {
                    sChannel = new SChannel();
                } else {
                    sChannelDaoImpl.delete(event.getGuild().getIdLong(), "welcome");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            sChannel.setGuildid(event.getMessage().getGuild().getIdLong());
            sChannel.setChannelid(Long.parseLong(contentRaw.substring(9)));
            sChannel.setType("welcome");
            try {
                sChannelDaoImpl.add(sChannel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            MessageChannel channel = event.getChannel();
            channel.sendMessage(contentRaw.substring(9) + " is the new welcome channel for this server.").queue();
            LOGGER.info("A new welcome channel has been set.");
        }

        if (contentRaw.startsWith(prefix + "answers")) {
            SChannel sChannel = new SChannel();
            try {
                if (sChannelDaoImpl.findByGuildidAndType(event.getGuild().getIdLong(), "answers") == null) {
                    sChannel = new SChannel();
                } else {
                    sChannelDaoImpl.delete(event.getGuild().getIdLong(), "answers");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            sChannel.setGuildid(event.getMessage().getGuild().getIdLong());
            sChannel.setChannelid(Long.parseLong(contentRaw.substring(9)));
            sChannel.setType("answers");
            try {
                sChannelDaoImpl.add(sChannel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            MessageChannel channel = event.getChannel();
            channel.sendMessage(contentRaw.substring(9) + " is the new answers channel for this server.").queue();
            LOGGER.info("A new answers channel has been set.");
        }
    }
}
