package dao;

import model.Question;
import model.SChannel;

import java.sql.*;

public class SChannelDaoImpl {

    // TODO : Close connections
    Connection connection;

    {
        String dbUrl = "jdbc:postgresql://" + "ec2-52-50-171-4.eu-west-1.compute.amazonaws.com" + ':' + "5432/" + "d29dfj6gvlgjar" + "?sslmode=require";
        String username = "fneyxnzadnmoxi";
        String password = "8b4cddfd1245d9894f9e80046aee0f25d97654fb66488fc67dcd2d21790a3edf";
        try {
            connection = DriverManager.getConnection(dbUrl, username, password);
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
