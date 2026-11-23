CREATE TABLE stock_movement (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id  BIGINT       NOT NULL REFERENCES product (id),
    type        VARCHAR(20)  NOT NULL CHECK (type IN ('INBOUND', 'OUTBOUND', 'ADJUSTMENT')),
    quantity    INTEGER      NOT NULL CHECK (quantity <> 0),
    stock_after INTEGER      NOT NULL CHECK (stock_after >= 0),
    reference   VARCHAR(200),
    created_by  VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX stock_movement_product_created_idx
    ON stock_movement (product_id, created_at DESC);