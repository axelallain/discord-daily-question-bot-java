package dao;

import model.Question;

import java.sql.SQLException;
import java.util.List;

public interface QuestionDao {

    List<Question> findAll() throws SQLException;
}
