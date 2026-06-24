-- V2: Seed Data

-- Categories
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and accessories'),
('Clothing', 'Apparel and fashion items'),
('Books', 'Books and educational materials'),
('Home & Kitchen', 'Home appliances and kitchen items'),
('Sports', 'Sports and outdoor equipment');

INSERT INTO categories (name, description, parent_category_id) VALUES
('Smartphones', 'Mobile phones and accessories', 1),
('Laptops', 'Laptops and computers', 1),
('Headphones', 'Audio equipment', 1),
('Men''s Wear', 'Men clothing', 2),
('Women''s Wear', 'Women clothing', 2);

-- Admin User (password: admin123)
INSERT INTO users (email, password, first_name, last_name, role) VALUES
('admin@ecommerce.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'User', 'ADMIN');

-- Customer Users (password: password123)
INSERT INTO users (email, password, first_name, last_name, phone, role) VALUES
('john.doe@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'John', 'Doe', '+1234567890', 'CUSTOMER'),
('jane.smith@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Jane', 'Smith', '+0987654321', 'CUSTOMER');

-- Products
INSERT INTO products (name, description, price, stock, category_id, image_url) VALUES
('iPhone 15 Pro', 'Latest Apple iPhone with A17 Pro chip', 999.99, 50, 6, 'https://example.com/iphone15.jpg'),
('Samsung Galaxy S24', 'Samsung flagship with Snapdragon 8 Gen 3', 899.99, 45, 6, 'https://example.com/s24.jpg'),
('MacBook Pro 14"', 'Apple MacBook Pro with M3 chip', 1999.99, 20, 7, 'https://example.com/macbook.jpg'),
('Dell XPS 15', 'Premium Windows laptop', 1499.99, 25, 7, 'https://example.com/dellxps.jpg'),
('Sony WH-1000XM5', 'Noise-cancelling wireless headphones', 349.99, 100, 8, 'https://example.com/sony.jpg'),
('AirPods Pro', 'Apple wireless earbuds with ANC', 249.99, 150, 8, 'https://example.com/airpods.jpg'),
('Levi''s 501 Jeans', 'Classic straight leg jeans', 69.99, 200, 9, 'https://example.com/levis.jpg'),
('Nike Air Max', 'Running shoes', 129.99, 120, 9, 'https://example.com/nike.jpg'),
('Clean Code Book', 'A Handbook of Agile Software Craftsmanship', 34.99, 80, 3, 'https://example.com/cleancode.jpg'),
('Instant Pot Duo', '7-in-1 Electric Pressure Cooker', 89.99, 60, 4, 'https://example.com/instantpot.jpg');
