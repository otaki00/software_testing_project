
CREATE TABLE IF NOT EXISTS exchange_rates (
    id serial PRIMARY KEY,
    base varchar(3) NOT NULL,
    code varchar(3) NOT NULL,
    rate double precision NOT NULL
);