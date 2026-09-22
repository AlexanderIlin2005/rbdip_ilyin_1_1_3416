

CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(50)
);


ALTER TABLE orders ADD COLUMN customer_id BIGINT REFERENCES customers(id);

INSERT INTO customers (full_name, address, phone)
SELECT DISTINCT customer_full_name, customer_address, customer_phone
FROM orders;

UPDATE orders o
SET customer_id = c.id
FROM customers c
WHERE c.full_name = o.customer_full_name
  AND COALESCE(c.address, '') = COALESCE(o.customer_address, '')
  AND COALESCE(c.phone, '') = COALESCE(o.customer_phone, '');