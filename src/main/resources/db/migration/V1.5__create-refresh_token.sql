CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    valeur VARCHAR(500),
    creation TIMESTAMP,
    expiration TIMESTAMP,
    expire BOOLEAN
);