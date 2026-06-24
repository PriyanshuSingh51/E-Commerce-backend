-- V3: Add Indexes for Performance

-- Product indexes
CREATE INDEX IF NOT EXISTS idx_product_name ON products(name);
CREATE INDEX IF NOT EXISTS idx_product_category ON products(category_id);
CREATE INDEX IF NOT EXISTS idx_product_price ON products(price);
CREATE INDEX IF NOT EXISTS idx_product_active ON products(is_active);

-- Order indexes
CREATE INDEX IF NOT EXISTS idx_order_user ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_order_created ON orders(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_order_number ON orders(order_number);

-- Order item indexes
CREATE INDEX IF NOT EXISTS idx_order_item_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_item_product ON order_items(product_id);

-- User indexes
CREATE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_user_role ON users(role);

-- Payment indexes
CREATE INDEX IF NOT EXISTS idx_payment_order ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payment_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payment_transaction ON payments(transaction_id);
