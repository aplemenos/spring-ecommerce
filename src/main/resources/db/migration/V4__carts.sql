-- Shopping carts. One active cart per user; deleting a user removes their cart.

CREATE TABLE carts (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT      NOT NULL UNIQUE REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE cart_items (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cart_id    BIGINT  NOT NULL REFERENCES carts (id) ON DELETE CASCADE,
    product_id BIGINT  NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    quantity   INTEGER NOT NULL CHECK (quantity > 0),
    -- A product appears at most once per cart; adding again increments quantity.
    CONSTRAINT uq_cart_items_cart_product UNIQUE (cart_id, product_id)
);

CREATE INDEX idx_cart_items_cart_id ON cart_items (cart_id);
