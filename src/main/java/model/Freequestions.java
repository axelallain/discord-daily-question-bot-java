package model;

public class Freequestions {

    private String content;
    private boolean used;

    public Freequestions() {
    }

    public Freequestions(String content, boolean used) {
        this.content = content;
        this.used = used;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public String toString() {
        return "Freequestions{" +
                "content='" + content + '\'' +
                ", used=" + used +
                '}';
    }
}
