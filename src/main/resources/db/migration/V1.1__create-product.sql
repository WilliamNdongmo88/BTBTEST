CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price DOUBLE,
    description TEXT,
    files_id BIGINT,
    added_by BIGINT
);