import model.Freequestions;
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
}
