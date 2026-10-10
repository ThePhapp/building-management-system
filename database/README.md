# Database setup and migrations

The application uses one schema name everywhere: `estateadvance`. Hibernate schema generation is disabled, so schema changes are explicit and reviewable.

## New installation

1. Create an empty UTF-8 MySQL database named `estateadvance`.
2. Run `migrations/V001__baseline_schema.sql` while connected to that database.
3. Create the first administrator with a unique BCrypt hash generated outside SQL. Never reuse the example or a shared default password.
4. Configure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` for a dedicated application account with only the required CRUD permissions.

Example administrator statement (replace every placeholder before running):

```sql
START TRANSACTION;
INSERT INTO `user` (username, password, fullname, status, email)
VALUES ('<admin-username>', '<bcrypt-hash>', '<admin-full-name>', 1, '<admin-email>');
INSERT INTO user_role (user_id, role_id)
SELECT LAST_INSERT_ID(), id FROM role WHERE code = 'ADMIN';
COMMIT;
```

## Existing installation

1. Take and verify a database backup.
2. Run read-only duplicate checks before applying constraints:

   ```sql
   SELECT buildingid, staffid, COUNT(*)
   FROM assignmentbuilding
   GROUP BY buildingid, staffid
   HAVING COUNT(*) > 1;
   ```

3. Review and run `migrations/V002__safe_indexes.sql`. It does not delete rows; it skips the assignment uniqueness constraint when duplicate pairs exist.
4. Run the role-only `insert_database.sql` if any standard roles are missing.

These scripts never drop a table, truncate data, or create a default account. Production migrations must be reviewed and backed up before execution.
