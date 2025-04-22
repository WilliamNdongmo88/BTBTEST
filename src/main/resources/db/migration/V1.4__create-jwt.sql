CREATE TABLE jwt(
    id bigint PRIMARY KEY auto_increment,
    valeur VARCHAR(255),
    desactive BOOLEAN,
    expire BOOLEAN,
    user_id bigint
);