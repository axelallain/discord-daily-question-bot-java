package dao;

import model.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuestionDaoImpl implements QuestionDao {

    Connection connection = DatabaseConnection.getConnection();

    @Override
    public List<Question> findAll() throws SQLException {
        String query = "SELECT * FROM question";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        List<Question> questions = new ArrayList<>();

        while(rs.next()) {
            Question question = new Question();
            question.setId(rs.getInt("id"));
            question.setContent(rs.getString("content"));
            questions.add(question);
        }

        return questions;
    }
}
