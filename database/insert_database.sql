-- Legacy compatibility seed. Run database/migrations/V001__baseline_schema.sql first.
-- This file intentionally creates no accounts and contains no reusable password hash.
USE estateadvance;

INSERT INTO role(code, name)
SELECT 'ADMIN', 'Quản trị hệ thống'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE code = 'ADMIN');

INSERT INTO role(code, name)
SELECT 'MANAGER', 'Quản lý tòa nhà'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE code = 'MANAGER');

INSERT INTO role(code, name)
SELECT 'STAFF', 'Nhân viên'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE code = 'STAFF');

INSERT INTO role(code, name)
SELECT 'USER', 'Người dùng'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE code = 'USER');
