CREATE TABLE todos (
    id UUID NOT NULL CONSTRAINT todos_pkey PRIMARY KEY,
    name VARCHAR(512) NOT NULL,
    text TEXT NOT NULL,
    "timestamp" timestamp NOT NULL
);
