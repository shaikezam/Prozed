CREATE TABLE heartbeat
(
    id    INT PRIMARY KEY,
    ticks INT NOT NULL
);

INSERT INTO heartbeat (id, ticks) VALUES (1, 0);
