package model;

public class Premiumguilds {

    private Long guildid;
    private boolean premium = false;

    public Premiumguilds() {
    }

    public Premiumguilds(Long guildid, boolean premium) {
        this.guildid = guildid;
        this.premium = premium;
    }

    public Long getGuildid() {
        return guildid;
    }

    public void setGuildid(Long guildid) {
        this.guildid = guildid;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    @Override
    public String toString() {
        return "Premiumguilds{" +
                "guildid=" + guildid +
                ", premium=" + premium +
                '}';
    }
}
