-- Product catalog: categories and products.

CREATE TABLE categories (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE products (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    price       NUMERIC(10, 2) NOT NULL,
    stock       INTEGER        NOT NULL DEFAULT 0,
    category_id BIGINT         NOT NULL REFERENCES categories (id),
    created_at  TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_products_category_id ON products (category_id);

-- Seed a few categories so products can be created out of the box.
INSERT INTO categories (name) VALUES ('Electronics'), ('Books'), ('Clothing');
