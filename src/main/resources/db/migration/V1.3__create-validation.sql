CREATE TABLE validation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    creation TIMESTAMP,
    expiration TIMESTAMP,
    activation TIMESTAMP,
    code VARCHAR(255),
    user_id BIGINT UNIQUE,
    validation_day DATE
);