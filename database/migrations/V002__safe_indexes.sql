-- Non-destructive indexes for an existing estateadvance schema.
-- Each ALTER runs only when the index is absent. The assignment uniqueness
-- constraint is added only when existing data contains no duplicate pairs.

SET @schema_name = DATABASE();

SET @ddl = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = @schema_name AND table_name = 'role' AND index_name = 'uk_role_code') = 0,
    'ALTER TABLE role ADD UNIQUE KEY uk_role_code (code)',
    'SELECT ''uk_role_code already exists'''
);
PREPARE migration_statement FROM @ddl;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @ddl = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = @schema_name AND table_name = 'rentarea' AND index_name = 'idx_rentarea_building') = 0,
    'ALTER TABLE rentarea ADD KEY idx_rentarea_building (buildingid)',
    'SELECT ''idx_rentarea_building already exists'''
);
PREPARE migration_statement FROM @ddl;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @ddl = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = @schema_name AND table_name = 'assignmentbuilding' AND index_name = 'idx_assignment_staff') = 0,
    'ALTER TABLE assignmentbuilding ADD KEY idx_assignment_staff (staffid)',
    'SELECT ''idx_assignment_staff already exists'''
);
PREPARE migration_statement FROM @ddl;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;

SET @duplicate_assignments = (
    SELECT COUNT(*)
    FROM (
        SELECT buildingid, staffid
        FROM assignmentbuilding
        GROUP BY buildingid, staffid
        HAVING COUNT(*) > 1
    ) duplicates
);
SET @ddl = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = @schema_name AND table_name = 'assignmentbuilding'
       AND index_name = 'uk_assignment_building_staff') = 0
       AND @duplicate_assignments = 0,
    'ALTER TABLE assignmentbuilding ADD UNIQUE KEY uk_assignment_building_staff (buildingid, staffid)',
    'SELECT ''assignment uniqueness unchanged; inspect duplicate pairs if the index is absent'''
);
PREPARE migration_statement FROM @ddl;
EXECUTE migration_statement;
DEALLOCATE PREPARE migration_statement;
