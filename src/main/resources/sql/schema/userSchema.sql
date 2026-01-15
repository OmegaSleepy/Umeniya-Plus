CREATE TABLE if not exists USERS
(
    username TEXT PRIMARY KEY,
    password_hash TEXT,
    role TEXT NOT NULL,
    registrated_at TIMESTAMP,
    last_login TIMESTAMP
)