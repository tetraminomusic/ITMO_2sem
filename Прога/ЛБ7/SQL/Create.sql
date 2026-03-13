CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     login TEXT UNIQUE NOT NULL,
                                     password_hash TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS labworks (
                                        id SERIAL PRIMARY KEY,
                                        name TEXT NOT NULL CHECK (name <> ''),
                                        x BIGINT NOT NULL CHECK (x <= 162),
                                        y BIGINT NOT NULL,
                                        creation_date TIMESTAMP NOT NULL DEFAULT NOW(),
                                        minimal_point FLOAT NOT NULL CHECK (minimal_point > 0),
                                        description TEXT,
                                        tuned_in_works INT,
                                        difficulty TEXT NOT NULL,

                                        author_name TEXT,
                                        author_birthday TIMESTAMP,
                                        author_height FLOAT CHECK (author_height > 0),
                                        author_weight DOUBLE PRECISION CHECK (author_weight > 0),
                                        author_passport_id TEXT CHECK (length(author_passport_id) <= 50),
-- FK
                                        owner_id INT REFERENCES users(id) ON DELETE CASCADE
);