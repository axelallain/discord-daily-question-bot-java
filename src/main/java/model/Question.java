package model;

public class Question {

    private Long guildid;
    private String content;

    public Question() {}

    public Question(Long guildid, String content) {
        this.guildid = guildid;
        this.content = content;
    }

    public Long getGuildid() {
        return guildid;
    }

    public void setGuildid(Long guildid) {
        this.guildid = guildid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Question{" +
                "guildid=" + guildid +
                ", content='" + content + '\'' +
                '}';
    }
}
