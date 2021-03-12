package model;

public class Freequestions {

    private String content;

    public Freequestions() {
    }

    public Freequestions(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Freequestions{" +
                "content='" + content + '\'' +
                '}';
    }
}
