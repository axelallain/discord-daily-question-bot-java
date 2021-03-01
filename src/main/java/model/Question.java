package model;

public class Question {

    private String content;

    public Question() {}

    public Question(String content) {
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
        return "Question{" +
                "content='" + content + '\'' +
                '}';
    }
}
