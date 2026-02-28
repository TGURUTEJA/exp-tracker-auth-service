DROP TABLE IF EXISTS userCred CASCADE;
DROP TABLE IF EXISTS user_otp CASCADE;

CREATE TABLE IF NOT EXISTS userCred (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    role VARCHAR(50) DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS user_otp (
    id BIGINT PRIMARY KEY,                            -- Shared PK & FK to userCred
    otp VARCHAR(12) NOT NULL,
    expire_time TIMESTAMP NOT NULL,
    count INT DEFAULT 0,
    reset_time TIMESTAMP NOT NULL,
    Password_reset_time TIMESTAMP,
    CONSTRAINT fk_usercred_otp
        FOREIGN KEY (id)
        REFERENCES userCred(id)
        ON DELETE CASCADE
);
