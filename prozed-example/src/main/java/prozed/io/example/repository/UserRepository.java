package prozed.io.example.repository;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.example.model.User;
import prozed.io.jdbc.JdbcOperations;

import java.util.List;

import static prozed.io.example.model.User.MAPPER;

@Bean
public class UserRepository {

    @Inject
    private JdbcOperations jdbcOperations;

    public User getUser(int id) {
        return jdbcOperations.selectOne(
                "select * from users where id = ?", MAPPER,
                id);
    }

    public List<User> searchByName(String name, int limit) {
        return jdbcOperations.select(
                "select * from users where name like ? order by id limit ?", MAPPER,
                "%" + name + "%", limit);
    }

    public int createUser(User user) {
        return jdbcOperations.update("INSERT INTO users (name) VALUES (?)", user.name());
    }

    public void createUsers(List<User> users) {
        jdbcOperations.inTransaction((connection -> {
            for (User user : users) {
                jdbcOperations.update("INSERT INTO users (name) VALUES (?)", user.name());
            }
            return null;
        }));
    }

    public void deleteUser(int id) {
        jdbcOperations.update("DELETE FROM users where id = ?", id);
    }

    public void updateUser(User user) {
        jdbcOperations.inTransaction(connection -> {
            User exists = jdbcOperations.selectOne("select * from users where id = ?", MAPPER, user.id());
            if (exists == null) {
                throw new IllegalStateException("User %s not found".formatted(user));
            }
            jdbcOperations.update("update users set name = ? where id = ?", user.name(), user.id());
            return null;
        });
    }
}
