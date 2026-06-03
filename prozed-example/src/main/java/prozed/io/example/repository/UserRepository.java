package prozed.io.example.repository;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.example.model.User;
import prozed.io.jdbc.JdbcOperations;
import prozed.io.jdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Bean
public class UserRepository {
    @Inject
    private JdbcOperations jdbcOperations;

    public User getUser(int id) {
        return jdbcOperations.selectOne(
                "select * from users where id = ?", new RowMapper<User>() {
                    @Override
                    public User map(ResultSet rs) throws SQLException {
                        return new User(rs.getInt(1), rs.getString(2));
                    }
                },
                id);
    }

    public int createUsers(User user) {
        return jdbcOperations.update("INSERT INTO users (name) VALUES (?)", user.name());
    }

    public void createUsers(List<User> users) {
        jdbcOperations.inTransaction((connection -> {
            for(User user : users) {
                jdbcOperations.update("INSERT INTO users (name) VALUES (?)", user.name());
            }
            return null;
        }));
    }
}
