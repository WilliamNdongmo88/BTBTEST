CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255),
    actif BOOLEAN,
    mot_de_passe VARCHAR(255),
    role_id BIGINT,
    create_day TIMESTAMP
);
