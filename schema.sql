CREATE DATABASE THUCTAP
GO
USE THUCTAP
GO
CREATE TABLE users (
    id INT PRIMARY KEY IDENTITY(1,1),
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255)  NOT NULL
);

CREATE TABLE projects (
    id INT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(100) NOT NULL
);

CREATE TABLE tasks (
    id INT PRIMARY KEY IDENTITY(1,1),
    title VARCHAR(255) NOT NULL,
    deadline DATE NOT NULL DEFAULT GETDATE(),
    status VARCHAR(20) NOT NULL,
    assignee_id INT,
    project_id INT NOT NULL,

    FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);
GO

CREATE TABLE project_users (
	project_id INT,
	user_id INT,
	FOREIGN KEY (project_id) REFERENCES projects(id),
	FOREIGN KEY (user_id) REFERENCES users(id),
	PRIMARY KEY(project_id,user_id)
)

CREATE TABLE role (
    id INT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(50) NOT NULL
)

CREATE TABLE user_role (
    user_id INT,
    role_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES role(id),
    PRIMARY KEY(user_id,role_id)
)

ALTER TABLE tasks
ADD CONSTRAINT chk_status 
CHECK (status IN ('TODO','IN_PROGRESS','DONE'));
GO

CREATE INDEX idx_task_user ON tasks(assignee_id);
CREATE INDEX idx_task_project ON tasks(project_id);
CREATE INDEX idx_task_status ON tasks(status);
GO

-- Users
INSERT INTO users (full_name, email, password) VALUES
('Alice','alice@gmail.com','$2a$08$RzOmawEnHiDhr3NhJmn0xuc/nwDOIbvtr0PoEwGl1fJAmmDtdlxf2'),
('Bob','bob@gmail.com', '$2a$08$RzOmawEnHiDhr3NhJmn0xuc/nwDOIbvtr0PoEwGl1fJAmmDtdlxf2'),
('Charlie','charlie@gmail.com', '$2a$08$RzOmawEnHiDhr3NhJmn0xuc/nwDOIbvtr0PoEwGl1fJAmmDtdlxf2'),
('David','david@gmail.com', '$2a$08$RzOmawEnHiDhr3NhJmn0xuc/nwDOIbvtr0PoEwGl1fJAmmDtdlxf2'),
('Eve','eve@gmail.com', '$2a$08$RzOmawEnHiDhr3NhJmn0xuc/nwDOIbvtr0PoEwGl1fJAmmDtdlxf2');

-- Projects
INSERT INTO projects (name) VALUES
('Website'),
('Mobile App'),
('API System');

-- Tasks (30)
INSERT INTO tasks (title, status, assignee_id, project_id) VALUES
('Task 1','TODO',1,1),
('Task 2','IN_PROGRESS',2,1),
('Task 3','DONE',3,1),
('Task 4','TODO',1,2),
('Task 5','DONE',2,2),
('Task 6','IN_PROGRESS',3,2),
('Task 7','TODO',4,3),
('Task 8','DONE',5,3),
('Task 9','IN_PROGRESS',1,3),
('Task 10','TODO',2,1),
('Task 11','DONE',3,1),
('Task 12','TODO',4,2),
('Task 13','DONE',5,2),
('Task 14','IN_PROGRESS',1,3),
('Task 15','TODO',2,3),
('Task 16','DONE',3,1),
('Task 17','TODO',4,1),
('Task 18','IN_PROGRESS',5,2),
('Task 19','DONE',1,2),
('Task 20','TODO',2,3),
('Task 21','DONE',3,3),
('Task 22','TODO',4,1),
('Task 23','IN_PROGRESS',5,1),
('Task 24','DONE',1,2),
('Task 25','TODO',2,2),
('Task 26','DONE',3,3),
('Task 27','IN_PROGRESS',4,3),
('Task 28','TODO',5,1),
('Task 29','DONE',1,1),
('Task 30','IN_PROGRESS',2,2);

INSERT INTO project_users (project_id, user_id) VALUES
(1,1),(1,2),(1,3),
(2,1),(2,2),(2,4),
(3,3),(3,4),(3,5);

INSERT INTO role (name) VALUES
('MANAGER'),
('USER');

INSERT INTO user_role (user_id, role_id) VALUES
(1,1),
(2,2),
(3,2),
(4,1),
(5,2);
GO

SELECT * FROM tasks
WHERE assignee_id = 1;

SELECT * FROM tasks
WHERE project_id = 1;

SELECT * FROM tasks
WHERE status = 'DONE';

SELECT t.id, t.title, t.status, u.full_name, p.name AS project_name
FROM tasks t
LEFT JOIN users u ON t.assignee_id = u.id
JOIN projects p ON t.project_id = p.id
WHERE t.status = 'IN_PROGRESS';
