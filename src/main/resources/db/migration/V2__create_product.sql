CREATE TABLE product (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sku         VARCHAR(30)  NOT NULL UNIQUE,
    name        VARCHAR(120) NOT NULL,
    stock       INTEGER      NOT NULL CHECK (stock >= 0),
    min_stock   INTEGER      NOT NULL CHECK (min_stock >= 0),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);