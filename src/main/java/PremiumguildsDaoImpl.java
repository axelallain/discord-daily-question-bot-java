import model.Premiumguilds;
import model.Question;
import model.SChannel;

import java.sql.*;

public class PremiumguildsDaoImpl {

    String dbUrl = "jdbc:postgresql://" + "ec2-52-50-171-4.eu-west-1.compute.amazonaws.com" + ':' + "5432/" + "d29dfj6gvlgjar" + "?sslmode=require";
    String username = "fneyxnzadnmoxi";
    String password = "8b4cddfd1245d9894f9e80046aee0f25d97654fb66488fc67dcd2d21790a3edf";

    public int add(Premiumguilds premiumguilds) throws SQLException {
        String query = "INSERT INTO premiumguilds(guildid, premium) VALUES (?, ?)";
        Connection connection = DriverManager.getConnection(dbUrl, username, password);
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, premiumguilds.getGuildid());
        ps.setBoolean(2, premiumguilds.isPremium());
        int n = ps.executeUpdate();
        connection.close();
        return n;
    }

    public Premiumguilds findByGuildid(Long guildid) throws SQLException {
        String query = "SELECT * FROM premiumguilds WHERE guildid=?";
        Connection connection = DriverManager.getConnection(dbUrl, username, password);
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, guildid);
        Premiumguilds premiumguilds = new Premiumguilds();
        ResultSet rs = ps.executeQuery();
        boolean check = false;

        while(rs.next()) {
            check = true;
            premiumguilds.setGuildid(rs.getLong("guildid"));
            premiumguilds.setPremium(rs.getBoolean("premium"));
        }

        connection.close();
        if (check == true) {
            return premiumguilds;
        } else {
            return null;
        }
    }

    public void delete(Long guildid) throws SQLException {
        String query = "DELETE FROM premiumguilds WHERE guildid=?";
        Connection connection = DriverManager.getConnection(dbUrl, username, password);
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, guildid);
        ps.executeUpdate();
        connection.close();
    }
}
