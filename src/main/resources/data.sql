
-- Insert into userCred (linked via user_id)
INSERT INTO userCred (username, email, password,is_verified) VALUES
('johndoe',   'john.doe@example.com',   'password123',true),
('janesmith', 'jane.smith@example.com', 'securepass',true),
('test',    'alice.johnson@example.com', 'test',true),
('testuser',  't.guruteja5@gmail.com',  'testpass', false);

-- Reset sequences
SELECT setval('usercred_id_seq',
    COALESCE((SELECT MAX(id) FROM usercred), 0) + 1,
    false);

