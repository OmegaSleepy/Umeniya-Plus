CREATE TABLE if not exists sessions (
    token TEXT PRIMARY KEY,
    username TEXT NOT NULL,
    expires_at INTEGER NOT NULL,
    FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE
);
