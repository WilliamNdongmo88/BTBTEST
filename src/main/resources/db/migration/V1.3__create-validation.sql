CREATE TABLE validation(
    id bigint PRIMARY KEY auto_increment,
    creation TIMESTAMP,
    expiration TIMESTAMP,
    activation TIMESTAMP,
    code VARCHAR(25),
    user_id BIGINT,
    validation_day TIMESTAMP
);