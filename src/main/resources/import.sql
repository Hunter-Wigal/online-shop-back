INSERT INTO roles (name) VALUES ('ADMIN'), ('USER');
-- Use single quotes for strings

INSERT INTO product (id, item_name, description, price) VALUES (1, 'New item', 'Cool description', 60.00);

INSERT INTO product (id, item_name, description, price) VALUES (2, 'New item 2', 'The second edition', 50.00);

INSERT INTO product (id, item_name, description, price) VALUES (3, 'New item 3', 'New and improved', 70.00);

--INSERT INTO users (user_id, name, email, age, password) VALUES (0, 'Hunter Wigal', 'test@gmail.com', 10, 'passwordhash');

--INSERT INTO transactions (transaction_id, product, user_id, quantity, status) VALUES (0, 1, 0, 5, 'In progress');