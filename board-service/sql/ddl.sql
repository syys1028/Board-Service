CREATE TABLE addresses (
                           address_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           city VARCHAR(255) NOT NULL,
                           street VARCHAR(255) NOT NULL,
                           zipcode VARCHAR(50) NOT NULL
);

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       pw VARCHAR(255) NOT NULL,
                       age INT NOT NULL,
                       gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
                       createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       address_id BIGINT NOT NULL,
                       FOREIGN KEY (address_id) REFERENCES addresses (address_id) ON DELETE CASCADE
);

CREATE TABLE posts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       contents TEXT NOT NULL,
                       user_id BIGINT NOT NULL,
                       createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       likes INT DEFAULT 0,
                       FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
