INSERT INTO roles (name) VALUES ('ADMIN'), ('USER');
-- Use single quotes for strings

INSERT INTO products (product_id, item_name, description, price, image_url) VALUES (1000, 'New item', 'Cool description', 60.00, 'https://picsum.photos/400?random=1');

INSERT INTO products (product_id, item_name, description, price, image_url) VALUES (2000, 'New item 2', 'The second edition', 50.00, 'https://picsum.photos/400?random=2');

INSERT INTO products (product_id, item_name, description, price, image_url) VALUES (3000, 'New item 3', 'New and improved', 70.00, 'https://picsum.photos/400?random=3');

--INSERT INTO users (user_id, name, email, age, password) VALUES (0, 'Hunter Wigal', 'test@gmail.com', 10, 'passwordhash');

--INSERT INTO transactions (transaction_id, product, user_id, quantity, status) VALUES (0, 1, 0, 5, 'In progress');