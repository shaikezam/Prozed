package prozed.io.example.repository;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.example.model.User;
import prozed.io.jdbc.JdbcOperations;

@Bean
public class UserRepository {
    @Inject
    private JdbcOperations jdbcOperations;

    public User getuser(int id) {
        return jdbcOperations.selectOne(
                "select * from users where id = ?",
                rs -> new User(rs.getInt(0), rs.getString(1)),
                id);
    }
}
