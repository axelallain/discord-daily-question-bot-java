package dao;

import model.Question;
import model.SChannel;

import java.sql.*;

public class SChannelDaoImpl {

    // TODO : Close connections
    Connection connection;

    {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/elliot", "postgres", "at22x");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int add(SChannel sChannel) throws SQLException {
        String query = "INSERT INTO schannel(guildid, channelid, type) VALUES (?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, sChannel.getGuildid());
        ps.setLong(2, sChannel.getChannelid());
        ps.setString(3, sChannel.getType());
        int n = ps.executeUpdate();
        return n;
    }

    public SChannel findByGuildidAndType(Long guildid, String type) throws SQLException {
        String query = "SELECT * FROM schannel WHERE guildid=? AND type=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, guildid);
        ps.setString(2, type);
        SChannel sChannel = new SChannel();
        ResultSet rs = ps.executeQuery();
        boolean check = false;

        while(rs.next()) {
            check = true;
            sChannel.setGuildid(rs.getLong("guildid"));
            sChannel.setChannelid(rs.getLong("channelid"));
            sChannel.setType(rs.getString("type"));
        }

        if (check == true) {
            return sChannel;
        } else {
            return null;
        }
    }

    public void delete(Long guildid, String type) throws SQLException {
        String query = "DELETE FROM schannel WHERE guildid=? AND type=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, guildid);
        ps.setString(2, type);
        ps.executeUpdate();
    }
}
