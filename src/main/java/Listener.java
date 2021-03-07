import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import model.Dailytime;
import model.Question;
import model.SChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
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
import java.util.ArrayList;
import java.util.List;

public class Listener extends ListenerAdapter {

    private final QuestionDaoImpl questionDaoImpl = new QuestionDaoImpl();
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private EventWaiter waiter;
    EmbedBuilder embedBuilder = new EmbedBuilder();
    private final SChannelDaoImpl sChannelDaoImpl = new SChannelDaoImpl();
    private final DailytimeDaoImpl dailytimeDaoImpl = new DailytimeDaoImpl();

    public String privateWelcomeMessage() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/privateWelcomeMessage.txt")));
    }

    public String helpCommandMessage() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/helpCommandMessage.txt")));
    }

    public String onGuildJoinMessage() throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/main/resources/onGuildJoinMessage.txt")));
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info(event.getJDA().getSelfUser().getName() + " has connected to Discord!");
    }

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        MessageChannel channel = event.getGuild().getDefaultChannel();
        try {
            channel.sendMessage(onGuildJoinMessage()).queue();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        channel.sendMessage("Hi " + event.getMember().getEffectiveName() + ", and welcome to our server!").queue();
        LOGGER.info("Public welcome message has been sent for " + event.getMember().getEffectiveName());

        /*

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

        */
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
            sChannel.setChannelid(event.getChannel().getIdLong());
            sChannel.setType("welcome");
            try {
                sChannelDaoImpl.add(sChannel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            MessageChannel channel = event.getChannel();
            channel.sendMessage(event.getChannel().getName() + " is the new welcome channel for this server.").queue();
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
            sChannel.setChannelid(event.getChannel().getIdLong());
            sChannel.setType("answers");
            try {
                sChannelDaoImpl.add(sChannel);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            MessageChannel channel = event.getChannel();
            channel.sendMessage(event.getChannel().getName() + " is the new answers channel for this server.").queue();
            LOGGER.info("A new answers channel has been set.");
        }

        if (contentRaw.startsWith(prefix + "help")) {
            MessageChannel channel = event.getChannel();
            try {
                channel.sendMessage(helpCommandMessage()).queue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (contentRaw.startsWith(prefix + "questions")) {
            List<Question> questions = new ArrayList<>();
            try {
                questions = questionDaoImpl.findAllByGuildid(event.getGuild().getIdLong());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            MessageChannel channel = event.getChannel();
            if (questions.isEmpty()) {
                channel.sendMessage("Your questions list is empty.").queue();
            } else {
                // TODO : Format the list for displaying.
                channel.sendMessage(questions.toString()).queue();
            }
        }

        if (contentRaw.startsWith(prefix + "dailytime")) {
            Dailytime dailytime = new Dailytime();

            try {
                if (dailytimeDaoImpl.findByGuildid(event.getGuild().getIdLong()) == null) {
                    dailytime = new Dailytime();
                } else {
                    dailytimeDaoImpl.delete(event.getGuild().getIdLong());
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            String rawTime = contentRaw.substring(11);
            String partsTime[] = rawTime.split(":");
            int hour = Integer.parseInt(partsTime[0]);
            int minutes = Integer.parseInt(partsTime[1]);
            dailytime.setGuildid(event.getGuild().getIdLong());
            dailytime.setHour(hour);
            dailytime.setMinutes(minutes);

            try {
                dailytimeDaoImpl.add(dailytime);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            MessageChannel channel = event.getChannel();
            channel.sendMessage("The time for sending questions is set to " + hour + ":" + minutes).queue();
            LOGGER.info("A new dailytime has been added.");
        }
    }
}
