package model;

public class Dailytime {

    private Long guildid;
    private int hour;
    private int minutes;

    public Dailytime() {
    }

    public Dailytime(Long guildid, int hour, int minutes) {
        this.guildid = guildid;
        this.hour = hour;
        this.minutes = minutes;
    }

    public Long getGuildid() {
        return guildid;
    }

    public void setGuildid(Long guildid) {
        this.guildid = guildid;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        return "Dailytime{" +
                "guildid=" + guildid +
                ", hour=" + hour +
                ", minutes=" + minutes +
                '}';
    }
}
