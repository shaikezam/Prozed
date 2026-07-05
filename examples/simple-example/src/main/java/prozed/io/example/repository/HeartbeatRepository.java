package prozed.io.example.repository;

import prozed.io.core.api.di.Bean;
import prozed.io.core.api.di.Inject;
import prozed.io.jdbc.JdbcOperations;

@Bean
public class HeartbeatRepository {

    @Inject
    private JdbcOperations jdbcOperations;

    public void tick() {
        jdbcOperations.update("UPDATE heartbeat SET ticks = ticks + 1 WHERE id = 1");
    }

    public int getTicks() {
        return jdbcOperations.selectOne(
                "SELECT ticks FROM heartbeat WHERE id = 1",
                rs -> rs.getInt("ticks"));
    }
}
