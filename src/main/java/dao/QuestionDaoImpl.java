package dao;

import model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDaoImpl {

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

    public List<Question> findAll() throws SQLException {
        String query = "SELECT * FROM question";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        List<Question> questions = new ArrayList<>();

        while(rs.next()) {
            Question question = new Question();
            question.setContent(rs.getString("content"));
            questions.add(question);
        }

        return questions;
    }

    public List<Question> findAllByGuildid(Long guildid) throws SQLException {
        String query = "SELECT * FROM question WHERE guildid=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, guildid);
        ResultSet rs = ps.executeQuery();
        List<Question> questions = new ArrayList<>();

        while(rs.next()) {
            Question question = new Question();
            question.setGuildid(rs.getLong("guildid"));
            question.setContent(rs.getString("content"));
            questions.add(question);
        }

        return questions;
    }

    public int add(Question question) throws SQLException {
        String query = "INSERT INTO question(guildid, content) VALUES (?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setLong(1, question.getGuildid());
        ps.setString(2, question.getContent());
        int n = ps.executeUpdate();
        return n;
    }

    public void delete(String content) throws SQLException {
        String query = "DELETE FROM question WHERE content=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, content);
        ps.executeUpdate();
    }
}
