CREATE TABLE issues (
    id INT AUTO_INCREMENT PRIMARY KEY,
    issue_key VARCHAR(30) NOT NULL UNIQUE,
    project_key VARCHAR(20) NOT NULL,
    type VARCHAR(20) NOT NULL,
    summary VARCHAR(300) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(20) NOT NULL DEFAULT 'TODO',
    parent_key VARCHAR(30),
    assignee VARCHAR(100),
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO issues (issue_key, project_key, type, summary, description, status, parent_key, assignee, priority) VALUES
('PROZ-1', 'PROZ', 'EPIC',    'Authentication overhaul',   'Revamp authentication across all services.',     'IN_PROGRESS', NULL,     'alice', 'HIGH'),
('PROZ-2', 'PROZ', 'STORY',   'OAuth2 login',              'Add OAuth2 provider login support.',             'TODO',        'PROZ-1', 'bob',   'MEDIUM'),
('PROZ-3', 'PROZ', 'TASK',    'Token refresh endpoint',    'Implement refresh token rotation endpoint.',     'TODO',        'PROZ-2', 'carol', 'MEDIUM'),
('PROZ-4', 'PROZ', 'SUBTASK', 'Write refresh tests',       'Unit tests for the refresh token flow.',         'TODO',        'PROZ-3', 'carol', 'LOW'),
('PROZ-5', 'PROZ', 'BUG',     'Login 500 on empty body',   'POST /login throws NPE on an empty payload.',    'TODO',        NULL,     'bob',   'HIGH'),
('WEB-1',  'WEB',  'EPIC',    'New marketing site',        'Rebuild the public marketing site.',             'TODO',        NULL,     'dave',  'MEDIUM'),
('WEB-2',  'WEB',  'STORY',   'Landing page',              'Design and build the new landing page.',         'IN_PROGRESS', 'WEB-1',  'erin',  'MEDIUM');
