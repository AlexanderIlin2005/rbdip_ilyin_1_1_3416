
ALTER TABLE order_items ADD COLUMN product_id BIGINT REFERENCES products(id);

UPDATE order_items oi
SET product_id = p.id
FROM products p
WHERE p.name = oi.product_name
  AND p.price = oi.product_price;