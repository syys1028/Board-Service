-- ========================
-- Drop existing tables
-- ========================
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;

-- ========================
-- Create users table
-- ========================
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       pw VARCHAR(255) NOT NULL,
                       nickname VARCHAR(255) NOT NULL,
                       name VARCHAR(100) NOT NULL,
                       phone VARCHAR(20) NOT NULL,
                       age INT NOT NULL,
                       gender VARCHAR(10) NOT NULL CHECK (gender IN ('MALE', 'FEMALE')),
                       city VARCHAR(255) NOT NULL,
                       street VARCHAR(255) NOT NULL,
                       zipcode VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- Create posts table
-- ========================
CREATE TABLE posts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       contents TEXT NOT NULL,
                       category VARCHAR(20) NOT NULL,
                       image_url VARCHAR(255),
                       youtube_url VARCHAR(255),
                       is_achieved BOOLEAN DEFAULT FALSE,
                       likes INT DEFAULT 0,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       version BIGINT,
                       user_id BIGINT NOT NULL,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ========================
-- Create tags table
-- ========================
CREATE TABLE tags (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(100) NOT NULL UNIQUE
);

-- ========================
-- Create post_tags table (N:M relation between posts and tags)
-- ========================
CREATE TABLE post_tags (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           post_id BIGINT NOT NULL,
                           tag_id BIGINT NOT NULL,
                           FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
                           FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- ========================
-- Create comments table
-- ========================
CREATE TABLE comments (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          content TEXT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          user_id BIGINT NOT NULL,
                          post_id BIGINT NOT NULL,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- ========================
-- Create likes table (user ↔ post 좋아요, 복합키 사용)
-- ========================
CREATE TABLE likes (
                       user_id BIGINT NOT NULL,
                       post_id BIGINT NOT NULL,
                       liked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       PRIMARY KEY (user_id, post_id),
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                       FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);
