-- Payment attempts against orders. One order may have several payment rows over
-- time (e.g. a failed attempt then a successful one); the order becomes PAID when
-- a payment SUCCEEDS.

CREATE TABLE payments (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id           BIGINT         NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    provider           VARCHAR(32)    NOT NULL,          -- STRIPE | PAYPAL
    status             VARCHAR(32)    NOT NULL DEFAULT 'PENDING',
    amount             NUMERIC(12, 2) NOT NULL,
    external_reference VARCHAR(255),                     -- provider session/intent id
    created_at         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ    NOT NULL DEFAULT now()
);

CREATE INDEX idx_payments_order_id ON payments (order_id);

-- The provider's reference is our idempotency key for webhooks: a repeated
-- webhook for the same reference must not be processed twice.
CREATE UNIQUE INDEX uq_payments_external_reference
    ON payments (external_reference)
    WHERE external_reference IS NOT NULL;
