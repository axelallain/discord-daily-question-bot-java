import model.Freequestions;
import model.Premiumguilds;
import model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FreequestionsDaoImpl {

    String dbUrl = "jdbc:postgresql://" + "ec2-52-50-171-4.eu-west-1.compute.amazonaws.com" + ':' + "5432/" + "d29dfj6gvlgjar" + "?sslmode=require";
    String username = "fneyxnzadnmoxi";
    String password = "8b4cddfd1245d9894f9e80046aee0f25d97654fb66488fc67dcd2d21790a3edf";

    public List<Freequestions> findAll() throws SQLException {
        String query = "SELECT * FROM freequestions";
        Connection connection = DriverManager.getConnection(dbUrl, username, password);
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        List<Freequestions> freequestions = new ArrayList<>();

        while(rs.next()) {
            Freequestions freequestionsunit = new Freequestions();
            freequestionsunit.setContent(rs.getString("content"));
            freequestions.add(freequestionsunit);
        }

        connection.close();
        return freequestions;
    }

    public Freequestions findByContent(String content) throws SQLException {
        String query = "SELECT * FROM freequestions WHERE content=?";
        Connection connection = DriverManager.getConnection(dbUrl, username, password);
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, content);
        Freequestions freequestions = new Freequestions();
        ResultSet rs = ps.executeQuery();
        boolean check = false;

        while(rs.next()) {
            check = true;
            freequestions.setUsed(rs.getBoolean("used"));
            freequestions.setContent(rs.getString("content"));
        }

        connection.close();
        if (check == true) {
            return freequestions;
        } else {
            return null;
        }
    }

    public int updateByContent(boolean used, String content) throws SQLException {
        String query = "UPDATE freequestions SET used=?, content=? WHERE content=?";
        Connection connection = DriverManager.getConnection(dbUrl, username, password);
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setBoolean(1, used);
        ps.setString(2, content);
        ps.setString(3, content);
        int n = ps.executeUpdate();
        return n;
    }

    public List<Freequestions> findAllByUsed(boolean used) throws SQLException {
        String query = "SELECT * FROM freequestions WHERE used=?";
        Connection connection = DriverManager.getConnection(dbUrl, username, password);
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setBoolean(1, used);
        ResultSet rs = ps.executeQuery();
        List<Freequestions> freequestionsList = new ArrayList<>();

        while(rs.next()) {
            Freequestions freequestions = new Freequestions();
            freequestions.setUsed(rs.getBoolean("used"));
            freequestions.setContent(rs.getString("content"));
            freequestionsList.add(freequestions);
        }

        connection.close();
        return freequestionsList;
    }
}
