--liquibase formatted sql
--changeset Max:3

INSERT INTO events("id", "type")
VALUES
    (1, 'CREATE'),
    (2, 'UPDATE'),
    (3, 'DELETE');

INSERT INTO units("id", "cost", "description", "booked")
VALUES
    (1, 50.5, 'Large apartment with sea view', false),
    (2, 45.5, 'Modern studio on high floor', false),
    (3, 35.5, 'Comfortable apartment', false),
    (4, 27.7, 'Comfortable studio', false),
    (5, 60,   'Large and Comfortable apartment near with the sea', false),
    (6, 90.8,   'Very Large and Comfortable apartment near with the sea', false),
    (7, 108.8,   'VIP apartment near with the sea', false),
    (8, 153,   'VIP house near with the sea', false),
    (9, 20,   'small flat', false),
    (10, 15.5,   'very small flat', false);

INSERT INTO event_unit_relation("event_id", "unit_id")
VALUES
    (1,1),
    (1,2),
    (1,3),
    (1,4),
    (1,5),
    (2,6),
    (2,7),
    (2,8),
    (2,9),
    (1,10);


INSERT INTO unit_properties("id", "unit_id", "type", "rooms", "floor")
VALUES
    (1, 1, 'APARTMENTS', 3, 1),
    (2, 2, 'FLAT', 2, 1),
    (3, 3, 'APARTMENTS', 2, 3),
    (4, 4, 'HOME', 2, 2),
    (5, 5, 'APARTMENTS', 3, 8),
    (6, 6, 'APARTMENTS', 4, 2),
    (7, 7, 'APARTMENTS', 4, 88),
    (8, 8, 'HOME', 8, 1),
    (9, 9, 'FLAT', 1, 1),
    (10, 10, 'FLAT', 1, 2);



