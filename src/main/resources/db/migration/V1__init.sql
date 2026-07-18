-- Baseline schema for the e-commerce store.
-- Later milestones add products, carts, orders and payments in new V*.sql files.

CREATE TABLE users (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(32)  NOT NULL DEFAULT 'USER',
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);
