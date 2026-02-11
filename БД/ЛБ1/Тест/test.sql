CREATE TABLE IF NOT EXISTS aboba  (
                       id INTEGER,
                       name VARCHAR(200),
                       year_old SMALLINT
);

INSERT INTO aboba VALUES
                      (1, 'Кирилл', 18),
                      (2, 'Андрей', 19);

SELECT * FROM aboba;