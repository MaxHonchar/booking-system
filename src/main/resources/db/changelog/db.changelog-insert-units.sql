
--liquibase formatted sql
--changeset Max:4 runOnChange:false splitStatements:false

SELECT setval('unit_seq_id', 13, true);
SELECT setval('unit_properties_seq_id', 13, true);

DO $$
DECLARE
    i INT;
    unit_cost NUMERIC;
    unit_rooms INT;
    unit_floor INT;
    unit_type accommodation_type;
    event_random_id BIGINT;
BEGIN
FOR i IN 1..90 LOOP
        unit_cost := 20 + floor(random() * 900);
        unit_rooms := 1 + floor(random() * 10);
        unit_floor := 1 + floor(random() * 90);
        unit_type := (ARRAY['FLAT','HOME','APARTMENTS'])[1 + floor(random()*3)];
        event_random_id := 1 + floor(random() * 3);

INSERT INTO units (id, description, cost, booked)
VALUES (nextval('unit_seq_id'), 'Unit #' || i, unit_cost, false);

INSERT INTO unit_properties (id, unit_id, type, rooms, floor)
VALUES (nextval('unit_properties_seq_id'), currval('unit_seq_id'), unit_type, unit_rooms, unit_floor);

INSERT INTO event_unit_relation (event_id, unit_id)
VALUES (event_random_id, currval('unit_seq_id'));
END LOOP;
END $$;