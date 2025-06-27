CREATE TABLE jwt (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    valeur TEXT,
    desactive BOOLEAN,
    expire BOOLEAN,
    refresh_token_id BIGINT UNIQUE,
    user_id BIGINT
);
