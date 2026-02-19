CREATE TABLE IF NOT EXISTS user_extras (
    username TEXT PRIMARY KEY,
    flames INTEGER DEFAULT 0,
    style_profile TEXT,
    FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE
);