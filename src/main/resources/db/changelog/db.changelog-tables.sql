--liquibase formatted sql

--changeset Max:2
CREATE TABLE users
(
    id    BIGINT PRIMARY KEY     NOT NULL DEFAULT nextval('user_seq_id'),
    email character varying(255) NOT NULL,
    CONSTRAINT UNIQUE_EMAIL_INDEX UNIQUE (email)
);

CREATE TABLE events
(
    id   BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('events_seq_id'),
    type event_type,
    CONSTRAINT UNIQUE_EVENT_TYPE_INDEX UNIQUE (type)
);

CREATE TABLE event_unit_relation
(
    event_id BIGINT NOT NULL,
    unit_id  BIGINT NOT NULL,
    CONSTRAINT candidate_company_relation_pkey PRIMARY KEY (event_id, unit_id)
);

CREATE TABLE booking
(
    id        BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('booking_seq_id'),
    user_id   BIGINT             NOT NULL,
    unit_id   BIGINT             NOT NULL,
    check_in  timestamp,
    check_out timestamp
);


CREATE TABLE payments
(
    id         BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('payments_seq_id'),
    booking_id BIGINT             NOT NULL,
    status     payment_status,
    paid_at    timestamp,
    amount     numeric
);

CREATE TABLE units
(
    id          BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('unit_seq_id'),
    description character varying(255),
    booked      boolean,
    cost        numeric
);

CREATE TABLE unit_properties
(
    id      BIGINT PRIMARY KEY NOT NULL DEFAULT nextval('unit_properties_seq_id'),
    unit_id BIGINT             NOT NULL,
    type    accommodation_type,
    rooms   int,
    floor   int
);

