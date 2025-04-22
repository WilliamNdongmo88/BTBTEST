CREATE TABLE users(
    id bigint PRIMARY KEY auto_increment,
    name VARCHAR(255),
    email VARCHAR(255),
    actif BOOLEAN,
    mot_de_passe VARCHAR(255),
    create_day TIMESTAMP,
    role_id bigint
);