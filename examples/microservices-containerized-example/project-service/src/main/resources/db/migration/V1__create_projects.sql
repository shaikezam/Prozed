CREATE TABLE projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    project_key VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO projects (project_key, name, description) VALUES
('PROZ', 'Prozed Platform', 'Core framework and platform engineering work.'),
('WEB', 'Website Revamp', 'Public website redesign and content migration.');
