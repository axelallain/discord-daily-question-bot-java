package dao;

import model.Dailytime;

import java.sql.*;

public class DailytimeDaoImpl {

    // TODO : Close connections
    Connection connection;

    {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/elliot", "postgres", "at22x");
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
