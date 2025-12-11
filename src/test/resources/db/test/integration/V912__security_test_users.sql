-- V912: Security Test Users for Authorization Testing
-- Creates users with different roles for security regression tests

-- Staff user (limited operations - view, create drafts)
INSERT INTO users (id, username, password, full_name, email, active, created_at, updated_at)
VALUES (
    'b0000000-0000-0000-0000-000000000001',
    'staff',
    '$2a$10$mMan.18CFTqJA/FVpkJr3OgCD0uTuhF9Enjf99QHm9tWPJH.nCj5S', -- password: admin
    'Staff User',
    'staff@artivisi.com',
    TRUE,
    NOW(),
    NOW()
);

INSERT INTO user_roles (id, id_user, role, created_at, created_by)
VALUES (
    'b0000000-0000-0000-0000-000000000011',
    'b0000000-0000-0000-0000-000000000001',
    'STAFF',
    NOW(),
    'system'
);

-- Employee user (own payslips and profile only)
INSERT INTO users (id, username, password, full_name, email, active, created_at, updated_at)
VALUES (
    'b0000000-0000-0000-0000-000000000002',
    'employee',
    '$2a$10$mMan.18CFTqJA/FVpkJr3OgCD0uTuhF9Enjf99QHm9tWPJH.nCj5S', -- password: admin
    'Employee User',
    'employee@artivisi.com',
    TRUE,
    NOW(),
    NOW()
);

INSERT INTO user_roles (id, id_user, role, created_at, created_by)
VALUES (
    'b0000000-0000-0000-0000-000000000012',
    'b0000000-0000-0000-0000-000000000002',
    'EMPLOYEE',
    NOW(),
    'system'
);

-- Auditor user (read-only access to reports)
INSERT INTO users (id, username, password, full_name, email, active, created_at, updated_at)
VALUES (
    'b0000000-0000-0000-0000-000000000003',
    'auditor',
    '$2a$10$mMan.18CFTqJA/FVpkJr3OgCD0uTuhF9Enjf99QHm9tWPJH.nCj5S', -- password: admin
    'Auditor User',
    'auditor@artivisi.com',
    TRUE,
    NOW(),
    NOW()
);

INSERT INTO user_roles (id, id_user, role, created_at, created_by)
VALUES (
    'b0000000-0000-0000-0000-000000000013',
    'b0000000-0000-0000-0000-000000000003',
    'AUDITOR',
    NOW(),
    'system'
);
