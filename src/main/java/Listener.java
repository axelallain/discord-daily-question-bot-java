import dao.QuestionDaoImpl;
import model.Question;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class Listener extends ListenerAdapter {

    private final QuestionDaoImpl questionDaoImpl = new QuestionDaoImpl();
    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info(event.getJDA().getSelfUser().getName() + " has connected to Discord!");
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String prefix = Config.get("PREFIX");
        String contentRaw = event.getMessage().getContentRaw();

        if (contentRaw.startsWith(prefix + "add")) {
            // add question dao from contentRaw
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
}
