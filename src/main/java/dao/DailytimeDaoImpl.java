package dao;

import model.Dailytime;

import java.sql.*;

public class DailytimeDaoImpl {

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

    public Dailytime findByGuildid(Long guildid) throws SQLException {
        String query = "SELECT * FROM dailytime WHERE guildid=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, guildid);
        Dailytime dailytime = new Dailytime();
        ResultSet rs = ps.executeQuery();
        boolean check = false;

        while(rs.next()) {
            check = true;
            dailytime.setGuildid(rs.getLong("guildid"));
            dailytime.setHour(rs.getInt("hour"));
            dailytime.setMinutes(rs.getInt("minutes"));
        }

        if (check == true) {
            return dailytime;
        } else {
            return null;
        }
    }

    public int add(Dailytime dailytime) throws SQLException {
        String query = "INSERT INTO dailytime(guildid, hour, minutes) VALUES(?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, dailytime.getGuildid());
        ps.setInt(2, dailytime.getHour());
        ps.setInt(3, dailytime.getMinutes());
        int n = ps.executeUpdate();
        return n;
    }

    public void delete(Long guildid) throws SQLException {
        String query = "DELETE FROM dailytime WHERE guildid=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, guildid);
        ps.executeUpdate();
    }
}
