package model;

public class SChannel {

    private Long guildid;
    private Long channelid;
    private String type;

    public SChannel() {
    }

    public SChannel(Long guildid, Long channelid, String type) {
        this.guildid = guildid;
        this.channelid = channelid;
        this.type = type;
    }

    public Long getGuildid() {
        return guildid;
    }

    public void setGuildid(Long guildid) {
        this.guildid = guildid;
    }

    public Long getChannelid() {
        return channelid;
    }

    public void setChannelid(Long channelid) {
        this.channelid = channelid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SChannel{" +
                "guildid=" + guildid +
                ", channelid=" + channelid +
                ", type='" + type + '\'' +
                '}';
    }
}
