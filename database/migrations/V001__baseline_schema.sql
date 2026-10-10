-- Baseline schema for a new estateadvance database.
-- Select an empty database before running this file. Existing tables are never dropped.

CREATE TABLE IF NOT EXISTS role (
    id BIGINT NOT NULL AUTO_INCREMENT,
    createddate DATETIME NULL,
    createdby VARCHAR(255) NULL,
    modifieddate DATETIME NULL,
    modifiedby VARCHAR(255) NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    createddate DATETIME NULL,
    createdby VARCHAR(255) NULL,
    modifieddate DATETIME NULL,
    modifiedby VARCHAR(255) NULL,
    username VARCHAR(100) NOT NULL,
    fullname VARCHAR(255) NOT NULL,
    password VARCHAR(100) NOT NULL,
    status INT NOT NULL DEFAULT 1,
    email VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username),
    UNIQUE KEY uk_user_email (email),
    KEY idx_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS building (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    street VARCHAR(255) NULL,
    district VARCHAR(50) NULL,
    ward VARCHAR(100) NULL,
    numberofbasement BIGINT NULL,
    managername VARCHAR(255) NULL,
    managerphone VARCHAR(30) NULL,
    rentprice INT NULL,
    floorarea INT NULL,
    brokeragefee FLOAT NULL,
    servicefee VARCHAR(100) NULL,
    direction VARCHAR(50) NULL,
    level VARCHAR(50) NULL,
    rentpricedescription VARCHAR(500) NULL,
    carfee VARCHAR(100) NULL,
    motofee VARCHAR(100) NULL,
    overtimefee VARCHAR(100) NULL,
    electricityfee VARCHAR(100) NULL,
    deposit VARCHAR(100) NULL,
    payment VARCHAR(100) NULL,
    renttime VARCHAR(100) NULL,
    decorationtime VARCHAR(100) NULL,
    note TEXT NULL,
    structure VARCHAR(255) NULL,
    type VARCHAR(255) NULL,
    avatar VARCHAR(500) NULL,
    PRIMARY KEY (id),
    KEY idx_building_district (district),
    KEY idx_building_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS rentarea (
    id BIGINT NOT NULL AUTO_INCREMENT,
    value BIGINT NOT NULL,
    buildingid BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_rentarea_building (buildingid),
    CONSTRAINT fk_rentarea_building FOREIGN KEY (buildingid) REFERENCES building(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    KEY idx_user_role_role (role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS assignmentbuilding (
    id BIGINT NOT NULL AUTO_INCREMENT,
    staffid BIGINT NOT NULL,
    buildingid BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_assignment_building_staff (buildingid, staffid),
    KEY idx_assignment_staff (staffid),
    CONSTRAINT fk_assignment_staff FOREIGN KEY (staffid) REFERENCES `user`(id) ON DELETE CASCADE,
    CONSTRAINT fk_assignment_building FOREIGN KEY (buildingid) REFERENCES building(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO role(code, name)
SELECT 'ADMIN', 'Quản trị hệ thống' WHERE NOT EXISTS (SELECT 1 FROM role WHERE code = 'ADMIN');
INSERT INTO role(code, name)
SELECT 'MANAGER', 'Quản lý tòa nhà' WHERE NOT EXISTS (SELECT 1 FROM role WHERE code = 'MANAGER');
INSERT INTO role(code, name)
SELECT 'STAFF', 'Nhân viên' WHERE NOT EXISTS (SELECT 1 FROM role WHERE code = 'STAFF');
INSERT INTO role(code, name)
SELECT 'USER', 'Người dùng' WHERE NOT EXISTS (SELECT 1 FROM role WHERE code = 'USER');
