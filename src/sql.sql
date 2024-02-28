DROP TABLE IF EXISTS files;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL
) ENGINE=INNODB;

CREATE TABLE files (
                       users_id INT NOT NULL,
                       FOREIGN KEY (users_id)
                           REFERENCES users(user_id)
                           ON DELETE CASCADE,
                       file_name VARCHAR(255) NOT NULL UNIQUE,
                       content MEDIUMBLOB,
                       size BIGINT NOT NULL,
                       added_in TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=INNODB;