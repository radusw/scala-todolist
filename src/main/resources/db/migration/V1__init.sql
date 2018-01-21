CREATE TABLE todos (
    id UUID NOT NULL CONSTRAINT todos_pkey PRIMARY KEY,
    name VARCHAR(512) NOT NULL,
    text TEXT NOT NULL
);

INSERT INTO todos("id","name","text")
VALUES
(E'49cb2a20-55ca-4a8b-aab5-1c065dc101ab',E'Radu',E'Hello lorem ipsum');