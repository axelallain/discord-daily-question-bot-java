package dao;

import model.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDaoImpl {

    // TODO : Close connections
    Connection connection;

    {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/elliot", "postgres", "at22x");
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

    public int add(Question question) throws SQLException {
        String query = "INSERT INTO question(content) VALUES (?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, question.getContent());
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
