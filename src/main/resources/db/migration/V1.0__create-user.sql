CREATE TABLE users(
    id bigint primary key auto_increment,
    name varchar(255),
    email varchar(255),
    mot_de_passe varchar(255),
    role_id bigint
);