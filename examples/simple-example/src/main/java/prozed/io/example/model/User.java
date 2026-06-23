package prozed.io.example.model;

import prozed.io.jdbc.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public record User(int id, String name) {
    public static final RowMapper<User> MAPPER = rs -> new User(rs.getInt(1), rs.getString(2));
}
