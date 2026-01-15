CREATE TABLE if not exists blogs
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    tag TEXT NOT NULL,
    excerpt TEXT NOT NULL,
    content TEXT NOT NULL,
    creator_username TEXT NOT NULL,
    created_at TIMESTAMP
)