import net.dv8tion.jda.api.JDA;

public class MyJda {

    private static JDA jda;

    public static void setDefaultJda(JDA jda) {
        MyJda.jda = jda;
    }

    public static JDA getJda() {
        return jda;
    }
}
