CREATE TABLE cached_crypto_price (
    asset_symbol VARCHAR(255) NOT NULL,
    price_usd DOUBLE PRECISION NOT NULL,
    market_cap DOUBLE PRECISION NOT NULL,
    volume24h DOUBLE PRECISION NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (asset_symbol)
);
