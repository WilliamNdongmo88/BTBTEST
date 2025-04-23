CREATE TABLE refresh_token(
    id bigint PRIMARY KEY auto_increment,
    valeur VARCHAR(255),
    creation TIMESTAMP,
    expiration TIMESTAMP,
    expire BOOLEAN
);