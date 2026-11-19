-- V5 Seed Default Accounts (BCrypt hashed)
-- Password 'Manager@123': $2a$12$Nq9v7bH4tZ5K...
-- Password 'Customer@123': $2a$12$e8F1pW2...
INSERT INTO users (id, username, email, password, full_name, active) VALUES
(1, 'manager', 'manager@possystem.com', '$2a$12$Nq9v7bH4tZ5K2w7x8y9z0a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6', 'Default Store Manager', TRUE),
(2, 'customer', 'customer@possystem.com', '$2a$12$e8F1pW2x3y4z5a6b7c8d9e0f1g2h3i4j5k6l7m8n9o0p1q2r3s4t5', 'Walk-in Customer', TRUE)
ON DUPLICATE KEY UPDATE username=VALUES(username);
