-- ---------------------------------------------------------------------------
-- DEVELOPMENT SEED ADMIN
--
-- Bootstraps a single ADMIN account so a freshly cloned repo is usable
-- immediately: admin endpoints require an existing admin, and registration
-- always creates USER accounts, so without this the first admin could only be
-- created by editing the database by hand.
--
--     email:    admin@example.com
--     password: admin12345
--
-- !! THIS CREDENTIAL IS PUBLIC — IT IS IN VERSION CONTROL. !!
-- It is intended for local development and demos ONLY. Before any real
-- deployment either delete this account or change its password:
--
--     DELETE FROM users WHERE email = 'admin@example.com';
--
-- The password below is a Bcrypt hash (cost 10) of 'admin12345'; the raw
-- password is never stored.
-- ---------------------------------------------------------------------------

INSERT INTO users (name, email, password, role)
VALUES (
    'Dev Admin',
    'admin@example.com',
    '$2a$10$viw1PLnF60r35uI2onTPIumTVzOuYfb41fyGb.17pvwjpHu6Md8Ei',
    'ADMIN'
)
ON CONFLICT (email) DO NOTHING;
