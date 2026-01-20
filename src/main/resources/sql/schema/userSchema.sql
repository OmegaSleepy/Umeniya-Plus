CREATE TABLE if not exists users
(
    username TEXT PRIMARY KEY,
    password_hash TEXT,
    permittion_level TEXT NOT NULL,
    registrated_at TIMESTAMP,
    last_login TIMESTAMP
)