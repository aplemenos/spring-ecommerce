-- Orders placed from a cart. Order items snapshot the product name and price at
-- purchase time, so historical orders stay correct even if the product later
-- changes price or is deleted.

CREATE TABLE orders (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id      BIGINT         NOT NULL REFERENCES users (id),
    status       VARCHAR(32)    NOT NULL DEFAULT 'PENDING',
    total_amount NUMERIC(12, 2) NOT NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_orders_user_id ON orders (user_id);

CREATE TABLE order_items (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id     BIGINT         NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    -- Keep a soft link to the product for reporting, but survive its deletion.
    product_id   BIGINT         REFERENCES products (id) ON DELETE SET NULL,
    product_name VARCHAR(255)   NOT NULL,   -- snapshot
    unit_price   NUMERIC(10, 2) NOT NULL,   -- snapshot
    quantity     INTEGER        NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
