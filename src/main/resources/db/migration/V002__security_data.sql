-- V002: Security Data
-- Insert default admin user (password: admin)

INSERT INTO users (id, username, password, full_name, email, active, created_at, updated_at)
VALUES (
    'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11',
    'admin',
    '$2a$10$mMan.18CFTqJA/FVpkJr3OgCD0uTuhF9Enjf99QHm9tWPJH.nCj5S',
    'Administrator',
    'admin@artivisi.com',
    TRUE,
    NOW(),
    NOW()
);
