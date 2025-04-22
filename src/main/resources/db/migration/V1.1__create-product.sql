CREATE TABLE product(
    id bigint primary key auto_increment,
    name varchar(255),
    price varchar(255),
    description varchar(255),
    added_by bigint
);